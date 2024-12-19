import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';
import { v1 } from 'uuid';

registerWebworker(async ({
  data,
  secretKeyOfAES,
}: {
  data: string,
  secretKeyOfAES: string,
}) => {
  const salt = CryptoJS.MD5(v1()).toString(CryptoJS.enc.Base64);
  let result = CryptoJS.AES.encrypt(
    CryptoJS.enc.Utf8.parse(data),
    CryptoJS.enc.Base64.parse(secretKeyOfAES),
    {
      iv: CryptoJS.enc.Base64.parse(salt),
      padding: CryptoJS.pad.Pkcs7,
      mode: CryptoJS.mode.CBC,
    }
  ).toString();
  result = `${salt}${result}`;
  return result;
});
