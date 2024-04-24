import { ServerAddress } from '@/common/Server/get_server_address'

export function getResourceUrl(url: string) {
  const urlAbsolute = new URL(url, url.startsWith("/") ? ServerAddress : undefined);
  if (urlAbsolute.pathname.startsWith("/download/resource")) {
    return new URL(urlAbsolute.pathname.slice(9), ServerAddress).toString();
  } else if (urlAbsolute.pathname.startsWith("/resource")) {
    return new URL(`${urlAbsolute.pathname}`, ServerAddress).toString();
  }
  throw new Error("Incorrect url");
}