package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {
    private Button serverStartButton = null;
    private Button getForecastButton = null;
    private EditText serverPortText = null;
    private EditText clientAddressText = null;
    private EditText clientPortText = null;
    private TextView weatherForecastTextView = null;
    private Spinner informationTypeSpinner = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ServerStartButtonCLickListener serverStartButtonCLickListener
            = new ServerStartButtonCLickListener();
    private class ServerStartButtonCLickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should" +
                        "be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e("[PracticalTest02]", "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private GetForecastButtonClickListener getForecastButtonClickListener
            = new GetForecastButtonClickListener();
    private class GetForecastButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressText.getText().toString();
            String clientPort = clientPortText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client" +
                        " (key / value / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            weatherForecastTextView.setText("");
            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort),
                    informationType, weatherForecastTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortText = (EditText) findViewById(R.id.serverPortText);
        clientAddressText = (EditText) findViewById(R.id.clientAddressText);
        clientPortText = (EditText) findViewById(R.id.clientPortText);
        serverStartButton = (Button) findViewById(R.id.serverStartButton);
        getForecastButton = (Button) findViewById(R.id.getForecastButton);
        weatherForecastTextView = (TextView) findViewById(R.id.responseTextView);
        informationTypeSpinner = (Spinner) findViewById(R.id.requestTypeSpinner);

        serverStartButton.setOnClickListener(serverStartButtonCLickListener);
        getForecastButton.setOnClickListener(getForecastButtonClickListener);
    }

    @Override
    protected void onDestroy() {
        Log.i("[PracticalTest02]", "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}