import { PaginationModel } from "@/model/PaginationModel";
import { UserModel } from "@/model/UserModel";
import axios from "axios";

export async function searchByPagination() {
  const { data } = await axios.get("/super-admin/user/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  return new PaginationModel(data, UserModel);
}
