package elink.vpn.app;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;

public class SendDataService extends Service {


    protected OpenVPNService mService;

    private Integer t_time = 0;

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


    private final LocalBinder mBinder = new LocalBinder();
    protected Handler handler;
    protected Toast mToast;

    public class LocalBinder extends Binder {
        public SendDataService getService() {
            return SendDataService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {


            Integer time = intent.getIntExtra("time", 2);

            t_time = time * 60 * 1000;
        } catch (Exception e) {
            e.printStackTrace();


        }


//        Log.e("the_time",extras.getBundle("time").toString());

//        Toast.makeText(getApplicationContext(),time+"heo",Toast.LENGTH_LONG).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                Log.e("kkllll", "jkl;ghjkghjklghj");


//                Intent new_i = new Intent(getApplicationContext(),MainActivity.class);
//                new_i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                new_i.putExtra("hard_disconnect","yes");
//                startActivity(new_i);

//                Intent disconnectVPN = new Intent(getApplicationContext(), DisconnectVPN2.class);
//                startActivity(disconnectVPN);
                d_now();

                //performe the deskred task
            }
        }, t_time);


        return Service.START_STICKY;
    }

    public void d_now() {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        ProfileManager.setConntectedVpnProfileDisconnected(getApplicationContext());
        if (mService != null) {
            if (mService.getManagement() != null) {
                mService.getManagement().stopVPN(false);
            }
//                setResult(-1);
        }

//        finish();
//            }
//        }, 2000);

    }

}