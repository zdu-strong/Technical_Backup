import { UserEmailModel } from '@/model/UserEmailModel';
import { makeAutoObservable } from 'mobx-react-use-autorun'
import { Type } from 'class-transformer';

export class UserModel {

  @Type(() => String)
  id!: string;

  @Type(() => String)
  username!: string;

  @Type(() => String)
  password!: string;

  @Type(() => UserEmailModel)
  userEmailList!: UserEmailModel[];

  @Type(() => String)
  accessToken!: string;

  @Type(() => Boolean)
  menuOpen!: boolean;

  @Type(() => Date)
  createDate!: Date;

  @Type(() => Date)
  updateDate!: Date;

  constructor() {
    makeAutoObservable(this);
  }
}