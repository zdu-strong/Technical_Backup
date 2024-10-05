import {
  app,
  BrowserWindow,
  protocol,
  ProtocolRequest,
  ProtocolResponse,
} from "electron";
import { createHomeWindow } from "@/window";
import { initialize } from "@electron/remote/main";
import linq from "linq";

async function main() {
  if (!app.requestSingleInstanceLock()) {
    app.exit();
    return;
  }

  app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
      app.exit();
    }
  });

  initialize();

  await app.whenReady();

  if (!protocol.isProtocolRegistered("file")) {
    protocol.registerFileProtocol(
      "file",
      (
        request: ProtocolRequest,
        callback: (response: string | ProtocolResponse) => void
      ) => {
        const filePath = decodeURI(new URL(request.url).pathname);
        callback(filePath);
      }
    );
  }

  await createHomeWindow();

  app.on("activate", async () => {
    if (linq.from(BrowserWindow.getAllWindows()).any()) {
      const mainWindow = linq.from(BrowserWindow.getAllWindows()).last();
      if (mainWindow.isMinimized()) {
        mainWindow.restore();
      }
      mainWindow.focus();
    } else {
      await createHomeWindow();
    }
  });

  app.on("second-instance", async () => {
    // When running the second instance, it will focus on the window mainWindow
    if (linq.from(BrowserWindow.getAllWindows()).any()) {
      const mainWindow = linq.from(BrowserWindow.getAllWindows()).last();
      if (mainWindow.isMinimized()) {
        mainWindow.restore();
      }
      mainWindow.focus();
    }
  });
}

export default main()
