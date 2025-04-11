import { StorageSpaceService } from "@/service";
import { concatMap, lastValueFrom, retry, repeat, timer, of, from } from "rxjs";
import remote from "@/remote";

async function runManageStorageSpace() {
  const { totalPages } = await StorageSpaceService.getStorageSpaceListByPagination(1, 1);
  for (let i = totalPages; i > 0; i--) {
    try {
      const result = await StorageSpaceService.getStorageSpaceListByPagination(i, 1);
      for (const storageSpaceModel of result.items) {
        if (!(await StorageSpaceService.isUsed(storageSpaceModel.folderName))) {
          await StorageSpaceService.deleteFolder(storageSpaceModel.folderName);
        }
      }
    } catch (error) {
      // do nothing
    }
  }

  const folderNameListOfRootFolder = await remote.ElectronStorage.listRoots();
  for (const folderName of folderNameListOfRootFolder) {
    try {
      if (!(await StorageSpaceService.isUsed(folderName))) {
        await StorageSpaceService.deleteFolder(folderName);
      }
    } catch (error) {
      // do nothing
    }
  }
}

async function main() {
  await lastValueFrom(timer(60 * 1000));
  await lastValueFrom(
    of(null).pipe(
      concatMap(() => {
        return from(runManageStorageSpace());
      }),
      repeat({ delay: 10 * 60 * 1000 }),
      retry({ delay: 10 * 60 * 1000 }),
    )
  );
}

export default main()
