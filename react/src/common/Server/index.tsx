import { ClientAddress } from '@/common/Server/get_client_address'
import { ServerAddress } from '@/common/Server/get_server_address'
import { getWebSocketServerAddress } from '@/common/Server/get_websocket_server_address'
import '@/common/Server/axios_config'
import { GlobalUserInfo, setGlobalUserInfo, removeGlobalUserInfo, getAccessToken } from '@/common/Server/get_global_user_info'

export { ClientAddress }
export { ServerAddress }
export { getWebSocketServerAddress }
export { GlobalUserInfo, setGlobalUserInfo, removeGlobalUserInfo, getAccessToken }
