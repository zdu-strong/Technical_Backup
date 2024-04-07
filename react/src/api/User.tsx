import { decryptByPublicKeyOfRSA, encryptByPublicKeyOfRSA } from "@/common/RSAUtils";
import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getUserById(userId: string) {
  const response = await axios.get<UserModel>("/user", { params: { id: userId } });
  response.data = new TypedJSON(UserModel).parse(response.data)!;
  response.data.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(data, response.data!.publicKeyOfRSA);
  }
  response.data.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(data, response.data!.publicKeyOfRSA);
  }
  return response.data;
}
