import { observer, useMobxState } from "mobx-react-use-autorun";
import { useGlobalSingleMessage } from "./js/Global_Chat";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import SingleMessageAlreadyWithdrawn from "./SingleMessageAlreadyWithdrawn";
import SingleMessageLoaded from "./SingleMessageLoaded";

export default observer((props: {
  pageNum: number
}) => {

  const state = useMobxState({
  }, {
    ...useGlobalSingleMessage(props.pageNum)
  })

  return <LoadingOrErrorComponent ready={state.ready} error={null}>
    {state.message.isDelete || state.message.isRecall ? <SingleMessageAlreadyWithdrawn /> : <SingleMessageLoaded message={state.message} />}
  </LoadingOrErrorComponent>
})
