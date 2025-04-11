import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { GlobalChatMessage, GlobalScrollToLastItemSubject } from '@/component/Message/js/Global_Chat';
import { List, Size } from 'react-virtualized';
import SingleMessage from "@/component/Message/SingleMessage";
import { useRef } from "react";
import { EMPTY, concatMap, delay, interval, of, take, tap } from "rxjs";
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing'


export default observer((props: Size) => {

  const state = useMobxState({
  }, {
    listRef: useRef<List>(null),
  })

  useMount(sub => {
    GlobalScrollToLastItemSubject.next();
    sub.add(GlobalScrollToLastItemSubject.pipe(
      exhaustMapWithTrailing(() => {
        return interval(1).pipe(
          concatMap(() => {
            if (GlobalChatMessage.totalRecords === 0) {
              return of(null);
            }
            if (!state.listRef.current) {
              return EMPTY;
            }
            return of(null).pipe(
              tap(() => {
                state.listRef.current?.scrollToRow(GlobalChatMessage.totalRecords - 1);
              }),
              delay(1),
              tap(() => {
                state.listRef.current?.scrollToRow(GlobalChatMessage.totalRecords - 1);
              }),
            );
          }),
          take(1)
        );
      }),
    ).subscribe())
  })

  return <List
    ref={state.listRef}
    width={props.width}
    height={props.height}
    rowCount={GlobalChatMessage.totalRecords}
    rowHeight={150}
    rowRenderer={(s) => <div style={s.style} key={s.key}>
      <SingleMessage pageNum={s.index + 1} key={s.index + 1} />
    </div>}
  >
  </List>
})