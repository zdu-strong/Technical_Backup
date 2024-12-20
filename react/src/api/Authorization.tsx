import { getKeyOfRSAPublicKey } from '@/api/EncryptDecrypt';
import { encryptByPublicKeyOfRSA } from "@/common/RSAUtils";
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/Server";
import { UserEmailModel } from "@/model/UserEmailModel";
import { UserModel } from "@/model/UserModel";
import { VerificationCodeEmailModel } from "@/model/VerificationCodeEmailModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  let { data: user } = await axios.post<UserModel>(`/sign_up`, {
    username: nickname,
    password: password,
    userEmailList: userEmailList,
  });
  user = new TypedJSON(UserModel).parse(user)!;
  user.menuOpen = true;
  await signOut();
  await setGlobalUserInfo(user);
}

export async function sendVerificationCode(email: string) {
  const { data } = await axios.post<VerificationCodeEmailModel>("/email/send_verification_code", null, { params: { email } });
  return new TypedJSON(VerificationCodeEmailModel).parse(data)!;
}

export async function signIn(username: string, password: string): Promise<void> {
  await signOut();
  let { data: user } = await axios.post<UserModel>(`/sign_in/one_time_password`, null, {
    params: {
      username: username,
      password: await encryptByPublicKeyOfRSA(password, await getKeyOfRSAPublicKey()),
    }
  });
  user = new TypedJSON(UserModel).parse(user)!;
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