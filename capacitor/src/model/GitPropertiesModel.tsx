import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class GitPropertiesModel {

  @jsonMember(String)
  commitId!: string;

  @jsonMember(Date)
  commitDate!: Date;

  constructor() {
    makeAutoObservable(this);
  }
}

