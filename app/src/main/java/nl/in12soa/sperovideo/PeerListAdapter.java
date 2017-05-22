package nl.in12soa.sperovideo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.in12soa.sperovideo.Models.Peer;

/**
 * Created by ahmadrahimi on 5/11/17.
 */

public class PeerListAdapter extends RecyclerView.Adapter<PeerListAdapter.PeerViewHolder> {

    private List<Peer> peerList;
    private RemoteActivity remoteActivity;
    public PeerListAdapter(List<Peer> peerList, RemoteActivity remoteActivity) {
        this.peerList = peerList;
        this.remoteActivity = remoteActivity;
    }

    public void addItem(Peer peer){
        if(!peerExists(peer)) {
            peerList.add(peer);
            this.notifyItemInserted(peerList.size() - 1);
        }
    }

    public boolean peerExists(Peer peer){
        for(Peer p : peerList){
            if(p.getDevice().deviceAddress.equals(peer.getDevice().deviceAddress)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    @Override
    public void onBindViewHolder(PeerViewHolder peerViewHolder, int i) {
        Peer peer = peerList.get(i);
        peerViewHolder.peername.setText(peer.getName());
        peerViewHolder.peertype.setText(peer.getType());
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.peer_layout, viewGroup, false);
        final int index = i;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Peer peer = peerList.get(index);
                remoteActivity.mReceiver.connect(peer.getDevice());
            }
        });
        return new PeerViewHolder(itemView);
    }

    public static class PeerViewHolder extends RecyclerView.ViewHolder {
        protected TextView peername;
        protected TextView peertype;

        public PeerViewHolder(View v) {
            super(v);
            peername =  (TextView) v.findViewById(R.id.tv_peername);
            peertype = (TextView)  v.findViewById(R.id.tv_peertype);
        }
    }

}
