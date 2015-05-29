package com.acme.socket;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private Socket socket;

    private int port;
    private String server;

    private static final int MAX_FILE_SIZE = 1024;

    private byte[] array;
    boolean connected =false;
    private Thread soketThread;
    OutputStream ops;

    public SocketClient(int port, String server) {
        this.port = port;
        this.server = server;
        initialize();
    }

    private void initialize(){
        soketThread = new Thread(new ClientThread());
    }

    public void sendData() throws IOException {
        System.out.println("sendData - byte");
        System.out.println("new socket thread init");
        if(soketThread==null){
            initialize();
        }
        soketThread.start();
    }

    public void sendData(String path) throws IOException {
        System.out.println("sendData - path");
        array = readFile(new File(path));
        sendData();
    }

    public void sendData(byte[] data) throws IOException {
        System.out.println("sendData - array");
        array = data;
        sendData();
    }

    public void write(byte[] data) throws IOException {
        System.out.println("write data in array");
        array = data;
    }

    private void close() throws IOException {
        System.out.println("close socket client");
        if(socket.isConnected()){
            socket.close();
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("in client thread");
                socket = new Socket(server, port);

                if(socket!=null){
                    System.out.println("NOT NULL CLIENT SOCKET");
                } else {
                    System.out.println("NULL CLIENT SOCKET");
                }

                OutputStream ops = socket.getOutputStream();
                connected = true;
                System.out.println("SOCKET CLIENT START LISTEN FOR INPUT");
                while (connected){
                    if(array!=null && array.length>0){
                        System.out.println("Client write -> "+array.length);
                        ops.write(array);
                        array = null;
                        connected = false;
                    }
                }
                System.out.println("SOCKET CLIENT STOP LISTEN FOR INPUT");
                if(ops!=null){
                    ops.flush();
                    ops.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public byte[] readFile(File file) throws IOException {
        System.out.println("readFile");
        if ( file.length() > MAX_FILE_SIZE ) {
            System.err.println("Need split file");
        }


        byte []buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if ( ios.read(buffer) == -1 ) {
                throw new IOException("EOF reached while trying to read the whole file");
            }
        } finally {
            try {
                if ( ios != null )
                    ios.close();
            } catch ( IOException e) {
            }
        }

        return buffer;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Socket getSocket() {
        return socket;
    }

    public Thread getSoketThread() {
        return soketThread;
    }
}
