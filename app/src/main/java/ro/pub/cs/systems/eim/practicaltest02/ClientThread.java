package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private String address;
    private int port = 0;
    private Socket socket = null;
    private String key = "";
    private String value = "";
    private String informationType = "";
    private TextView textView = null;

    public ClientThread(String address, int port, String informationType, TextView textView) {
        this.address = address;
        this.port = port;
        this.key = key;
        this.value = value;
        this.informationType = informationType;
        this.textView = textView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utils.getReader(socket);
            PrintWriter printWriter = Utils.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("[PracticalTest02]", "[CLIENT THREAD] Buffered Reader " +
                        "/ Print Writer are null!");
                return;
            }
            String request = informationType;
            printWriter.println(request);
            printWriter.flush();

            String response;
            while ((response = bufferedReader.readLine()) != null) {
                String finalInformation = response;
                textView.post(() -> textView.setText(finalInformation));
            }

        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[CLIENT THREAD] An exception has occurred: " +
                    "" + ioException.getMessage());
            ioException.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[CLIENT THREAD] An exception has occurred: " +
                            "" + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }
    }
}
