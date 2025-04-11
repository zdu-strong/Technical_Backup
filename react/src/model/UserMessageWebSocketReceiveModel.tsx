import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserMessageModel } from '@/model/UserMessageModel';

@jsonObject
export class UserMessageWebSocketReceiveModel {

  @jsonMember(Number)
  totalPages!: number;

  @jsonArrayMember(UserMessageModel)
  items!: UserMessageModel[];

  constructor() {
    makeAutoObservable(this);
  }
}