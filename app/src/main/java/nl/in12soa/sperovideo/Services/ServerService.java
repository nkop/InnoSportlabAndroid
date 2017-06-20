package nl.in12soa.sperovideo.Services;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import nl.in12soa.sperovideo.CameraActivity;
import nl.in12soa.sperovideo.CameraViewActivity;

/**
 * Created by Ahmad on 3/10/2017.
 */

public class ServerService extends AsyncTask<Void, Void, Void> {

    private CameraActivity mActivity;
    private ServerSocket serverSocket;
    private static final int CAMERA_RESULT = 5;
    private HashMap<String, Socket> clientmap;
    public static Uri VIDEOURI;
    public ServerService(CameraActivity actp){
        mActivity = actp;
        clientmap = new HashMap<>();
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(8888));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(true) {
            byte buf[]  = new byte[1024];
            int len;
            try {

                final Socket client = serverSocket.accept();
                client.setReuseAddress(true);
                clientmap.put(client.getInetAddress().getHostAddress(), client);
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message = "";



                while ((line = in.readLine()) != null) {
                    message += line;
                }
                final JSONObject command = new JSONObject(message);
                if(command.getString("command").equals("start_camera")) {

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mActivity, CameraViewActivity.class);
                            try {
                                intent.putExtra("resolution_y",command.getJSONObject("parameters").getInt("resolution_y"));
                                intent.putExtra("resolution_x",command.getJSONObject("parameters").getInt("resolution_x"));
                                intent.putExtra("framerate", command.getJSONObject("parameters").getInt("framerate"));
                                intent.putExtra("duration", command.getJSONObject("parameters").getInt("duration"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mActivity.startActivityForResult(intent, CAMERA_RESULT);

                        }
                    });
                    while(VIDEOURI == null){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ContentResolver cr = mActivity.getContentResolver();
                    InputStream inputStream = null;
                    inputStream = cr.openInputStream(VIDEOURI);
                    OutputStream outputStream = client.getOutputStream();
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    outputStream.close();
//                    serverSocket.close();
                    VIDEOURI = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
