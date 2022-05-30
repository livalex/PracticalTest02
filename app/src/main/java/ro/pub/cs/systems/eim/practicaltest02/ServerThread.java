package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    private int port = 0;
    private ServerSocket serverSocket = null;
    private HashMap<String, String> data = null;

    public int getPort() {
        return port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public synchronized void setData(String city, String weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public ServerThread(int port) {
        this.port = port;
        this.data = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("[PracticalTest02]", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("[PracticalTest02]", "[SERVER THREAD] A connection request" +
                        "was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e("[PracticalTest02]", "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
            clientProtocolException.printStackTrace();
        } catch (IOException iOException) {
            Log.e("[PracticalTest02]", "[SERVER THREAD] An exception has occurred: " + iOException.getMessage());
            iOException.printStackTrace();
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("[PracticalTest02]", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                ioException.printStackTrace();
            }
        }
    }
}
