import { observer, useMobxState } from "mobx-react-use-autorun";
import { useGlobalSingleMessage } from "@/component/Message/js/Global_Chat";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import SingleMessageLoaded from "@/component/Message/SingleMessageLoaded";

export default observer((props: {
  pageNum: number
}) => {

  const state = useMobxState({
  }, {
    ...useGlobalSingleMessage(props.pageNum)
  })

  return <LoadingOrErrorComponent ready={state.ready} error={null}>
    <SingleMessageLoaded message={state.message} key={state.message.id} />
  </LoadingOrErrorComponent>
})
