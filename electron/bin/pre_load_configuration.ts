import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

const run = async () => {
    await deletePackageLockFile();
    await deleteBuildFolder();
    await deleteDistFolder();
    await deleteOutputFolder();
    await installNodeGyp();
    await installDependencies();
    await compileCode();
    await cancelCopyExeForCache();
};

const cancelCopyExeForCache = async () => {
    execSync(
        [
            "cross-env",
            "ts-node",
            "bin/cancel_copy_exe_for_cache.ts"
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
};

const installDependencies = async () => {
    execSync(
        [
            "cross-env",
            "SASS_BINARY_SITE=https://npmmirror.com/mirrors/node-sass",
            "npm_config_canvas_binary_host_mirror=https://npmmirror.com/mirrors/node-canvas-prebuilt",
            "npm_config_package_lock=false",
            "ELECTRON_BUILDER_BINARIES_MIRROR=https://npmmirror.com/mirrors/electron-builder-binaries/",
            "ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/",
            "npm install",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
};

const compileCode = async () => {
    execSync(
        [
            "cd main",
            "&&",
            "tsc"
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
    execSync(
        [
            "eslint",
            "./main",
            "--ext .tsx",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
};

const installNodeGyp = async () => {
    execSync(
        [
            "npm install",
            "-g",
            "node-gyp@8.1.0",
        ].join(" "),

        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
};

const deletePackageLockFile = async () => {
    const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
    await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
};

const deleteBuildFolder = async () => {
    const folderPathOfBuild = path.join(__dirname, "..", "build");
    await fs.promises.rm(folderPathOfBuild, { recursive: true, force: true });
};

const deleteOutputFolder = async () => {
    const folderPathOfOutput = path.join(__dirname, "..", "output");
    await fs.promises.rm(folderPathOfOutput, { recursive: true, force: true });
};

const deleteDistFolder = async () => {
    const folderPathOfDist = path.join(__dirname, "..", "dist");
    await fs.promises.rm(folderPathOfDist, { recursive: true, force: true });
};

export default run();