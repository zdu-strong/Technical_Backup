import path from "path";
import { timer, lastValueFrom } from "rxjs";
import fs from "fs";
import { v1 } from "uuid";
import linq from "linq";

async function getBaseFolderPathOfInit() {
  await lastValueFrom(timer(0));
  const folderPath: string = (() => {
    try {
      const { app } = require("electron");
      return path.join(app.getPath("userData"), "storage");
    } catch {
      const folderPathEnv: any =
        process.env["ELECTRON_STORAGE_BASE_FOLDER_PATH"];
      if (folderPathEnv) {
        return folderPathEnv;
      } else {
        throw new Error(
          "Does not support getting the root path of the storage"
        );
      }
    }
  })();
  await fs.promises.mkdir(folderPath, { recursive: true });
  return folderPath;
}

const promiseOfGetBaseFolderPath = getBaseFolderPathOfInit();

export async function getBaseFolderPath() {
  return promiseOfGetBaseFolderPath;
}

export async function createTempFolder() {
  const tempFolderPath = path.join(await promiseOfGetBaseFolderPath, v1());
  await fs.promises.mkdir(tempFolderPath, { recursive: true });
  return tempFolderPath;
}

export async function deleteFolderOrFile(folderName: string) {
  const relativePath = await getFolderNameBaseOnBaseFolderPath(folderName);
  await fs.promises.rm(
    path.join(await promiseOfGetBaseFolderPath, relativePath),
    { recursive: true, force: true }
  );
}

export async function getFolderNameBaseOnBaseFolderPath(filePath: string) {
  const filePathOfAbsolute = await (async () => {
    if (path.isAbsolute(filePath)) {
      return filePath;
    } else {
      return path.join(await promiseOfGetBaseFolderPath, filePath);
    }
  })();
  const filePathTwo = path.normalize(filePathOfAbsolute);
  const relativePath = path.relative(
    await promiseOfGetBaseFolderPath,
    filePathTwo
  );
  if (
    !(
      relativePath &&
      !relativePath.startsWith("./") &&
      !relativePath.startsWith("../")
    )
  ) {
    throw new Error("Unsupported Path!");
  }
  return linq
    .from(relativePath.split("/"))
    .selectMany((s) => s.split("\\"))
    .take(1)
    .single();
}

export async function listRoots() {
  const baseFolderPath = await promiseOfGetBaseFolderPath;
  const folderNameList = await fs.promises.readdir(baseFolderPath);
  return folderNameList;
}
