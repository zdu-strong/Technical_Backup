import path from "path";
import { isPackaged } from "@/util/IsPackagedUtil";

export const publicFolderPath = (() => {
  if (isPackaged) {
    return path.join(__dirname, "../../../app.asar.unpacked/public");
  } else {
    return path.join(__dirname, "public");
  }
})();
