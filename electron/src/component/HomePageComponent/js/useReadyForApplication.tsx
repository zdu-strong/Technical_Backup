import { observable, timeout } from 'mobx-react-use-autorun';
import { useMount } from 'react-use';
import { IsLoadedUtil, remote, StorageManageRunUtil } from '../../../remote';

const state = observable({
    ready: false,
})

export const useReadyForApplication = () => {

    useMount(async () => {
        if (!state.ready) {
            const isLoaded = await IsLoadedUtil.getIsLoaded();
            if (!isLoaded) {
                const display = remote.screen.getDisplayNearestPoint(remote.screen.getCursorScreenPoint());
                const defaultWidth = display.workArea.width / 2;
                const defaultHeight = display.workArea.height / 2;
                const width = display.workArea.width > defaultWidth ? defaultWidth : display.workArea.width;
                const height = display.workArea.height > defaultHeight ? defaultHeight : display.workArea.height;
                remote.getCurrentWindow().setBounds({
                    width: width,
                    height: height,
                    x: display.workArea.x + ((display.workArea.width - width) / 2),
                    y: display.workArea.y + ((display.workArea.height - height) / 2),
                });
                remote.getCurrentWindow().maximize();
                remote.getCurrentWindow().show();
                remote.getCurrentWindow().setAlwaysOnTop(true, "status");
                remote.getCurrentWindow().focus();
                remote.getCurrentWindow().moveTop();
                remote.getCurrentWindow().setAlwaysOnTop(false);
                remote.getCurrentWindow().setMenuBarVisibility(true);
                remote.getCurrentWindow().setTitle("React App");
                for (let i = 10; i > 0; i--) {
                    await timeout(1);
                }
                StorageManageRunUtil.default();
                for (let i = 10; i > 0; i--) {
                    await timeout(1);
                }
                await IsLoadedUtil.setIsLoadedToTrue();
            }
        }
        state.ready = true
    })

    return state.ready;
}