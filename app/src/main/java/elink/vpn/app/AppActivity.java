package elink.vpn.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import elink.vpn.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.App.App;
import elink.vpn.app.util.CustomRequest;

//import elink.vpn.app.App.App;

public class AppActivity extends AppCompatActivity {




    App app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
//        app = new App();
//        ICSOpenVPNApplication.getContext().the
//        Log.d("The_token",ICSOpenVPNApplicatio)

//        ICSOpenVPNApplication.getInstance().ge

        Log.e("Updatess","1");
        checkforUpdates();


    }

    public void continueToApp(){

        Log.e("Updatess","8");


        if(ICSOpenVPNApplication.getInstance().isConnected()) {
            Log.e("Updatess","9");

            auth_now();
        }
        else{
            AlertDialog.Builder  builder = new AlertDialog.Builder(AppActivity.this);
            builder.setMessage("You are not connected to internet, this app doesn't operate offline.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    }).show();
        }
    }

    public void checkforUpdates(){

        Log.e("Updatess","2");



        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

        Log.e("Updatess","3");


        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        Log.e("Updatess","4");

        appUpdateInfoTask.addOnFailureListener(failure->{
            Log.e("Updatess","44");
            continueToApp();
        });


        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.e("Updatess","5");

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                Log.e("Updatess","6");

                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            44);
                } catch (IntentSender.SendIntentException e) {

                    updateFailed();
                    e.printStackTrace();

                }
            }else{
                Log.e("Updatess","7");


                continueToApp();
            }
        });
    }

    public  void updateFailed(){


        AlertDialog.Builder  builder = new AlertDialog.Builder(AppActivity.this);
        builder.setMessage("App update failed, please update app from playstore to continue")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 44) {
            if (resultCode != RESULT_OK) {
                updateFailed();
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }else{
                continueToApp();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void auth_now()
    {
        if (ICSOpenVPNApplication.getInstance().getAccessToken() !=  "" && ICSOpenVPNApplication.getInstance().getAccessToken().length() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"check_login", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e("i got",response.toString());

                            try {

                                if (response.getString("action").equals("success")) {

                                    if(ICSOpenVPNApplication.getInstance().authorize(response))
                                    {
                                        Intent i = new Intent(AppActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{

                                        Intent i = new Intent(AppActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    }



//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent i = new Intent(AppActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("setGcmToken()", error.toString());
                    Intent i = new Intent(AppActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("device",ICSOpenVPNApplication.id((AppActivity.this)));
                    params.put("token", ICSOpenVPNApplication.getInstance().getAccessToken());
                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);
        }
        else{
            Intent i = new Intent(AppActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }




}
