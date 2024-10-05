import { ClientAddress } from '@/common/Server/get_client_address';

const urlOfClientAddress = new URL(ClientAddress);
let ServerAddress = `${urlOfClientAddress.protocol}//${urlOfClientAddress.hostname}:8080`;

if (process.env.NODE_ENV === "development") {
  if (process.env.REACT_APP_SERVER_PORT) {
    ServerAddress = `${urlOfClientAddress.protocol}//${urlOfClientAddress.hostname}:${process.env.REACT_APP_SERVER_PORT}`;
  }
}

if (process.env.NODE_ENV === "production") {
  if (process.env.REACT_APP_SERVER_ADDRESS) {
    if (process.env.REACT_APP_SERVER_ADDRESS.endsWith("/")) {
      ServerAddress = process.env.REACT_APP_SERVER_ADDRESS.slice(0, process.env.REACT_APP_SERVER_ADDRESS.length - 1);
    } else {
      ServerAddress = process.env.REACT_APP_SERVER_ADDRESS;
    }
  }
}

export { ServerAddress }
