import { PaginationModel } from "@/model/PaginationModel";
import { OrganizeModel } from "@/model/OrganizeModel";
import axios from "axios";

export async function searchByPagination() {
  const { data } = await axios.get("/super-admin/organize/search/pagination", { params: { pageNum: 1, pageSize: 100 } });
  return PaginationModel.fromJson(data, OrganizeModel);
}
