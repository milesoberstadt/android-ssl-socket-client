package com.milesoberstadt.wifidetectiontuner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final int SERVER_PORT = 5666;
    public static final String SERVER_IP = "192.168.1.23"; //"10.42.0.1"; // In case I have to use a hotspot

    private Button sendButton;
    public TextView outputText;
    private EditText inputText;

    private ConnectionManagerTask connectionManager;

    public static final String TAG = "TCP Client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize our control references
        sendButton = (Button)findViewById(R.id.btnOutput);
        outputText = (TextView)findViewById(R.id.editOutput);
        inputText = (EditText)findViewById(R.id.edit_send);

        // Connect to the server
        connectionManager = new ConnectionManagerTask(this);
        connectionManager.execute("thanks");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String textToSend = inputText.getText().toString();
            //Send the message to the server
            try{
                if (connectionManager.mTcpClient != null) {
                    if(connectionManager.mTcpClient.socket.isConnected())
                        connectionManager.mTcpClient.sendMessage(textToSend);
                }

                // Append the message we sent to our output log
                String previousOutput = outputText.getText().toString();
                previousOutput = textToSend + "\r\n" + previousOutput;
                outputText.setText(previousOutput);
                // Clear the input field
                inputText.setText("");
            }
            catch (Exception e) {
                Log.e(TAG, "Error sending: "+ e.getMessage());
            }
            }
        });
    }

    public void appendServerMessageToLog(String message){
        // Append this new message to our log TextView
        String outputLogText = outputText.getText().toString();
        outputLogText = "server: " + message + "\r\n" + outputLogText;
        outputText.setText(outputLogText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
