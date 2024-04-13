import { getUserById } from "@/api/User";
import { generateSecretKeyOfAES } from "@/common/AESUtils";
import { GlobalUserInfo } from "@/common/Server";
import { FriendshipModel } from "@/model/FriendshipModel";
import { FriendshipPaginationModel } from "@/model/FriendshipPaginationModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function addToFriendList(friendId: string) {
  const keyOfAES = await generateSecretKeyOfAES();
  const aesOfUser = await GlobalUserInfo.encryptByPublicKeyOfRSA(await GlobalUserInfo.encryptByPrivateKeyOfRSA(keyOfAES));
  const friend = await getUserById(friendId);
  const aesOfFriend = friend.encryptByPublicKeyOfRSA(await GlobalUserInfo.encryptByPrivateKeyOfRSA(keyOfAES));
  await axios.post("/friendship/add_to_friend_list", null, { params: { friendId, aesOfUser, aesOfFriend } });
}

export async function deleteFromFriendList(friendId: string) {
  await axios.delete("/friendship/delete_from_friend_list", { params: { friendId } })
}

export async function deleteFromBlacklist(friendId: string) {
  await axios.delete("/friendship/delete_from_black_list", { params: { friendId } })
}

export async function getFriendList() {
  const { data } = await axios.get<FriendshipPaginationModel>("/friendship/get_friend_list", { params: { pageNum: 1, pageSize: 100 } });
  return new TypedJSON(FriendshipPaginationModel).parse(data)!;
}

export async function getStrangerList() {
  const { data } = await axios.get<FriendshipPaginationModel>("/friendship/get_stranger_list", { params: { pageNum: 1, pageSize: 100 } });
  return new TypedJSON(FriendshipPaginationModel).parse(data)!;
}

export async function getFriendship(friendId: string) {
  const { data } = await axios.get<FriendshipModel>("/friendship/get_friendship", { params: { friendId } });
  return new TypedJSON(FriendshipModel).parse(data)!;
}
