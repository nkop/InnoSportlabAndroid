package nl.in12soa.sperovideo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import java.net.InetAddress;

import nl.in12soa.sperovideo.Services.ClientService;

public class RemoteActivitybak extends RemoteActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        clientService = new ClientService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_ID)) {
            byte[] serial = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String serialstring = "";

            for (int i = 0; i < serial.length; i++) {
                String x = Integer.toHexString(((int) serial[i] & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }
                serialstring += x + ' ';
            }
            clientService.sendData("{ \"command\" : \"start_camera\", \"parameters\" : { \"framerate\" : 30, \"resolution_y\" : 640, \"resolution_x\" : 480, \"duration\" : 5000 } }");
        }
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, RemoteActivitybak.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        InetAddress inetAddress = info.groupOwnerAddress;
        ClientService.setHost(inetAddress.getHostAddress());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
