import axios from "axios";

export async function getKeyOfRSAPublicKey() {
  const { data } = await axios.get<string>("/encrypt_decrypt/rsa/public_key");
  return data;
}