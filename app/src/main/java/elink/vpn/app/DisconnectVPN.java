
package elink.vpn.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;

public class DisconnectVPN extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    protected OpenVPNService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        showDisconnectDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private void showDisconnectDialog() {

//        new SweetAlertDialog(DisconnectVPN.this, SweetAlertDialog.NORMAL_TYPE)
//                .setTitleText("Are you sure?")
//                .setContentText(getString(R.string.msg_of_disconnect_vpn))
//                .setConfirmText("Ok")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProfileManager.setConntectedVpnProfileDisconnected(DisconnectVPN.this);
                if (mService != null && mService.getManagement() != null) {
                    mService.getManagement().stopVPN(false);
                }
                finish();
            }
        },300);



//                    }
//                })
//                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        finish();
//                        sDialog.dismissWithAnimation();
//                    }
//                })
//                .show();


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.title_cancel);
//            builder.setMessage(R.string.msg_of_disconnect_vpn);
//            builder.setNegativeButton(android.R.string.cancel, this);
//            builder.setPositiveButton(R.string.cancel_connection, this);
//            builder.setNeutralButton(R.string.reconnect, this);
//            builder.setOnCancelListener(this);
//            builder.show();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ProfileManager.setConntectedVpnProfileDisconnected(this);
            if (mService != null && mService.getManagement() != null) {
                mService.getManagement().stopVPN(false);
            }
        }/* else if (which == DialogInterface.BUTTON_NEUTRAL) {
            mService.getManagement().stopVPN(false);
            Intent intent = new Intent(this, OpenVPNService.class);
            intent.setAction(OpenVPNService.START_SERVICE);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }*/
        finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
