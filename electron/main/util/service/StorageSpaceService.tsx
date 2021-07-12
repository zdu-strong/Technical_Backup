import PaginationModel from '../../model/PaginationMode';
import { databaseStorageFolderName, getElectronStore } from './electron-store-instance/ElectronStorageInstance';
import { getFolderNameBaseOnBaseFolderPath, deleteFolderOrFile } from '../StorageUtil';
import linq from 'linq';
import { v4 } from 'uuid';
import StorageSpaceModelType from '../../model/StorageSpaceModel';
import { subMilliseconds } from 'date-fns'

class StorageSpaceService {
    async getStorageSpaceListByPagination(pageNum: number, pageSize: number) {
        const electronStore = await getElectronStore();
        const stream = linq.from(electronStore.get("StorageSpaceList")).orderBy(s => s.createDate);
        return new PaginationModel<StorageSpaceModelType>(pageNum, pageSize, stream);
    }

    private async createStorageSpaceEntityIfNotExist(folderName: string) {
        /* Do not add if it already exists */
        const electronStore = await getElectronStore();
        const StorageSpaceList = electronStore.get("StorageSpaceList");
        if (linq.from(StorageSpaceList).where(s => s.folderName === folderName).any()) {
            return;
        }

        /* Add a row of data */
        StorageSpaceList.push({
            id: v4(),
            folderName: folderName,
            createDate: new Date(),
            updateDate: new Date(),
        });
        electronStore.set("StorageSpaceList", StorageSpaceList);
    }

    async isUsed(folderName: string) {
        /* Save data to database */
        const electronStore = await getElectronStore();
        const folderNameOfRelative = await getFolderNameBaseOnBaseFolderPath(folderName);
        await this.createStorageSpaceEntityIfNotExist(folderNameOfRelative);

        /* Has been used */
        if (folderNameOfRelative === databaseStorageFolderName) {
            return true;
        }

        /* Check if it is used */
        const tempFileValidTime = 24 * 60 * 60 * 1000;
        const expiredDate = subMilliseconds(new Date(), 0 - tempFileValidTime);
        const list = linq.from(electronStore.get("StorageSpaceList")).where(s => s.folderName === folderNameOfRelative).toArray();
        const isUsed = !linq.from(list).where(s => s.updateDate.getTime() < expiredDate.getTime()).where(() => linq.from(list).all(m => m.updateDate.getTime() < expiredDate.getTime())).any();
        return isUsed;
    }

    async deleteFolder(folderName: string): Promise<void> {
        /* Do not delete when in use */
        const electronStore = await getElectronStore();
        const folderNameOfRelative = await getFolderNameBaseOnBaseFolderPath(folderName);
        if (await this.isUsed(folderNameOfRelative)) {
            return;
        }

        /* Delete from database */
        const StorageSpaceList = electronStore.get("StorageSpaceList");
        const storageSpaceEntityList = linq.from(StorageSpaceList).where(s => s.folderName === folderName).toArray();
        for (const storageSpaceEntity of storageSpaceEntityList) {
            StorageSpaceList.splice(StorageSpaceList.indexOf(storageSpaceEntity), 1);
        }

        /* Delete from disk */
        electronStore.set("StorageSpaceList", StorageSpaceList);
        await deleteFolderOrFile(folderNameOfRelative);
    }
}

export default new StorageSpaceService();