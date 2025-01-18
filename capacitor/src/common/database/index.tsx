import { StorageSpaceModel } from '@/model/StorageSpaceModel';
import Dexie, { Table } from 'dexie'

export class Database extends Dexie {
  public StorageSpaceList!: Table<StorageSpaceModel, string>;

  public constructor() {
    super("9d60d290-0a4a-11ee-ba0c-67bf365e9d72");
    this.version(1).stores({
      StorageSpaceList: "id, folderName, createDate, updateDate"
    });
  }
}