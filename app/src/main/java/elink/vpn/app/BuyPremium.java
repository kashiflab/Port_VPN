package elink.vpn.app;

import static elink.vpn.app.Constants.API_SERVER;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseState;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;

import elink.vpn.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.util.CustomRequest;

//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingClientStateListener;
//import com.android.billingclient.api.BillingResult;
//import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchasesUpdatedListener;

public class BuyPremium extends AppCompatActivity implements BillingProcessor.IBillingHandler {

//    private BillingClient billingClient;

    public boolean ready = false;

    BillingProcessor bp;
//    @BindView(R.id.pkg_1)
//    LinearLayout pkg_1;

//    @BindView(R.id.pkg_2)
//    LinearLayout pkg_2;

//    @BindView(R.id.pkg_3)
//    LinearLayout pkg_3;

//    @BindView(R.id.pkg_4)
//    LinearLayout pkg_4;

//    LinearLayout pkg_5;


    CardView vipCD, weeklyCD, monthlyCD, sixMonthCD, yearlyCD;

    private SweetAlertDialog pDialog;

    private String context_sku;
    private String context_package_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);


        vipCD = findViewById(R.id.vipCD);
        weeklyCD = findViewById(R.id.weeklyCD);
        monthlyCD = findViewById(R.id.monthlyCD);
        sixMonthCD = findViewById(R.id.sixMonthCD);
        yearlyCD = findViewById(R.id.yearCD);


//        pkg_1 = (LinearLayout) findViewById(R.id.pkg_1); //monthly package
//        pkg_2 = (LinearLayout) findViewById(R.id.pkg_2); //6 months package
//        pkg_3 = (LinearLayout) findViewById(R.id.pkg_3); //yearly package
//        pkg_4 = (LinearLayout) findViewById(R.id.pkg_4); //weekly trial
//        pkg_5 = (LinearLayout) findViewById(R.id.pkg_5); //vip package

        TextView sku_1_title_text = (TextView) findViewById(R.id.sku_1_title);
        TextView sku_2_title_text = (TextView) findViewById(R.id.sku_2_title);
        TextView sku_3_title_text = (TextView) findViewById(R.id.sku_3_title);
        TextView sku_4_title_text = (TextView) findViewById(R.id.sku_4_title);
        TextView sku_5_title_text = (TextView) findViewById(R.id.sku_5_title);

        TextView sku_1_save_text = (TextView) findViewById(R.id.sku_1_save);
        TextView sku_2_save_text = (TextView) findViewById(R.id.sku_2_save);
        TextView sku_3_save_text = (TextView) findViewById(R.id.sku_3_save);
        TextView sku_4_save_text = (TextView) findViewById(R.id.sku_4_save);
        TextView sku_5_save_text = (TextView) findViewById(R.id.sku_5_save);

        TextView sku_1_price_title_text = (TextView) findViewById(R.id.sku_1_price_title);
        TextView sku_2_price_title_text = (TextView) findViewById(R.id.sku_2_price_title);
        TextView sku_3_price_title_text = (TextView) findViewById(R.id.sku_3_price_title);
        TextView sku_4_price_title_text = (TextView) findViewById(R.id.sku_4_price_title);
        TextView sku_5_price_title_text = (TextView) findViewById(R.id.sku_5_price_title);


        sku_1_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_1_title());
        sku_2_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_2_title());
        sku_3_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_3_title());
        sku_4_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_4_title());
        sku_5_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_5_title());

        Log.e("I did set title",ICSOpenVPNApplication.getInstance().getSku_2_title());


        sku_1_save_text.setText(ICSOpenVPNApplication.getInstance().getSku_1_save());
        sku_2_save_text.setText(ICSOpenVPNApplication.getInstance().getSku_2_save());
        sku_3_save_text.setText(ICSOpenVPNApplication.getInstance().getSku_3_save());
        sku_4_save_text.setText(ICSOpenVPNApplication.getInstance().getSku_4_save());
        sku_5_save_text.setText(ICSOpenVPNApplication.getInstance().getSku_5_save());


        sku_1_price_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_1_price_title());
        sku_2_price_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_2_price_title());
        sku_3_price_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_3_price_title());
        sku_4_price_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_4_price_title());
        sku_5_price_title_text.setText(ICSOpenVPNApplication.getInstance().getSku_5_price_title());


//        Toast.makeText(BuyPremium.this,ICSOpenVPNApplication.getInstance().getSku_5_price_title()+"s",Toast.LENGTH_LONG).show();




        initpDialog();
        String key__ = ICSOpenVPNApplication.getInstance().getGoogle_billing_key();
//        key__ = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkViuimu1gItxj4D5l7JMS1Z4bLzPiX5Rpz1esgZjF0wrtmpaS4kQz0gb+GqIh7OtcbRwSbV0N1nn3tOCf2eMvb3rXoKdoa2t5nPCspy3EwicU6XkoNHj8E529vQgvxDfMV7o8ql9OBYvyqkrJqv+JIKq3l1Wle/CbgQGqu/kzO4ODXGiw19WX8UzuMT7BklWhac5CizrGltl0WTyJqCmiYMONtzbDXzunRsaABA9pQsQX2HUZKZUQiS+Lb2w/YKwn823ZzlefMCyV9ktN09feV5+By/iY9FpU3iN1woit5Yw7E+LjhVJwgMYvKvwGiehWYjdo9gHLsBBKv6AmIJRsQIDAQAB";

        bp = new BillingProcessor(this,key__,this);


        monthlyCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Cliked","1");

                go_buy_pkg(1);

            }
        });

        sixMonthCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                go_buy_pkg(2);

            }
        });

        yearlyCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                go_buy_pkg(3);

            }
        });
        weeklyCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                go_buy_pkg(4);

            }
        });
        vipCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                go_buy_pkg(5);

            }
        });

//        pkg_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                go_buy_pkg(2);
//
//            }
//        });
//
//
//
//        pkg_3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                go_buy_pkg(3);
//
//            }
//        });
//
//        pkg_4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                go_buy_pkg(4);
//
//            }
//        });



    }


    public void go_buy_pkg(int i_i)
    {
        if(ready)
        {

//            do_google(i);
//            return;

//            if(i_i==4)
//            {
//                do_google(i_i);
//                return;
//            }

            new SweetAlertDialog(BuyPremium.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Upgrade")
                    .setContentText("How would you like to pay?")
                    .setConfirmText("Google")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            //Do whatever you want to do if usre selects Google Play
                            do_google(i_i);
                        }
                    })
                    .setCancelButton("Card", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            do_paystack(i_i);
//                            do_bitcoin(i_i);
//                            do_flutterwave(i_i);
                        }
                    })
                    .show();

        }
        else{
            // 5. Confirm success
            new SweetAlertDialog(BuyPremium.this, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("Sorry, Billing service is not available yet, please wait.")
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
    void do_bitcoin(int i)
    {

        String sku = "android.test.purchased";
        switch (i)
        {
            case 1:
                sku = ICSOpenVPNApplication.getInstance().getSku_1();
                break;
            case 2:
                sku = ICSOpenVPNApplication.getInstance().getSku_2();
                break;
            case 3:
                sku = ICSOpenVPNApplication.getInstance().getSku_3();
                break;
            case 4:
                sku = ICSOpenVPNApplication.getInstance().getSku_4();
                break;
            case 5:
            sku = ICSOpenVPNApplication.getInstance().getSku_5();
            break;

        }
        Float price = 50f;

        Log.e("float_price",i+"--");

        switch (i)
        {
            case 1:
                price = ICSOpenVPNApplication.getInstance().getSku_1_price();
                break;
            case 2:
                price = ICSOpenVPNApplication.getInstance().getSku_2_price();
                break;
            case 3:
                price = ICSOpenVPNApplication.getInstance().getSku_3_price();
                break;
            case 5:
                price = ICSOpenVPNApplication.getInstance().getSku_5_price();
                break;


        }

        Intent _i = new Intent(BuyPremium.this, BitcoinActivity.class);
        _i.putExtra("price",price);
        _i.putExtra("sku",sku);
        startActivity(_i);
    }

    void do_paystack(int i)
    {
        String sku = "android.test.purchased";
        switch (i)
        {
            case 1:
                sku = ICSOpenVPNApplication.getInstance().getSku_1();
                break;
            case 2:
                sku = ICSOpenVPNApplication.getInstance().getSku_2();
                break;
            case 3:
                sku = ICSOpenVPNApplication.getInstance().getSku_3();
                break;
            case 4:
                sku = ICSOpenVPNApplication.getInstance().getSku_4();
                break;

        }
        Log.e("Did",sku);

        Intent _i = new Intent(BuyPremium.this, ChargePaystack.class);
         _i.putExtra("paystack_sku",sku);
         startActivityForResult(_i, 1945);


    }

    void do_flutterwave(int i)
    {
        String sku = "android.test.purchased";
        float price_to_charge = 0;
        String title = "";
        switch (i)
        {
            case 1:
                sku = ICSOpenVPNApplication.getInstance().getSku_1();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_1_price();
                title = ICSOpenVPNApplication.getInstance().getSku_1_title();
                break;
            case 2:
                sku = ICSOpenVPNApplication.getInstance().getSku_2();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_2_price();
                title = ICSOpenVPNApplication.getInstance().getSku_2_title();
                break;
            case 3:
                sku = ICSOpenVPNApplication.getInstance().getSku_3();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_3_price();
                title = ICSOpenVPNApplication.getInstance().getSku_3_title();
                break;
            case 4:
                sku = ICSOpenVPNApplication.getInstance().getSku_4();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_4_price();
                title = ICSOpenVPNApplication.getInstance().getSku_4_title();
                break;
            case 5:
                sku = ICSOpenVPNApplication.getInstance().getSku_5();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_5_price();
                title = ICSOpenVPNApplication.getInstance().getSku_5_title();
                break;

        }
        Log.e("Did",sku);
        Log.e("amount to charge: ", String.valueOf(price_to_charge));
        Log.e("sku to charge: ", String.valueOf(sku));
        context_sku = sku;
        context_package_name = title;

        Toast.makeText(BuyPremium.this,"Loading... "+price_to_charge,Toast.LENGTH_SHORT).show();

        getFreshRates(price_to_charge);
        Log.e("PRICEBRED price",price_to_charge+"");

//        return;




    }

    public void chargeNow(double price_to_charge){
        String pub_key = ICSOpenVPNApplication.getInstance().getflutterwave_pub_key();
        String enc_key = ICSOpenVPNApplication.getInstance().getflutterwave_enc_key();
        new RaveUiManager(BuyPremium.this).setAmount(price_to_charge)
                .setCurrency("USD")
                .setEmail(ICSOpenVPNApplication.getInstance().getEmail())
                .setfName(ICSOpenVPNApplication.getInstance().getUsername())
                .setlName(ICSOpenVPNApplication.getInstance().getFullname())
                .setNarration(context_package_name)
                .setPublicKey(pub_key)
                .setEncryptionKey(enc_key)
                .setTxRef(ICSOpenVPNApplication.getInstance().getEmail()+"||"+context_sku+"||"+ICSOpenVPNApplication.id(BuyPremium.this))
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptMpesaPayments(true)
                .acceptAchPayments(true)
                .acceptGHMobileMoneyPayments(false)
                .acceptUgMobileMoneyPayments(false)
                .acceptZmMobileMoneyPayments(false)
                .acceptRwfMobileMoneyPayments(false)
                .acceptSaBankPayments(false)
                .acceptUkPayments(true)
                .acceptBankTransferPayments(true)
                .acceptUssdPayments(false)
                .acceptBarterPayments(false)
                .allowSaveCardFeature(true)
                .onStagingEnv(false)
                .shouldDisplayFee(false)
                .showStagingLabel(false)
                .withTheme(R.style.FlutterWaveTheme)
                .initialize();

    }

    private void getFreshRates(float price_to_charge) {


        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, API_SERVER + "convert.php?from=USD&to=NGN&amount=1", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null){
                            try {
                                Log.e("Response:",response.toString());
//                                Log.e("Found:",response.getString("rate"));
                                String ngnConverter = response.getString("rate");

                                Log.e("PRICEBRED ngnConverter",ngnConverter+"");


                                double temp = price_to_charge*Double.parseDouble(ngnConverter);
                                Log.e("PRICEBRED temp",temp+"");

                                double finalprice  = Integer.parseInt(String.valueOf(Math.round(temp)));
                                Log.e("PRICEBRED finalPrice",finalprice+"");







//                                hidepDialog();

                                chargeNow(price_to_charge);

//                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else{
                            Toast.makeText(BuyPremium.this, "Invalid Request for usd", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                /*Map<String, String> params = new HashMap<String, String>();

                params.put("symbols", "USD");*/
                return null;
            }
        };

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);
    }


    // This is to handle the response of fluterwave
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Amara", "one ");
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            Log.e("Amara", "one two");
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Log.e("Amara", "one three");
//                Log.e("Amara", data.getStringExtra("id"));
                Toast.makeText(this, "SUCCESS, saving...", Toast.LENGTH_SHORT).show();
                go_process(context_sku,message,context_package_name,message);
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Log.e("Amara", "one four");
                new SweetAlertDialog(BuyPremium.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Error, " + message)
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
//                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                new SweetAlertDialog(BuyPremium.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Cancelled, " + message)
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                Log.e("Amara", "one five");
//                Toast.makeText(this, "CANCELLED ", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.e("Amara", "one six");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void do_google(int i )
    {
        String sku = "android.test.purchased";
        float price_to_charge = 0;
        switch (i)
        {
            case 1:
                sku = ICSOpenVPNApplication.getInstance().getSku_1();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_1_price();
                break;
            case 2:
                sku = ICSOpenVPNApplication.getInstance().getSku_2();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_2_price();
                break;
            case 3:
                sku = ICSOpenVPNApplication.getInstance().getSku_3();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_3_price();
                break;
            case 4:
                sku = ICSOpenVPNApplication.getInstance().getSku_4();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_4_price();
                break;
            case 5:
                sku = ICSOpenVPNApplication.getInstance().getSku_5();
                price_to_charge = ICSOpenVPNApplication.getInstance().getSku_5_price();
                break;

        }
        Log.e("Did",sku);
        if(bp.isSubscribed(sku))
        {
            Toast.makeText(BuyPremium.this,"You've already subscribed, email us at admin@impresslabs.tk to get your account restored",Toast.LENGTH_LONG).show();

        }
        bp.subscribe(BuyPremium.this,sku);

    }


    @Override
    public void onProductPurchased(String productId, PurchaseInfo details) {

//        bp.consumePurchase(productId);
        Log.e("details",details.toString());
        if(details.purchaseData.purchaseState== PurchaseState.PurchasedSuccessfully)
        {
            go_process(productId,details.purchaseData.purchaseToken,details.purchaseData.packageName,details.responseData);

           Log.e("e",details.responseData);

        }
        else{
            show_msg("You've not completed your purchase, please complete your purchase and email us at admin@impresslabs.tk");
        }
//        bp.




    }
//    onProduc

    void go_process(String id,String purchaseToken, String packageName, String details)
    {
        showpDialog();




        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"do_purchase", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                ICSOpenVPNApplication.getInstance().setSubscribed(true);
                                ICSOpenVPNApplication.getInstance().setAccount_mode(response.getString("account_mode"));
                                ICSOpenVPNApplication.getInstance().setExpires_at(response.getString("expires_at"));

//                                ICSOpenVPNApplication.getInstance().

                                new androidx.appcompat.app.AlertDialog.Builder(BuyPremium.this,R.style.AppCompatAlertDialogStyle)
                                        .setMessage("Congratulations, Your account has been upgraded successfully! ")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                dialogInterface.dismiss();
                                                BuyPremium.super.onBackPressed();
                                            }
                                        }).show();


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

                if(id.equals(ICSOpenVPNApplication.getInstance().getSku_1()))
                {
                    type = 1;
                }
                if(id.equals(ICSOpenVPNApplication.getInstance().getSku_2()))
                {
                    type = 2;
                }
                if(id.equals(ICSOpenVPNApplication.getInstance().getSku_3()))
                {
                    type = 3;
                }
                if(id.equals(ICSOpenVPNApplication.getInstance().getSku_4()))
                {
                    type = 4;
                }

                if(id.equals(ICSOpenVPNApplication.getInstance().getSku_5()))
                {
                    type = 5;
                }

                Log.e("token",ICSOpenVPNApplication.getInstance().getAccessToken());
                Log.e("Type",type+"type");

                params.put("sku",id);
                params.put("package_name",packageName);
                params.put("details",details);
                params.put("purchase_token",purchaseToken);

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

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e("err",String.valueOf(errorCode));
//        AlertDialog.Builder  builder = new AlertDialog.Builder(BuyPremium.this);
//        builder.setMessage("Sorry, "+error.getMessage())
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        dialogInterface.dismiss();
//
//                    }
//                }).show();
    }

    @Override
    public void onBillingInitialized() {
        Log.e("Did","init");
        if(BillingProcessor.isIabServiceAvailable(BuyPremium.this)) {
            Log.e("Did","ready");
            ready = true;
        }
        else{
            Log.e("Did","nready");
        }

    }
    // good to go
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public void show_msg(String msg)
    {
        // 5. Confirm success
        new SweetAlertDialog(BuyPremium.this, SweetAlertDialog.NORMAL_TYPE)
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

        pDialog = new SweetAlertDialog(BuyPremium.this, SweetAlertDialog.PROGRESS_TYPE);
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
