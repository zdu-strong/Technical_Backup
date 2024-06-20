import axios from 'axios';
import qs from 'qs';
import { ServerAddress } from '@/common/Server/get_server_address'
import { GlobalUserInfo } from '@/common/Server/get_global_user_info';
import { handleErrorWhenNotSignInToSignIn } from '@/common/Server/handleErrorWhenNotSignInToSignin';

axios.defaults.baseURL = ServerAddress;

axios.defaults.paramsSerializer = {
  serialize(params: Record<string, any>) {
    return qs.stringify(
      params,
      {
        arrayFormat: 'repeat',
      }
    );
  }
}

axios.interceptors.response.use(undefined, async (error) => {
  if (typeof error?.response?.data === "object") {
    for (const objectKey in error.response.data) {
      error[objectKey] = error.response.data[objectKey];
    }
  }

  handleErrorWhenNotSignInToSignIn(error);

  throw error;
})

axios.interceptors.request.use((config) => {
  if (config.url?.startsWith("/") || config.url?.startsWith(ServerAddress + "/") || config.url === ServerAddress) {
    const accessToken = GlobalUserInfo.accessToken;
    if (accessToken) {
      config.headers!["Authorization"] = 'Bearer ' + accessToken;
    }
  }
  return config;
})
