import { getKeyOfRSAPublicKey } from '@/api/EncryptDecrypt';
import { encryptByPublicKeyOfRSA } from "@/common/RSAUtils";
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/Server";
import { UserEmailModel } from "@/model/UserEmailModel";
import { UserModel } from "@/model/UserModel";
import { VerificationCodeEmailModel } from "@/model/VerificationCodeEmailModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  const { data } = await axios.post(`/sign_up`, {
    username: nickname,
    password: password,
    userEmailList: userEmailList,
  });
  const user = new TypedJSON(UserModel).parse(data)!;
  user.menuOpen = true;
  await signOut();
  await setGlobalUserInfo(user);
}

export async function sendVerificationCode(email: string) {
  const { data } = await axios.post("/email/send_verification_code", null, { params: { email } });
  return new TypedJSON(VerificationCodeEmailModel).parse(data)!;
}

export async function signIn(username: string, password: string): Promise<void> {
  await signOut();
  const { data } = await axios.post(`/sign_in/one_time_password`, null, {
    params: {
      username: username,
      password: await encryptByPublicKeyOfRSA(password, await getKeyOfRSAPublicKey()),
    }
  });
  const user = new TypedJSON(UserModel).parse(data)!;
  user.menuOpen = true;
  await setGlobalUserInfo(user);
}

export async function signOut() {
  if (GlobalUserInfo.accessToken) {
    try {
      await axios.post("/sign_out");
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
    await axios.get("/get_user_info");
  } catch (e) {
    if (e && (e as any).status === 401) {
      removeGlobalUserInfo();
      return false;
    }
  }
  return true;
}