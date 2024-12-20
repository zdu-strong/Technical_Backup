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
  const salt = CryptoJS.MD5(v1());;
  const result = CryptoJS.enc.Base64.stringify(salt.concat(CryptoJS.enc.Hex.parse(CryptoJS.AES.encrypt(
    CryptoJS.enc.Utf8.parse(data),
    CryptoJS.enc.Base64.parse(secretKeyOfAES),
    {
      iv: salt,
      padding: CryptoJS.pad.Pkcs7,
      mode: CryptoJS.mode.CBC,
    }
  ).toString(CryptoJS.format.Hex))));
  return result;
});
