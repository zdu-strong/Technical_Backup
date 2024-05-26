import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { FriendshipModel } from '@/model/FriendshipModel';

@jsonObject
export class FriendshipPaginationModel {

  @jsonMember(Number)
  pageNum!: number;

  @jsonMember(Number)
  pageSize!: number;

  @jsonMember(Number)
  totalRecord!: number;

  @jsonMember(Number)
  totalPage!: number;

  @jsonArrayMember(FriendshipModel)
  list!: FriendshipModel[];

  constructor() {
    makeAutoObservable(this);
  }
}