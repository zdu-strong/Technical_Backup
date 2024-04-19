import { observer, useMobxState } from "mobx-react-use-autorun";
import React from "react";
import { BrowserRouter, HashRouter } from 'react-router-dom';

export default observer((props: { children: React.ReactNode }) => {

  const state = useMobxState({
  }, {
    ...props
  })

  if (process.env.NODE_ENV === "production") {
    return (<HashRouter>{state.children}</HashRouter>);
  } else {
    return (<BrowserRouter>{state.children}</BrowserRouter>);
  }
})