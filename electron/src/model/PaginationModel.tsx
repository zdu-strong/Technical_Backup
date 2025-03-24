import { makeAutoObservable } from 'mobx-react-use-autorun';
import linq from "linq";
import * as mathjs from 'mathjs';
import { ClassConstructor, plainToInstance, Type } from 'class-transformer';

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

  constructor(jsonString: string, rootConstructor: ClassConstructor<T>);

  constructor(jsonObject: object, rootConstructor: ClassConstructor<T>);

  constructor(
    arg1?: any,
    arg2?: any,
    arg3?: any
  ) {
    makeAutoObservable(this);
    this.handleStream(arg1, arg2, arg3);
    this.handleJsonString(arg1, arg2);
  }

  private handleStream(
    pageNum: number,
    pageSize: number,
    stream: linq.IEnumerable<T>
  ) {
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
    const totalPage = Math.floor(mathjs.divide(totalRecord, pageSize)) + (mathjs.mod(totalRecord, pageSize) > 0 ? 1 : 0);
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

  private handleJsonString(jsonString: string, rootConstructor: ClassConstructor<T>) {
    if (!(["string", "object"].includes(typeof jsonString) && typeof rootConstructor === "function")) {
      return;
    }

    class CustomerPaginationModel {
      @Type(() => Number)
      pageNum!: number;

      @Type(() => Number)
      pageSize!: number;

      @Type(() => Number)
      totalRecord!: number;

      @Type(() => Number)
      totalPage!: number;

      @Type(() => rootConstructor)
      list!: T[];
    }
    const customerPaginationModel = plainToInstance(CustomerPaginationModel, typeof jsonString === "string" ? JSON.parse(jsonString) : jsonString);
    this.pageNum = customerPaginationModel.pageNum;
    this.pageSize = customerPaginationModel.pageSize;
    this.totalPage = customerPaginationModel.totalPage;
    this.totalRecord = customerPaginationModel.totalRecord;
    this.list = customerPaginationModel.list;
  }

}