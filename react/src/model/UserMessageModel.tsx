import { UserModel } from "@/model/UserModel"
import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class UserMessageModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(Date)
  createDate!: Date;

  @jsonMember(Date)
  updateDate!: Date;

  @jsonMember(String)
  content!: string;

  @jsonMember(String)
  url!: string;

  @jsonMember(Number)
  pageNum!: number;

  @jsonMember(UserModel)
  user!: UserModel;

  constructor() {
    makeAutoObservable(this);
  }
}

