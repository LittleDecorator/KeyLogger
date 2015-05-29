package com.acme.util;


import com.acme.socket.SocketClient;

public class SocketHandler {
    private static SocketClient socket;

    public static synchronized SocketClient getSocket(){
        return socket;
    }

    public static synchronized void setSocket(SocketClient socket){
        SocketHandler.socket = socket;
    }
}
