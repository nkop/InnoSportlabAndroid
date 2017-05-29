package nl.in12soa.sperovideo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.in12soa.sperovideo.Models.Video;
import nl.in12soa.sperovideo.R;

/**
 * Created by Wouter on 18-5-2017.
 */

public class VideoAdapter extends ArrayAdapter<Video> {

    private Context mContext;
    //Wouter check dit even, wordt wel ge-assigned maar niet gebruikt.
    private ArrayList<Video> videoList;

    public VideoAdapter(Context context, ArrayList<Video> videoList){
        super(context, 0, videoList);
        this.mContext = context;
        this.videoList = videoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Video video = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.overview_list_item, parent, false);
        }

//        String dateString = video.date.substring(0, 10);
        TextView videoText = (TextView) convertView.findViewById(R.id.video_item);
        videoText.setText((position + 1) + " - Video " + (position + 1));

        return convertView;
    }
}
