import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

const run = async () => {
    await deletePackageLockFile();
    await deleteBuildFolder();
    await deleteAndroidFolder();
    await deleteIOSFolder();
    execSync(
        [
            "cross-env",
            "SASS_BINARY_SITE=https://npmmirror.com/mirrors/node-sass",
            "npm install",
            "--package-lock=false",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
};

const deleteAndroidFolder = async () => {
    const folderPath = path.join(__dirname, "..", "android");
    await fs.promises.rm(folderPath, { recursive: true, force: true });
};

const deleteIOSFolder = async () => {
    const folderPath = path.join(__dirname, "..", "ios");
    await fs.promises.rm(folderPath, { recursive: true, force: true });
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