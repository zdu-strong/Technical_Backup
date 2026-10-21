import registerWebworker from 'webworker-promise/lib/register';
import { createMlKem1024 } from "mlkem";
import { encryptByAES } from '@/common/AESUtils';

registerWebworker(async ({
    data,
    publicKeyOfMLKEM,
}: {
    data: string,
    publicKeyOfMLKEM: string,
}) => {
    const mlKemUtil = await createMlKem1024();
    const [encapsulateCiphertext, sharedSecretKeyOfAES] = mlKemUtil.encap(Buffer.from(publicKeyOfMLKEM, "hex"));
    const result = await encryptByAES(data, Buffer.from(sharedSecretKeyOfAES).toString("hex"));
    return Buffer.concat([encapsulateCiphertext, Buffer.from(result, "hex")]).toString("hex");
});