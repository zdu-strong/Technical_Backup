import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class SystemRoleModel {
  
  @jsonMember(String)
  id!: string;

  @jsonMember(Date)
  createDate!: Date;

  @jsonMember(Date)
  updateDate!: Date;

  @jsonMember(String)
  name!: string;

  constructor() {
    makeAutoObservable(this);
  }
}
