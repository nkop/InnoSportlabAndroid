package nl.in12soa.sperovideo.Services;
import android.content.Intent;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import nl.in12soa.sperovideo.CameraActivity;
import nl.in12soa.sperovideo.CameraViewActivity;

/**
 * Created by Ahmad on 3/10/2017.
 */

public class DataService extends AsyncTask<Void, Void, Void> {

    private CameraActivity mActivity;
    ServerSocket serverSocket;
    private static final int CAMERA_RESULT = 5;
    public DataService(CameraActivity actp){
        mActivity = actp;
        try {
            serverSocket = new ServerSocket(8888);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(true) {
            try {

                Socket client = serverSocket.accept();

                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                boolean end = false;
                String message = "";
                while ((line = in.readLine()) != null) {
                    message += line;
                }

                if(message.toLowerCase().equals("command_start_camera")) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mActivity, CameraViewActivity.class);
                            mActivity.startActivityForResult(intent, CAMERA_RESULT);
                        }
                    });
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

}
