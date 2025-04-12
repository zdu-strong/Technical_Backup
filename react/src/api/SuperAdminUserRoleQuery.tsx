import { PaginationModel } from "@/model/PaginationModel";
import { SuperAdminUserRoleQueryPaginationModel } from "@/model/SuperAdminUserRoleQueryPaginationModel";
import { SystemRoleModel } from "@/model/SystemRoleModel";
import axios from "axios";

export async function searchByPagination(query: SuperAdminUserRoleQueryPaginationModel) {
  const { data } = await axios.get("/super-admin/user-role/search/pagination", { params: query });
  return PaginationModel.fromJson(data, SystemRoleModel);
}
