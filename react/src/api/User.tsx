import { decryptByPublicKeyOfRSA, encryptByPublicKeyOfRSA } from "@/common/RSAUtils";
import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getUserById(userId: string) {
  let { data: user } = await axios.get<UserModel>("/user", { params: { id: userId } });
  user = new TypedJSON(UserModel).parse(user)!;
  user.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(data, user!.publicKeyOfRSA);
  }
  user.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(data, user!.publicKeyOfRSA);
  }
  return user;
}
