import { makeAutoObservable } from "mobx-react-use-autorun";
import { Type } from 'class-transformer';

export class StorageSpaceModel {

  @Type(() => String)
  id!: string;

  @Type(() => String)
  folderName!: string;

  @Type(() => Date)
  createDate!: Date;

  @Type(() => Date)
  updateDate!: Date;

  constructor() {
    makeAutoObservable(this);
  }
}
