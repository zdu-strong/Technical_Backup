import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@common/Server";
import { UserEmailModel } from "@model/UserEmailModel";
import { UserModel } from "@model/UserModel";
import { VerificationCodeEmailModel } from "@model/VerificationCodeEmailModel";
import axios from "axios";
import { TypedJSON } from "typedjson";
import { getPublicKeyOfMLKEM } from "@api/EncryptDecrypt";
import { sha3_512 } from 'js-sha3';
import { encryptByPublicKeyOfMLKEM } from "@/common/MLKEMUtils";

export async function signUp(password: string, username: string, userEmailList: UserEmailModel[]): Promise<void> {
    await signOut();
    const { data } = await axios.post(`/sign-up/rsa/one-time`, {
        username: username,
        password: await getEncryptedPassword(password),
        userEmailList: userEmailList,
    });
    const user = new TypedJSON(UserModel).parse(data)!;
    await setGlobalUserInfo(user);
}

export async function sendVerificationCode(email: string) {
    const { data } = await axios.post("/email/send-verification-code", null, { params: { email } });
    return new TypedJSON(VerificationCodeEmailModel).parse(data)!;
}

export async function signIn(username: string, password: string): Promise<void> {
    await signOut();
    const { data } = await axios.post(`/sign-in/rsa/one-time`, null, {
        params: {
            username: username,
            password: await getEncryptedPassword(password),
        }
    });
    const user = new TypedJSON(UserModel).parse(data)!;
    await setGlobalUserInfo(user);
}

export async function signOut() {
    if (GlobalUserInfo.accessToken) {
        try {
            await axios.post("/sign-out");
        } catch {
            // do nothing
        }
        removeGlobalUserInfo();
    }
}

export async function isSignIn() {
    if (!GlobalUserInfo.accessToken) {
        return false;
    }
    try {
        await axios.get("/user/me");
    } catch (e) {
        if (e && (e as any).status === 401) {
            removeGlobalUserInfo();
            return false;
        }
    }
    return true;
}

export async function getEncryptedPassword(password: string) {
    return await encryptByPublicKeyOfMLKEM(JSON.stringify([sha3_512(password), new Date()]), await getPublicKeyOfMLKEM());
}