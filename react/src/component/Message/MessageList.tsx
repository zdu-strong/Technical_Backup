import { observer, useAsLocalSource, useLocalObservable } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { Alert, Button, CircularProgress, TextField } from "@mui/material";
import SendIcon from '@mui/icons-material/Send';
import { MessageService } from "../../common/MessageService";
import * as api from '../../api'
import { useMount, useUnmount } from "react-use";
import { tap, Subscription, catchError, timer, concatMap, Observable, concat, interval, take, map, of, filter, from, Subject } from 'rxjs'
import { globalMessageMap } from './js/GlobalMessageMap'
import makeWebSocketObservable from 'rxjs-websockets'
import { MessageType } from "./js/MessageType";
import { jsonDateParser } from "json-date-parser"
import VariableSizeListComponent from "./VariableSizeListComponent";
import { useRef } from "react";
import MessageListChild from "./MessageListChild";

export default observer((props: { email: string }) => {
    const source = useAsLocalSource({
        ...props,
        variableSizeListRef: useRef<{
            scrollToItemByPageNum: (pageNum: number) => void,
            isNeedScrollToEnd: () => boolean,
        }>(),
    })

    const state = useLocalObservable(() => ({

        /* 想要跳转到哪一项, 输入框的值 */
        jumpItemOfInput: "",

        /* 待发送的消息 */
        messageContent: "",

        /* 是否正在发送消息 */
        loadingOfSend: false,

        subscription: new Subscription(),

        /* 消息的总条数 */
        totalPage: 0,

        async loadMessage(pageNum: number) {
            state.loadMessageSubject.next(pageNum);
        },
        loadMessageSubject: new Subject<number>(),
        ready: false,
        loadingMap: {

        } as Record<number, boolean>,
        errorMap: {

        } as Record<number, boolean>,
        error: null as boolean | null,

        child: (props: { pageNum: number }) => <MessageListChild
            loadMessage={() => state.loadMessage(props.pageNum)}
            pageNum={props.pageNum}
            email={source.email}
            error={!globalMessageMap[props.pageNum] && !state.loadingMap[props.pageNum] && state.errorMap[props.pageNum]}
        />
    }))

    useMount(() => {
        const ws = makeWebSocketObservable(api.getUserMessageWebsocketAddress(source.email)).pipe(
            concatMap((getResponses) => getResponses(state.loadMessageSubject.pipe(
                concatMap(s => concat(of(null), interval(500)).pipe(
                    filter(() => state.ready),
                    take(1),
                    map(() => s)
                )),
                filter(s => !globalMessageMap[s]),
                filter((s) => !state.loadingMap[s]),
                tap((s) => {
                    state.loadingMap[s] = true;
                }),
            ) as Observable<any>)),
            concatMap((messageListJsonString: any)=> from((async()=>{
                let isNeedScrollToEnd = source.variableSizeListRef?.current?.isNeedScrollToEnd();
                if(!state.ready){
                    isNeedScrollToEnd = true;
                }
                const messageList = JSON.parse(messageListJsonString, jsonDateParser) as MessageType[];
                for (const message of messageList) {
                    globalMessageMap[message.pageNum] = message;
                    state.totalPage = message.totalPage;
                }
                const totalPage = state.totalPage;
                if (isNeedScrollToEnd) {
                    isNeedScrollToEnd = messageList.some(item => item.pageNum === state.totalPage);
                }
                if (isNeedScrollToEnd) {
                    source.variableSizeListRef?.current?.scrollToItemByPageNum(totalPage);
                }
                state.ready = true;
                state.error = null;
            })())),
            catchError((_, caught) => {
                for (const key in state.loadingMap) {
                    state.errorMap[key] = true;
                }
                state.loadingMap = {};
                state.error = true;
                return timer(1000).pipe(
                    concatMap(() => caught)
                );
            })
        ).subscribe();
        state.subscription.add(ws);
    })

    useUnmount(() => {
        state.subscription.unsubscribe();
    })

    async function sendMessage() {
        if (!state.messageContent) {
            return MessageService.error("请填写消息内容");
        }
        if (state.loadingOfSend) {
            return;
        }
        try {
            state.loadingOfSend = true;
            await api.sendMessage(source.email, state.messageContent);
            state.messageContent = "";
        } catch (error) {
            MessageService.error("发送失败")
        } finally {
            state.loadingOfSend = false;
        }
    }

    function jump() {
        if (state.jumpItemOfInput === (0 as any)) {
            return MessageService.error("最小值为1");
        }
        if (!state.jumpItemOfInput) {
            return MessageService.error("请填写数字");
        }
        if (typeof Number(state.jumpItemOfInput) !== "number") {
            return MessageService.error("请填写数字");
        }
        const jumpItem = Math.floor(Number(state.jumpItemOfInput));
        if (state.totalPage === 0 && jumpItem !== 1) {
            if (jumpItem < 1) {
                return MessageService.error("最小值为1");
            } else {
                return MessageService.error("最大值为1");
            }
        }
        if (state.totalPage === 0) {
            return;
        }
        if (jumpItem > state.totalPage) {
            return MessageService.error(`最大值为${state.totalPage}`);
        }
        if (jumpItem < 1) {
            return MessageService.error("最小值为1");
        }
        source.variableSizeListRef.current?.scrollToItemByPageNum(jumpItem);
    }


    return <div className={css.container}>
        <div className="flex flex-row" style={{ paddingBottom: "1em" }}>
            <div>
                当前用户
            </div>
            <div style={{ marginLeft: "1em" }}>
                {source.email}
            </div>
        </div>
        <div className="flex flex-row justify-center items-center w-full" style={{ paddingBottom: "1em" }}>
            <div className="flex flex-auto" style={{ marginRight: "3em" }}>
                <TextField label="消息内容" className="flex flex-auto" variant="outlined" onChange={(e) => {
                    state.messageContent = e.target.value;
                }} style={{ width: "230px" }} value={state.messageContent} onKeyDown={(e) => {
                    if (!e.shiftKey && e.key === "Enter") {
                        sendMessage()
                    }
                }}
                    autoComplete="off"
                />
                <Button variant="contained" color="secondary" size="large" style={{ marginLeft: "1em" }} startIcon={state.loadingOfSend ? <CircularProgress /> : <SendIcon />}
                    onClick={sendMessage}
                >发送</Button>
            </div>
            <div className="flex" style={{}}>
                <TextField type="number" label={`请输入你想跳转到哪一项 max(${state.totalPage})`} variant="outlined" onChange={async (e) => {
                    state.jumpItemOfInput = e.target.value;
                }} style={{ width: "230px" }} value={state.jumpItemOfInput} onKeyDown={(e) => {
                    if (!e.shiftKey && e.key === "Enter") {
                        jump()
                    }
                }}
                    autoComplete="off"
                />
                <Button variant="contained" color="secondary" size="large" style={{ marginLeft: "1em" }} startIcon={<SendIcon />}
                    onClick={jump}
                >跳转</Button>
            </div>
        </div>
        {!state.error && !state.ready && <CircularProgress style={{ width: "40px", height: "40px" }} />}
        {state.error && <Alert severity="error">服务器访问出错哦</Alert>}
        <VariableSizeListComponent totalPage={state.totalPage} ref={source.variableSizeListRef as any} ready={state.ready && !state.error}>
            {state.child}
        </VariableSizeListComponent>
    </div>;

})

const css = stylesheet({
    container: {
        width: "100%",
        height: "100%",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        paddingLeft: "3em",
        paddingRight: "3em",
    },
})