import { app, BrowserWindow, screen } from "electron";
import * as remote from "@electron/remote/main";
import { isPackaged } from '@/util/IsPackagedUtil';
import { loadWindowFromRelativeUrl } from '@/util/LoadWindowFromRelativeUrlUtil';
import { isNotShowForTest } from '@/util/IsNotShowForTestUtil';

export default async () => {
  const display = screen.getDisplayNearestPoint(screen.getCursorScreenPoint());
  const defaultWidth = 400;
  const defaultHeight = 0;
  const width =
    display.workArea.width > defaultWidth
      ? defaultWidth
      : display.workArea.width;
  const height =
    display.workArea.height > defaultHeight
      ? defaultHeight
      : display.workArea.height;
  const win = new BrowserWindow({
    show: !isNotShowForTest,
    width: Math.floor(width),
    height: Math.floor(height),
    x: Math.floor(display.workArea.x + (display.workArea.width - width) / 2),
    y: Math.floor(display.workArea.y + (display.workArea.height - height) / 2),
    title: "React App (Loading... Please wait)",
    webPreferences: {
      contextIsolation: false,
      nodeIntegration: true,
    },
  });
  win.on("close", () => {
    app.exit();
  });
  win.setMenuBarVisibility(false);

  remote.enable(win.webContents);

  if (isPackaged) {
    win.removeMenu();
  }

  if (!isNotShowForTest) {
    win.show();
  }
  win.setAlwaysOnTop(true, "status");
  win.focus();
  if (!isNotShowForTest) {
    win.moveTop();
  }
  win.setAlwaysOnTop(false);

  await loadWindowFromRelativeUrl(win, "/");
}