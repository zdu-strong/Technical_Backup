import { SystemRolePaginationModel } from "@/model/SystemRolePaginationModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function searchByPagination() {
  let { data: paginationModel } = await axios.get<SystemRolePaginationModel>("/super_admin/user_role/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  paginationModel = new TypedJSON(SystemRolePaginationModel).parse(paginationModel)!;
  return paginationModel;
}
