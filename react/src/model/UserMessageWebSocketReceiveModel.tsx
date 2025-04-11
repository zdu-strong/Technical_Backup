import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserMessageModel } from '@/model/UserMessageModel';

@jsonObject
export class UserMessageWebSocketReceiveModel {

  @jsonMember(Number)
  totalRecords!: number;

  @jsonArrayMember(UserMessageModel)
  items!: UserMessageModel[];

  constructor() {
    makeAutoObservable(this);
  }
}