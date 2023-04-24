package elink.vpn.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import elink.vpn.app.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.util.CustomRequest;

public class BitcoinActivity extends AppCompatActivity {
    ImageView qr_code;
    MaterialEditText trans_ref_edit;
    Button do_submit;
    String sku = "";
    TextView wallet_text;
    private SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_bitcoin);

        Float price = 100f;


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            price = bundle.getFloat("price");
            sku = bundle.getString("sku");
        }
        else
        {



        }
        initpDialog();

        trans_ref_edit = findViewById(R.id.trans_ref);
        do_submit = findViewById(R.id.submit_transaction);



        TextView price_in_used = findViewById(R.id.the_price_in_usd);
        price_in_used.setText("$"+price + " USD");


        Float  bitcoin_price = ICSOpenVPNApplication.getInstance().getBtc();

        Double one_dollars_btc = 1 / (double) bitcoin_price;


        Double final_btc = one_dollars_btc * price;

//        final_btc = final_btc.

        DecimalFormat _final_btc = new DecimalFormat("0.00000000");

        String __final_btc = _final_btc.format(final_btc);


        qr_code = (ImageView) findViewById(R.id.qr_code);

        wallet_text = findViewById(R.id.copy_me);

        String wallet = ICSOpenVPNApplication.getInstance().getWallet();



        wallet_text.setText(wallet);

        String data = "bitcoin:"+wallet+"?amount="+__final_btc;


        TextView price_in_btc = findViewById(R.id.the_price_in_btc);
        price_in_btc.setText(__final_btc + " BTC");

        Picasso.get().load("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="+data+"&color=255-255-255&bgcolor=0-0-0").into(qr_code);






        wallet_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Elink VPN Wallet", wallet);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
//                    wallet_text.setText("Copied!");

                    Toast.makeText(BitcoinActivity.this, "Address Copied!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        Float finalPrice = price;
        do_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ref = trans_ref_edit.getText().toString();
                if(ref.equals(""))
                {
                    Toast.makeText(BitcoinActivity.this,"Please enter the reference of your transaction",Toast.LENGTH_LONG).show();
                    return;
                }
                final String pri = String.valueOf(finalPrice);

                make_bitcoin_trans(ref,sku,__final_btc, pri);



            }
        });







//https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Example&color=255-255-255&bgcolor=0-0-0
    }
//    void set_back(String wallet)
//    {
//        wallet_text.setText(wallet);
//    }

    void make_bitcoin_trans(String ref,String sku, String btc, String price)
    {
        showpDialog();




        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"do_purchase_btc", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

//                                ICSOpenVPNApplication.getInstance().setSubscribed(true);
//                                ICSOpenVPNApplication.getInstance().setAccount_mode(response.getString("account_mode"));
//                                ICSOpenVPNApplication.getInstance().setExpires_at(response.getString("expires_at"));
//
////                                ICSOpenVPNApplication.getInstance().

                                new SweetAlertDialog(BitcoinActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Thanks, Your transaction has been recorded, we'll upgrade your account and let you know as soon as your transaction gets confirmed ")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                BitcoinActivity.super.onBackPressed();
                                            }
                                        })
                                        .show();


//                                ICSOpenVPNApplication.getInstance().set

                            }
                            else{
                                show_msg(response.getString("error"));
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


                int type = 1;

                if(sku.equals(ICSOpenVPNApplication.getInstance().getSku_1()))
                {
                    type = 1;
                }
                if(sku.equals(ICSOpenVPNApplication.getInstance().getSku_2()))
                {
                    type = 2;
                }
                if(sku.equals(ICSOpenVPNApplication.getInstance().getSku_3()))
                {
                    type = 3;
                }

                Log.e("token",ICSOpenVPNApplication.getInstance().getAccessToken());
                Log.e("Type",type+"type");

                params.put("sku",sku);
                params.put("ref",ref);
                params.put("btc",btc);
                params.put("usd",price);

                params.put("token", ICSOpenVPNApplication.getInstance().getAccessToken());
                params.put("type",String.valueOf(type));

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);
    }

    protected void initpDialog() {

        pDialog = new SweetAlertDialog(BitcoinActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
    public void show_msg(String msg)
    {

        new SweetAlertDialog(BitcoinActivity.this, SweetAlertDialog.NORMAL_TYPE)
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

}
