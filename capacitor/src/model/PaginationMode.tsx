import { makeAutoObservable } from 'mobx-react-use-autorun';
import linq from "linq";
import * as mathjs from 'mathjs';

export class PaginationModel<T> {

  pageNum!: number;

  pageSize!: number;

  totalRecord!: number;

  totalPage!: number;

  list!: T[];

  constructor(
    pageNum: number,
    pageSize: number,
    stream: linq.IEnumerable<T>
  ) {
    makeAutoObservable(this);
    if (pageNum < 1) {
      throw new Error("The page number cannot be less than 1");
    }
    if (pageSize < 1) {
      throw new Error("The page size cannot be less than 1");
    }

    if (pageNum !== Math.floor(pageNum)) {
      throw new Error("The page number must be an integer");
    }

    if (pageSize !== Math.floor(pageSize!)) {
      throw new Error("The page size must be an integer");
    }

    const totalRecord = stream.count();
    const totalPage = Math.floor(mathjs.divide(totalRecord, pageSize)) + mathjs.mod(totalRecord, pageSize) > 0 ? 1 : 0;
    const list = stream
      .skip(mathjs.multiply(pageNum - 1, pageSize))
      .take(pageSize)
      .toArray();
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.totalPage = totalPage;
    this.totalRecord = totalRecord;
    this.list = list;
  }

}
