import { observer, useAsLocalSource, useLocalObservable } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { ReactNode, useRef } from "react";
import { useMount, useUnmount, } from "react-use";
import { Subscription } from 'rxjs'

export default observer((props: {
    setRowHeight: (rowHeight: number) => void,
    children: (props: { pageNum: number }) => ReactNode,
    style: any,
    index: number,
}) => {
    /* 属性来源, props, 第三方hooks */
    const source = useAsLocalSource({
        ...props,
        containerRef: useRef<any>(),
    })

    const state = useLocalObservable(() => ({
        subscription: new Subscription(),
    }))

    function autoChangeSize() {
        const resizeObserver = new ResizeObserver((entries) => {
            source.setRowHeight(entries[0].contentRect.height);
        });
        state.subscription.add(new Subscription(() => {
            resizeObserver.disconnect();
        }));
        resizeObserver.observe(source.containerRef.current);
    }

    function initSetRowHeight() {
        const rowHeight = source.containerRef.current?.clientHeight;
        if (typeof rowHeight === "number") {
            source.setRowHeight(rowHeight);
        }
    }

    useMount(() => {
        initSetRowHeight();
        autoChangeSize();
    })

    useUnmount(() => {
        state.subscription.unsubscribe()
    })

    return <div style={source.style}>
        <div className={css.container} ref={source.containerRef}>
            {source.children({ pageNum: source.index + 1 })}
        </div>
    </div>
})

const css = stylesheet({
    container: {
        width: "100%",
        display: "flex",
        flexDirection: "column",
    }
})
