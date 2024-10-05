import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserEmailModel } from '@/model/UserEmailModel';
import { makeAutoObservable } from 'mobx-react-use-autorun'

@jsonObject
export class UserModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(String)
  username!: string;

  @jsonMember(String)
  password!: string;

  @jsonArrayMember(UserEmailModel)
  userEmailList!: UserEmailModel[];

  @jsonMember(String)
  accessToken!: string;

  @jsonMember(Boolean)
  menuOpen!: boolean;

  constructor() {
    makeAutoObservable(this);
  }
}