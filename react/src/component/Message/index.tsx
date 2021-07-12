import { observer, useAsLocalSource, useLocalObservable } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { Button, TextField } from "@mui/material";
import SendIcon from '@mui/icons-material/Send';
import { MessageService } from "../../common/MessageService";
import MessageList from "./MessageList";
import { useSearchParams } from "react-router-dom";

export default observer(() => {
    const source = useAsLocalSource({
        ...((() => {
            const [searchParams, nextSearchParams] = useSearchParams();
            return {
                nextSearchParams,
                email: searchParams.get("email") || ""
            };
        })()),
    })

    const state = useLocalObservable(() => ({
        readyForStart: false,
    }));

    function start() {
        const regex = new RegExp("^[A-Za-z0-9]+([_\\.][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$");
        if (!regex.test(source.email!)) {
            return MessageService.error("邮箱地址不正确");
        }
        state.readyForStart = true;
    }

    return <div className={css.container}>
        {!state.readyForStart && <div className="flex flex-row">
            <div className="flex" style={{ marginRight: "3em" }}>
                <TextField
                    label="请输入你的邮箱"
                    variant="outlined"
                    onChange={(e) => {
                        source.nextSearchParams({
                            email: e.target.value
                        })
                    }}
                    style={{ width: "230px" }}
                    value={source.email}
                    onKeyDown={(e) => {
                        if (!e.shiftKey && e.key === "Enter") {
                            start()
                        }
                    }}
                    autoComplete="off"
                />
                <Button
                    variant="contained"
                    color="secondary"
                    size="large"
                    style={{ marginLeft: "1em" }}
                    startIcon={<SendIcon />}
                    onClick={start}
                >开始使用</Button>
            </div>
        </div>}
        {state.readyForStart && <MessageList email={source.email!} />}
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
    },
})