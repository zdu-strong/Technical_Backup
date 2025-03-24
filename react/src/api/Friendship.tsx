import { FriendshipModel } from "@/model/FriendshipModel";
import { PaginationModel } from "@/model/PaginationModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function addToFriendList(friendId: string) {
  await axios.post("/friendship/add-to-friend-list", null, { params: { friendId } });
}

export async function deleteFromFriendList(friendId: string) {
  await axios.delete("/friendship/delete-from-friend-list", { params: { friendId } })
}

export async function deleteFromBlacklist(friendId: string) {
  await axios.delete("/friendship/delete-from-black-list", { params: { friendId } })
}

export async function getFriendList() {
  const { data } = await axios.get("/friendship/get-friend-list", { params: { pageNum: 1, pageSize: 100 } });
  return new PaginationModel(data, FriendshipModel);
}

export async function getStrangerList() {
  const { data } = await axios.get("/friendship/get-stranger-list", { params: { pageNum: 1, pageSize: 100 } });
  return new PaginationModel(data, FriendshipModel);
}

export async function getFriendship(friendId: string) {
  const { data } = await axios.get("/friendship/get-friendship", { params: { friendId } });
  return new TypedJSON(FriendshipModel).parse(data)!;
}
