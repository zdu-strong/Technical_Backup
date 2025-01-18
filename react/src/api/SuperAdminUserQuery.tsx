import { UserPaginationModel } from "@/model/UserPaginationModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function searchByPagination() {
  const { data } = await axios.get("/super_admin/user/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  return new TypedJSON(UserPaginationModel).parse(data)!;
}
