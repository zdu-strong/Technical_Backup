import { getKeyOfRSAPublicKey } from '@/api/EncryptDecrypt';
import { decryptByAES, encryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';
import { encryptByPublicKeyOfRSA, generateKeyPairOfRSA } from "@/common/RSAUtils";
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/Server";
import { UserEmailModel } from "@/model/UserEmailModel";
import { UserModel } from "@/model/UserModel";
import { VerificationCodeEmailModel } from "@/model/VerificationCodeEmailModel";
import axios from "axios";
import { TypedJSON } from "typedjson";
import { v1 } from "uuid";

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  const secretKeyOfAESForRSA = await generateSecretKeyOfAES(password + password);
  const { privateKey, publicKey } = await generateKeyPairOfRSA();
  const secretKeyOfAES = await generateSecretKeyOfAES(password);
  let { data: user } = await axios.post<UserModel>(`/sign_up`, {
    username: nickname,
    userEmailList: userEmailList,
    publicKeyOfRSA: publicKey,
    privateKeyOfRSA: await encryptByAES(privateKey, secretKeyOfAESForRSA),
    password: await encryptByAES(secretKeyOfAES, secretKeyOfAES),
  });
  user = new TypedJSON(UserModel).parse(user)!;
  user.privateKeyOfRSA = privateKey;
  await signOut();
  await setGlobalUserInfo(user);
}

export async function sendVerificationCode(email: string) {
  const { data } = await axios.post<VerificationCodeEmailModel>("/email/send_verification_code", null, { params: { email } });
  return new TypedJSON(VerificationCodeEmailModel).parse(data)!;
}

export async function signIn(username: string, password: string): Promise<void> {
  await signOut();
  const passwordPartList = [new Date(), v1(), await generateSecretKeyOfAES(password)];
  const passwordJsonString = JSON.stringify(passwordPartList);
  const publicKey = await getKeyOfRSAPublicKey();
  const passwordParameter = await encryptByPublicKeyOfRSA(passwordJsonString, publicKey);
  let { data: user } = await axios.post<UserModel>(`/sign_in`, null, {
    params: {
      username: username,
      password: passwordParameter,
    }
  });
  user = new TypedJSON(UserModel).parse(user)!;
  user.privateKeyOfRSA = await decryptByAES(user.privateKeyOfRSA, await generateSecretKeyOfAES(password + password));
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