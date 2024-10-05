import LoadingOrErrorComponent from '@/common/MessageService/LoadingOrErrorComponent';
import { initGameEngine } from '@/component/Game/js/initGameEngine';
import * as BABYLON from '@babylonjs/core';
import { observer, useMobxState, useMount } from 'mobx-react-use-autorun';
import { EMPTY, Subscription, concat, concatMap, delay, fromEvent, interval, of, retry, take, tap, timer } from 'rxjs';
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing';
import { stylesheet } from 'typestyle';

const css = stylesheet({
  div: {
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
    height: "100vh",
    $nest: {
      "& > canvas": {
        width: "100%",
        height: "100%",
      },
      "& > div": {
        position: "absolute",
        top: 0,
        left: 0,
        width: "100%",
        height: "100%",
        zIndex: 1000,
        backgroundColor: "white",
      }
    }
  }
})

export default observer((props: {
  canvasRef: React.RefObject<HTMLCanvasElement>,
}) => {

  const state = useMobxState({
    engine: null as BABYLON.Engine | null,
    ready: false,
    error: null as any,
  }, {
    ...props,
  })

  useMount(async (subscription) => {
    try {
      for (let i = 100; i > 0; i--) {
        await timer(1).toPromise();
      }
      state.engine = await initGameEngine(state.canvasRef);
      subscription.add(new Subscription(() => {
        state.engine?.dispose();
      }));
      resizeGameCanvas(subscription);
      for (let i = 10; i > 0; i--) {
        await timer(16).toPromise();
      }
      state.canvasRef.current!.focus();
      state.ready = true;
    } catch (error) {
      state.error = error
    }
  })

  function resizeGameCanvas(subscription: Subscription) {
    subscription.add(concat(of(null), fromEvent(window, "resize")).pipe(
      exhaustMapWithTrailing(() => concat(of(null), interval(1)).pipe(
        concatMap(() => {
          if (state.error) {
            throw state.error;
          }
          if (state.engine) {
            return of(null);
          }
          return EMPTY;
        }),
        take(1),
        tap(() => {
          state.engine?.resize();
        }),
        delay(16),
      )),
      retry(),
    ).subscribe())
  }

  return <>
    <div className={css.div} style={state.ready ? {} : { position: "relative" }}>
      <canvas ref={state.canvasRef} style={{ outlineStyle: "none" }} />
      {!state.ready && <LoadingOrErrorComponent ready={state.ready} error={state.error} />}
    </div>
  </>
})