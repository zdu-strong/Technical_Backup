import { observable } from 'mobx-react-use-autorun';
import { useMount } from "mobx-react-use-autorun";
import { timer } from 'rxjs';
import remote from '@/remote';

const GlobalState = observable({
  ready: false,
})

export function useReadyForApplication() {

  useMount(async () => {
    if (!GlobalState.ready) {
      const isLoaded = await remote.getIsLoaded();
      if (!isLoaded) {
        const display = remote.screen.getDisplayNearestPoint(remote.screen.getCursorScreenPoint());
        const defaultWidth = display.workArea.width / 2;
        const defaultHeight = display.workArea.height / 2;
        const width = display.workArea.width > defaultWidth ? defaultWidth : display.workArea.width;
        const height = display.workArea.height > defaultHeight ? defaultHeight : display.workArea.height;
        remote.getCurrentWindow().setBounds({
          width: Math.floor(width),
          height: Math.floor(height),
          x: Math.floor(display.workArea.x + ((display.workArea.width - width) / 2)),
          y: Math.floor(display.workArea.y + ((display.workArea.height - height) / 2)),
        });
        if (!remote.isNotShowForTest) {
          remote.getCurrentWindow().maximize();
          remote.getCurrentWindow().show();
          remote.getCurrentWindow().setAlwaysOnTop(true, "status");
          remote.getCurrentWindow().focus();
          remote.getCurrentWindow().moveTop();
        }
        remote.getCurrentWindow().setAlwaysOnTop(false);
        remote.getCurrentWindow().setMenuBarVisibility(true);
        remote.getCurrentWindow().setTitle("React App");
        for (let i = 10; i > 0; i--) {
          await timer(1).toPromise();
        }
        for (let i = 10; i > 0; i--) {
          await timer(1).toPromise();
        }
        await remote.setIsLoadedToTrue();
      }
    }
    GlobalState.ready = true
  })

  return GlobalState.ready;
}