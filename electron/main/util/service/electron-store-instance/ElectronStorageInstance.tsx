import Storage from 'electron-store';
import { jsonDateParser } from 'json-date-parser';
import path from 'path';
import StorageSpaceModelType from '../../../model/StorageSpaceModel';
import { getBaseFolderPath } from '../../StorageUtil';

const databaseStorageFolderName = "ed069481-6ae2-8360-61e5-66f528c13020";

const getDatabaseStorage = async () => {
    const databaseStorageFolder = path.join(await getBaseFolderPath(), databaseStorageFolderName);
    const databaseStorage = new Storage<{
        StorageSpaceList: StorageSpaceModelType[]
    }>({
        cwd: databaseStorageFolder,
        defaults: {
            StorageSpaceList: [],
        },
        deserialize: (data) => {
            return JSON.parse(data, jsonDateParser);
        }
    });
    return databaseStorage;
};

export const getElectronStore = getDatabaseStorage;

export { databaseStorageFolderName };
