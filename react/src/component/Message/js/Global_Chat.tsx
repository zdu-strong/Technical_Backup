import api from "@/api";
import { UserMessageModel } from "@/model/UserMessageModel";
import { observable, useMount } from "mobx-react-use-autorun";
import { ReplaySubject, Subscription, of, retry, share, switchMap, tap } from "rxjs"

export const GlobalChatMessage = observable({
  totalRecord: 0,
  lastMessageId: "",
  ready: false,
  messageMap: {

  } as Record<number, { ready: boolean, message: UserMessageModel }>
})

const subject = new ReplaySubject<{ pageNum: number, isCancel: boolean }>(100);

const GlobalShareMessageSubject = of(null).pipe(
  switchMap(() => api.UserMessage.getUserMessageWebsocket(subject)),
  tap((s) => {
    let hasNewMessage = false;
    if (s.totalPage !== null && s.totalPage !== undefined) {
      hasNewMessage = s.totalPage > GlobalChatMessage.totalRecord;
      GlobalChatMessage.totalRecord = s.totalPage;
    }
    for (const message of s.list) {
      GlobalChatMessage.messageMap[message.pageNum] = {
        ready: true,
        message
      };
      if (message.pageNum === GlobalChatMessage.totalRecord) {
        if (message.id !== GlobalChatMessage.lastMessageId) {
          GlobalChatMessage.lastMessageId = message.id;
          hasNewMessage = true;
        }
      }
    }
    if (hasNewMessage) {
      scrollToLastItem();
    }
    GlobalChatMessage.ready = true;
  }),
  retry({ delay: 2000 }),
  share(),
);

function loadMessage(pageNum: number) {
  subject.next({ pageNum, isCancel: false });
}

function unloadMessage(pageNum: number) {
  subject.next({ pageNum, isCancel: true });
}

export function useGlobalSingleMessage(pageNum: number) {
  useMount((subjectSingle) => {
    subjectSingle.add(GlobalShareMessageSubject.subscribe());
    subjectSingle.add(new Subscription(() => {
      unloadMessage(pageNum);
    }));
    loadMessage(pageNum);
  })

  let ready = false;
  let message = new UserMessageModel();
  if (GlobalChatMessage.messageMap[pageNum]) {
    message = GlobalChatMessage.messageMap[pageNum].message;
    ready = GlobalChatMessage.messageMap[pageNum].ready;
  }
  if (GlobalChatMessage.ready && GlobalChatMessage.totalRecord === 0 && pageNum === 1) {
    ready = true;
  }
  return { ready, message };
}

export const GlobalScrollToLastItemSubject = new ReplaySubject<void>(1);
GlobalScrollToLastItemSubject.next();

export async function scrollToLastItem() {
  GlobalScrollToLastItemSubject.next();
}

export function reinitializeOfGlobalChat() {
  GlobalChatMessage.totalRecord = 0;
  GlobalChatMessage.lastMessageId = "";
  GlobalChatMessage.ready = false;
  GlobalChatMessage.messageMap = {
  };
}
