import path from "path";

const getBuildFoldePath = ((): string => {
    return path.join(__dirname, "../../../app.asar.unpacked/build");
})();

export { getBuildFoldePath };