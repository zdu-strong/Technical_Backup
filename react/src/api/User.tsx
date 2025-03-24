import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { plainToInstance } from "class-transformer";

export async function getUserById(userId: string) {
  const { data } = await axios.get("/user", { params: { id: userId } });
  return plainToInstance(UserModel, data);
}
