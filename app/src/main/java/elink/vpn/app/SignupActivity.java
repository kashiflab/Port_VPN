package elink.vpn.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import elink.vpn.app.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.util.CustomRequest;

public class SignupActivity extends AppCompatActivity {

    Button signupBtn,guestBtn;
    MaterialEditText emailInput,passwordInput,cpasswordInput,fullnameInput,usernameInput;
    String email,password,cpassword,fullname,username;
    TextView loginText;
    private SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initpDialog();

        emailInput = findViewById(R.id.email);
        usernameInput = findViewById(R.id.username);
        fullnameInput = findViewById(R.id.fullname);
        passwordInput = findViewById(R.id.password);
        cpasswordInput = findViewById(R.id.conpassword);
        signupBtn = (Button) findViewById(R.id.signup);
        guestBtn = (Button) findViewById(R.id.guest_login);
        loginText = (TextView) findViewById(R.id.signuptv);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailInput.getText().toString();
                username = usernameInput.getText().toString();
                fullname = fullnameInput.getText().toString();
                password = passwordInput.getText().toString();
                cpassword = cpasswordInput.getText().toString();

                Log.d("Here","1" + fullname);

                if(fullname.equals(""))
                {
//                    fullnameInput.setError("Please enter your Full name",getDrawable(R.drawable.ic_warning_black_24dp));
                    show_msg("Please enter your Full Name");
                    return;
                }

                if(username.equals(""))
                {
                    show_msg("Please enter your Username");
//                    usernameInput.setError("Please enter your username",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }

                if(email.equals(""))
                {
                    show_msg("Please enter your Email");
//                    emailInput.setError("Please enter your email",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }


                if(!isValidEmail(email))
                {
                    show_msg("Please enter a valid Email");
//                    emailInput.setError("Please enter your email",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }

                if(password.length() < 6)
                {
                    show_msg("Please enter your at least 6 characters");
//                    passwordInput.setError("Please enter your at least 6 characters",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }

                if(!cpassword.equals(password))
                {
                    show_msg("Confirm password don't match");
//                    cpasswordInput.setError("Confirm password don't match",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }


                do_signup();



            }
        });


        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignupActivity.super.onBackPressed();



            }
        });

        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Please Note: Your subscription will work only on this device if you go with guest mode")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                do_guest();
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
        });




//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void do_signup()
    {
        Log.d("Here","2");
        showpDialog();

        String un_id = ICSOpenVPNApplication.id(SignupActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"signup", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                if(ICSOpenVPNApplication.getInstance().authorize(response))
                                {

                                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                                else{
                                   show_msg("Sorry, something went wrong");
                                }


//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Sorry, " + response.getString("error"))
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

                Log.e("setGcmToken()", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                params.put("fullname", fullname);
                params.put("apiKey",Constants.API_KEY);
                params.put("encryptionKey",Constants.ENCRYPTION_KEY);
                params.put("device",un_id);

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);

    }
    public void do_guest()
    {
        Log.d("Here","2");
        showpDialog();

        String un_id = ICSOpenVPNApplication.id(SignupActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"signup", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                if(ICSOpenVPNApplication.getInstance().authorize(response))
                                {

                                    Intent i = new Intent(SignupActivity.this, NicknameActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    show_msg("Sorry, something went wrong");
                                }


//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{

                                new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Sorry, " + response.getString("error"))
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

                Log.e("setGcmToken()", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", un_id);
                params.put("username", un_id);
                params.put("password", un_id+un_id+"We're really unc48 creablab4934%45 323 yehepwe aho mera name e maryumm ha4"+un_id);
                params.put("fullname", un_id);
                params.put("apiKey",Constants.API_KEY);
                params.put("encryptionKey",Constants.ENCRYPTION_KEY);
                params.put("device",un_id);

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
        new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.NORMAL_TYPE)
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

        pDialog = new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

}
