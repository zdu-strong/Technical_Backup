import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { GlobalUserInfo } from "@/common/Server";
import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { ReactNode, useEffect } from "react";
import { ReplaySubject, concatMap, from } from "rxjs";
import { v1 } from "uuid";

export default observer((props: {
  children: ReactNode,
  isAutoLogin?: boolean
  checkIsSignIn?: boolean
}) => {

  const state = useMobxState({
    subject: new ReplaySubject<void>(),
    ready: false,
    error: null as any,
  })

  useMount(async (subscription) => {
    subscription.add(state.subject.pipe(
      concatMap(() => from((async () => {
        state.ready = false;
        state.error = null;
        try {
          if (props.isAutoLogin) {
            if (!(await api.Authorization.isSignIn())) {
              await api.Authorization.signUp(v1(), "visitor", []);
            }
          }
          check();
          state.ready = true;
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());
  })

  useEffect(() => {
    state.subject.next();
  }, [props.isAutoLogin, props.checkIsSignIn, state.subject])

  function check() {
    checkIsSignIn();
  }

  function checkIsSignIn() {
    if (!props.checkIsSignIn) {
      return;
    }
    if (!GlobalUserInfo.accessToken) {
      throw new Error("Please login first and then visit")
    }
  }

  return <LoadingOrErrorComponent ready={state.ready} error={state.error} >
    {props.children}
  </LoadingOrErrorComponent>
})