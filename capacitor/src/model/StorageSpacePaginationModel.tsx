import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { StorageSpaceModel } from '@/model/StorageSpaceModel';

@jsonObject
export class StorageSpacePaginationModel {

  @jsonMember(Number)
  pageNum!: number;

  @jsonMember(Number)
  pageSize!: number;

  @jsonMember(Number)
  totalRecord!: number;

  @jsonMember(Number)
  totalPage!: number;

  @jsonArrayMember(StorageSpaceModel)
  list!: StorageSpaceModel[];

  constructor() {
    makeAutoObservable(this);
  }
}