import { UserModel } from "@model/UserModel";
import axios from "axios";
import { TypedJSON } from "typedjson";
import { getEncryptedPassword } from "@api/Authorization";


export async function createUser(user: UserModel) {
    const body = new TypedJSON(UserModel).parse(JSON.stringify(user))!;
    body.password = await getEncryptedPassword(body.password);
    const { data } = await axios.post("/user/create/ml-kem/one-time", body);
    return new TypedJSON(UserModel).parse(data)!;
}

export async function updateUser(user: UserModel) {
    const body = new TypedJSON(UserModel).parse(JSON.stringify(user))!;
    if (body.password) {
        body.password = await getEncryptedPassword(body.password);
    }
    await axios.post("/user/update/ml-kem/one-time", body);
}

export async function getUserById(userId: string) {
    const { data } = await axios.get("/user", { params: { id: userId } });
    return new TypedJSON(UserModel).parse(data)!;
}

export async function deleteUserById(userId: string) {
    await axios.post("/user/delete", null, { params: { id: userId } });
}