package nl.in12soa.sperovideo.Controllers;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Ahmad on 3/1/2017.
 */

public class PeerListAdapter {
    public ArrayList<String> peerArray;
    public ArrayAdapter<String> peerAdapter;
    public PeerListAdapter(){
        peerArray = new ArrayList<>();
    }
    public void setPeerAdapter(ArrayAdapter<String> pla){
        peerAdapter = pla;
    }

    public ArrayList<String> getList(){
        return peerArray;
    }
}
