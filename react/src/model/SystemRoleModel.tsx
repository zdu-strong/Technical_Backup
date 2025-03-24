import { makeAutoObservable } from "mobx-react-use-autorun";
import { Type } from 'class-transformer';

export class SystemRoleModel {
  
  @Type(() => String)
  id!: string;

  @Type(() => Date)
  createDate!: Date;

  @Type(() => Date)
  updateDate!: Date;

  @Type(() => String)
  name!: string;

  constructor() {
    makeAutoObservable(this);
  }
}
