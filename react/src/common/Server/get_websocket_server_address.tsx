import qs from 'qs';
import { ServerAddress } from '@/common/Server/get_server_address';
import { getAccessToken } from '@/common/Server/get_global_user_info'

export function getWebSocketServerAddress(url: string, params?: Record<string, any>) {
  let tempParams = {

  } as Record<string, any>;
  const accessToken = getAccessToken();
  if (accessToken) {
    tempParams.accessToken = accessToken;
  }
  if (params) {
    tempParams = {
      ...tempParams,
      ...params,
    };
  }
  const serverUrl = new URL(ServerAddress);
  let tempUrl = `${serverUrl.protocol.replace("http", "ws")}//${serverUrl.host}${url}`;
  if (Object.keys(tempParams).length > 0) {
    tempUrl = `${tempUrl}?${qs.stringify(tempParams)}`;
  }
  return tempUrl;
}