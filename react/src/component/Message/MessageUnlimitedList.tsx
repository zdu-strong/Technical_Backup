import api from '@/api';
import MessageUnlimitedListChild from "@/component/Message/MessageUnlimitedListChild";
import { UserMessageModel } from "@/model/UserMessageModel";
import { Alert, CircularProgress } from "@mui/material";
import { observer, useMobxEffect, useMobxState, useMount } from "mobx-react-use-autorun";
import { useImperativeHandle, useRef } from 'react';
import { FormattedMessage } from "react-intl";
import { Virtuoso, VirtuosoHandle } from 'react-virtuoso';
import { EMPTY, ReplaySubject, Subscription, catchError, concatMap, delay, from, interval, repeat, switchMap, take, tap, timer } from 'rxjs';
import { stylesheet } from "typestyle";
import { v1 } from 'uuid';

const css = stylesheet({
  messsageListContainer: {
    width: "100%",
    height: "100%",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    flex: "1 1 auto",
  },
})

export default observer((props: {
  userId: string,
  username: string,
  setReadyForMessageEntry: (readyForMessageList: boolean) => Promise<void>,
  setErrorForMessageEntry: (error: boolean) => Promise<void>,
  variableSizeListRef: React.RefObject<{
    scrollToItemByLast: () => Promise<void>;
  }>
}) => {

  const state = useMobxState({
    /* total number of messages */
    totalPage: 0,
    /* message websocket */
    websocketInput: new ReplaySubject<{
      pageNum: number,
      isCancel: boolean,
    }>(1000),
    /* Is ready */
    ready: false,
    /* Is there an error */
    error: null as boolean | null,
    messageMap: {} as Record<string, UserMessageModel>,
    listenMessageSet: new Set<number>(),
    // extraScrollDate: new Date(),
    // extraScrollItem: 0,
    extraScrollItemSubject: new ReplaySubject<number>(1),
    /* The prefix of the child element id */
    idPrefix: `${v1()}-message-child-`,
    child,
  }, {
    ...props,
    virtuosoRef: useRef<VirtuosoHandle>(null),
    containerRef: useRef<HTMLDivElement>(null),
  })

  useMount((subscription) => {
    loadAllMessage(subscription);
    subscribeToExtraScrollItemSubject(subscription);
  })

  function child(index: number) {
    const pageNum = index + 1;
    return <MessageUnlimitedListChild
      idPrefix={state.idPrefix}
      loadMessage={() => {
        state.listenMessageSet.add(pageNum);
        state.websocketInput.next({
          pageNum: pageNum,
          isCancel: false,
        });
      }}
      unloadMessage={() => {
        state.listenMessageSet.delete(pageNum);
        state.websocketInput.next({
          pageNum: pageNum,
          isCancel: true,
        });
      }}
      pageNum={pageNum}
      ready={!!state.messageMap[pageNum]}
      loading={!state.messageMap[pageNum]}
      message={state.messageMap[pageNum]}
      userId={state.userId}
      username={state.username}
    />
  }

  useMobxEffect(() => {
    state.child = child;
  }, [state.userId, state.username, state.messageMap])



  function loadAllMessage(subscription: Subscription) {
    subscription.add(timer(1).pipe(
      switchMap(() => api.UserMessage.getUserMessageWebsocket(state.websocketInput)),
      concatMap(({ list: messageList, totalPage }) => from((async () => {
        let isNeedScrollToEndResult = await isNeedScrollToEnd();

        if (typeof totalPage === "number") {
          state.totalPage = totalPage;
        }
        for (const message of messageList) {
          state.messageMap[message.pageNum] = message;
        }

        if (!state.ready) {
          isNeedScrollToEndResult = true;
        }
        if (isNeedScrollToEndResult) {
          await scrollToItemByLast();
        }
        state.ready = true;
        state.error = null;
        await state.setReadyForMessageEntry(true);
        await state.setErrorForMessageEntry(false);
      })())),
      tap(() => {
        cleanMessageMap();
      }),
      repeat({ delay: 2000 }),
      catchError((error, caught) => {
        if (!state.ready) {
          if (!error || !error.message) {
            error = new Error("Network Error");
          }
          state.error = error;
          state.setErrorForMessageEntry(error);
        }
        return timer(2000).pipe(
          switchMap(() => caught),
        );
      }),
    ).subscribe());
  }

  function cleanMessageMap() {
    for (const pageNum in state.messageMap) {
      if (Number(pageNum) > state.totalPage - 100) {
        continue;
      }
      if (state.listenMessageSet.has(Number(pageNum))) {
        continue;
      }

      if (Array.from(state.listenMessageSet).some(s => s - 100 < Number(pageNum) && s + 100 > Number(pageNum))) {
        continue;
      }

      delete state.messageMap[pageNum];
    }
  }

  useImperativeHandle(state.variableSizeListRef, () => ({
    scrollToItemByLast,
  }))

  async function scrollToItemByLast() {
    await scrollToItemByPageNum(state.totalPage);
  }

  async function scrollToItemByPageNum(pageNum: number) {
    if (pageNum > state.totalPage || pageNum === 0) {
      return;
    }
    state.extraScrollItemSubject.next(pageNum);
  }

  async function isNeedScrollToEnd() {
    const scrollTop: number = await new Promise((resolve, reject) => {
      try {
        state.virtuosoRef.current?.getState(({ scrollTop }) => {
          resolve(scrollTop);
        })
      } catch (error) {
        reject(error)
      }
    });
    const clientHeight = state.containerRef.current!.clientHeight;
    const totalHeight = state.containerRef.current!.firstElementChild!.firstElementChild!.firstElementChild!.clientHeight;
    let isNeedScrollToEndResult = scrollTop + clientHeight >= totalHeight - 2;
    if (!isNeedScrollToEndResult) {
      const itemElement = window.document.getElementById(`${state.idPrefix}${state.totalPage}`);
      if (itemElement) {
        if (scrollTop + itemElement.clientHeight >= totalHeight - 2) {
          isNeedScrollToEndResult = true;
        }
      }
    }
    return isNeedScrollToEndResult;
  }

  function subscribeToExtraScrollItemSubject(subscription: Subscription) {
    subscription.add(state.extraScrollItemSubject.pipe(
      switchMap((pageNum) => {
        return interval(1).pipe(
          take(200),
          concatMap(() => {
            state.virtuosoRef?.current?.scrollToIndex(pageNum - 1);
            const itemElement = window.document.getElementById(`${state.idPrefix}${pageNum}`);
            if (itemElement) {
              return timer(1).pipe(
                tap(() => {
                  state.virtuosoRef?.current?.scrollToIndex(pageNum - 1);
                }),
                delay(1),
                tap(() => {
                  state.virtuosoRef?.current?.scrollToIndex(pageNum - 1);
                }),
              );
            } else {
              return EMPTY;
            }
          }),
          take(1),
        )
      })
    ).subscribe());
  }

  return <div className={css.messsageListContainer} ref={state.containerRef}>
    {!state.error && !state.ready && <CircularProgress style={{ width: "40px", height: "40px" }} />}
    {state.error && <Alert severity="error">
      <FormattedMessage id="ServerAccessError" defaultMessage="Server access error" />
    </Alert>}
    <Virtuoso
      className='flex flex-auto w-full'
      totalCount={state.totalPage}
      style={(state.ready && !state.error) ? {} : { visibility: "hidden" }}
      itemContent={state.child}
      ref={state.virtuosoRef}
    />
  </div>;
})