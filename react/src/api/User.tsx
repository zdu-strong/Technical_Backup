import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getUserById(userId: string) {
  let { data: user } = await axios.get<UserModel>("/user", { params: { id: userId } });
  user = new TypedJSON(UserModel).parse(user)!;
  return user;
}
