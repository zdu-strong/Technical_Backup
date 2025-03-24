import { makeAutoObservable } from 'mobx-react-use-autorun';
import { UserModel } from '@/model/UserModel';
import { Type } from 'class-transformer';

export class FriendshipModel {

  @Type(() => String)
  id!: string;

  @Type(() => Boolean)
  isFriend!: boolean;

  @Type(() => Boolean)
  isInBlacklist!: boolean;

  @Type(() => Boolean)
  isFriendOfFriend!: boolean;

  @Type(() => Boolean)
  isInBlacklistOfFriend!: boolean;

  @Type(() => Boolean)
  hasInitiative!: boolean;

  @Type(() => Date)
  createDate!: Date;

  @Type(() => Date)
  updateDate!: Date;

  @Type(() => UserModel)
  user!: UserModel;

  @Type(() => UserModel)
  friend!: UserModel;

  constructor() {
    makeAutoObservable(this);
  }

}