import { observer } from "mobx-react-use-autorun";
import React from "react";
import { BrowserRouter, HashRouter } from 'react-router-dom';

export default observer((props: { children: React.ReactNode }) => {

  if (process.env.NODE_ENV === "production") {
    return (<HashRouter future={{ v7_relativeSplatPath: true, v7_startTransition: true }}>{props.children}</HashRouter>);
  } else {
    return (<BrowserRouter future={{ v7_relativeSplatPath: true, v7_startTransition: true }}>{props.children}</BrowserRouter>);
  }
})