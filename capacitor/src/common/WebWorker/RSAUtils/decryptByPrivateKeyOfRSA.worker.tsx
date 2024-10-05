import NodeRSA from "node-rsa";
import registerWebworker from 'webworker-promise/lib/register'

registerWebworker(async ({
  data,
  privateKeyOfRSA,
}: {
  data: string,
  privateKeyOfRSA: string,
}) => {
  const rsa = new NodeRSA(Buffer.from(privateKeyOfRSA, "base64"), "pkcs8-private-der", { encryptionScheme: "pkcs1" });
  return rsa.decrypt(Buffer.from(data, 'base64'), "utf8");
});
