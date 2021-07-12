import { observer, useAsLocalSource, useLocalObservable } from "mobx-react-use-autorun";
import { VariableSizeList } from 'react-window'
import linq from 'linq'
import { stylesheet } from "typestyle";
import { useImperativeHandle, forwardRef, useRef, ReactNode, Ref } from "react";
import VariableSizeListChildComponent from "./VariableSizeListChildComponent";
import { addMilliseconds } from "date-fns";

export default observer(forwardRef((props: {
    totalPage: number,
    children: (props: { pageNum: number }) => ReactNode,
    ready: boolean,
}, ref: Ref<{
    scrollToItemByPageNum: (pageNum: number) => void,
    isNeedScrollToEnd: () => boolean,
}>) => {

    const source = useAsLocalSource({
        ...props,
        ref,
        variableSizeListRef: useRef<VariableSizeList<string[]>>(),
    })

    const state = useLocalObservable(() => ({
        /* 虚拟渲染容器的高度 */
        height: 700,

        /* 每一行的高度 */
        rowHeightMap: {

        } as Record<number, number>,

        /* 每一行默认的行高, 行高的初始值 */
        defaultRowHeight: 50,

        /* 可见区域之外呈现的行的数量 */
        overscanCount: 0,

        /* 期待的滚动显示的元素 */
        expectScrollItemByPageNum: null as number | null,

        /* 期待的滚动显示元素的最后调整时间 */
        expectScrollItemDate: null as Date | null,
    }))

    /* 获得每行的行高 */
    function getRowHeightByPageNum(pageNum: number) {
        if (typeof state.rowHeightMap[pageNum] === "number") {
            return state.rowHeightMap[pageNum];
        }
        return state.defaultRowHeight;
    }

    /* 为ref添加方法 */
    useImperativeHandle(source.ref, () => ({
        scrollToItemByPageNum(pageNum: number) {
            source.variableSizeListRef?.current?.scrollToItem(pageNum - 1, "start");
            state.expectScrollItemByPageNum = pageNum;
            state.expectScrollItemDate = addMilliseconds(new Date(), 200);
        },
        isNeedScrollToEnd() {
            let isNeedScroll = linq.range(0, source.totalPage > 0 ? source.totalPage - 1 : 0).select(index => getRowHeightByPageNum(index + 1)).sum() === (source.variableSizeListRef?.current?.state as any).scrollOffset;
            if (!isNeedScroll) {
                isNeedScroll = linq.range(0, source.totalPage).select(index => getRowHeightByPageNum(index + 1)).sum() <= (source.variableSizeListRef?.current?.state as any).scrollOffset + state.height;
            }
            return isNeedScroll;
        }
    }))

    return <VariableSizeList
        height={state.height}
        itemCount={source.totalPage}
        itemSize={(index) => getRowHeightByPageNum(index + 1)}
        width="100%"
        ref={source.variableSizeListRef as any}
        className={css.VariableSizeList}
        /* 要在可见区域之外呈现的行的数量 */
        overscanCount={state.overscanCount}
        estimatedItemSize={state.defaultRowHeight}
        style={source.ready ? {} : { visibility: "hidden" }}
    >
        {/* 子元素 */}
        {(props) => <VariableSizeListChildComponent
            {...props}
            /* 设置当前元素的高度 */
            setRowHeight={async (rowHeight) => {
                const originHeight = getRowHeightByPageNum(props.index + 1);
                if (rowHeight !== originHeight) {
                    const isNeedScrollToEnd = (() => {
                        let isNeedScroll = linq.range(0, source.totalPage > 0 ? source.totalPage - 1 : 0).select(index => getRowHeightByPageNum(index + 1)).sum() === (source.variableSizeListRef?.current?.state as any).scrollOffset;
                        if (!isNeedScroll) {
                            isNeedScroll = linq.range(0, source.totalPage).select(index => getRowHeightByPageNum(index + 1)).sum() <= (source.variableSizeListRef?.current?.state as any).scrollOffset + state.height;
                        }
                        return isNeedScroll;
                    })();
                    if (isNeedScrollToEnd) {
                        state.expectScrollItemByPageNum = source.totalPage;
                        state.expectScrollItemDate = addMilliseconds(new Date(), 200);
                    }
                    let isNeedSetScrollOffset = linq.range(0, props.index).select(index => getRowHeightByPageNum(index + 1)).sum() <= (source.variableSizeListRef?.current?.state as any).scrollOffset;
                    state.rowHeightMap[props.index + 1] = rowHeight;
                    source.variableSizeListRef?.current?.resetAfterIndex(props.index)
                    if (isNeedSetScrollOffset) {
                        source.variableSizeListRef?.current?.scrollTo((source.variableSizeListRef?.current?.state as any).scrollOffset + rowHeight - originHeight);
                    }
                    if (state.expectScrollItemByPageNum !== null && state.expectScrollItemDate !== null) {
                        if (new Date().getTime() < state.expectScrollItemDate.getTime()) {
                            source.variableSizeListRef.current?.scrollToItem(state.expectScrollItemByPageNum - 1, "start");
                        }
                    }
                }
            }}
        >
            {source.children}
        </VariableSizeListChildComponent>}
    </VariableSizeList>
}))

const css = stylesheet({
    VariableSizeList: {
        border: "1px solid purple",
        scrollbarWidth: "none",
        $nest: {
            "&::-webkit-scrollbar": {
                display: "none",
            }
        }
    }
})