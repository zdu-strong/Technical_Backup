export let ServerAddress = window.location.protocol + "//" + window.location.hostname + ":" + 8080;

if(process.env.NODE_ENV === "development"){
    if(process.env.REACT_APP_SERVER_PORT){
        ServerAddress = window.location.protocol + "//" + window.location.hostname + ":" + process.env.REACT_APP_SERVER_PORT;
    }
}

const serverUrl = new URL(ServerAddress);
export const WebSocketServerAddress = `${serverUrl.protocol.replace("http", "ws")}//${serverUrl.host}`;