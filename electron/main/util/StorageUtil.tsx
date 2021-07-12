import path from 'path';
import { timer, lastValueFrom } from 'rxjs';
import fs from 'fs';
import { v4 } from 'uuid';
import linq from 'linq';

const getBaseFolderPathOfInit = async () => {
    await lastValueFrom(timer(0));
    const folderPath: string = (() => {
        try {
            const { app } = require("electron");
            return path.join(app.getPath("userData"), "storage");
        } catch {
            const folderPathEnv: any = process.env["ELECTRON_STORAGE_BASE_FOLDER_PATH"];
            if (folderPathEnv) {
                return folderPathEnv;
            } else {
                throw new Error("Does not support getting the root path of the storage");
            }
        }
    })();
    await fs.promises.mkdir(folderPath, { recursive: true });
    return folderPath;
};

const promiseOfGetBaseFolderPath = getBaseFolderPathOfInit();

const getBaseFolderPath = async () => {
    return promiseOfGetBaseFolderPath;
};

const createTempFolder = async () => {
    const tempFolderPath = path.join(await promiseOfGetBaseFolderPath, v4());
    await fs.promises.mkdir(tempFolderPath, { recursive: true });
    return tempFolderPath;
};

const deleteFolderOrFile = async (folderName: string) => {
    const relativePath = await getFolderNameBaseOnBaseFolderPath(folderName);
    await fs.promises.rm(path.join(await promiseOfGetBaseFolderPath, relativePath), { recursive: true, force: true });
};

const getFolderNameBaseOnBaseFolderPath = async (filePath: string) => {
    const filePathOfAbsolute = await (async () => {
        if (path.isAbsolute(filePath)) {
            return filePath;
        } else {
            return path.join(await promiseOfGetBaseFolderPath, filePath);
        }
    })();
    const filePathTwo = path.normalize(filePathOfAbsolute);
    const relativePath = path.relative(await promiseOfGetBaseFolderPath, filePathTwo);
    if (!(relativePath && !relativePath.startsWith("./") && !relativePath.startsWith("../"))) {
        throw new Error("Unsupported Path!");
    }
    return linq.from(relativePath.split("/")).selectMany(s => s.split("\\")).take(1).single();
};

const listRoots = async () => {
    const baseFolderPath = await promiseOfGetBaseFolderPath;
    const folderNameList = await fs.promises.readdir(baseFolderPath);
    return folderNameList;
};

export { createTempFolder, deleteFolderOrFile, listRoots, getBaseFolderPath, getFolderNameBaseOnBaseFolderPath };