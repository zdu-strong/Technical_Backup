import { makeAutoObservable } from 'mobx-react-use-autorun';
import linq from "linq";
import * as mathjs from 'mathjs';
import { jsonArrayMember, jsonMember, jsonObject, Serializable, TypedJSON } from 'typedjson';

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

  constructor(jsonString: string, rootConstructor: Serializable<T>);

  constructor(jsonObject: object, rootConstructor: Serializable<T>);

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

  private handleJsonString(jsonString: string, rootConstructor: Serializable<T>) {
    if (!(["string", "object"].includes(typeof jsonString) && typeof rootConstructor === "function")) {
      return;
    }

    @jsonObject
    class CustomerPaginationModel {
      @jsonMember(Number)
      pageNum!: number;

      @jsonMember(Number)
      pageSize!: number;

      @jsonMember(Number)
      totalRecord!: number;

      @jsonMember(Number)
      totalPage!: number;

      @jsonArrayMember(rootConstructor)
      list!: T[];
    }
    const customerPaginationModel = new TypedJSON(CustomerPaginationModel).parse(jsonString)!;
    this.pageNum = customerPaginationModel.pageNum;
    this.pageSize = customerPaginationModel.pageSize;
    this.totalPage = customerPaginationModel.totalPage;
    this.totalRecord = customerPaginationModel.totalRecord;
    this.list = customerPaginationModel.list;
  }

}