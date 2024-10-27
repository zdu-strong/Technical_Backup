import { UserPaginationModel } from "@/model/UserPaginationModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function searchByPagination() {
  let { data: paginationModel } = await axios.get<UserPaginationModel>("/super_admin/user/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  paginationModel = new TypedJSON(UserPaginationModel).parse(paginationModel)!;
  return paginationModel;
}
