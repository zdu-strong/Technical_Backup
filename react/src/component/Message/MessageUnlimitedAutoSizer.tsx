import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { AutoSizer } from 'react-virtualized';
import MessageUnlimitedList from '@/component/Message/MessageUnlimitedList';
import { scrollToLastItem, useGlobalSingleMessage } from "@/component/Message/js/Global_Chat";

const css = stylesheet({
  containerAutoSizer: {
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
    width: "100%",
  }
})


export default observer(() => {

  const state = useMobxState({}, {
    ...useGlobalSingleMessage(1)
  })

  useMobxEffect(() => {
    if (state.ready) {
      scrollToLastItem();
    }
  }, [state.ready])

  return <div className={css.containerAutoSizer}>
    {state.ready && <AutoSizer>
      {(size) => <MessageUnlimitedList {...size} />}
    </AutoSizer>}
  </div>
})