import api from "@/api";
import { UserMessageModel } from "@/model/UserMessageModel";
import { observable, useMount } from "mobx-react-use-autorun";
import { ReplaySubject, Subscription, of, retry, share, switchMap, tap } from "rxjs"

export const GlobalChatMessage = observable({
  totalRecord: 0,
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
    }
    if (hasNewMessage) {
      scrollToLastItem();
    }
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

  let ready = true;
  let message = new UserMessageModel();
  if (GlobalChatMessage.messageMap[pageNum]) {
    message = GlobalChatMessage.messageMap[pageNum].message;
    ready = GlobalChatMessage.messageMap[pageNum].ready;
  }
  return { ready, message };
}

export const GlobalScrollToLastItemSubject = new ReplaySubject<void>(1);
GlobalScrollToLastItemSubject.next();

export async function scrollToLastItem() {
  GlobalScrollToLastItemSubject.next();
}


