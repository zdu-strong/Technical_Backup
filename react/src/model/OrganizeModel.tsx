import { jsonArrayMember, jsonMember, jsonObject, TypedJSON } from 'typedjson'
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

  @jsonMember(() => Object, { deserializer: (value: any) => new TypedJSON(OrganizeModel).parse(value) })
  parent!: OrganizeModel;

  @jsonArrayMember(() => Object, { deserializer: (value: any) => new TypedJSON(OrganizeModel).parse(value) })
  childList!: OrganizeModel[];

  constructor() {
    makeAutoObservable(this);
  }

}
