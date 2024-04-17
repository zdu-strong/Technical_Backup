import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { GlobalChatMessage, GlobalScrollToLastItemSubject } from '@/component/Message/js/Global_Chat';
import { List, Size } from 'react-virtualized';
import SingleMessage from "./SingleMessage";
import { useRef } from "react";
import { delay, tap } from "rxjs";


export default observer((props: Size) => {

  const state = useMobxState({}, {
    listRef: useRef<List>(null),
  })

  useMount(sub => {
    sub.add(GlobalScrollToLastItemSubject.pipe(
      delay(10),
      tap((s) => {
        if (GlobalChatMessage.totalRecord > 0) {
          state.listRef.current?.scrollToRow(GlobalChatMessage.totalRecord - 1);
        }
      })
    ).subscribe())
  })

  return <List
    ref={state.listRef}
    {...props}
    rowCount={GlobalChatMessage.totalRecord}
    rowHeight={100}
    rowRenderer={(s) => <div style={s.style} key={s.key}>
      <SingleMessage pageNum={s.index + 1} />
    </div>}
  >
  </List>
})