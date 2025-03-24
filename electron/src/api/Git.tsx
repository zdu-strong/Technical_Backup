import { GitPropertiesModel } from "@/model/GitPropertiesModel";
import axios from "axios";
import { plainToInstance } from "class-transformer";

export async function getServerGitInfo() {
  const { data } = await axios.get("/git");
  return plainToInstance(GitPropertiesModel, data);
}