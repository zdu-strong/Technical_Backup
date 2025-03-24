import registerWebworker from 'webworker-promise/lib/register'
import { v6 } from 'uuid';
import { createHash, pbkdf2Sync } from 'crypto';

registerWebworker(async ({
  password,
}: {
  password?: string,
}) => {
  if (!password) {
    password = v6();
  }
  const salt = createHash("MD5").update(Buffer.from(password, "utf-8")).digest("base64");
  return pbkdf2Sync(Buffer.from(password, "utf-8"), Buffer.from(salt, "base64"), 65536, 256 / 8, "sha256").toString("base64");
});
