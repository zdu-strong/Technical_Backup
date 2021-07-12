import { Alert, Skeleton } from "@mui/material"
import React from "react";
import { MESSAGE_TYPE_ENUM, getMessage } from ".";
import { useAsLocalSource, useAutorun, observer, useLocalObservable } from 'mobx-react-use-autorun';

export default observer((props: { ready: boolean, error: any, children: React.ReactNode }) => {

    const state = useLocalObservable(() => ({
        errorMessage: "",
    }));

    const source = useAsLocalSource(props);

    useAutorun(() => {
        state.errorMessage = getMessage(MESSAGE_TYPE_ENUM.error, source.error).message;
    }, [source.error])

    return <>
        {source.error && <Alert severity="error" style={{ margin: "1em" }}>
            {state.errorMessage}
        </Alert>}
        {!source.error && !source.ready && <div className="flex flex-auto flex-col" style={{ minHeight: "10em", minWidth: "100%", padding: "1em" }}>
            <Skeleton variant="text" />
            <Skeleton variant="circular" width={40} height={40} />
            <Skeleton variant="rectangular" className="flex flex-auto" />
        </div>}
        {!source.error && source.ready && source.children}
    </>
})