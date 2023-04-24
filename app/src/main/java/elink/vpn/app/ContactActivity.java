

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

//        import android.widget.Toolbar;

public class ContactActivity extends AppCompatActivity {

    Button signupBtn,back_to_serversBtn;
    MaterialEditText subjectInput,emailInput,msgInput;
    String subject,email,msg;
    TextView loginText;
    private SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initpDialog();

        emailInput = findViewById(R.id.email);
        subjectInput = findViewById(R.id.subject);
        msgInput = findViewById(R.id.msg);

        signupBtn = (Button) findViewById(R.id.button_send2);
        back_to_serversBtn = (Button) findViewById(R.id.back_to_servers);

        back_to_serversBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             startActivity(new Intent(ContactActivity.this, MainActivity.class));
                                             finishAffinity();
                                             finish();
                                         }
                                     });

                signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailInput.getText().toString();
                subject = subjectInput.getText().toString();
                msg = msgInput.getText().toString();





                if(subject.equals(""))
                {
//                    fullnameInput.setError("Please enter your Full name",getDrawable(R.drawable.ic_warning_black_24dp));
                    show_msg("Please enter a subject");
                    return;
                }

                if(email.equals(""))
                {
                    show_msg("Please enter your Email");
//                    usernameInput.setError("Please enter your username",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }

                if(!isValidEmail(email))
                {
                    show_msg("Please enter a valid Email");
//                    emailInput.setError("Please enter your email",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }

                if(msg.equals(""))
                {
                    show_msg("Please enter your message");
//                    emailInput.setError("Please enter your email",getDrawable(R.drawable.ic_warning_black_24dp));
                    return;
                }



                do_contact();



            }
        });




    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void do_contact()
    {
        Log.d("Here","2");
        showpDialog();

        String un_id = ICSOpenVPNApplication.id(ContactActivity.this);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"contact", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();

                        try {

                            if (response.getString("action").equals("success")) {

                                new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Success, Your email has been received, you'll be contacted shortly")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                ContactActivity.super.onBackPressed();
                                            }
                                        })
                                        .show();

                            }
                            else{

                                new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Sorry"+response.getString("error"))
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
                params.put("subject", subject);
                params.put("msg", msg);
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
        new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.NORMAL_TYPE)
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

        pDialog = new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

