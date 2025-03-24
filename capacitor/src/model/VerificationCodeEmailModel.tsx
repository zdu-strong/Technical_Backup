import { makeAutoObservable } from 'mobx-react-use-autorun';
import { Type } from 'class-transformer';

export class VerificationCodeEmailModel {

  @Type(() => String)
  id?: string;

  @Type(() => String)
  email?: string;

  @Type(() => String)
  verificationCode!: string;

  @Type(() => Number)
  verificationCodeLength!: number;

  @Type(() => Date)
  createDate?: Date;

  @Type(() => Date)
  updateDate?: Date;

  constructor() {
    makeAutoObservable(this);
  }
}