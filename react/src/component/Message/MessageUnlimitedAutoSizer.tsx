import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { AutoSizer } from 'react-virtualized';
import MessageUnlimitedList from '@/component/Message/MessageUnlimitedList';
import { useGlobalMessageReady } from "@/component/Message/js/Global_Chat";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";

const css = stylesheet({
  containerAutoSizer: {
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
    width: "100%",
  }
})


export default observer(() => {

  const state = useMobxState({
  }, {
    ...useGlobalMessageReady()
  })

  return <div className={css.containerAutoSizer}>
    <LoadingOrErrorComponent ready={state.ready} error={null}>
      <AutoSizer>
        {(size) => <MessageUnlimitedList {...size} />}
      </AutoSizer>
    </LoadingOrErrorComponent>
  </div>
})