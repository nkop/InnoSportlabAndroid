package nl.in12soa.sperovideo.Services;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Ahmad on 3/10/2017.
 */

public class ClientService{

    private Context mContext;
    private static String host;
    private int port;
    private Socket socket;
    private String data;
    private Uri videoUri;
    public ClientService(Context ctxp, String hostp){
        mContext = ctxp;
        host = hostp;
        port = 8888;
    }

    public ClientService(Context ctxp){
        mContext = ctxp;
        port = 8888;
    }

    public static void setHost(String hostp){
        host = hostp;
    }
    public void sendData(String datap){
        data = datap;
        new DataSender().execute();
    }

    public void sendData(Uri videoUrip){
        videoUri = videoUrip;
        new VideoSender().execute();
    }



    private class DataSender extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void ...params) {
            byte buf[]  = new byte[1024];
            try {
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket = new Socket();
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 500);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            /**
             * Clean up any open sockets when done
             * transferring or if an exception occurred.
             */
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
            return null;
        }
    }

    private class VideoSender extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void ...params) {
            byte buf[]  = new byte[1024];
            int len;
            try {
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket = new Socket();
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 500);

                ContentResolver cr = mContext.getContentResolver();
                InputStream inputStream = null;
                inputStream = cr.openInputStream(videoUri);
                OutputStream outputStream = socket.getOutputStream();
                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            /**
             * Clean up any open sockets when done
             * transferring or if an exception occurred.
             */
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
            return null;
        }
    }
}
