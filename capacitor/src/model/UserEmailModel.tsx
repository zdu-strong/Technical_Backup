import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonMember, jsonObject } from 'typedjson'
import { VerificationCodeEmailModel } from '@/model/VerificationCodeEmailModel';

@jsonObject
export class UserEmailModel {

  @jsonMember(String)
  id?: string;

  @jsonMember(String)
  email!: string;

  @jsonMember(VerificationCodeEmailModel)
  verificationCodeEmail!: VerificationCodeEmailModel;

  constructor() {
    makeAutoObservable(this);
  }
}