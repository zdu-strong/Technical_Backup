import Dexie, { Table } from 'dexie'
import { StorageSpaceModel } from '@/model/StorageSpaceModel'

export class Database extends Dexie {
  public StorageSpaceList!: Table<StorageSpaceModel, string>; // id is number in this case

  public constructor() {
    super("4fd81bc0-08e5-11ee-9bce-958dba59edea");
    this.version(1).stores({
      StorageSpaceList: "id, folderName, createDate, updateDate"
    });
  }
}