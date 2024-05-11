import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { GlobalUserInfo } from "@/common/Server";
import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { ReactNode } from "react";
import { v1 } from "uuid";

export default observer((props: {
  children: ReactNode,
  isAutoLogin?: boolean
  checkIsSignIn?: boolean
}) => {

  const state = useMobxState({
    isCheckFailed: false as any,
    ready: false,
    error: null as any,
  })

  useMount(async () => {
    if (!props.isAutoLogin) {
      check();
      state.ready = true;
      return;
    }
    try {
      if (!(await api.Authorization.isSignIn())) {
        await api.Authorization.signUp(v1(), "visitor", []);
      }
      check();
      state.ready = true;
    } catch (error) {
      state.error = error;
    }
  })

  function check() {
    if (checkIsSignIn()) {
      state.error = checkIsSignIn();
      state.isCheckFailed = true;
    }
  }

  function checkIsSignIn() {
    if (!props.checkIsSignIn) {
      return;
    }
    if (!GlobalUserInfo.accessToken) {
      return "Please login first and then visit";
    }
  }

  return <LoadingOrErrorComponent ready={state.ready && !state.isCheckFailed} error={state.error} >
    {props.children}
  </LoadingOrErrorComponent>
})