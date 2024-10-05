import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';
import { v1, v4 } from 'uuid'

registerWebworker(async ({
  password,
}: {
  password?: string,
}) => {
  if (!password) {
    password = JSON.stringify([v1(), v4()]);
  }
  const salt = CryptoJS.MD5(password);
  const key256Bits = CryptoJS.PBKDF2(password, salt, {
    keySize: 256 / 32,
    hasher: CryptoJS.algo.SHA256,
    iterations: 65536
  });
  return key256Bits.toString(CryptoJS.enc.Base64);
});
