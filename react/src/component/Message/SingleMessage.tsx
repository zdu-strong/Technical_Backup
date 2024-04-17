import { observer, useMobxState } from "mobx-react-use-autorun";
import { useGlobalSingleMessage } from "./js/Global_Chat";

export default observer((props: {
  pageNum: number
}) => {

  const state = useMobxState({
  }, {
    ...useGlobalSingleMessage(props.pageNum)
  })

  return <div>
    {state.message.content}
  </div>
})
