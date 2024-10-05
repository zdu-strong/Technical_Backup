import path from "path";

export const buildFolderPath = ((): string => {
  return path.join(__dirname, "../../../app.asar.unpacked/build");
})();
