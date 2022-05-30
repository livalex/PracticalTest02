package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationThread extends Thread {
    private ServerThread serverThread = null;
    private Socket socket = null;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utils.getReader(socket);
            PrintWriter printWriter = Utils.getWriter(socket);
            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Waiting for parameters from" +
                    "client (city / information type!");

            String request = bufferedReader.readLine();
            if (request == null || request.isEmpty()) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error receiving request from client!");
                return;
            }

            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = null;

            HttpGet httpGet = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice/EUR.json");
            httpClient.getConnectionManager().getSchemeRegistry().register( new Scheme("https", SSLSocketFactory.getSocketFactory(), 443) );
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpGetEntity = httpGetResponse.getEntity();

            if (httpGetEntity != null)
                pageSourceCode = EntityUtils.toString(httpGetEntity);
            if (pageSourceCode == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error getting the time from the webservice!");
                return;
            } else {
                Log.i("[PracticalTest02]", pageSourceCode);
            }

            String time = new JSONObject(pageSourceCode).getString("time");
            String updated = new JSONObject(time).getString("updated");
            String bpi = new JSONObject(pageSourceCode).getString("bpi");
            String usdObj = new JSONObject(bpi).getString("USD");
            String eurObj = new JSONObject(bpi).getString("EUR");
            String usdRate = new JSONObject(usdObj).getString("rate");
            String eurRate = new JSONObject(eurObj).getString("rate");

            serverThread.setData("USD", usdRate);
            serverThread.setData("EUR", eurRate);

            if (request.equals("USD")) {
                printWriter.println(usdRate);
                printWriter.flush();
            } else if (request.equals("EUR")) {
                printWriter.println(eurRate);
                printWriter.flush();
            }

        } catch (IOException | JSONException ioException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }
    }
}
