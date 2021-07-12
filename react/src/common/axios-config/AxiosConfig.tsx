import axios from 'axios';
import qs from 'qs';
import { ServerAddress } from '../Server'

axios.defaults.baseURL = ServerAddress;
axios.defaults.paramsSerializer = (params: any) => {
    return qs.stringify(
        params,
        {
            arrayFormat: 'repeat',
        }
    );
};
axios.interceptors.response.use(undefined, async (error) => {
    if (typeof error?.response?.data === "object") {
        for (const objectKey in error.response.data) {
            error[objectKey] = error.response.data[objectKey];
        }
    }
    throw error;
});