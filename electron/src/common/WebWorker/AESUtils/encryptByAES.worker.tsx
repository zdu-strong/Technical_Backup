import registerWebworker from 'webworker-promise/lib/register'
import { v6 } from 'uuid';
import { createHash, createCipheriv } from 'crypto';

registerWebworker(async ({
  data,
  secretKeyOfAES,
}: {
  data: string,
  secretKeyOfAES: string,
}) => {
  const salt = createHash("MD5").update(v6(), "utf-8").digest("base64");
  const cipher = createCipheriv("aes-256-gcm", Buffer.from(secretKeyOfAES, "base64"), Buffer.from(salt, "base64"), { authTagLength: 16 });
  const result = Buffer.concat([Buffer.from(salt, "base64"), Buffer.from(cipher.update(data, "utf-8", "base64"), "base64"), Buffer.from(cipher.final("base64"), "base64"), Buffer.from(cipher.getAuthTag().toString("base64"), "base64")]).toString("base64");
  return result;
});
