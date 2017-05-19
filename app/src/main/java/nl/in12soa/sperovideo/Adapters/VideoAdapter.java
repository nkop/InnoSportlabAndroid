package nl.in12soa.sperovideo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.in12soa.sperovideo.Model.Video;
import nl.in12soa.sperovideo.R;

/**
 * Created by Wouter on 18-5-2017.
 */

public class VideoAdapter extends ArrayAdapter<Video> {

    Context mContext;
    ArrayList<Video> videoList;

    public VideoAdapter(Context context, ArrayList<Video> videoList){
        super(context, 0, videoList);
        this.mContext = context;
        this.videoList = videoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Video video = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.overview_list_item, parent, false);
        }

        String dateString = video.date.substring(0, 10);
        TextView videoText = (TextView) convertView.findViewById(R.id.videoItem);
        videoText.setText((position + 1) + " - Video van " + dateString);

        return convertView;
    }
}
