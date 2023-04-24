package elink.vpn.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import de.blinkt.openvpn.core.VpnStatus;

public class VpnStateListener extends BroadcastReceiver {
    public static final String LISTENER = "de.blinkt.openvpn.VPN_STATUS";
    //public static final String STATE_CONNECTING   = "elink.vpn.app.connecting";
    //public static final String STATE_DISCONNECTED = "elink.vpn.app.disconnected";
    //public static final String STATE_PAUSED       = "elink.vpn.app.paused";
    private final Context context;
    private final VpnState listener;

    public VpnStateListener(Context context, VpnState listener) {
        this.context = context;
        this.listener = listener;
    }

    public void listen() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LISTENER);
        context.registerReceiver(this, intentFilter);

    }

    public void stopListening() {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (listener == null) {
            return;
        }
        VpnStatus.ConnectionStatus status = VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status"));
        switch (status) {
            case LEVEL_START:
            case LEVEL_CONNECTING_SERVER_REPLIED:
            case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
            case LEVEL_WAITING_FOR_USER_INPUT:
                listener.stateConnecting();
                break;
            case LEVEL_CONNECTED:
                listener.stateConnected();
                break;
            case LEVEL_NOTCONNECTED:
            case LEVEL_NONETWORK:
                listener.stateDisconnected();
                break;
            case LEVEL_VPNPAUSED:
                listener.statePaused();
                break;

            default:

        }
    }

    public interface VpnState {
        void stateConnecting();

        void stateConnected();

        void stateDisconnected();

        void statePaused();
    }
}
