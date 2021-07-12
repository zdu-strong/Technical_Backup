import { Alert, Dialog, DialogContent, DialogTitle, Divider, Fab } from "@mui/material"
import { messageList, MessageService, MESSAGE_TYPE_ENUM } from "."
import linq from "linq";
import { useAsLocalSource, observer } from 'mobx-react-use-autorun';
import CloseIcon from '@mui/icons-material/Close';
import HelpIcon from '@mui/icons-material/Help';
import Tooltip from '@mui/material/Tooltip';
import { stylesheet } from "typestyle";

export default observer(() => {

    const source = useAsLocalSource({ messageList });

    function getMessageType() {
        if (source.messageList.length > 0) {
            const type = linq.from(messageList).select(s => s.type).first();
            if (type === MESSAGE_TYPE_ENUM.warning) {
                return 'warning';
            } else if (type === MESSAGE_TYPE_ENUM.info) {
                return 'info';
            } else if (type === MESSAGE_TYPE_ENUM.success) {
                return 'success';
            }
        }
        return "error";
    }

    function getMessageContentTextColor() {
        if (source.messageList.length > 0) {
            const type = linq.from(messageList).select(s => s.type).first();
            if (type === MESSAGE_TYPE_ENUM.warning) {
                return '#ff9800';
            } else if (type === MESSAGE_TYPE_ENUM.info) {
                return '#03a9f4';
            } else if (type === MESSAGE_TYPE_ENUM.success) {
                return '#4caf50';
            }
        }
        return "#ef5350";
    }

    function getTitleTextColor() {
        if (source.messageList.length > 0) {
            const type = linq.from(messageList).select(s => s.type).first();
            if (type === MESSAGE_TYPE_ENUM.warning) {
                return '#ed6c02';
            } else if (type === MESSAGE_TYPE_ENUM.info) {
                return '#0288d1';
            } else if (type === MESSAGE_TYPE_ENUM.success) {
                return '#2e7d32';
            }
        }
        return "#d32f2f";
    }

    return <>
        {source.messageList.length > 0 && <Dialog
            open={true}
            onClose={() => {
                MessageService.error([])
            }}
            disableRestoreFocus={true}
            fullWidth={true}
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" style={{ color: getTitleTextColor() }}>
                    {getMessageType() === MESSAGE_TYPE_ENUM.error && 'Error'}
                    {getMessageType() === MESSAGE_TYPE_ENUM.success && 'Success'}
                    {getMessageType() === MESSAGE_TYPE_ENUM.warning && 'Warning'}
                    {getMessageType() === MESSAGE_TYPE_ENUM.info && 'Info'}
                    <Tooltip title={<div className="flex flex-col">
                        <div>{"关闭弹窗的方式: "}</div>
                        <div>{"1. 按ESC键"}</div>
                        <div>{"2. 点击关闭按钮"}</div>
                        <div>{"3. 点击背景板"}</div>
                    </div>} arrow={true}>
                        <HelpIcon color={getMessageType()} fontSize="medium" style={{ marginLeft: "4px" }} />
                    </Tooltip>
                </div>
                <Fab color="default" id="closeButton" onClick={() => MessageService.error([])}>
                    <CloseIcon />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                {source.messageList.map(messsageObject =>
                    <Alert severity={messsageObject.type as any} className={css.alertMessageContent} key={messsageObject.id} style={{ marginTop: "1em", color: getMessageContentTextColor() }}>
                        {messsageObject.message}
                    </Alert>
                )}
            </DialogContent>
        </Dialog>}
    </>
})

const css = stylesheet({
    alertMessageContent: {
        $nest: {
            '.MuiAlert-message': {
                fontSize: "large",
                fontWeight: 'bold',
                alignItems: "center",
                display: "flex"
            },
            '.MuiAlert-icon': {
                alignItems: "center",
                fontSize: "x-large"
            }
        }
    }
})