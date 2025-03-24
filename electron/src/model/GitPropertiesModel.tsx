import { makeAutoObservable } from "mobx-react-use-autorun";
import { Type } from 'class-transformer';

export class GitPropertiesModel {

  @Type(() => String)
  commitId!: string;

  @Type(() => Date)
  commitDate!: Date;

  constructor() {
    makeAutoObservable(this);
  }
}

