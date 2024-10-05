import { runWoker } from '@/common/WebWorker/WebWorkerUtils';

export async function generateSecretKeyOfAES(password?: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/AESUtils/generateSecretKeyOfAES.worker', import.meta.url), { type: "module" }),
    {
      password
    }
  );
}

export async function encryptByAES(data: string, secretKeyOfAES: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/AESUtils/encryptByAES.worker', import.meta.url), { type: "module" }),
    {
      data,
      secretKeyOfAES,
    }
  );
}

export async function decryptByAES(data: string, secretKeyOfAES: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/AESUtils/decryptByAES.worker', import.meta.url), { type: "module" }),
    {
      data,
      secretKeyOfAES,
    }
  );
}