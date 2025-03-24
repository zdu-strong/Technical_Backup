import { makeAutoObservable } from 'mobx-react-use-autorun';
import { VerificationCodeEmailModel } from '@/model/VerificationCodeEmailModel';
import { Type } from 'class-transformer';

export class UserEmailModel {

  @Type(() => String)
  id?: string;

  @Type(() => String)
  email!: string;

  @Type(() => VerificationCodeEmailModel)
  verificationCodeEmail!: VerificationCodeEmailModel;

  constructor() {
    makeAutoObservable(this);
  }
}