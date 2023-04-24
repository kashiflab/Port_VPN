package elink.vpn.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    Button loginBtn,guestBtn;
    EditText emailInput,passwordInput;
    String email,password;
    TextView signupText,forgtText;
    private SweetAlertDialog pDialog;

    private MaterialEditText etEmail, etPassword;
    private Button btnSignIn, btnGuest_Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initpDialog();

        //Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login2);

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnGuest_Login = findViewById(R.id.guest_login);
        btnSignIn = findViewById(R.id.signInbtn);


//        emailInput = (EditText) findViewById(R.id.email);
//        passwordInput = (EditText) findViewById(R.id.password);
        signupText = (TextView) findViewById(R.id.signuptv);
        forgtText = (TextView) findViewById(R.id.forgotpasstv);
//        guestBtn = (Button) findViewById(R.id.guest_login);



//        loginBtn = (Button) findViewById(R.id.button_send);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if(email.length() == 0)
                {
                    show_msg("Please enter your email or username");
                    return;
                }

                if(password.length() == 0)
                {
                    show_msg("Please enter your password");
                    return;
                }


                do_login();


            }
        });


        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btnGuest_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                do_login_guest();
            }
        });

        forgtText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(i);
            }
        });
    }
    public void do_login()
    {

        showpDialog();

        String un_id = ICSOpenVPNApplication.id(LoginActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"login", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                if(ICSOpenVPNApplication.getInstance().authorize(response))
                                {

                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    show_msg("Sorry, something went wrong");
                                }


//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else if(response.getString("action").equals("device"))
                            {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setContentText("Error, " + response.getString("error"))
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                go_login_in_anyway();
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
                            else{
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setContentText("Error: "+ response.getString("error"))
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

                Log.e("Email",email);
                Log.e("Password",password);
                params.put("email", email);
                params.put("device",un_id);
                params.put("password", password);
                params.put("apiKey",Constants.API_KEY);
                params.put("encryptionKey",Constants.ENCRYPTION_KEY);

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);

    }
    public void do_login_guest()
    {

        showpDialog();

        String un_id = ICSOpenVPNApplication.id(LoginActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"login", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                if(ICSOpenVPNApplication.getInstance().authorize(response))
                                {new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Please Note: Your subscription will work only on this device if you go with guest mode")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                Intent i = new Intent(LoginActivity.this, NicknameActivity.class);
                                                startActivity(i);
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
                                else{
                                    show_msg("Sorry, something went wrong");
                                }


//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else if(response.getString("action").equals("device"))
                            {

                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setContentText("Error: " + response.getString("error"))
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                go_login_in_anyway();
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
                            else{
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
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

                params.put("email", un_id);
                params.put("device",un_id);
                params.put("apiKey",Constants.API_KEY);
                params.put("encryptionKey",Constants.ENCRYPTION_KEY);
                params.put("password", un_id+un_id+"We're really unc48 creablab4934%45 323 yehepwe aho mera name e maryumm ha4"+un_id);
                Log.e("I sent:", Constants.API_DOMAIN+"login");
                Log.e("data:", params.toString());
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

        String un_id = ICSOpenVPNApplication.id(LoginActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"signup", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                if(ICSOpenVPNApplication.getInstance().authorize(response))
                                {

                                    Intent i = new Intent(LoginActivity.this, NicknameActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    show_msg("Sorry, something went wrong");
                                }


//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Error: "+response.getString("error"))
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
                params.put("device",un_id);
                params.put("apiKey",Constants.API_KEY);
                params.put("encryptionKey",Constants.ENCRYPTION_KEY);

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);

    }
    public void go_login_in_anyway()
    {
        showpDialog();

        String un_id = ICSOpenVPNApplication.id(LoginActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"login", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                if(ICSOpenVPNApplication.getInstance().authorize(response))
                                {

                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    show_msg("Sorry, something went wrong");
                                }


//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else if(response.getString("action").equals("device"))
                            {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setContentText("Error: " + response.getString("error"))
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
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
                            else{
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Error: "+ response.getString("error"))
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

                Log.e("Email",email);
                Log.e("Password",password);
                params.put("email", email);
                params.put("anyway","yes");
                params.put("device",un_id);
                params.put("password", password);
                params.put("apiKey",Constants.API_KEY);
                params.put("encryptionKey",Constants.ENCRYPTION_KEY);

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

        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
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

        pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

}
