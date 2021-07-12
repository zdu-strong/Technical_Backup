import { immerable } from "immer";
import linq from "linq";

class BasePaginationModel<T> {
    [immerable] = true

    pageNum: number;
    pageSize: number;
    totalPage: number;
    totalRecord: number;
    list: T[];

    constructor(pageNum: number, pageSize: number, stream: linq.IEnumerable<T>) {
        if (pageSize < 1) {
            throw new Error("The page number cannot be less than 1");
        }
        if (pageNum < 1) {
            throw new Error("The page size cannot be less than 1");
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalRecord = stream.count();
        this.totalPage = Math.ceil(this.totalRecord / pageSize);
        this.list = stream.skip((pageNum - 1) * pageSize).take(pageSize).toArray();
    }
}

export default BasePaginationModel;