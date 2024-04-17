import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { MessageService } from "@/common/MessageService";
import { GlobalUserInfo } from "@/common/Server";
import MessageChat from "@/component/Message/MessageChat";
import MessageMenu from "@/component/Message/MessageMenu";
import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import { concatMap, from, timer } from "rxjs";
import { stylesheet } from "typestyle";
import { v1 } from "uuid";
import MessageUnlimitedAutoSizer from "./MessageUnlimitedAutoSizer";

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
    ready: false,
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
          state.ready = true;
        } catch (error) {
          MessageService.error(error)
        }
      })()))
    ).subscribe());
  })

  return <>
    <LoadingOrErrorComponent ready={state.ready} error={null} />
    {
      state.ready && <div className={css.container} style={state.ready ? {} : { position: "absolute", visibility: "hidden" }} >
        <MessageMenu userId={GlobalUserInfo.id} username={GlobalUserInfo.username} />
        <MessageUnlimitedAutoSizer
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