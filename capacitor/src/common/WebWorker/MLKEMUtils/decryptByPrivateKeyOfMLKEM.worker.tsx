import registerWebworker from 'webworker-promise/lib/register';
import { createMlKem1024 } from "mlkem";
import { decryptByAES } from '@/common/AESUtils';

registerWebworker(async ({
    data,
    privateKeyOfMLKEM,
}: {
    data: string,
    privateKeyOfMLKEM: string,
}) => {
    const dataByteList = Buffer.from(data, "hex");
    const encapsulateCiphertext = Buffer.from(dataByteList.buffer.slice(0, 1568));
    const encrypted = Buffer.from(dataByteList.buffer.slice(1568)).toString("hex");
    const mlKemUtil = await createMlKem1024();
    const sharedSecretKeyOfAES = mlKemUtil.decap(encapsulateCiphertext, Buffer.from(privateKeyOfMLKEM, "hex"));
    const result = await decryptByAES(encrypted, Buffer.from(sharedSecretKeyOfAES).toString("hex"));
    return result;
});
