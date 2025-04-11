import { makeAutoObservable } from 'mobx-react-use-autorun';
import linq from "linq";
import * as mathjs from 'mathjs';
import { jsonArrayMember, jsonMember, jsonObject, Serializable, TypedJSON } from 'typedjson';

export class PaginationModel<T> {

  pageNum!: number;

  pageSize!: number;

  totalRecords!: number;

  totalPages!: number;

  items!: T[];

  constructor();

  constructor(
    pageNum: number,
    pageSize: number,
    stream: linq.IEnumerable<T>
  );

  constructor(
    pageNum?: number,
    pageSize?: number,
    stream?: linq.IEnumerable<T>
  ) {
    makeAutoObservable(this);
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

    const totalRecords = (stream as linq.IEnumerable<T>).count();
    const totalPages = Math.floor(mathjs.divide(totalRecords, pageSize)) + (mathjs.mod(totalRecords, pageSize) > 0 ? 1 : 0);
    const items = (stream as linq.IEnumerable<T>)
      .skip(mathjs.multiply(pageNum - 1, pageSize))
      .take(pageSize)
      .toArray();
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.totalPages = totalPages;
    this.totalRecords = totalRecords;
    this.items = items;
  }

  static fromJson<U>(json: string | object, rootConstructor: Serializable<U>): PaginationModel<U> {
    @jsonObject
    class CustomPaginationModel {
      @jsonMember(Number)
      pageNum!: number;

      @jsonMember(Number)
      pageSize!: number;

      @jsonMember(Number)
      totalRecords!: number;

      @jsonMember(Number)
      totalPages!: number;

      @jsonArrayMember(rootConstructor)
      items!: U[];
    }

    const customPaginationModel = new TypedJSON(CustomPaginationModel).parse(json)!;

    const model = new PaginationModel<U>();
    model.pageNum = customPaginationModel.pageNum;
    model.pageSize = customPaginationModel.pageSize;
    model.totalPages = customPaginationModel.totalPages;
    model.totalRecords = customPaginationModel.totalRecords;
    model.items = customPaginationModel.items;
    return model;
  }

}