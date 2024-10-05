import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonMember, jsonObject } from 'typedjson'
import { UserModel } from '@/model/UserModel';

@jsonObject
export class FriendshipModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(Boolean)
  isFriend!: boolean;

  @jsonMember(Boolean)
  isInBlacklist!: boolean;

  @jsonMember(Boolean)
  isFriendOfFriend!: boolean;

  @jsonMember(Boolean)
  isInBlacklistOfFriend!: boolean;

  @jsonMember(Boolean)
  hasInitiative!: boolean;

  @jsonMember(Date)
  createDate!: Date;

  @jsonMember(Date)
  updateDate!: Date;

  @jsonMember(UserModel)
  user!: UserModel;

  @jsonMember(UserModel)
  friend!: UserModel;

  constructor() {
    makeAutoObservable(this);
  }

}