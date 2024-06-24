import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { GlobalUserInfo, toSignIn } from "@/common/Server";
import { observer, useMobxEffect, useMobxState, useMount } from "mobx-react-use-autorun";
import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { ReplaySubject, from } from "rxjs";
import { exhaustMapWithTrailing } from "rxjs-exhaustmap-with-trailing";
import { v1 } from "uuid";

export default observer((props: {
  children: ReactNode,
  isAutoLogin?: boolean,
  checkIsSignIn?: boolean,
  checkIsNotSignIn?: boolean,
}) => {

  const state = useMobxState(() => ({
    subject: new ReplaySubject<void>(1),
    ready: isReadyOfInit(),
    error: null as any,
    hasInitAccessToken: false,
  }), {
    ...props,
    navigate: useNavigate()
  })

  useMount(async (subscription) => {
    subscription.add(state.subject.pipe(
      exhaustMapWithTrailing(() => from((async () => {
        state.ready = false;
        state.error = null;
        try {
          if (state.isAutoLogin && !state.hasInitAccessToken && !GlobalUserInfo.accessToken) {
            await api.Authorization.signUp(v1(), "visitor", []);
          }
          if (!state.hasInitAccessToken && GlobalUserInfo.accessToken) {
            state.hasInitAccessToken = true;
          }
          if (state.checkIsSignIn && !GlobalUserInfo.accessToken) {
            toSignIn();
            return;
          }
          if (state.checkIsNotSignIn && GlobalUserInfo.accessToken) {
            state.navigate("/");
            return;
          }
          state.ready = true;
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());
  })

  useMobxEffect(() => {
    state.subject.next();
  }, [state.isAutoLogin, state.checkIsSignIn, state.checkIsNotSignIn, GlobalUserInfo.accessToken])

  function isReadyOfInit() {
    if (props.isAutoLogin && !GlobalUserInfo.accessToken) {
      return false;
    }
    if (props.checkIsSignIn && !GlobalUserInfo.accessToken) {
      return false;
    }
    if (props.checkIsNotSignIn && GlobalUserInfo.accessToken) {
      return false;
    }
    return true;
  }

  return <LoadingOrErrorComponent ready={isReadyOfInit() && state.ready} error={state.error} >
    {state.children}
  </LoadingOrErrorComponent>
})