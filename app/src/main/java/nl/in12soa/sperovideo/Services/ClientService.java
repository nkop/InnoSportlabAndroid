package nl.in12soa.sperovideo.Services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import nl.in12soa.sperovideo.RemoteActivity;

/**
 * Created by Ahmad on 3/10/2017.
 */

public class ClientService{

    private Context mContext;
    private static String host;
    private int port;
    private Socket socket;
    private String data;

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

    private class DataSender extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void ...params) {
            try {
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket = new Socket();
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 1024);
                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data.getBytes());
                socket.shutdownOutput();
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + mContext.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".mp4");
                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                InputStream inputstream = socket.getInputStream();
                copyInputStreamToFile(inputstream, f);
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            /**
             * Clean up any open sockets when done
             * transferring or if an exception occurred.
             */
            return null;
        }
    }

    private void copyInputStreamToFile(InputStream in, File f) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                fileOutputStream.write(buf,0,len);
            }
            fileOutputStream.close();
            in.close();
            f.setReadable(true, false);
            f.setExecutable(true, false);
            ((RemoteActivity)mContext).playVideo(Uri.fromFile(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
