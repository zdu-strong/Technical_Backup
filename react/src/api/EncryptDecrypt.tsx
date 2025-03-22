import axios from "axios";

export async function getKeyOfRSAPublicKey() {
  const { data } = await axios.get<string>("/encrypt-decrypt/rsa/public-key");
  return data;
}