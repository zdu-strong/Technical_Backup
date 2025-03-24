import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { GlobalUserInfo, toSignIn } from "@/common/Server";
import { observer, useMobxEffect, useMobxState, useMount } from "mobx-react-use-autorun";
import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { ReplaySubject, from } from "rxjs";
import { exhaustMapWithTrailing } from "rxjs-exhaustmap-with-trailing";
import { v6 } from "uuid";

export default observer((props: {
  children: ReactNode,
  isAutoLogin?: boolean,
  checkIsSignIn?: boolean,
  checkIsNotSignIn?: boolean,
}) => {

  const state = useMobxState(() => ({
    subject: new ReplaySubject<void>(1),
    error: null as any,
    hasInitAccessToken: false,
  }), {
    ...props,
    navigate: useNavigate()
  })

  useMount(async (subscription) => {
    subscription.add(state.subject.pipe(
      exhaustMapWithTrailing(() => from((async () => {
        try {
          state.error = null;
          await handleIsAutoSignIn();
          handleIsSignIn();
          handleCheckIsNotSignIn();
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());
  })

  useMobxEffect(() => {
    state.subject.next();
  }, [state.isAutoLogin, state.checkIsSignIn, state.checkIsNotSignIn, GlobalUserInfo.accessToken])

  function handleCheckIsNotSignIn() {
    if (state.checkIsNotSignIn && state.checkIsSignIn) {
      throw new Error("Must check if sign in")
    }
    if (state.checkIsNotSignIn && GlobalUserInfo.accessToken) {
      state.navigate("/");
    }
  }

  function handleIsSignIn() {
    if (state.checkIsSignIn && state.checkIsNotSignIn) {
      throw new Error("Must check if sign in")
    }

    if (state.checkIsSignIn && !GlobalUserInfo.accessToken) {
      toSignIn();
    }
  }

  async function handleIsAutoSignIn() {
    if (state.isAutoLogin && !state.checkIsSignIn) {
      throw new Error("Must check if sign in")
    }
    if (state.isAutoLogin && state.checkIsNotSignIn) {
      throw new Error("Must check if sign in")
    }
    if (state.isAutoLogin && !state.hasInitAccessToken && !GlobalUserInfo.accessToken) {
      await api.Authorization.signUp(v6(), "visitor", []);
    }
    if (!state.hasInitAccessToken && GlobalUserInfo.accessToken) {
      state.hasInitAccessToken = true;
    }
  }

  function isReady() {
    if (state.checkIsSignIn && !GlobalUserInfo.accessToken) {
      return false;
    }
    if (state.checkIsNotSignIn && GlobalUserInfo.accessToken) {
      return false;
    }
    return true;
  }

  return <LoadingOrErrorComponent ready={isReady()} error={state.error} >
    {state.children}
  </LoadingOrErrorComponent>
})