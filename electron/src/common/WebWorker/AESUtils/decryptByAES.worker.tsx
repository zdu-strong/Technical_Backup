import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';

registerWebworker(async ({
  data,
  secretKeyOfAES,
}: {
  data: string,
  secretKeyOfAES: string,
}) => {
  const dataByteList = Buffer.from(data, "base64");
  const salt = CryptoJS.enc.Base64.parse(Buffer.from(dataByteList.buffer.slice(0, 16)).toString("base64"));
  const text = CryptoJS.AES.decrypt(
    Buffer.from(dataByteList.buffer.slice(16)).toString("base64"),
    CryptoJS.enc.Base64.parse(secretKeyOfAES),
    {
      iv: salt,
      padding: CryptoJS.pad.Pkcs7,
      mode: CryptoJS.mode.CBC,
    }
  ).toString(CryptoJS.enc.Utf8);
  return text;
});
