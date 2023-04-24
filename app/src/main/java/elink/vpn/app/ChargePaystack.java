package elink.vpn.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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

import cn.pedant.SweetAlert.SweetAlertDialog;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.util.CustomRequest;

//import android.support.annotation.RequiresApi;
//import android.support.v7.widget.Toolbar;
//import com.tokfree.calls.app.App;
//import com.tokfree.calls.common.ActivityBase;
//import com.tokfree.calls.constants.Constants;
//import com.tokfree.calls.util.CustomRequest;
//import com.tokfree.calls.util.IabHelper;
//import com.tokfree.calls.R;


public class ChargePaystack extends Activity implements Constants {

    private Card card;

    private Charge charge;
    private ProgressDialog pDialog;
//    private BalanceActivity balance;

    private EditText emailField;
    private EditText cardNumberField;
    private EditText expiryMonthField;
    private EditText expiryYearField;
    private EditText cvvField;
    private String USD,NGN,finalNGN;
//    IabHelper mHelper;

    private Boolean loading = false;

    private String SKU;
    private Integer custom_amount =0;

    private String email, cardNumber, cvv;
    private int expiryMonth, expiryYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init paystack sdk
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY);
        setContentView(R.layout.content_charge_paystack);
        initpDialog();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

//        balance = new BalanceActivity();




        //init view

        Bundle bundle = getIntent().getExtras();
        Button payBtn = (Button) findViewById(R.id.pay_button);

        if (bundle != null) {
            SKU = bundle.getString("paystack_sku");


        }
        else{
            Toast.makeText(getApplicationContext(),"Bundle Error!",Toast.LENGTH_LONG).show();
            return;
        }

        emailField = (EditText) findViewById(R.id.edit_email_address);
        cardNumberField = (EditText) findViewById(R.id.edit_card_number);
        expiryMonthField = (EditText) findViewById(R.id.edit_expiry_month);
        expiryYearField = (EditText) findViewById(R.id.edit_expiry_year);
        cvvField = (EditText) findViewById(R.id.edit_cvv);


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) {
                    return;
                }
                try {
                    showpDialog();
                    email = emailField.getText().toString().trim();
                    cardNumber = cardNumberField.getText().toString().trim();
                    expiryMonth = Integer.parseInt(expiryMonthField.getText().toString().trim());
                    expiryYear = Integer.parseInt(expiryYearField.getText().toString().trim());
                    cvv = cvvField.getText().toString().trim();

//                    String cardNumber = "4084084084084081";
//                    int expiryMonth = 11; //any month in the future
//                    int expiryYear = 18; // any year in the future
//                    String cvv = "408";

                    emailField.setText(email);
                    card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

                    if (card.isValid()) {
//                        Toast.makeText(ChargePaystack.this, "Card is Valid", Toast.LENGTH_LONG).show();
//                        performCharge();
                        getFreshRates();

                    } else {
                        hidepDialog();
                        Toast.makeText(ChargePaystack.this, "Card is not Valid", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void getFreshRates() {
        NGN="";USD="";finalNGN ="";

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, API_SERVER + "convert.php?from=USD&to=NGN&amount=1", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null){
                            try {
                                Log.e("Response:",response.toString());
//                                Log.e("Found:",response.getString("rate"));
                                finalNGN = response.getString("rate");



//                                hidepDialog();

                                performCharge();

//                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else{
                            Toast.makeText(ChargePaystack.this, "Invalid Request for usd", Toast.LENGTH_SHORT).show();
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
    public double get_amount_new(String sku)
    {
        Float f=0f;
//        one_month_premium_sku11
        //six_months_premium_sku2
        //1_year_premium_sku3
        //one_week_vip_sku5
        //one_week_premium_sku4
        if(sku.equals("one_month_premium_sku11")) f= ICSOpenVPNApplication.getInstance().getSku_1_price();
        if(sku.equals("six_months_premium_sku2")) f= ICSOpenVPNApplication.getInstance().getSku_2_price();
        if(sku.equals("1_year_premium_sku3")) f= ICSOpenVPNApplication.getInstance().getSku_3_price();
        if(sku.equals("one_week_premium_sku4")) f= ICSOpenVPNApplication.getInstance().getSku_4_price();
        if(sku.equals("one_week_vip_sku5")) f= ICSOpenVPNApplication.getInstance().getSku_4_price();
//        f = 40.00f;

        return (double) f;
    }

    /**
     * Method to perform the charging of the card
     */
    private void performCharge() {
        //create a Charge object
        charge = new Charge();

        Log.e("Gonna","Chagggggggghghghghg");

        Integer amount_to_charge=0;
        Integer amount_in_KOBO=0;
        if(SKU.equals("custom")) {
            if(finalNGN.equals("")){
                Toast.makeText(ChargePaystack.this, "finalNGN is null", Toast.LENGTH_SHORT).show();
                return;
            }
            double temp =(Double.parseDouble(custom_amount.toString())*Double.parseDouble(finalNGN))*100;
            /*amount_to_charge = custom_amount;
            amount_in_KOBO = amount_to_charge * 100; //converting NGN to KOBO
*/
            amount_in_KOBO = Integer.parseInt(String.valueOf(Math.round(temp)));
        }
        else
        {
            if(finalNGN.equals("")){
                Toast.makeText(ChargePaystack.this, "finalNGN is null", Toast.LENGTH_SHORT).show();
                return;
            }
            try{

                double temp = get_amount_new(SKU) * Double.parseDouble(finalNGN)*100;
                amount_in_KOBO  = Integer.parseInt(String.valueOf(Math.round(temp)));

            }catch (Exception ex){
                Toast.makeText(this, "error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }


            /*amount_to_charge = get_amount_(SKU);
            amount_in_KOBO = amount_to_charge * 100;*/ //converting NGN to KOBO


        }



        //set the card to charge
        charge.setCard(card);

        //call this method if you set a plan
        //charge.setPlan("PLN_yourplan");

        charge.setEmail(email); //dummy email address




//        if(SKU.equals("custom")) {
//            amount_to_charge = custom_amount;
//            amount_in_KOBO = amount_to_charge * 100; //converting NGN to KOBO
//
//
//        }
//        else
//        {
//
//            amount_to_charge = get_amount_(SKU);
//            amount_in_KOBO = amount_to_charge * 100; //converting NGN to KOBO
//
//
//        }


        charge.setAmount(amount_in_KOBO); // amount to Charge, accepts in KOBO, (1NGN * 100) = 1 KOBO

        Log.e("amount_in_kobooo","koboo"+amount_in_KOBO);

        PaystackSdk.chargeCard(ChargePaystack.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                chargeFinished(transaction);
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
                Log.e("OTOp","Reuired");
                hidepDialog();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.e("OTOp","Reuired2"+ error.getMessage()+", "+transaction);
                new SweetAlertDialog(ChargePaystack.this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText(error.getMessage())
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

                hidepDialog();
                //handle error here
            }

        });

//        PaystackSdk.chargeCard(ChargePaystack.this, charge, new Paystack.TransactionCallback() {
//            @Override
//            public void onSuccess(Transaction transaction) {
//
//                // This is called only after transaction is deemed successful.
//                // Retrieve the transaction, and send its reference to your server
//                // for verification.
//                String paymentReference = transaction.getReference();
////                Toast.makeText(ChargePaystack.this, "Transaction Successful! payment reference: "
////                        + paymentReference, Toast.LENGTH_LONG).show();
//
//
//                chargeFinished(transaction);
//            }
//
//            @Override
//            public void beforeValidate(Transaction transaction) {
//
//
//
//
//                // This is called only before requesting OTP.
//                // Save reference so you may send to server. If
//                // error occurs with OTP, you should still verify on server.
//            }
//
//            @Override
//            public void onError(Throwable error, Transaction transaction) {
//                hidepDialog();
////                Log.e("PaymentUndone",transaction.toString());
//                customDialog("Payment error","Payment was unsuccessful, Please try later "+error.getMessage());
//
//                //handle error here
//            }
//        });
    }
    public void chargeFinished(Transaction transaction)
    {

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
                                new SweetAlertDialog(ChargePaystack.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Congratulations, Your account has been upgraded successfully!")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                startActivity(new Intent(ChargePaystack.this, MainActivity.class));
                                                finishAffinity();
                                                finish();
//                                                ChargePaystack.super.onBackPressed();
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

                if(SKU.equals(ICSOpenVPNApplication.getInstance().getSku_1()))
                {
                    type = 1;
                }
                if(SKU.equals(ICSOpenVPNApplication.getInstance().getSku_2()))
                {
                    type = 2;
                }
                if(SKU.equals(ICSOpenVPNApplication.getInstance().getSku_3()))
                {
                    type = 3;
                }
                if(SKU.equals(ICSOpenVPNApplication.getInstance().getSku_4()))
                {
                    type = 4;
                }

                Log.e("token",ICSOpenVPNApplication.getInstance().getAccessToken());

                params.put("token", ICSOpenVPNApplication.getInstance().getAccessToken());
                params.put("type",String.valueOf(type));

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);

        hidepDialog();

    }

    public void update() {

//        mLabelCredits.setText(getString(R.string.label_credits) + " (" + Integer.toString(App.getInstance().getBalance()) + ")");
    }

    public void success() {

        Toast.makeText(ChargePaystack.this, getString(R.string.msg_success_purchase), Toast.LENGTH_SHORT).show();
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String cardNumber = cardNumberField.getText().toString();
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberField.setError("Required.");
            valid = false;
        } else {
            cardNumberField.setError(null);
        }


        String expiryMonth = expiryMonthField.getText().toString();
        if (TextUtils.isEmpty(expiryMonth)) {
            expiryMonthField.setError("Required.");
            valid = false;
        } else {
            expiryMonthField.setError(null);
        }

        String expiryYear = expiryYearField.getText().toString();
        if (TextUtils.isEmpty(expiryYear)) {
            expiryYearField.setError("Required.");
            valid = false;
        } else {
            expiryYearField.setError(null);
        }

        String cvv = cvvField.getText().toString();
        if (TextUtils.isEmpty(cvv)) {
            cvvField.setError("Required.");
            valid = false;
        } else {
            cvvField.setError(null);
        }

        return valid;
    }

    private void cancelMethod1(){
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void okMethod1(){



    }
    private void startmain()

    {
        Intent get_paystack = new Intent(this, MainActivity.class);
        startActivity(get_paystack);
    }




    /**
     * Custom alert dialog that will execute method in the class
     * @param title
     * @param message
     */
    public void customDialog(String title, String message){
        AlertDialog.Builder  builder = new AlertDialog.Builder(ChargePaystack.this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
    }

    public void show_msg(String message){
        AlertDialog.Builder  builder = new AlertDialog.Builder(ChargePaystack.this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(ChargePaystack.this);
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }





}
