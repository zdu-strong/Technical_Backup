import { observer, toJS, useAsLocalSource } from "mobx-react-use-autorun";
import { useMount } from "react-use";
import { Button, Chip, Divider } from "@mui/material";
import * as api from '../../api'
import { MessageService } from "../../common/MessageService";
import DeleteIcon from '@mui/icons-material/Delete'
import { globalMessageMap } from './js/GlobalMessageMap'

export default observer((props: { loadMessage: () => void, email: string, pageNum: number, error: boolean }) => {

    /* 属性来源, props, 第三方hooks */
    const source = useAsLocalSource({
        ...props,
        get message() {
            return globalMessageMap[props.pageNum]
        },
        get ready() {
            return !!globalMessageMap[props.pageNum]
        },
        get loading() {
            return !globalMessageMap[props.pageNum] && !props.error
        }
    })

    toJS(globalMessageMap[source.pageNum]);

    async function recall() {
        try {
            await api.recallMessage(source.message?.id!);
            source.message.isRecall = true
        } catch (error) {
            MessageService.error("撤回失败");
        }
    }

    useMount(() => {
        source.loadMessage();
    })

    return <>
        <div className="flex flex-row flex-wrap w-full justify-between" style={{ whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }}>
            <div className="flex">
                {source.loading && `第${source.pageNum}行, 正在加载中`}
                {source.error && `第${source.pageNum}行, 拉取失败`}
                {source.ready && source.message.isRecall && source.message.user.email !== source.email && <>
                    {`第${props.pageNum}行, ${source.message.user.email}: `}
                </>}
                {source.ready && source.message.isRecall && source.message.user.email === source.email && `第${props.pageNum}行, 我:`}
                {source.ready && !source.message.isRecall && source.message.user.email === source.email && `第${props.pageNum}行, 我:\n${source.message?.content || null}`}
                {source.ready && !source.message.isRecall && source.message.user.email !== source.email && `第${props.pageNum}行, ${source.message.user.email}:\n${source.message?.content || null}`}
            </div>
            {source.ready && source.message.isRecall && source.message.user.email === source.email && <div className="inline-flex" style={{}}>
                <Chip
                    label="已撤回"
                    deleteIcon={<DeleteIcon />}
                    variant="outlined"
                />
            </div>}
            {source.ready && !source.message.isRecall && source.message.user.email === source.email && <div>
                <Button size="small" variant="outlined" style={{ marginRight: "3px", marginTop: "3px" }} onClick={recall} >撤回</Button>
            </div>}
        </div>
        <div className="flex">
            {source.ready && source.message.isRecall && source.message.user.email !== source.email && <Chip
                label="撤回了一条消息"
                deleteIcon={<DeleteIcon />}
                variant="outlined"
            />}
        </div>
        <Divider />
    </>
})

