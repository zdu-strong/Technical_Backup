import { PaginationModel } from "@/model/PaginationModel";
import { SystemRoleModel } from "@/model/SystemRoleModel";
import axios from "axios";

export async function searchByPagination() {
  const { data } = await axios.get("/super-admin/user-role/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  return PaginationModel.fromJson(data, SystemRoleModel);
}
