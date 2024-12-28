import registerWebworker from 'webworker-promise/lib/register'
import { createDecipheriv } from 'crypto';

registerWebworker(async ({
  data,
  secretKeyOfAES,
}: {
  data: string,
  secretKeyOfAES: string,
}) => {
  const dataByteList = Buffer.from(data, "base64");
  const salt = Buffer.from(dataByteList.buffer.slice(0, 16)).toString("base64");
  const tag = Buffer.from(dataByteList.buffer.slice(dataByteList.length - 16)).toString("base64");
  const encrypted = Buffer.from(dataByteList.buffer.slice(16, dataByteList.length - 16)).toString("base64");
  const decipher = createDecipheriv("aes-256-gcm", Buffer.from(secretKeyOfAES, "base64"), Buffer.from(salt, "base64"), { authTagLength: 16 });
  decipher.setAuthTag(Buffer.from(tag, "base64"));
  const result = Buffer.concat([Buffer.from(decipher.update(encrypted, "base64", "base64"), "base64"), Buffer.from(decipher.final("base64"), "base64")]).toString("utf-8");
  return result;
});
