import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

const run = async () => {
    await deletePackageLockFile();
    await deleteBuildFolder();
    const cypressInstallUrl = (() => {
        const packageJSON = JSON.parse(fs.readFileSync(path.join(__dirname, "..", "package.json")).toString());
        const cypressVersion = packageJSON.devDependencies["cypress"];
        const cypressMirror = "https://npmmirror.com/mirrors/cypress/" + cypressVersion;
        return cypressMirror + "/" + process.platform + "-" + process.arch + "/cypress.zip";
    })();
    execSync(
        [
            "cross-env",
            "CYPRESS_INSTALL_BINARY=" + cypressInstallUrl,
            "SASS_BINARY_SITE=https://npmmirror.com/mirrors/node-sass",
            "npm_config_canvas_binary_host_mirror=https://npmmirror.com/mirrors/node-canvas-prebuilt",
            "npm_config_package_lock=false",
            "npm install",
        ].join(" "),
        {
            stdio: "inherit",
        }
    );
};

const deletePackageLockFile = async () => {
    const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
    await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
};

const deleteBuildFolder = async () => {
    const folderPath = path.join(__dirname, "..", "build");
    await fs.promises.rm(folderPath, { recursive: true, force: true });
};

export default run();