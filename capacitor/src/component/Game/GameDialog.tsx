import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from './Game';
import { useMount } from "mobx-react-use-autorun";
import { LANDSCAPE, PORTRAIT_PRIMARY } from '@/common/ScreenOrentation';
import ExitButton from '@/component/Game/ExitButton';
import { Subscription, tap, timer } from 'rxjs';

export default observer((props: {
  closeDialog: () => void
}) => {
  const state = useMobxState({
    ready: false,
  }, {
    ...props,
  })

  useMount(async (subscription) => {
    subscription.add(timer(0).pipe(
      tap(() => {
        LANDSCAPE();
        state.ready = true;
      })
    ).subscribe());

    subscription.add(new Subscription(() => {
      PORTRAIT_PRIMARY()
    }));
  })

  return <>
    <Dialog
      fullScreen
      open={true}
      onClose={state.closeDialog}
      disableRestoreFocus={true}
    >
      {state.ready && <ExitButton exit={state.closeDialog} />}
      <Game />
    </Dialog>
  </>
})