import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonMember, jsonObject } from 'typedjson'

@jsonObject
export class VerificationCodeEmailModel {

  @jsonMember(String)
  id?: string;

  @jsonMember(String)
  email?: string;

  @jsonMember(String)
  verificationCode!: string;

  @jsonMember(Number)
  verificationCodeLength!: number;

  @jsonMember(Boolean)
  isDeleted?: boolean;

  @jsonMember(Date)
  createDate?: Date;

  @jsonMember(Date)
  updateDate?: Date;

  constructor() {
    makeAutoObservable(this);
  }
}