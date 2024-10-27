import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserModel } from '@/model/UserModel';

@jsonObject
export class UserPaginationModel {

  @jsonMember(Number)
  pageNum!: number;

  @jsonMember(Number)
  pageSize!: number;

  @jsonMember(Number)
  totalRecord!: number;

  @jsonMember(Number)
  totalPage!: number;

  @jsonArrayMember(UserModel)
  list!: UserModel[];

  constructor() {
    makeAutoObservable(this);
  }
}