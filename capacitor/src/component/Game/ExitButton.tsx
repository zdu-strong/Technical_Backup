import { Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import ExitDialog from "@/component/Game/ExitDialog";
import { useMount } from "mobx-react-use-autorun";
import { AndroidNotch } from '@awesome-cordova-plugins/android-notch'
import { Capacitor } from '@capacitor/core'
import { delay, distinctUntilChanged, from, of, repeat, Subscription, tap } from "rxjs";
import { exhaustMapWithTrailing } from "rxjs-exhaustmap-with-trailing";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons'

const css = stylesheet({
  container: {
    width: "100%",
    height: "0px",
    position: "relative",
    display: "flex",
    flexDirection: "row",
  },
  exitButton: {
    position: "absolute",
    top: "10px"
  }
})

export default observer((props: { exit: () => void }) => {

  const state = useMobxState({
    exitDialog: {
      open: false,
    },
    ready: false,
    isLeftAndNotIsRight: false,
    leftOrRight: 10,
  }, {
    ...props,
  })

  useMount(async (subscription) => {
    await loadSafeAreaInsets();
    await refreshSafeAreaInsets(subscription);
    state.ready = true;
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
      state.isLeftAndNotIsRight = insetRight >= insetLeft;
    }
  }

  async function refreshSafeAreaInsets(subscription: Subscription) {
    if (Capacitor.getPlatform() === "web") {
      return;
    }

    subscription.add(of(null).pipe(
      exhaustMapWithTrailing(() => from((async () => {
        if (Capacitor.getPlatform() === "android") {
          const insetLeftPromise = AndroidNotch.getInsetLeft();
          const insetRightPromise = AndroidNotch.getInsetRight();
          const insetLeft = await insetLeftPromise;
          const insetRight = await insetRightPromise;
          const isLeftAndNotIsRight = insetRight >= insetLeft;
          return isLeftAndNotIsRight;
        }
        return false;
      })())),
      distinctUntilChanged(),
      tap((isLeftAndNotIsRight) => {
        state.isLeftAndNotIsRight = isLeftAndNotIsRight;
      }),
      delay(100),
      repeat(),
    ).subscribe());
  }

  return <>
    <div className={css.container}>
      {state.ready && <Fab
        size="small"
        color="primary"
        aria-label="add"
        style={state.isLeftAndNotIsRight ? { left: `${state.leftOrRight}px`, position: "absolute" } : { right: `${state.leftOrRight}px`, position: "absolute" }}
        className={css.exitButton}
        onClick={() => {
          state.exitDialog.open = true
        }}
      >
        <FontAwesomeIcon icon={faGear} size="xl" />
      </Fab>}
    </div>
    {state.exitDialog.open && <ExitDialog exit={state.exit} closeDialog={() => { state.exitDialog.open = false }} />}
  </>
})