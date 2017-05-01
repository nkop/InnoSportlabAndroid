package nl.in12soa.sperovideo.Services;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import arrcreations.prototype1_project.AnalyseActivity;
import nl.in12soa.sperovideo.AnalyseActivity;

/**
 * Created by Ahmad on 3/10/2017.
 */

public class DataServiceAsyncTask extends AsyncTask<Void, Void, Void> {

    private AnalyseActivity mActivity;
    ServerSocket serverSocket;
    public DataServiceAsyncTask(AnalyseActivity actp){
        mActivity = actp;
        try {
            serverSocket = new ServerSocket(8888);
        }catch (IOException e){
            mActivity.tvlogger.setText(e.getMessage());
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(true) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */

                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */
            /* GEBRUIKEN VOOR TEXT ONTVANGEN */
//                BufferedInputStream in = new BufferedInputStream(client.getInputStream());
//                byte[] contents = new byte[1024];
//
//                int bytesRead = 0;
//                String strFileContents = "";
//                while((bytesRead = in.read(contents)) != -1) {
//                    strFileContents += new String(contents, 0, bytesRead);
//                }
//                final String cts = strFileContents;
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mActivity.tv4.setText(cts);
//                        }
//                    });
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + mActivity.getApplicationContext().getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".mp4");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                InputStream inputstream = client.getInputStream();
                copyInputStreamToFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.playVideo(f.getPath());
                        new DataServiceAsyncTask(mActivity).execute();
                    }
                });

            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

    public void copyInputStreamToFile( InputStream in, FileOutputStream fileOutputStream) {
        try {
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                fileOutputStream.write(buf,0,len);
            }
            fileOutputStream.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
