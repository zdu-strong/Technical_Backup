import { StorageSpaceService } from "@/service";
import { concatMap, lastValueFrom, retry, repeat, timer, of, from } from "rxjs";
import remote from "@/remote";

async function runManageStorageSpace() {
  let totalPages = 1;
  let pageNum = 1;
  while (pageNum <= totalPages) {
    try {
      const result = await StorageSpaceService.getStorageSpaceListByPagination(pageNum, 1);
      totalPages = result.totalPages;
      pageNum++;
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
