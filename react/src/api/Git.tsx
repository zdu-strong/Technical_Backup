import { GitPropertiesModel } from "@/model/GitPropertiesModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getServerGitInfo() {
  const { data } = await axios.get("/git");
  return new TypedJSON(GitPropertiesModel).parse(data)!;
}