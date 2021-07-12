import { BrowserWindow } from "electron";
import path from "path";
import { getBuildFoldePath } from "./GetBuildFolderPathUtil";
import { isPackaged } from './IsPackagedUtil';

const loadWindowFromRelativeUrl = async (browserWindow: BrowserWindow, url: string) => {
    if (!url.startsWith("/")) {
        throw new Error("Unsupport url");
    }
    if (isPackaged) {
        await browserWindow.loadFile(path.join(getBuildFoldePath, "index.html"), {
            hash: new URL("http://localhost" + url).pathname,
            search: new URL("http://localhost" + url).search,
        });
    } else {
        await browserWindow.loadURL(new URL(url, `http://localhost:${process.env.PORT}`).toString());
    }
};

export { loadWindowFromRelativeUrl };