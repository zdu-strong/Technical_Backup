import { SystemRolePaginationModel } from "@/model/SystemRolePaginationModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function searchByPagination() {
  const { data } = await axios.get("/super_admin/user_role/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  return new TypedJSON(SystemRolePaginationModel).parse(data)!;
}
