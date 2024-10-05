import '@/common/Server/axios_config'
import { ClientAddress } from '@/common/Server/get_client_address'
import { GlobalUserInfo, getAccessToken, removeGlobalUserInfo, setGlobalUserInfo } from '@/common/Server/get_global_user_info'
import { ServerAddress } from '@/common/Server/get_server_address'
import { getWebSocketServerAddress } from '@/common/Server/get_websocket_server_address'
import { getResourceUrl } from '@/common/Server/get_resource_url'
import { getDownloadResourceUrl } from '@/common/Server/get_download_resource_url'

export { ClientAddress, GlobalUserInfo, ServerAddress, getAccessToken, getWebSocketServerAddress, removeGlobalUserInfo, setGlobalUserInfo, getResourceUrl, getDownloadResourceUrl }
