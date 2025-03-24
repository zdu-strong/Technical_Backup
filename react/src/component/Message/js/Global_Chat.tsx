import api from "@/api";
import { UserMessageModel } from "@/model/UserMessageModel";
import { observable, useMount } from "mobx-react-use-autorun";
import { ReplaySubject, Subscription, catchError, concatMap, repeat, share, tap, timer } from "rxjs"
import { v6 } from "uuid";

export const GlobalChatMessage = observable({
  totalRecord: 0,
  lastMessageId: "",
  ready: false,
  error: null,
  messageMap: {

  } as Record<number, UserMessageModel>
})

let subject = new ReplaySubject<{ pageNum: number, isCancel: boolean }>(100);

const GlobalShareMessageSubject = api.UserMessage.getUserMessageWebsocket(subject)
  .pipe(
    tap((s) => {
      let hasNewMessage = false;
      if (typeof s.totalPage === "number") {
        hasNewMessage = s.totalPage > GlobalChatMessage.totalRecord;
        GlobalChatMessage.totalRecord = s.totalPage;
      }
      for (const message of s.list) {
        GlobalChatMessage.messageMap[message.pageNum] = message;
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
      GlobalChatMessage.error = null;
    }),
    repeat({ delay: 2000 }),
    catchError((error, caught) => {
      GlobalChatMessage.error = error;

      return timer(2000).pipe(
        concatMap(() => caught)
      );
    }),
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

  let ready = !!(GlobalChatMessage.messageMap[pageNum] && GlobalChatMessage.messageMap[pageNum].id);
  let message = new UserMessageModel();

  if (GlobalChatMessage.messageMap[pageNum]) {
    message = GlobalChatMessage.messageMap[pageNum];
  }
  if (!message.id) {
    message.pageNum = pageNum;
    message.id = v6();
  }
  return { ready, message };
}

export function useGlobalMessageReady() {
  useMount((subjectSingle) => {
    subjectSingle.add(GlobalShareMessageSubject.subscribe());
  })

  return { ready: GlobalChatMessage.ready, error: GlobalChatMessage.error };
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
  subject = new ReplaySubject<{ pageNum: number, isCancel: boolean }>(100);
}
