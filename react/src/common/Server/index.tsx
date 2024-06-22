import '@/common/Server/axios_config'
import { ClientAddress } from '@/common/Server/get_client_address'
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from '@/common/Server/get_global_user_info'
import { ServerAddress } from '@/common/Server/get_server_address'
import { getWebSocketServerAddress } from '@/common/Server/get_websocket_server_address'
import { getResourceUrl } from '@/common/Server/get_resource_url'
import { getDownloadResourceUrl } from '@/common/Server/get_download_resource_url'
import { handleErrorWhenNotSignInToSignIn, toSignIn } from '@/common/Server/handleErrorWhenNotSignInToSignin';

export { ClientAddress, GlobalUserInfo, ServerAddress, getWebSocketServerAddress, removeGlobalUserInfo, setGlobalUserInfo, getResourceUrl, getDownloadResourceUrl, handleErrorWhenNotSignInToSignIn, toSignIn }
