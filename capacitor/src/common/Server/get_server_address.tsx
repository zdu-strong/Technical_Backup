import { ClientAddress } from '@/common/Server/get_client_address';

const urlOfClientAddress = new URL(ClientAddress);
let ServerAddress = `${urlOfClientAddress.protocol}//${urlOfClientAddress.hostname}:8080`;

if (process.env.NODE_ENV === "development") {
  if (process.env.REACT_APP_SERVER_PORT) {
    ServerAddress = `${urlOfClientAddress.protocol}//${urlOfClientAddress.hostname}:${process.env.REACT_APP_SERVER_PORT}`;
  }
}

export { ServerAddress }
