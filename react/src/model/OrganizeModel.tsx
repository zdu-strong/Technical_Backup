import { makeAutoObservable } from 'mobx-react-use-autorun'
import { Type } from 'class-transformer';

export class OrganizeModel {

  @Type(() => String)
  id!: string;

  @Type(() => String)
  name!: string;

  @Type(() => Number)
  level!: number;

  @Type(() => Date)
  createDate!: Date;

  @Type(() => Date)
  updateDate!: Date;

  @Type(() => Number)
  childCount!: number;

  @Type(() => Number)
  descendantCount!: number;

  @Type(() => OrganizeModel)
  parent!: OrganizeModel;

  @Type(() => OrganizeModel)
  childList!: OrganizeModel[];

  constructor() {
    makeAutoObservable(this);
  }
}