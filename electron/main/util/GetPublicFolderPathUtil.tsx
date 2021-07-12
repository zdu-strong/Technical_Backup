import path from "path";
import { isPackaged } from './IsPackagedUtil';

const getPublicFoldePath = (() => {
    if (isPackaged) {
        return path.join(__dirname, "../../../app.asar.unpacked/public");
    } else {
        return path.join(__dirname, "public");
    }
})();

export { getPublicFoldePath };