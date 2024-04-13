import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { GlobalUserInfo } from "@/common/Server";
import MessageChat from "@/component/Message/MessageChat";
import MessageMenu from "@/component/Message/MessageMenu";
import MessageUnlimitedList from "@/component/Message/MessageUnlimitedList";
import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import { concatMap, from, timer } from "rxjs";
import { stylesheet } from "typestyle";
import { v1 } from "uuid";

const css = stylesheet({
  container: {
    width: "100%",
    height: "100%",
    flex: "1 1 auto",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    paddingLeft: "1em",
    paddingRight: "1em",
  },
})

export default observer(() => {

  const state = useMobxState({
    readyForStart: false,
    readyForMessageEntry: false,
    async setReadyForMessageEntry(readyForMessageEntry: boolean) {
      state.readyForMessageEntry = readyForMessageEntry;
    },
    async setErrorForMessageEntry(error: any) {
      state.error = error;
    },
    error: null as any,
  }, {
    navigate: useNavigate(),
    variableSizeListRef: useRef<{
      scrollToItemByLast: () => Promise<void>,
    }>(null),
  })

  useMount(async (subscription) => {
    subscription.add(timer(1).pipe(
      concatMap(() => from((async () => {
        try {
          if (!(await api.Authorization.isSignIn())) {
            await api.Authorization.signUp(v1(), "visitor", []);
          }
          state.readyForStart = true;
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());
  })

  return <>
    <LoadingOrErrorComponent ready={state.readyForStart && state.readyForMessageEntry} error={state.error} />
    {
      state.readyForStart && <div className={css.container} style={state.readyForMessageEntry ? {} : { position: "absolute", visibility: "hidden" }} >
        <MessageMenu userId={GlobalUserInfo.id} username={GlobalUserInfo.username} />
        <MessageUnlimitedList
          userId={GlobalUserInfo.id!}
          username={GlobalUserInfo.username!}
          setReadyForMessageEntry={state.setReadyForMessageEntry}
          setErrorForMessageEntry={state.setErrorForMessageEntry}
          variableSizeListRef={state.variableSizeListRef}
          key={GlobalUserInfo.id}
        />
        <MessageChat
          userId={GlobalUserInfo.id!}
          username={GlobalUserInfo.username!}
          variableSizeListRef={state.variableSizeListRef}
        />
      </div>
    }
  </>
})