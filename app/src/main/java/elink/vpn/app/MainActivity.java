package elink.vpn.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.core.LogItem;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;
import elink.vpn.app.util.CustomRequest;
import elink.vpn.app.util.LoadingService;
import elink.vpn.app.util.Vpn;
import elink.vpn.app.util.VpnStateListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements VpnStateListener.VpnState {

    @BindView(R.id.city_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    View progressBar;

    List<Vpn> data;

    public Timer t;
    String[] string;
    private SweetAlertDialog pDialog;
    public String hard_disconnect = "";
    public boolean listen_to_wait_click = false;
    public boolean shifted_to_popup = true;
    public boolean listen_to_wait_click_popup = false;
    public boolean do_call_connect = false;

//    private InterstitialAd mInterstitialAd;
//    AdRequest adRequest;


    public Vpn myvpn;

    private VpnStateListener vpnStateListener;
    private ProfileManager profileManager;

    private SharedPreferences sharedPref;
    public SharedPreferences prefs;
    public int old_count = 0;
    private VpnProfile vpnProfile;

    final Handler handler = new Handler();
    Runnable myRunnable;
    public boolean adsReady = false;


    private static final int RC_DISCONNECT = 100;
    private static final int RC_DISCONNECT_LOGOUT = 1000;
    public boolean popup_loaded = false;

    @BindView(R.id.slidinguparrow)
    ImageView slidinguparrow;

    private BottomNavigationView mNavBottomView;
    private Menu mNavMenu;

//    public AdView mAdView;

    @BindView(R.id.account_mode)
    TextView account_mode;

    @BindView(R.id.connect_tv)
    TextView mode_connected;

//    @BindView(R.id.time_connected)
//    TextView time_connected;

    @BindView(R.id.cptv)
    TextView connectedip;


    @BindView(R.id.connectbtn)
    ImageButton connectBtn;

//    @BindView(R.id.connect_tv)
//    TextView connect_tv;

    @BindView(R.id.currentiptv)
    TextView currentip;

    @BindView(R.id.speedlayout)
    LinearLayout speedLayout;

    public static TextView uploadtv;

    public static TextView downloadtv;

    @BindView(R.id.account_expiry)
    TextView account_expiry;


//    @BindView(R.id.disconnect_btn)
//    FloatingActionButton disconnect_btn;

//    @BindView(R.id.rate_us)
//    FloatingActionButton rate_us;

//    @BindView(R.id.load_me)
//    FloatingActionButton load_me;

    private boolean paused;

    @BindView(R.id.slideView)
    LinearLayout linearLayout;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    VpnAdapter adapter;
    //    TextView logoutText;
    private Disposable disposable;

    @BindView(R.id.settingsbtn)
    ImageButton settingsBtn;

    @BindView(R.id.ipView)
    TextView ipViewLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


//        Appodeal.setBannerViewId(R.id.appodealBannerView);
//
//        String appodeal_key_ = "XXXXXXXXXXXXXXXXXXXX";// the key
//        Appodeal.setTesting(false); // live or testing
//        Appodeal.initialize(this, appodeal_key_, Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.BANNER_BOTTOM | Appodeal.NATIVE, new ApdInitializationCallback() {
//            @Override public void onInitializationFinished(@Nullable List<ApdInitializationError> list) {
//                //Appodeal initialization finished
//
//                Log.e("Appodeal:", " all good now");
//
                loadBannerAd();
//            }
//        });

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


//        profileMode = (TextView) findViewById(R.id.mode_of_user);
//        logoutText = (TextView) findViewById(R.id.mode_of_user3);
        initpDialog();

        uploadtv = findViewById(R.id.upspeedtv);
        downloadtv = findViewById(R.id.dwspeedtv);

        Intent intent = getIntent();
        String hard_disconnect_i = intent.getStringExtra("hard_disconnect");
        if (hard_disconnect_i != null) {
            if (hard_disconnect_i.equals("yes")) {
                hard_disconnect = "yes";
            } else {
                hard_disconnect = "no";
            }
        } else {
            hard_disconnect = "no";
        }


        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(5)
                .ratingBarColor(R.color.colorPrimary).build();
        ratingDialog.show();

        sharedPref = this.getSharedPreferences("dashboard", Context.MODE_PRIVATE);
        sharedPref.edit().putInt("int_int", 0).apply();
        prefs = this.getSharedPreferences(
                "elink.vpn.app", Context.MODE_PRIVATE);

        ButterKnife.bind(this);

        connectBtn.setBackgroundResource(R.drawable.disconnected);
        speedLayout.setVisibility(View.GONE);
        currentip.setVisibility(View.GONE);
        connectedip.setVisibility(View.GONE);
        uploadtv.setVisibility(View.GONE);
        downloadtv.setVisibility(View.GONE);

        slidingUpPanelLayout.stopNestedScroll();
        slidingUpPanelLayout.setNestedScrollingEnabled(true);
        slidingUpPanelLayout.setVerticalScrollBarEnabled(true);
        slidingUpPanelLayout.setVerticalFadingEdgeEnabled(true);
        slidingUpPanelLayout.setScrollableView(findViewById(R.id.city_recyclerview));


        slidinguparrow.setBackgroundResource(R.drawable.ic_action_up);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState== SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidinguparrow.setBackgroundResource(R.drawable.ic_action_down);
                }else{
                    slidinguparrow.setBackgroundResource(R.drawable.ic_action_up);
                }
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

//        logoutText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                if (VpnStatus.isVPNActive()) {
//                    Intent disconnectVPN = new Intent(MainActivity.this, DisconnectVPN.class);
//                    startActivityForResult(disconnectVPN, RC_DISCONNECT_LOGOUT);
//                } else {
//
//
//                    logout_now();
//                }
//            }
//        });


//        FloatingActionButton fab = findViewById(R.id.share_us);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT,
//                        "Hey check out Trejj VPN at: https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName());
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
//            }
//        });
//
//        FloatingActionButton fab2 = findViewById(R.id.email_us);
//        fab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i = new Intent(MainActivity.this, ContactActivity.class);
//                startActivity(i);
//
////                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "admin@impresslabs.tk"));
////                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
////                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
////
////                startActivity(Intent.createChooser(emailIntent, "Email Us"));
//            }
//        });
//
//        FloatingActionButton fab3 = findViewById(R.id.refresh_me);
//        fab3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onStart();
//            }
//        });

        if (!ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {
//            load_me.hide();
        } else {
//            load_me.show();
        }


//        load_me.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, BuyPremium.class);
//                startActivity(i);
//            }
//        });


//        Log.i("ADSSSS","Ads initilized 1");
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                adsReady=true;
//                Log.i("ADSSSS","Ads initilized");
//                try{
//
//                    if(ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")){
//                        mAdView = findViewById(R.id.adView);
//                        adRequest = new AdRequest.Builder().build();
//                        mAdView.loadAd(adRequest);
//                    }
//
//
//                }catch (Exception e){
//                    Log.e("ADSSSS error",e.getMessage());
//                }
//
//            }
//        });



    }

    public void loadBannerAd(){


        if(ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {
//            Appodeal.show(this, Appodeal.BANNER_BOTTOM); // Display banner at the bottom of the screen
//            Appodeal.show(this, Appodeal.BANNER_TOP);    // Display banner at the top of the screen
//            Appodeal.show(this, Appodeal.BANNER_LEFT);   // Display banner at the left of the screen
//            Appodeal.show(this, Appodeal.BANNER_RIGHT);
//
//
//            Appodeal.show(this, Appodeal.BANNER_VIEW);

            getIP();

        }
    }



    public void getIP(){

        Log.e("ipresponse","getting IP now ---------------------------------------------------");

        String url = "https://api.ipify.org/?format=json";













        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {
                            String ip = response.getString("ip");

                            Log.e("ipresponse","ip:"+ip+" ---------------------------------------------------");
                            ipViewLayout.setText("IP: "+ip);
                            ipViewLayout.setVisibility(View.VISIBLE);



                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
            }
        });

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);
        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    protected void onStart() {

        vpnStateListener = new VpnStateListener(this, this);

//        if (vpnProfile != null) {
        vpnStateListener.listen();
//        }

        try {
            Log.e("LastConnect","Fired");
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            disposable = LoadingService.getInstance()
                    .loadVpn()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showList, this::onError);
            super.onStart();

            Log.e("LastConnect","2");

//        if(hard_disconnect.equals("yes")) {
//            Intent disconnectVPN = new Intent(MainActivity.this, DisconnectVPN2.class);
//            startActivityForResult(disconnectVPN, RC_DISCONNECT);
//
//
//            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//            builder.setMessage(ICSOpenVPNApplication.getInstance().getFree_time_msg_after())
//                    .setPositiveButton("Upgrade Now", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            dialogInterface.dismiss();
//
//                            Intent i__i = new Intent(MainActivity.this, BuyPremium.class);
//                            startActivity(i__i);
//
//                        }
//                    })
//                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            dialogInterface.dismiss();
//
//
//                        }
//                    })
//                    .show();
//            }


            if (ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {

                if (ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {


                } else {
                }

                if (!ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {
//                        load_me.hide();
                } else {
//                        load_me.show();
                }
            }


            if (VpnStatus.isVPNActive()) {

                Log.e("LastConnect","3");

                Log.e("ipresponse", " i was tried to be called from here -----------------------------------------------------------------");


                speedLayout.setVisibility(View.VISIBLE);
                connectedip.setVisibility(View.VISIBLE);
                currentip.setVisibility(View.VISIBLE);
                uploadtv.setVisibility(View.VISIBLE);
                downloadtv.setVisibility(View.VISIBLE);


                connectBtn.setBackgroundResource(R.drawable.connected);
//            disconnect_btn.setEnabled(true);

//            VpnStatus.

                LogItem[] i = VpnStatus.getlogbuffer();

                Log.e("FInally", "I come hererere");
                SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                Long time__ = i[0].getLogtime();

                t = new Timer();
//            VpnStatus.getLastConnectedVPNProfile()

                String last_connected = VpnStatus.getLastConnectedVPNProfile();

                Log.e("LastConnect","3");
                Log.e("LastConnect",last_connected);

                String connected_title = ICSOpenVPNApplication.getInstance().getCurrent_connected();

                connectedip.setText(connected_title);

                Log.e("Last connected", last_connected);
                // Toast.makeText(this,"Last :"+last_connected, Toast.LENGTH_LONG).show();
//                ICSOpenVPNApplication.getInstance().setCurrent_connected(last_connected);


//            TimerTask tt = new TimerTask() {
//                @Override
//                public void run() {
//
//                    Long x = System.currentTimeMillis();
//                    String time = timeformat.format(new Date(time__));
//
//                    Long final__ = x - time__;
//                    String t_t_prit = getDurationBreakdown(final__);
//
//
//                    Log.e("Time of msg",  t_t_prit+"--A Kiss every 5 seconds");
//
//                    set_secs(t_t_prit);
//                };
//            };
//            t.schedule(tt,1000,1000);
//            Timer.class.can

                String connected_name = prefs.getString("last_con", "Successfully!");
                if (connected_name.equals("")) {
                    connected_name = "Successfully!";
                }
                set_secs("Connected");

//            Log.e("this time", time+ "--" );

//                disconnect_btn.show();

//            mode_connected.setText("Connected");

//            disconnect_btn.setVisibility(View.VISIBLE);
//                disconnect_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
////                    disconnect_btn.setEnabled(false);
////                    disconnect_btn.setVisibility(View.GONE);
////                    disconnect_btn.hide();
//
//                        Intent disconnectVPN = new Intent(MainActivity.this, DisconnectVPN.class);
//                        startActivityForResult(disconnectVPN, RC_DISCONNECT);
//
//
//                    }
//                });
            } else {
                set_secs("Disconnected");
//                disconnect_btn.hide();
            }


            if (!ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {
                account_mode.setText("Account: " + ICSOpenVPNApplication.getInstance().getAccount_mode());
                account_expiry.setText("Expiry: " + ICSOpenVPNApplication.getInstance().getExpires_at());
            } else {
                account_expiry.setText("Expiry: --:--:--");
            }


            old_count = sharedPref.getInt("int_int", 0);


            if (old_count > 0) {
//            Toast.makeText(MainActivity.this,"shown"+ old_count , Toast.LENGTH_SHORT).show();
                old_count = 0;
                sharedPref.edit().putInt("int_int", (old_count)).apply();
                if (ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {


                }
            } else {
//            Toast.makeText(MainActivity.this,"small"+ old_count , Toast.LENGTH_SHORT).show();
            }


//                rate_us.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//                        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
//                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                        // To count with Play market backstack, After pressing back button,
//                        // to taken back to our application, we need to add following flags to intent.
//                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                        try {
//                            startActivity(goToMarket);
//                        } catch (ActivityNotFoundException e) {
//                            startActivity(new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
//                        }
//
//
//                    }
//                });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
//        Log.w(TAG, "App destroyed");
//        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    public void set_secs(String secs) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mode_connected.setText(secs);


                // Stuff that updates the UI

            }
        });

    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
//        sb.append(days);
//        sb.append(" Days ");
        String seconds_s = String.valueOf(seconds);
        if (seconds_s.length() == 1) {
            seconds_s = "0" + seconds_s;
        }

        String hours_s = String.valueOf(hours);
        if (hours_s.length() == 1) {
            hours_s = "0" + hours_s;
        }

        String minutes_s = String.valueOf(minutes);
        if (minutes_s.length() == 1) {
            minutes_s = "0" + minutes_s;
        }


        sb.append(hours_s);
        sb.append(":");
        sb.append(minutes_s);
        sb.append(":");
        sb.append(seconds_s);
//        sb.append(":");

        return (sb.toString());
    }

    private void onError(Throwable error) {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setContentText(error.getMessage())
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void showList(@NonNull List<Vpn> data) {
//        Log.e("list is",data.toString());
        this.data = data;
        adapter = new VpnAdapter(data, this::loadProfile);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(this, 0));
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setVisibility(View.VISIBLE);
        hidepDialog();


        //Connect and Disconnect Button
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start vpn
                if (!VpnStatus.isVPNActive()) {

                    Random r = new Random();
                    int n=0;
                    for (int i = 0; i < data.size(); i++) {
                        Vpn vpn = data.get(i);
                        if(vpn.getPremium()!=1 && vpn.getVip()!=1) {
                            n++;
                        }
                    }

                    string = new String[n];
                    int a =0;
                    for (int i = 0; i < data.size(); i++) {
                        Vpn vpn = data.get(i);
                        if(vpn.getPremium()!=1 && vpn.getVip()!=1) {
                            string[a] = vpn.getName();
                            a++;
                        }
                    }

                    int vpnstr = r.nextInt(string.length);
                    String vpnProfile = string[vpnstr];
                    for(int i=0;i<data.size();i++){
                        Vpn vpn = data.get(i);
                        if(vpn.getName().equals(vpnProfile)){
                            connectVPN(vpn);
                            break;
                        }
                    }

                }
                else if (VpnStatus.isVPNActive()) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText(getString(R.string.msg_of_disconnect_vpn))
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    sDialog.dismissWithAnimation();

                                    if(ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")){
                                        showInterPopupAdAppodealDisconnect(true);
                                    }
                                    else{
                                        reallyDisconnectNow();
                                    }

//                                    Intent disconnectVPN = new Intent(MainActivity.this, DisconnectVPN.class);
//                                    startActivityForResult(disconnectVPN, RC_DISCONNECT);




                                }
                            })
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }

            }

        });
    }

    void reallyDisconnectNow(){
        Intent disconnectVPN = new Intent(MainActivity.this, DisconnectVPN.class);
        startActivityForResult(disconnectVPN, RC_DISCONNECT);
    }

    private void connectVPN(Vpn vpn){

        Log.e("adPath","path -1 wrong");

        if (vpn.getVip() == 0 && vpn.getPremium() == 0 && ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {

            Log.e("adPath","path -22 wrong");




            // account mode is free
            // 2. Confirmation message
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setContentText(ICSOpenVPNApplication.getInstance().getFree_time_msg())
                    .setCancelButton("Upgrade",new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                            Intent i__i = new Intent(MainActivity.this, BuyPremium.class);
                            startActivity(i__i);
                        }
                    })
                    .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                            Log.e("adPath","path -3 wrong");

                            if (vpn.getPremium() == 1 && ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {

                                Log.e("adPath","path -4 wrong");


                                Intent iii = new Intent(MainActivity.this, BuyPremium.class);
                                startActivity(iii);
                            } else if (vpn.getVip() == 1 && !ICSOpenVPNApplication.getInstance().getAccount_mode().equals("VIP")) {
                                Log.e("adPath","path -5 wrong");
                                Intent iii = new Intent(MainActivity.this, BuyPremium.class);
                                startActivity(iii);
                            } else {

                                //                                Toast.makeText(getApplicationContext(),ICSOpenVPNApplication.getInstance().getFree_time()+"t",Toast.LENGTH_LONG).show();


                                myvpn = vpn;
                                Log.e("adPath","path -6 wrong");

//                                showInterPopupAd(vpn,true);
                                showInterPopupAdAppodeal(vpn,true);




                                //                                start_vpn_now_now(vpn);
                            }
                        }
                    })
                    .show();
//            new AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle)
//                    .setMessage(ICSOpenVPNApplication.getInstance().getFree_time_msg())
//                    .setPositiveButton("Upgrade Now", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            dialogInterface.dismiss();
//
//
//                        }
//                    })
//                    .setNegativeButton("Continue for free", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            dialogInterface.dismiss();
//
//
//                        }
//                    })
//                    .show();
        } else {
            Log.e("adPath","path 0 wrong");
            if (vpn.getPremium() == 1 && ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {


                Intent iii = new Intent(MainActivity.this, BuyPremium.class);
                startActivity(iii);
            } else if (vpn.getVip() == 1 && !ICSOpenVPNApplication.getInstance().getAccount_mode().equals("VIP")) {
                Intent iii = new Intent(MainActivity.this, BuyPremium.class);
                startActivity(iii);
            } else {
                start_vpn_now_now(vpn);
            }
        }
    }

    void showInterPopupAdAppodeal(Vpn vpn, boolean holdConnectionUntilAdDone){

        Log.e("adPath","path a");
        if(!holdConnectionUntilAdDone){
            do_tell_server_connects(vpn);
        }

        showpDialog();

//        boolean isLoaded = Appodeal.isLoaded(Appodeal.INTERSTITIAL);

//        if(isLoaded){

//            Appodeal.show(this, Appodeal.INTERSTITIAL);
            do_tell_server_connects(vpn);
            hidepDialog();
//        }else {
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    boolean isLoaded = Appodeal.isLoaded(Appodeal.INTERSTITIAL);
//
//                    if (isLoaded) {
//                        Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
//                        do_tell_server_connects(vpn);
//                        hidepDialog();
//                    }else{
//                        do_tell_server_connects(vpn);
//                        hidepDialog();
//                    }
//                }
//            }, 2000);
//        }

    }

    void showInterPopupAdAppodealDisconnect( boolean holdConnectionUntilAdDone){

        Log.e("adPath","path a");
        if(!holdConnectionUntilAdDone){
            reallyDisconnectNow();
        }

        showpDialog();

//        boolean isLoaded = Appodeal.isLoaded(Appodeal.INTERSTITIAL);

//        if(isLoaded){

//            Appodeal.show(this, Appodeal.INTERSTITIAL);
            reallyDisconnectNow();
            hidepDialog();
//        }else {
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    boolean isLoaded = Appodeal.isLoaded(Appodeal.INTERSTITIAL);
//
//                    if (isLoaded) {
//                        Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
//                        reallyDisconnectNow();
//                        hidepDialog();
//                    }else{
//                        reallyDisconnectNow();
//                        hidepDialog();
//                    }
//                }
//            }, 2000);
//        }

    }

    void showInterPopupAd(Vpn vpn, boolean holdConnectionUntilAdDone){
        Log.e("adPath","path a");
        if(!holdConnectionUntilAdDone){
            do_tell_server_connects(vpn);
        }


        showpDialog();
//        InterstitialAd.load(this,"ca-app-pub-2818856964974442/8664949734", adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        hidepDialog();
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
//                        Log.i("popAd", "onAdLoaded");
//
//                        if (mInterstitialAd != null) {
//                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
//                                @Override
//                                public void onAdDismissedFullScreenContent() {
//                                    // Called when fullscreen content is dismissed.
//                                    Log.d("TAG", "The ad was dismissed.");
//
//                                    if(holdConnectionUntilAdDone){
                                        do_tell_server_connects(vpn);
//                                    }
//                                }
//
//                                @Override
//                                public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                    // Called when fullscreen content failed to show.
//                                    Log.d("TAG", "The ad failed to show.");
//                                    if(holdConnectionUntilAdDone){
//                                        do_tell_server_connects(vpn);
//                                    }
//                                }
//
//                                @Override
//                                public void onAdShowedFullScreenContent() {
//                                    // Called when fullscreen content is shown.
//                                    // Make sure to set your reference to null so you don't
//                                    // show it a second time.
//                                    mInterstitialAd = null;
//                                    Log.d("TAG", "The ad was shown.");
//                                }
//                            });
//
//                            mInterstitialAd.show(MainActivity.this);
//                        } else {
//                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
//                            if(holdConnectionUntilAdDone){
//                                do_tell_server_connects(vpn);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        hidepDialog();
//                        // Handle the error
//                        Log.i("popAd", loadAdError.getMessage());
//                        mInterstitialAd = null;
//                        if(holdConnectionUntilAdDone){
//                            do_tell_server_connects(vpn);
//                        }
//
//                    }
//
//                });
    }

//    void showInterPopupAdDisconnect(boolean holdConnectionUntilAdDone){
//        Log.e("adPath","path a");
////        if(!holdConnectionUntilAdDone){
//            reallyDisconnectNow();
////        }
//
//
//        showpDialog();
//        InterstitialAd.load(this,"ca-app-pub-2818856964974442/1381904824", adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        hidepDialog();
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
//                        Log.i("popAd", "onAdLoaded");
//
//                        if (mInterstitialAd != null) {
//                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
//                                @Override
//                                public void onAdDismissedFullScreenContent() {
//                                    // Called when fullscreen content is dismissed.
//                                    Log.d("TAG", "The ad was dismissed.");
//
//                                    if(holdConnectionUntilAdDone){
//                                        reallyDisconnectNow();
//                                    }
//                                }
//
//                                @Override
//                                public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                    // Called when fullscreen content failed to show.
//                                    Log.d("TAG", "The ad failed to show.");
//                                    if(holdConnectionUntilAdDone){
//                                        reallyDisconnectNow();
//                                    }
//                                }
//
//                                @Override
//                                public void onAdShowedFullScreenContent() {
//                                    // Called when fullscreen content is shown.
//                                    // Make sure to set your reference to null so you don't
//                                    // show it a second time.
//                                    mInterstitialAd = null;
//                                    Log.d("TAG", "The ad was shown.");
//                                }
//                            });
//
//                            mInterstitialAd.show(MainActivity.this);
//                        } else {
//                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
//                            if(holdConnectionUntilAdDone){
//                                reallyDisconnectNow();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        hidepDialog();
//                        // Handle the error
//                        Log.i("popAd", loadAdError.getMessage());
//                        mInterstitialAd = null;
//                        if(holdConnectionUntilAdDone){
//                            reallyDisconnectNow();
//                        }
//
//                    }
//
//                });
//    }


//    @OnTextChanged(R.id.mode_of_user2)
//    protected void onTextChanged(CharSequence text) {
//        filter(text.toString());
//    }

    void filter(String text) {
        try {
            List<Vpn> temp = new ArrayList();
            for (Vpn d : data) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (d.getLocation().toLowerCase().contains(text.toLowerCase()) || d.getName().toLowerCase().contains((text.toLowerCase()))) {
                    temp.add(d);
                }
            }
            adapter.updateList(temp);
            //update recyclerview
            recyclerView.setAdapter(adapter);
//        recyclerView.notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
//            t.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        onStart();
//        t.schedule();
        Log.d("Come here y", "MY child");
    }

    @Override
    protected void onStop() {
        if (vpnProfile != null) {
            vpnStateListener.stopListening();
        }
        disposable.dispose();
        try {
//            t.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disposable = null;
        super.onStop();
    }

    private void loadProfile(@NonNull Vpn vpn) {

        connectVPN(vpn);

        sharedPref.edit().putInt("int_int", 1).apply();


        if (ICSOpenVPNApplication.getInstance().getSubscribed()) {
            Log.e("Subscried", "Hai");
        } else {
            Log.e("Subscried", "Hai ni");
        }

        Log.e("ss", vpn.getPremium() + "ss");


        int is_super = 0;

        if (vpn.getPremium() == 1 || vpn.getVip() == 1) {
            is_super = 1;
        }

    }

    public void start_free_service()
    {
        Intent s_i = new Intent(MainActivity.this, SendDataService.class);
        s_i.putExtra("time", ICSOpenVPNApplication.getInstance().getFree_time());
//                                s_i.putExtra("time", 1);
        startService(s_i);
    }
    public void do_tell_server_connects(@NonNull Vpn vpn)
    {

        showpDialog();

        String un_id = ICSOpenVPNApplication.id(MainActivity.this);
        Log.e("New_UN_ID",un_id);
//        Toast.makeText(this,un_id,Toast.LENGTH_LONG).show();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"store_connect_count", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

//                        try {

                        start_free_service();
                        start_vpn_now_now(vpn);

//                            if (response.getString("action").equals("success")) {
//                                start_free_service();
//                                start_vpn_now_now(vpn);
//                            }
//                            else if(response.getString("action").equals("count"))
//                            {
//                                // 2. Confirmation message
//                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                        .setContentText("Free Limits Exceeded")
//                                        .setConfirmText("Buy")
//                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                            @Override
//                                            public void onClick(SweetAlertDialog sDialog) {
//                                                sDialog.dismissWithAnimation();
//
//                                                Intent iii = new Intent(MainActivity.this, BuyPremium.class);
//                                                startActivity(iii);
//
//                                            }
//                                        })
//                                        .setCancelButton("Exit", new SweetAlertDialog.OnSweetClickListener() {
//                                            @Override
//                                            public void onClick(SweetAlertDialog sDialog) {
//                                                sDialog.dismissWithAnimation();
//                                            }
//                                        })
//                                        .show();
//
//                            }
//                            else{
//                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                        .setContentText(response.getString("error"))
//                                        .setConfirmText("Okay")
//                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                            @Override
//                                            public void onClick(SweetAlertDialog sDialog) {
//                                                sDialog.dismissWithAnimation();
//                                            }
//                                        })
//                                        .show();
//
//                            }

//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();

                show_msg(error.toString());

                Log.e("setGcmToken()", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device",un_id);
                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);
        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);

    }
    public void start_vpn_now_now(@NonNull Vpn vpn)
    {
        progressBar.setBackgroundResource(R.color.alpha);
        progressBar.setVisibility(View.VISIBLE);
        disposable = LoadingService.getInstance().getProfile(vpn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openProfile, this::onError);
    }

    private void openProfile(@NonNull VpnProfile profile) {

        vpnProfile = profile;

        prefs.edit().putString("last_con", vpnProfile.getName()).apply();

        profileManager = ProfileManager.getInstance(this);
        profileManager.addProfile(vpnProfile);
        profileManager.saveProfileList(this);

        startOrStopVPN(profile);
//        if(profile.get)
//        startActivity(new Intent(this, VpnActivity.class)
//                .putExtra("vpn", profile));
        progressBar.setBackground(null);
        progressBar.setVisibility(View.GONE);



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_DISCONNECT)
        {


            if(resultCode == -1)
            {
//                disconnect_btn.hide();
                mode_connected.setText("Disconnected");
                Log.e("hello","Disconnecting");
//                time_connected.setText("--:--:--");
                try {
                    t.cancel();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
//            Toast.makeText(MainActivity.this,"this " + resultCode,Toast.LENGTH_LONG).show();
        }
        if (requestCode == RC_DISCONNECT && resultCode == RESULT_OK) {
            stateDisconnected();
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            startVPN(vpnProfile);
        }

        if(requestCode==RC_DISCONNECT_LOGOUT)
        {
            if(resultCode == -1)
            {
//                disconnect_btn.hide();
                mode_connected.setText("Disconnected");
//                time_connected.setText("--:--:--");
                Log.e("herereer", "yesss");

                logout_now();


            }
//            Toast.makeText(MainActivity.this,"this " + resultCode,Toast.LENGTH_LONG).show();
        }




    }
    public void logout_now()
    {
        showpDialog();

        String un_id = ICSOpenVPNApplication.id(MainActivity.this);




        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"logout", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {


                                ICSOpenVPNApplication.getInstance().removeData();

                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Sorry" + response.get("error"))
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();

                show_msg(error.toString());

                Log.e("setGcmToken()", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("device",un_id);
                params.put("token", ICSOpenVPNApplication.getInstance().getAccessToken());

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);
    }
    public void show_msg(String msg)
    {

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setContentText(msg)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    protected void initpDialog() {

//        pDialog = new ProgressDialog(MainActivity.this);
//        pDialog.setMessage(getString(R.string.please_wait));
//        pDialog.setCancelable(false);

        // 4. Loading message
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getString(R.string.please_wait));
        pDialog.setCancelable(true);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    private void startOrStopVPN(VpnProfile profile) {

        connectedip.setText(profile.getName());
        ICSOpenVPNApplication.getInstance().setCurrent_connected(profile.getName());
//        profile.mPassword = "faisal";
//        profile.mUsername = "faisal";
        if (paused) {
            paused = false;
            Intent pauseVPN = new Intent(this, OpenVPNService.class);
            pauseVPN.setAction(OpenVPNService.RESUME_VPN);
            startService(pauseVPN);
            return;
        }
        if (VpnStatus.isVPNActive() && profile.getUUIDString().equals(VpnStatus.getLastConnectedVPNProfile())) {
            Intent disconnectVPN = new Intent(this, DisconnectVPN.class);
            prefs.edit().putString("last_con", "Successfully!").apply();
            startActivityForResult(disconnectVPN, RC_DISCONNECT);
        } else {
            VpnStatus.setConnectedVPNProfile(profile.getUUIDString());
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 0);
            } else {
                startVPN(profile);
            }

        }
    }

    private void startVPN(VpnProfile profile) {
        VPNLaunchHelper.startOpenVpn(profile, this);
    }

    @Override
    public void stateConnecting() {

        uploadtv.setText("0 KB");
        downloadtv.setText("0 KB");

        slidinguparrow.setBackgroundResource(R.drawable.ic_action_up);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.setPanelHeight(230);
        connectBtn.setBackgroundResource(R.drawable.connecting);
        mode_connected.setText(R.string.state_connecting);
    }

    @Override
    public void stateConnected() {

        Log.e("ipresponse", " i was tried to be called ------------------------------------------------------");

        speedLayout.setVisibility(View.VISIBLE);
        connectedip.setVisibility(View.VISIBLE);
        currentip.setVisibility(View.VISIBLE);
        uploadtv.setVisibility(View.VISIBLE);
        downloadtv.setVisibility(View.VISIBLE);
        getIP();

        paused = false;
        connectBtn.setBackgroundResource(R.drawable.connected);
        mode_connected.setText(R.string.vpn_connected);
    }

    @Override
    public void stateDisconnected() {

        speedLayout.setVisibility(View.GONE);
        connectedip.setVisibility(View.GONE);
        currentip.setVisibility(View.GONE);
        uploadtv.setVisibility(View.GONE);
        downloadtv.setVisibility(View.GONE);

        connectBtn.setBackgroundResource(R.drawable.disconnected);
        mode_connected.setText(R.string.vpn_disconnected);
    }

    @Override
    public void statePaused() {
        paused = true;
        mode_connected.setText(R.string.paused);
    }

}
// run it and check please