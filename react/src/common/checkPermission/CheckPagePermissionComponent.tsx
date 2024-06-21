import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { GlobalUserInfo, handleErrorWhenNotSignInToSignIn } from "@/common/Server";
import { observer, useMobxEffect, useMobxState, useMount } from "mobx-react-use-autorun";
import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { ReplaySubject, concatMap, from } from "rxjs";
import { v1 } from "uuid";

export default observer((props: {
  children: ReactNode,
  isAutoLogin?: boolean,
  checkIsSignIn?: boolean,
  checkIsNotSignIn?: boolean,
}) => {

  const state = useMobxState({
    subject: new ReplaySubject<void>(),
    ready: false,
    error: null as any,
  }, {
    ...props,
    navigate: useNavigate()
  })

  useMount(async (subscription) => {
    subscription.add(state.subject.pipe(
      concatMap(() => from((async () => {
        state.ready = false;
        state.error = null;
        try {
          if (state.isAutoLogin && !GlobalUserInfo.accessToken) {
            await api.Authorization.signUp(v1(), "visitor", []);
          }
          check();
          state.ready = true;
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());

  })

  useMobxEffect(() => {
    state.subject.next();
  }, [state.isAutoLogin, state.checkIsSignIn, state.subject, state.checkIsNotSignIn])

  useMobxEffect(() => {
    if (state.checkIsNotSignIn && GlobalUserInfo.accessToken) {
      state.navigate("/");
    }
  }, [state.checkIsNotSignIn, GlobalUserInfo.accessToken])

  function check() {
    checkIsSignIn();
  }

  function checkIsSignIn() {
    if (!state.checkIsSignIn) {
      return;
    }
    if (!GlobalUserInfo.accessToken) {
      handleErrorWhenNotSignInToSignIn({ status: 401 });
      throw new Error("Please login first and then visit")
    }
  }

  return <LoadingOrErrorComponent ready={state.ready} error={state.error} >
    {state.children}
  </LoadingOrErrorComponent>
})