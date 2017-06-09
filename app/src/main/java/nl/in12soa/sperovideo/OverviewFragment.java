package nl.in12soa.sperovideo;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import nl.in12soa.sperovideo.Adapters.VideoAdapter;
import nl.in12soa.sperovideo.Models.Video;

public class OverviewFragment extends Fragment {

    OnItemSelectedListener listener;

    private ArrayList<Video> videoArray = new ArrayList<>();
    private File[] videoLocalArray;
    private VideoAdapter videoAdapter;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.overview_fragment, container, false);

        getVideos();

        videoArray = new ArrayList<>();
        if (videoLocalArray != null) {
            for (int i = 0; i < videoLocalArray.length; i++) {
                Video video = new Video("1", videoLocalArray[i].getAbsolutePath(), null, null);
                videoArray.add(video);
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "There are no videos yet.", Toast.LENGTH_LONG).show();
        }

        videoAdapter = new VideoAdapter(getActivity().getApplicationContext(), videoArray);
        ListView listView = (ListView) view.findViewById(R.id.video_list);
        videoAdapter.notifyDataSetChanged();
        listView.setAdapter(videoAdapter);

        AdapterView.OnItemClickListener mMessageClickedHandler = new
                AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        Video video = videoAdapter.getItem(position);
                        listener.onItemSelected(video);
                    }
                };

        listView.setOnItemClickListener(mMessageClickedHandler);

        return view;
    }


    public interface OnItemSelectedListener
    {
        void onItemSelected(Video video);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OnItemSelectedListener) getActivity();
    }

    private void getVideos() {
        try {
            String path = Environment.getExternalStorageDirectory() + "/" + getActivity().getApplicationContext().getPackageName();
            File dir = new File(path);
            if (!dir.isDirectory()) {
                dir.mkdir();
            }
            videoLocalArray = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isFile()) {
                        if (file.getName().equals(".nomedia"))
                            return false;
                        return checkFileExtension(file);
                    } else if (file.isDirectory()) {
                        return false;
                    } else {
                        return false;
                    }
                }
            });


        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean checkFileExtension( File fileName ) {
        String ext = getFileExtension(fileName);
        if ( ext == null) return false;
        try {
            if ( SupportedFileFormat.valueOf(ext.toUpperCase()) != null ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public String getFileExtension( File f ) {
        return getFileExtension( f.getName() );
    }

    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }

    public enum SupportedFileFormat {
        MP4("mp4");

        private String filesuffix;

        SupportedFileFormat( String filesuffix ) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }
}
