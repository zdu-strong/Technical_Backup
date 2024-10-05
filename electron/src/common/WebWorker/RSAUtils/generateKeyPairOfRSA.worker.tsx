import NodeRSA from "node-rsa";
import registerWebworker from 'webworker-promise/lib/register'

registerWebworker(async () => {
  const rsa = new NodeRSA({ b: 2048 });
  rsa.setOptions({ encryptionScheme: "pkcs1" });
  return {
    privateKey: rsa.exportKey("pkcs8-private-der").toString("base64"),
    publicKey: rsa.exportKey("pkcs8-public-der").toString("base64"),
  }
});
