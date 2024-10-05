import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { SystemRoleModel } from '@/model/SystemRoleModel';

@jsonObject
export class SystemRolePaginationModel {

  @jsonMember(Number)
  pageNum!: number;

  @jsonMember(Number)
  pageSize!: number;

  @jsonMember(Number)
  totalRecord!: number;

  @jsonMember(Number)
  totalPage!: number;

  @jsonArrayMember(SystemRoleModel)
  list!: SystemRoleModel[];

  constructor() {
    makeAutoObservable(this);
  }
}