import { runWoker } from "@/common/WebWorker/WebWorkerUtils";

export async function generateKeyPairOfRSA(): Promise<{ privateKey: string, publicKey: string }> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/RSAUtils/generateKeyPairOfRSA.worker', import.meta.url), { type: "module" }));
}

export async function encryptByPublicKeyOfRSA(data: string, publicKeyOfRSA: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/RSAUtils/encryptByPublicKeyOfRSA.worker', import.meta.url), { type: "module" }),
    {
      data,
      publicKeyOfRSA,
    }
  );
}

export async function decryptByPrivateKeyOfRSA(data: string, privateKeyOfRSA: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/RSAUtils/decryptByPrivateKeyOfRSA.worker', import.meta.url), { type: "module" }),
    {
      data,
      privateKeyOfRSA,
    }
  );
}

export async function encryptByPrivateKeyOfRSA(data: string, privateKeyOfRSA: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/RSAUtils/encryptByPrivateKeyOfRSA.worker', import.meta.url), { type: "module" }),
    {
      data,
      privateKeyOfRSA,
    }
  );
}

export async function decryptByPublicKeyOfRSA(data: string, publicKeyOfRSA: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/RSAUtils/decryptByPublicKeyOfRSA.worker', import.meta.url), { type: "module" }),
    {
      data,
      publicKeyOfRSA,
    }
  );
}
