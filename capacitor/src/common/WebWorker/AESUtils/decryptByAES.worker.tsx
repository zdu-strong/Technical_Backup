import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';

registerWebworker(async ({
  data,
  secretKeyOfAES,
}: {
  data: string,
  secretKeyOfAES: string,
}) => {
  const salt = data.slice(0, 24);
  data = data.slice(24);
  let text = CryptoJS.AES.decrypt(
    data,
    CryptoJS.enc.Base64.parse(secretKeyOfAES),
    {
      iv: CryptoJS.enc.Base64.parse(salt),
      padding: CryptoJS.pad.Pkcs7,
      mode: CryptoJS.mode.CBC,
    }
  ).toString(CryptoJS.enc.Utf8);
  return text;
});
