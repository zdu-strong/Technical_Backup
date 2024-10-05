import { Alert, Skeleton } from "@mui/material"
import React from "react";
import { MESSAGE_TYPE_ENUM, getMessageObject } from "@/common/MessageService";
import { observer, useMobxState } from 'mobx-react-use-autorun';

export default observer((props: { ready: boolean, error: Error | Error[] | any, children?: React.ReactNode }) => {

  const state = useMobxState({
  }, {
    ...props,
  });

  return <>
    {state.error && state.error instanceof Array && state.error.map(error => {
      const errorObject = getMessageObject(MESSAGE_TYPE_ENUM.error, error);
      return <Alert severity="error" style={{ margin: "1em", whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }} key={errorObject.id}>
        {errorObject.message}
      </Alert>
    })}
    {state.error && !(state.error instanceof Array) && <Alert severity="error" style={{ margin: "1em", whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }}>
      {getMessageObject(MESSAGE_TYPE_ENUM.error, state.error).message}
    </Alert>}
    {(!state.error || (state.error instanceof Array && state.error.length === 0)) && !state.ready && <div className="flex flex-auto flex-col" style={{ minHeight: "10em", minWidth: "100%", padding: "1em" }}>
      <Skeleton variant="text" />
      <Skeleton variant="circular" width={40} height={40} />
      <Skeleton variant="rectangular" className="flex flex-auto" />
    </div>}
    {(!state.error || (state.error instanceof Array && state.error.length === 0)) && state.ready && state.children}
  </>
})