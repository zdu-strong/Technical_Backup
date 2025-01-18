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
  );

  constructor(otherPaginationModel: any);

  constructor(
    pageNum?: number,
    pageSize?: number,
    stream?: linq.IEnumerable<T>
  ) {
    makeAutoObservable(this);
    if (typeof pageNum === "object" && typeof pageSize === "undefined" && typeof stream === "undefined") {
      const otherPaginationModel = pageNum as any;
      this.pageNum = otherPaginationModel.pageNum;
      this.pageSize = otherPaginationModel.pageSize;
      this.totalRecord = otherPaginationModel.totalRecord;
      this.totalPage = otherPaginationModel.totalPage;
      this.list = otherPaginationModel.list;
      if (!(typeof this.pageNum === "number" && typeof this.pageSize === "number" && typeof this.totalRecord === "number" && typeof this.totalPage === "number" && typeof this.list === "object" && this.list instanceof Array)) {
        throw new Error("Incorrect paging behavior");
      }
      return;
    }
    if (!(typeof pageNum === "number" && typeof pageSize === "number" && typeof stream === "object")) {
      return;
    }
    if (pageNum! < 1) {
      throw new Error("The page number cannot be less than 1");
    }
    if (pageSize! < 1) {
      throw new Error("The page size cannot be less than 1");
    }

    if (pageNum !== Math.floor(pageNum!)) {
      throw new Error("The page number must be an integer");
    }

    if (pageSize !== Math.floor(pageSize!)) {
      throw new Error("The page size must be an integer");
    }

    const totalRecord = (stream as linq.IEnumerable<T>).count();
    const totalPage = Math.floor(mathjs.divide(totalRecord, pageSize)) + mathjs.mod(totalRecord, pageSize) > 0 ? 1 : 0;
    const list = (stream as linq.IEnumerable<T>)
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
