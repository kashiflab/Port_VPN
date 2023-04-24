package elink.vpn.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import elink.vpn.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.core.VpnStatus;
import elink.vpn.app.util.CustomRequest;

public class SettingsActivity extends AppCompatActivity {

    private SweetAlertDialog pDialog;

    @BindView(R.id.premiumCD)
    CardView premiumCD;

    @BindView(R.id.logoutCD)
    CardView logoutCD;

    @BindView(R.id.shareCD)
    CardView shareCD;

    @BindView(R.id.feedbackCD)
    CardView feedbackCD;

    @BindView(R.id.rateusCD)
    CardView rateusCD;

    private static final int RC_DISCONNECT_LOGOUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);


        initpDialog();

        premiumCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, BuyPremium.class));
            }
        });

        rateusCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            }
        });

        feedbackCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SettingsActivity.this, ContactActivity.class);
                startActivity(i);
            }
        });

        shareCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out Elink VPN at: https://play.google.com/store/apps/details?id=" + SettingsActivity.this.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        logoutCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VpnStatus.isVPNActive()) {
                    Intent disconnectVPN = new Intent(SettingsActivity.this, DisconnectVPN.class);
                    startActivityForResult(disconnectVPN, RC_DISCONNECT_LOGOUT);
                } else {

                    logout_now();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_DISCONNECT_LOGOUT) {
            if (resultCode == -1) {
                Log.e("herereer", "yesss");
                logout_now();

            }
        }
    }

    public void logout_now()
    {
        showpDialog();

        String un_id = ICSOpenVPNApplication.id(SettingsActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"logout", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {


                                ICSOpenVPNApplication.getInstance().removeData();

                                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(i);
                                finishAffinity();
                                finish();
                            }
                            else{

                                new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE)
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

        new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.NORMAL_TYPE)
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


    protected void initpDialog() {

        pDialog = new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }


}