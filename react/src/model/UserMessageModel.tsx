import { UserModel } from "@/model/UserModel"
import { makeAutoObservable } from "mobx-react-use-autorun";
import { Type } from 'class-transformer';

export class UserMessageModel {

  @Type(() => String)
  id!: string;

  @Type(() => Date)
  createDate!: Date;

  @Type(() => Date)
  updateDate!: Date;

  @Type(() => String)
  content!: string;

  @Type(() => String)
  url!: string;

  @Type(() => Number)
  pageNum!: number;

  @Type(() => UserModel)
  user!: UserModel;

  constructor() {
    makeAutoObservable(this);
  }
}

