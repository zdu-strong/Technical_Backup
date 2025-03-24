import { jsonMember, jsonObject } from 'typedjson'
import { makeAutoObservable } from 'mobx-react-use-autorun'

@jsonObject
export class OrganizeModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(String)
  name!: string;

  @jsonMember(Number)
  level!: number;

  @jsonMember(Date)
  createDate!: Date;

  @jsonMember(Date)
  updateDate!: Date;

  @jsonMember(Number)
  childCount!: number;

  @jsonMember(Number)
  descendantCount!: number;

  parent!: OrganizeModel;

  childList!: OrganizeModel[];

  constructor() {
    makeAutoObservable(this);
  }
}