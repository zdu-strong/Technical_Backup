import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getUserById(userId: string) {
  const { data } = await axios.get("/user", { params: { id: userId } });
  return new TypedJSON(UserModel).parse(data)!;
}
