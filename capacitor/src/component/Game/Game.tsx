import LoadingOrErrorComponent from '@/common/MessageService/LoadingOrErrorComponent';
import { initGameEngine } from '@/component/Game/js/initGameEngine';
import { AndroidNotch } from '@awesome-cordova-plugins/android-notch';
import * as BABYLON from '@babylonjs/core';
import { Capacitor } from '@capacitor/core';
import { observer, useMobxState, useMount } from 'mobx-react-use-autorun';
import { useRef } from 'react';
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

export default observer(() => {

  const state = useMobxState({
    engine: null as BABYLON.Engine | null,
    ready: false,
    error: null as any,
    leftOrRight: 10,
  }, {
    canvasRef: useRef<HTMLCanvasElement>(null),
  })

  useMount(async (subscription) => {
    try {
      for (let i = 100; i > 0; i--) {
        await timer(1).toPromise();
      }
      await loadSafeAreaInsets();
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

  async function loadSafeAreaInsets() {
    if (Capacitor.getPlatform() === "web") {
      return;
    }

    if (Capacitor.getPlatform() === "android") {
      const insetLeftPromise = AndroidNotch.getInsetLeft();
      const insetRightPromise = AndroidNotch.getInsetRight();
      const insetLeft = await insetLeftPromise;
      const insetRight = await insetRightPromise;
      state.leftOrRight = Math.floor(Math.max(insetLeft, insetRight));
    }
  }

  function resizeGameCanvas(subscription: Subscription) {
    subscription.add(concat(of(null), fromEvent(window, "resize")).pipe(
      exhaustMapWithTrailing(() => concat(of(null), interval(1).pipe(
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
      ))),
      retry(),
    ).subscribe())
  }

  return <>
    <div className={css.div} style={state.ready ? {} : { position: "relative" }}>
      <canvas ref={state.canvasRef} style={{ outlineStyle: "none" }} />
      {!state.ready && <div
        className='flex flex-col flex-auto'
        style={{
          paddingLeft: `15px`,
          paddingRight: `15px`,
          paddingTop: '5px',
          paddingBottom: "5px",
        }}
      >
        <LoadingOrErrorComponent ready={state.ready} error={state.error} />
      </div>}
    </div>
  </>
})