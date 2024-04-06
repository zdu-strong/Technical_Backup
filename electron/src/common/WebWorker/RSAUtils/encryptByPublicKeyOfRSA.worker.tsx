import registerWebworker from 'webworker-promise/lib/register'
import JSEncrypt from 'jsencrypt'

registerWebworker(async ({
  data,
  publicKeyOfRSA,
}: {
  data: string,
  publicKeyOfRSA: string,
}) => {
  return await encrypt(publicKeyOfRSA, data);
});

async function encrypt(publicKeyOfRSA: string, data: string) {
  const jsencrypt = new JSEncrypt();
  jsencrypt.setPublicKey(publicKeyOfRSA);
  const k = jsencrypt.getKey();
  let ct = ''
  const bytes = []
  bytes.push(0)
  let byteNo = 0
  let c
  const len = data.length
  let temp = 0
  for (let i = 0; i < len; i++) {
    c = data.charCodeAt(i)
    if (c >= 0x010000 && c <= 0x10ffff) {
      byteNo += 4
    } else if (c >= 0x000800 && c <= 0x00ffff) {
      byteNo += 3
    } else if (c >= 0x000080 && c <= 0x0007ff) {
      byteNo += 2
    } else {
      byteNo += 1
    }
    if (byteNo % 117 >= 114 || byteNo % 117 === 0) {
      if (byteNo - temp >= 114) {
        bytes.push(i)
        temp = byteNo
      }
    }
  }
  if (bytes.length > 1) {
    for (let i = 0; i < bytes.length - 1; i++) {
      let str
      if (i === 0) {
        str = data.substring(0, bytes[i + 1] + 1)
      } else {
        str = data.substring(bytes[i] + 1, bytes[i + 1] + 1)
      }
      const t1 = k.encrypt(str)
      ct += t1
    }
    if (bytes[bytes.length - 1] !== data.length - 1) {
      const lastStr = data.substring(bytes[bytes.length - 1] + 1)
      ct += k.encrypt(lastStr)
    }
    return Buffer.from(ct, "hex").toString("base64");
  }
  const t = k.encrypt(data)
  const y = Buffer.from(t, "hex").toString('base64');
  return y;
}
