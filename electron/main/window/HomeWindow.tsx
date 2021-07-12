import { app, BrowserWindow, screen } from "electron";
import { isPackaged } from "../util/IsPackagedUtil";
import { loadWindowFromRelativeUrl } from "../util/LoadWindowFromRelativeUrlUtil";
import * as remote from '@electron/remote/main';

const createHomeWindow = async () => {
    const display = screen.getDisplayNearestPoint(screen.getCursorScreenPoint());
    const defaultWidth = 400;
    const defaultHeight = 0;
    const width = display.workArea.width > defaultWidth ? defaultWidth : display.workArea.width;
    const height = display.workArea.height > defaultHeight ? defaultHeight : display.workArea.height;
    const win = new BrowserWindow({
        show: true,
        width: width,
        height: height,
        x: display.workArea.x + ((display.workArea.width - width) / 2),
        y: display.workArea.y + ((display.workArea.height - height) / 2),
        title: "React App (加载中..., 请稍候)",
        webPreferences: {
            contextIsolation: false,
            nodeIntegration: true,
        }
    });
    win.on("close", () => {
        app.exit();
    });
    win.setMenuBarVisibility(false);

    remote.enable(win.webContents);

    if (isPackaged) {
        win.removeMenu();
    }

    win.show();
    win.setAlwaysOnTop(true, "status");
    win.focus();
    win.moveTop();
    win.setAlwaysOnTop(false);

    await loadWindowFromRelativeUrl(win, "/");

};

export default createHomeWindow;