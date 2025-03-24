import { makeAutoObservable } from 'mobx-react-use-autorun';
import { UserMessageModel } from '@/model/UserMessageModel';
import { Type } from 'class-transformer';

export class UserMessageWebSocketReceiveModel {

  @Type(() => Number)
  totalPage!: number;

  @Type(() => UserMessageModel)
  list!: UserMessageModel[];

  constructor() {
    makeAutoObservable(this);
  }
}