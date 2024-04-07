import { GitPropertiesModel } from "@/model/GitPropertiesModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getServerGitInfo() {
  const response = await axios.get<GitPropertiesModel>("/git");
  response.data = new TypedJSON(GitPropertiesModel).parse(response.data)!;
  return response.data;
}