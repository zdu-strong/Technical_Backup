import { observer } from "mobx-react-use-autorun";
import React from "react";
import { BrowserRouter, HashRouter } from 'react-router-dom';

export default observer((props: { children: React.ReactNode }) => {

  if (process.env.NODE_ENV === "production") {
    return (<HashRouter>{props.children}</HashRouter>);
  } else {
    return (<BrowserRouter>{props.children}</BrowserRouter>);
  }
})
