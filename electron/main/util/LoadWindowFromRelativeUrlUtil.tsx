import { BrowserWindow } from "electron";
import path from "path";
import { buildFolderPath } from "@/util/GetBuildFolderPathUtil";
import { isPackaged } from "@/util/IsPackagedUtil";

export async function loadWindowFromRelativeUrl(
  browserWindow: BrowserWindow,
  url: string
) {
  await Promise.resolve(null);
  if (!url.startsWith("/")) {
    throw new Error("Unsupport url");
  }
  if (isPackaged) {
    await browserWindow.loadFile(path.join(buildFolderPath, "index.html"), {
      hash: new URL(`http://127.0.0.1${url}`).pathname,
      search: new URL(`http://127.0.0.1${url}`).search,
    });
  } else {
    await browserWindow.loadURL(
      new URL(url, `http://127.0.0.1:${process.env.ELECTRON_PORT}`).toString()
    );
  }
}
