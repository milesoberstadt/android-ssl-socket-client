package com.milesoberstadt.wifidetectiontuner;

import android.os.AsyncTask;

/**
 * This class handles the creation of the TCPClient and registers for progress (new data) from the server
 * Created by miles on 12/16/15.
 */
public class ConnectionManagerTask extends AsyncTask<String, String, TCPClient> {

    private MainActivity mainActivity;
    public TCPClient mTcpClient;

    public ConnectionManagerTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * This function is basically an initializer and creates the TCPClient in a separate thread
     * @param message Message to send upon connection to the server
     * @return
     */
    @Override
    protected TCPClient doInBackground(String... message) {

        //Create a TCPClient (the actual socket builder)
        mTcpClient = new TCPClient(mainActivity, MainActivity.SERVER_IP, MainActivity.SERVER_PORT,
            new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }
        );
        mTcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        // We got a full message from the server!
        // Add output from the server to our output log
        mainActivity.appendServerMessageToLog(values[0]);
    }
}
