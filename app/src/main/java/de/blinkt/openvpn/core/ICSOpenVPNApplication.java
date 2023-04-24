

package de.blinkt.openvpn.core;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import elink.vpn.app.App.App;
import elink.vpn.app.Constants;
import elink.vpn.app.R;
import elink.vpn.app.util.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ICSOpenVPNApplication extends Application implements Constants {
    public static final String TAG = ICSOpenVPNApplication.class.getSimpleName();

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";


//    public static final String TAG = ICSOpenVPNApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static ICSOpenVPNApplication mInstance;

    private SharedPreferences sharedPref;

    private String current_connected,username, account_mode,expires_at, email,gender,usernamesinch, phone, fullname, accessToken, gcmToken = "", fb_id = "", photoUrl, coverUrl, area = "", country = "", city = "";
    private Double lat = 0.000000, lng = 0.000000;
    private long id;
    private int state, admob = 1, verify, balance = 0, vip, ghost, pro, freeMessagesCount, allowShowMyAge, allowShowMySexOrientation, allowShowOnline, allowShowMyInfo, allowShowMyGallery, allowShowMyFriends, allowShowMyLikes, allowShowMyGifts, allowPhotosComments, allowComments, allowMessages, allowMatchesGCM, allowLikesGCM, allowCommentsGCM, allowFollowersGCM, allowMessagesGCM, allowGiftsGCM, allowCommentReplyGCM, errorCode, currentChatId = 0, notificationsCount = 0, messagesCount = 0, guestsCount = 0, newFriendsCount = 0, newMatchesCount = 0, seenTyping = 1, allowRewardedAds = 1;

    private int photoCreateAt = 0, free_time=1, photoModerateAt = 0, accountModerateAt = 0;

    private Boolean circle_items = true;

    private String free_time_msg,free_time_msg_after,sku_1,sku_2,sku_3,sku_4,sku_5, sku_1_title,sku_2_title,sku_3_title,sku_4_title,sku_5_title,sku_1_save,sku_2_save,sku_3_save,sku_4_save,sku_5_save,Google_billing_key, flutterwave_enc_key, flutterwave_pub_key, appodeal_key;
    private Float sku_1_price,sku_2_price,sku_3_price,sku_4_price,sku_5_price;
    private String sku_1_price_title,sku_2_price_title,sku_3_price_title,sku_4_price_title,sku_5_price_title;
    private Float btc;

    private String wallet;



    public Boolean subscription = false;
    private String photoModerateUrl = "", coverModerateUrl = "";



    private static ICSOpenVPNApplication context;

    public static Application getApplication() {
        return context;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mInstance = this;
        PRNGFixes.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          //  createNotificationChannels();
        }


       //StatusListener mStatus = new StatusListener();
       //mStatus.init(getApplicationContext());

        sharedPref = this.getSharedPreferences("account", Context.MODE_PRIVATE);

        this.readData();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("2745b1a5-f714-4a06-af18-2c4e0944ac0e");
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();

    }
    public synchronized static String id(Context context) {
        if (uniqueID == null) {
//            SharedPreferences sharedPrefs = context.getSharedPreferences(
//                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
//            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
//            if (uniqueID == null) {
//                uniqueID = UUID.randomUUID().toString();
//                SharedPreferences.Editor editor = sharedPrefs.edit();
//                editor.putString(PREF_UNIQUE_ID, uniqueID);
//                editor.apply();
//
//            }
            uniqueID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return uniqueID;
    }
    public void setLocation() {

        if (App.getInstance().isConnected()) {

            if (App.getInstance().isConnected() && App.getInstance().getId() != 0 && App.getInstance().getLat() != 0.000000 && App.getInstance().getLng() != 0.000000) {

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN + "set_location", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

//                                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } finally {

                                    Log.d("GEO SAVE SUCCESS", response.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("GEO SAVE ERROR", error.toString());
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("token", App.getInstance().getAccessToken());
                        params.put("lat", Double.toString(App.getInstance().getLat()));
                        params.put("lng", Double.toString(App.getInstance().getLng()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);
            }
        }
    }

    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;
        }

        return false;
    }

    public void logout() {

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN + "logout", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {



                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    App.getInstance().removeData();
                    App.getInstance().readData();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);

                    params.put("token", App.getInstance().getAccessToken());

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);

        }

        App.getInstance().removeData();
        App.getInstance().readData();
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public void getSettings() {

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"get_settings", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

                                    if (response.has("subscription")) {

                                        App.getInstance().setSubscribed(response.getBoolean("subscription"));
                                    }


                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("getSettings()", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);
                    params.put("token", App.getInstance().getAccessToken());

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public void updateGeoLocation() {

        // Now it is empty
        // In this application, there is no a web version and this code has been deleted
    }

    public Boolean authorize(JSONObject authObj) {

        try {



            JSONObject accountArray = authObj.getJSONObject("data");

            if (2==2) {

                JSONObject accountObj = accountArray;


                if (accountObj.has("username")) {
                    this.setUsername(accountObj.getString("username"));
                }
                if (accountObj.has("username")) {
                    this.setEmail(accountObj.getString("email"));
                }

                if (accountObj.has("expires_at")) {
                    this.setExpires_at(accountObj.getString("expires_at"));
                }
                else{
                    this.setExpires_at("--:--:--");
                }

                Log.e("sku_1_one_only",accountObj.toString());

                if (accountObj.has("free_time")) {
                    this.setFree_time(accountObj.getInt("free_time"));
                }

                if (accountObj.has("free_time_msg")) {
                    this.setFree_time_msg(accountObj.getString("free_time_msg"));
                }
                if (accountObj.has("free_time_msg_after")) {
                    this.setFree_time_msg_after(accountObj.getString("free_time_msg_after"));
                }

                if (accountObj.has("sku_1")) {
                    this.setSku_1(accountObj.getString("sku_1"));
                }


                if (accountObj.has("sku_1")) {
                    this.setSku_1(accountObj.getString("sku_1"));
                }

                if (accountObj.has("sku_2")) {
                    this.setSku_2(accountObj.getString("sku_2"));
                }

                if (accountObj.has("sku_3")) {
                    this.setSku_3(accountObj.getString("sku_3"));
                }

                if (accountObj.has("sku_4")) {
                    this.setSku_4(accountObj.getString("sku_4"));
                }
                if (accountObj.has("sku_5")) {
                    this.setSku_5(accountObj.getString("sku_5"));
                }


                if (accountObj.has("sku_1_title")) {
                    this.setSku_1_title(accountObj.getString("sku_1_title"));
                }

                if (accountObj.has("sku_2_title")) {
                    this.setSku_2_title(accountObj.getString("sku_2_title"));
                    Log.e("Isaved_title",this.getSku_2_title()+"s");
                }

                if (accountObj.has("sku_3_title")) {
                    this.setSku_3_title(accountObj.getString("sku_3_title"));
                }

                if (accountObj.has("sku_4_title")) {
                    this.setSku_4_title(accountObj.getString("sku_4_title"));
                }
                if (accountObj.has("sku_5_title")) {
                    this.setSku_5_title(accountObj.getString("sku_5_title"));
                }


                //

                if (accountObj.has("sku_1_save")) {
                    this.setSku_1_save(accountObj.getString("sku_1_save"));
                }

                if (accountObj.has("sku_2_save")) {
                    this.setSku_2_save(accountObj.getString("sku_2_save"));
                }

                if (accountObj.has("sku_3_save")) {
                    this.setSku_3_save(accountObj.getString("sku_3_save"));
                }

                if (accountObj.has("sku_4_save")) {
                    this.setSku_4_save(accountObj.getString("sku_4_save"));
                }

                if (accountObj.has("sku_5_save")) {
                    this.setSku_5_save(accountObj.getString("sku_5_save"));
                }

                //


                if (accountObj.has("sku_1_price_title")) {
                    this.setSku_1_price_title(accountObj.getString("sku_1_price_title"));
                }

                if (accountObj.has("sku_2_price_title")) {
                    this.setSku_2_price_title(accountObj.getString("sku_2_price_title"));
                }

                if (accountObj.has("sku_3_price_title")) {
                    this.setSku_3_price_title(accountObj.getString("sku_3_price_title"));
                }

                if (accountObj.has("sku_4_price_title")) {
                    this.setSku_4_price_title(accountObj.getString("sku_4_price_title"));
                    Log.e("sku_4_price_title",this.getSku_4_price_title()+"s4");
                }

                if (accountObj.has("sku_5_price_title")) {
                    this.setSku_5_price_title(accountObj.getString("sku_5_price_title"));
//                    Log.e("sku_5_price_title",accountObj.getString("sku_5_price_title")+"s6");
                }

                //
                if (accountObj.has("sku_1_price")) {
                    this.setSku_1_price( (float) accountObj.getDouble("sku_1_price"));
                }

                if (accountObj.has("btc")) {
                    this.setBtc( (float) accountObj.getDouble("btc"));
                }
                if (accountObj.has("wallet")) {
                    this.setWallet( accountObj.getString("wallet"));
                }

                if (accountObj.has("sku_2_price")) {
                    this.setSku_2_price( (float) accountObj.getDouble("sku_2_price"));
                }

                if (accountObj.has("sku_3_price")) {
                    this.setSku_3_price( (float) accountObj.getDouble("sku_3_price"));

                    Log.e("Look_what_i_fount",this.getSku_1_price().toString());
                }

                if (accountObj.has("sku_4_price")) {
                    this.setSku_4_price( (float) accountObj.getDouble("sku_4_price"));
                }

                if (accountObj.has("sku_5_price")) {
                    this.setSku_5_price( (float) accountObj.getDouble("sku_5_price"));
                }

                if(accountObj.has("subscribed"))
                {
                    String x = accountObj.getString("subscribed");
                    if(x.equals("yes"))
                    {
                        this.setSubscribed(true);
                    }
                    else{
                        this.setSubscribed(false);
                    }
                }
                else{
                    this.setSubscribed(false);
                }

                if (accountObj.has("account_mode")) {
                    this.setAccount_mode(accountObj.getString("account_mode"));


                }
                else{
                    this.setAccount_mode("Free");
                }


                if (accountObj.has("google_billing_key")) {
                    this.setGoogle_billing_key(accountObj.getString("google_billing_key"));
                }
                if (accountObj.has("flutterwave_enc_key")) {
                    this.setflutterwave_pub_key( accountObj.getString("flutterwave_enc_key"));
                }
                if (accountObj.has("flutterwave_pub_key")) {
                    this.setflutterwave_pub_key( accountObj.getString("flutterwave_pub_key"));
                }

                if (accountObj.has("appodeal_key")) {
                    this.setappodeal_key( accountObj.getString("appodeal_key"));
                }



                this.setFullname(accountObj.getString("fullname"));

                this.setAccessToken(accountObj.getString("token"));

                this.saveData();

//                this.getSettings();

//                if (getGcmToken().length() != 0) {
//
//                    setGcmToken(getGcmToken());
//                }

                return true;
            }else{
                return false;
            }

        } catch (JSONException e) {

            e.printStackTrace();
            return false;
        }
    }

    public long getId() {

        return this.id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public void setGcmToken(final String gcmToken) {

        if (this.getId() != 0 && this.getAccessToken().length() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"store_push_id", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("setGcmToken()", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", App.getInstance().getAccessToken());

                    params.put("gcm_regId", gcmToken);

                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);
        }

        this.gcmToken = gcmToken;
    }

    public String getGcmToken() {

        return this.gcmToken;
    }

    public void setFacebookId(String fb_id) {

        this.fb_id = fb_id;
    }

    public String getFacebookId() {

        return this.fb_id;
    }

    public Float getBtc() {
        return btc;
    }

    public void setBtc(Float btc) {
        this.btc = btc;
    }

    public void setState(int state) {

        this.state = state;
    }

    public int getState() {

        return this.state;
    }

    public void setNotificationsCount(int notificationsCount) {

        this.notificationsCount = notificationsCount;

//        updateMainActivityBadges(this, "");
    }

    public int getNotificationsCount() {

        return this.notificationsCount;
    }

    public String getCurrent_connected() {

//        String x = "";
        return sharedPref.getString("current_connected", "44");
//        setCurrent_connected(x);
//        return current_connected;
    }

    public void setCurrent_connected(String current_connected) {
        sharedPref.edit().putString("current_connected", current_connected).apply();
//        this.current_connected = current_connected;
    }

    public void setGuestsCount(int guestsCount) {
        this.guestsCount = guestsCount;

//        return x;
//        return this.current_connected;
    }

    public void setSku_1(String sku_1) {
        this.sku_1 = sku_1;
    }

    public String getSku_1() {
        return this.sku_1;
    }

    public void setSku_2(String sku_2) {
        this.sku_2 = sku_2;
    }

    public String getSku_2() {
        return this.sku_2;
    }

    public void setSku_3(String sku_3) {
        this.sku_3 = sku_3;
    }

    public String getSku_3() {
        return this.sku_3;
    }

    public void setSku_4(String sku_4) {
        this.sku_4 = sku_4;
    }
    public void setSku_5(String sku_5) {
        this.sku_5 = sku_5;
    }

    public String getSku_4() {
        return this.sku_4;
    }
    public String getSku_5() {
        return this.sku_5;
    }

    public void setSku_1_title(String sku_1_title) {
        this.sku_1_title = sku_1_title;
    }

    public String getSku_1_title() {
        return this.sku_1_title;
    }

    public void setSku_2_title(String sku_2_title) {
        this.sku_2_title = sku_2_title;
    }

    public String getSku_2_title() {
        return this.sku_2_title;
    }

    public void setSku_3_title(String sku_3_title) {
        this.sku_3_title = sku_3_title;
    }

    public String getSku_3_title() {
        return this.sku_3_title;
    }

    public void setSku_4_title(String sku_4_title) {
        this.sku_4_title = sku_4_title;
    }


    public void setSku_5_title(String sku_5_title) {
        this.sku_5_title = sku_5_title;
    }


    public void setSku_1_save(String sku_1_save) {
        this.sku_1_save = sku_1_save;
    }

    public void setSku_2_save(String _sku_2_save) {
        this.sku_2_save = _sku_2_save;
    }

    public void setSku_3_save(String sku_3_save) {
        this.sku_3_save = sku_3_save;
    }

    public void setSku_4_save(String sku_4_save) {
        this.sku_4_save = sku_4_save;
    }
    public void setSku_5_save(String sku_5_save) {
        this.sku_5_save = sku_5_save;
    }

    public String getSku_1_save() {
        return this.sku_1_save;
    }

    public String getSku_2_save() {
        return this.sku_2_save;
    }

    public String getSku_3_save() {
        return this.sku_3_save;
    }

    public String getSku_4_save() {
        return this.sku_4_save;
    }

    public String getSku_5_save() {
        return this.sku_5_save;
    }

    public void setSku_1_price(Float sku_1_price) {
        this.sku_1_price = sku_1_price;
    }

    public int getFree_time() {
        return free_time;
    }

    public void setFree_time(int free_time) {
        this.free_time = free_time;
    }

    public String getFree_time_msg() {
        return free_time_msg;
    }

    public void setFree_time_msg(String free_time_msg) {
        this.free_time_msg = free_time_msg;
    }

    public String getFree_time_msg_after() {
        return free_time_msg_after;
    }

    public void setFree_time_msg_after(String free_time_msg_after) {
        this.free_time_msg_after = free_time_msg_after;
    }

    public void setSku_2_price(Float sku_2_price) {
        this.sku_2_price = sku_2_price;
    }

    public void setSku_3_price(Float sku_3_price) {
        this.sku_3_price = sku_3_price;
    }
    public void setSku_4_price(Float sku_4_price) {
        this.sku_4_price = sku_4_price;
    }
    public void setSku_5_price(Float sku_5_price) {
        this.sku_5_price = sku_5_price;
    }

    public void setGoogle_billing_key(String bk){
        this.Google_billing_key=bk;
    }
    public String getGoogle_billing_key(){
        return this.Google_billing_key;
    }

    public void setflutterwave_pub_key(String bk){
        this.flutterwave_pub_key=bk;
    }
    public String getflutterwave_pub_key(){
        return this.flutterwave_pub_key;
    }

    public void setappodeal_key(String bk){
        this.appodeal_key=bk;
    }
    public String getAppodeal_key(){
        return this.appodeal_key;
    }

    public void setflutterwave_enc_key(String bk){
        this.flutterwave_enc_key=bk;
    }
    public String getflutterwave_enc_key(){
        return this.flutterwave_enc_key;
    }






    public Float getSku_1_price() {
        return this.sku_1_price;
    }

    public Float getSku_2_price() {
        return this.sku_2_price;
    }

    public Float getSku_3_price() {
        return this.sku_3_price;
    }

    public Float getSku_4_price() {
        return this.sku_4_price;
    }
    public Float getSku_5_price() {
        return this.sku_5_price;
    }

    public void setSku_1_price_title(String sku_1_price_title) {
        this.sku_1_price_title = sku_1_price_title;
    }

    public void setSku_2_price_title(String sku_2_price_title) {
        this.sku_2_price_title = sku_2_price_title;
    }

    public void setSku_3_price_title(String sku_3_price_title) {
        this.sku_3_price_title = sku_3_price_title;
    }

    public void setSku_4_price_title(String sku_4_price_title) {
        this.sku_4_price_title = sku_4_price_title;
    }

    public void setSku_5_price_title(String sku_5_price_title) {
        this.sku_5_price_title = sku_5_price_title;
    }



    public String getSku_4_title() {
        return this.sku_4_title;
    }
    public String getSku_5_title() {
        return this.sku_5_title;
    }

    public String getSku_1_price_title() {
        return this.sku_1_price_title;
    }

    public String getSku_2_price_title() {
        return this.sku_2_price_title;
    }

    public String getSku_3_price_title() {
        return this.sku_3_price_title;
    }

    public String getSku_4_price_title() {
        return this.sku_4_price_title;
    }
    public String getSku_5_price_title() {
        return this.sku_5_price_title;
    }


    public int getGuestsCount() {

        return this.guestsCount;
    }

    public void setMessagesCount(int messagesCount) {

        this.messagesCount = messagesCount;

//        updateMainActivityBadges(this, "");
    }

    public int getMessagesCount() {

        return this.messagesCount;
    }

    public void setSeenTyping(int seenTyping) {

        this.seenTyping = seenTyping;
    }

    public int getSeenTyping() {

        return this.seenTyping;
    }

    public void setNewFriendsCount(int newFriendsCount) {

        this.newFriendsCount = newFriendsCount;
    }

    public int getNewFriendsCount() {

        return this.newFriendsCount;
    }

    public void setNewMatchesCount(int newMatchesCount) {

        this.newMatchesCount = newMatchesCount;
    }

    public int getNewMatchesCount() {

        return this.newMatchesCount;
    }

    public void setAllowMessagesGCM(int allowMessagesGCM) {

        this.allowMessagesGCM = allowMessagesGCM;
    }

    public int getAllowMessagesGCM() {

        return this.allowMessagesGCM;
    }

    public void setAllowGiftsGCM(int allowGiftsGCM) {

        this.allowGiftsGCM = allowGiftsGCM;
    }

    public int getAllowGiftsGCM() {

        return this.allowGiftsGCM;
    }

    public void setAllowCommentReplyGCM(int allowCommentReplyGCM) {

        this.allowCommentReplyGCM = allowCommentReplyGCM;
    }

    public int getAllowCommentReplyGCM() {

        return this.allowCommentReplyGCM;
    }

    public void setAllowFollowersGCM(int allowFollowersGCM) {

        this.allowFollowersGCM = allowFollowersGCM;
    }

    public int getAllowFollowersGCM() {

        return this.allowFollowersGCM;
    }

    public void setAllowCommentsGCM(int allowCommentsGCM) {

        this.allowCommentsGCM = allowCommentsGCM;
    }

    public int getAllowCommentsGCM() {

        return this.allowCommentsGCM;
    }

    public void setAllowLikesGCM(int allowLikesGCM) {

        this.allowLikesGCM = allowLikesGCM;
    }

    public int getAllowLikesGCM() {

        return this.allowLikesGCM;
    }

    public void setAllowMatchesGCM(int allowMatchesGCM) {

        this.allowMatchesGCM = allowMatchesGCM;
    }

    public int getAllowMatchesGCM() {

        return this.allowMatchesGCM;
    }

    public void setAllowMessages(int allowMessages) {

        this.allowMessages = allowMessages;
    }

    public int getAllowMessages() {

        return this.allowMessages;
    }

    public void setAllowComments(int allowComments) {

        this.allowComments = allowComments;
    }

    public int getAllowComments() {

        return this.allowComments;
    }

    public void setAllowPhotosComments(int allowPhotosComments) {

        this.allowPhotosComments = allowPhotosComments;
    }

    public int getAllowPhotosComments() {

        return this.allowPhotosComments;
    }

    // Privacy

    public void setAllowShowMyInfo(int allowShowMyInfo) {

        this.allowShowMyInfo = allowShowMyInfo;
    }

    public int getAllowShowMyInfo() {

        return this.allowShowMyInfo;
    }

    public void setAllowShowMyGallery(int allowShowMyGallery) {

        this.allowShowMyGallery = allowShowMyGallery;
    }

    public int getAllowShowMyGallery() {

        return this.allowShowMyGallery;
    }

    public void setAllowShowMyFriends(int allowShowMyFriends) {

        this.allowShowMyFriends = allowShowMyFriends;
    }

    public int getAllowShowMyFriends() {

        return this.allowShowMyFriends;
    }

    public void setAllowShowMyLikes(int allowShowMyLikes) {

        this.allowShowMyLikes = allowShowMyLikes;
    }

    public int getAllowShowMyLikes() {

        return this.allowShowMyLikes;
    }

    public void setAllowShowMyGifts(int allowShowMyGifts) {

        this.allowShowMyGifts = allowShowMyGifts;
    }

    public int getAllowShowMyGifts() {

        return this.allowShowMyGifts;
    }

    public void setAllowShowMyAge(int allowShowMyAge) {

        this.allowShowMyAge = allowShowMyAge;
    }

    public int getAllowShowMyAge() {

        return this.allowShowMyAge;
    }

    public void setAllowShowMySexOrientation(int allowShowMySexOrientation) {

        this.allowShowMySexOrientation = allowShowMySexOrientation;
    }

    public int getAllowShowMySexOrientation() {

        return this.allowShowMySexOrientation;
    }

    public void setAllowShowOnline(int allowShowOnline) {

        this.allowShowOnline = allowShowOnline;
    }

    public int getAllowShowOnline() {

        return this.allowShowOnline;
    }

    //

    public void setPhotoCreateAt(int photoCreateAt) {

        this.photoCreateAt = photoCreateAt;
    }

    public int getPhotoCreateAt() {

        return this.photoCreateAt;
    }

    public void setPhotoModerateAt(int photoModerateAt) {

        this.photoModerateAt = photoModerateAt;
    }

    public int getPhotoModerateAt() {

        return this.photoModerateAt;
    }

    //

    public void setAccountModerateAt(int accountModerateAt) {

        this.accountModerateAt = accountModerateAt;
    }

    public int getAccountModerateAt() {

        return this.accountModerateAt;
    }

    //

    public void setAdmob(int admob) {

        this.admob = admob;
    }

    public int getAdmob() {

        return this.admob;
    }

    public void setAllowRewardedAds(int allowRewardedAds) {

        this.allowRewardedAds = allowRewardedAds;
    }

    public int getAllowRewardedAds() {

        return this.allowRewardedAds;
    }

    public void setGhost(int ghost) {

        this.ghost = ghost;
    }

    public int getGhost() {

        return this.ghost;
    }

    public void setFreeMessagesCount(int freeMessagesCount) {

        this.freeMessagesCount = freeMessagesCount;
    }

    public int getFreeMessagesCount() {

        return this.freeMessagesCount;
    }

    public void setPro(int pro) {

        this.pro = pro;
    }

    public int getPro() {

        return this.pro;
    }

    public void setSubscribed(boolean pro) {

        this.subscription = pro;
    }

    public boolean getSubscribed() {

        return this.subscription;
    }

    public Boolean isPro() {

        if (this.pro > 0) {

            return true;
        }

        return false;
    }

    public void setVip(int vip) {

        this.vip = vip;
    }

    public int getVip() {

        return this.vip;
    }

    public void setBalance(int balance) {

        this.balance = balance;
    }

    public int getBalance() {

        return this.balance;
    }

    public void setCurrentChatId(int currentChatId) {

        this.currentChatId = currentChatId;
    }

    public int getCurrentChatId() {

        return this.currentChatId;
    }

    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;
    }

    public int getErrorCode() {

        return this.errorCode;
    }

    public String getUsername() {

        if (this.username == null) {

            this.username = "";
        }

        return this.username;
    }
    public String getEmail() {

        if (this.email == null) {

            this.email = "";
        }

        return this.email;
    }

    public String getUsernameSinch() {

        if (this.usernamesinch == null) {

            this.usernamesinch = "";
        }

        return this.usernamesinch;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public String getAccount_mode() {
        return account_mode;
    }

    public void setAccount_mode(String account_mode) {
        this.account_mode = account_mode;
    }

    public void setUsername(String username) {

        this.username = username;
    }
    public void setEmail(String email) {

        this.email = email;
    }

    public void setGender(String gender) {

        this.gender = gender;
    }
    public String getGender() {

        return this.gender;
    }
    public void setUsernameSinch(String username) {

        this.usernamesinch = username;
    }


    public String getPhone() {

        if (this.phone == null) {

            this.phone = "";
        }

        return this.phone;
    }

    public void setPhone(String phone) {

        this.phone = phone;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public void setFullname(String fullname) {

        this.fullname = fullname;
    }

    public String getFullname() {

        if (this.fullname == null) {

            this.fullname = "";
        }

        return this.fullname;
    }

    public void setVerify(int verify) {

        this.verify = verify;
    }

    public int getVerify() {

        return this.verify;
    }

    public void setPhotoUrl(String photoUrl) {

        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {

        if (this.photoUrl == null) {

            this.photoUrl = "";
        }

        return this.photoUrl;
    }

    public void setCoverUrl(String coverUrl) {

        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() {

        if (coverUrl == null) {

            this.coverUrl = "";
        }

        return this.coverUrl;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCountry() {

        if (this.country == null) {

            this.setCountry("");
        }

        return this.country;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public String getCity() {

        if (this.city == null) {

            this.setCity("");
        }

        return this.city;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getArea() {

        if (this.area == null) {

            this.setArea("");
        }

        return this.area;
    }

    public void setLat(Double lat) {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        this.lat = lat;
    }

    public Double getLat() {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        return this.lat;
    }

    public void setLng(Double lng) {

        if (this.lng == null) {

            this.lng = 0.000000;
        }

        this.lng = lng;
    }

    public Double getLng() {

        return this.lng;
    }

    public void setCircleItems(Boolean circle_items) {

        this.circle_items = circle_items;
    }

    public Boolean getCircleItems() {

        return this.circle_items;
    }

    public void setPhotoModerateUrl(String photoModerateUrl) {

        this.photoModerateUrl = photoModerateUrl;
    }

    public String getPhotoModerateUrl() {

        if (this.photoModerateUrl == null) {

            this.photoModerateUrl = "";
        }

        return this.photoModerateUrl;
    }

    public void setCoverModerateUrl(String coverModerateUrl) {

        this.coverModerateUrl = coverModerateUrl;
    }

    public String getCoverModerateUrl() {

        if (coverModerateUrl == null) {

            this.coverModerateUrl = "";
        }

        return this.coverModerateUrl;
    }

    public void readData() {

//        this.setId(sharedPref.getLong(getString(R.string.settings_account_id), 0));
        this.setUsername(sharedPref.getString("username", ""));
        this.setWallet(sharedPref.getString("wallet", ""));
        this.setExpires_at(sharedPref.getString("expires_at", ""));
        this.setAccount_mode(sharedPref.getString("account_mode", ""));
        this.setEmail(sharedPref.getString("email", ""));
        this.setGender(sharedPref.getString("gender", ""));
        this.setAccessToken(sharedPref.getString("token", ""));
//        this.setAllowMessagesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_messages_gcm), 1));

        this.setFullname(sharedPref.getString("fullname", ""));
        this.setPhotoUrl(sharedPref.getString("profile_pic", ""));
//        this.setCoverUrl(sharedPref.getString(getString(R.string.settings_account_cover_url), ""));

        this.setVerify(sharedPref.getInt("email_verified", 0));



        this.setFree_time_msg(sharedPref.getString("free_time_msg","You'll be disconnected after sometime, you need to buy premium to get long term connection"));
        this.setFree_time_msg_after(sharedPref.getString("free_time_msg_after","You're disconnected because you're using free account, please upgrade to prevent this disruption"));
        this.setFree_time(sharedPref.getInt("free_time",10));

        this.setSku_1(sharedPref.getString("sku_1","sku_1"));
        this.setSku_2(sharedPref.getString("sku_2","sku_1"));
        this.setSku_3(sharedPref.getString("sku_3","sku_1"));
        this.setSku_4(sharedPref.getString("sku_4","sku_1"));
        this.setSku_5(sharedPref.getString("sku_5","sku_1"));


        this.setSku_1_save(sharedPref.getString("sku_1_save","Save 10%"));
        this.setSku_2_save(sharedPref.getString("sku_2_save","Save 10%"));
        this.setSku_3_save(sharedPref.getString("sku_3_save","Save 10%"));
        this.setSku_4_save(sharedPref.getString("sku_4_save","Save 10%"));
        this.setSku_5_save(sharedPref.getString("sku_5_save","Save 10%"));


        this.setSku_1_title(sharedPref.getString("sku_1_title","1 Month"));
        this.setSku_2_title(sharedPref.getString("sku_2_title","--"));
        this.setSku_3_title(sharedPref.getString("sku_3_title","--"));
        this.setSku_4_title(sharedPref.getString("sku_4_title","--"));
        this.setSku_5_title(sharedPref.getString("sku_5_title","--"));

        this.setSku_1_price_title(sharedPref.getString("sku_1_price_title","$19/Mo"));
        this.setSku_2_price_title(sharedPref.getString("sku_2_price_title","--"));
        this.setSku_3_price_title(sharedPref.getString("sku_3_price_title","--"));
        this.setSku_4_price_title(sharedPref.getString("sku_4_price_title","--"));
        this.setSku_5_price_title(sharedPref.getString("sku_5_price_title","--"));

        float float_def = (float) 2.99;

        this.setSku_1_price(sharedPref.getFloat("sku_1_price", float_def));
        this.setSku_2_price(sharedPref.getFloat("sku_2_price",float_def));
        this.setSku_3_price(sharedPref.getFloat("sku_3_price",float_def));
        this.setSku_4_price(sharedPref.getFloat("sku_4_price",float_def));
        this.setSku_5_price(sharedPref.getFloat("sku_5_price",float_def));


//        this.setFreeMessagesCount(sharedPref.getInt(getString(R.string.settings_account_free_messages_count), 150));
        this.setSubscribed(sharedPref.getBoolean("subscription", false));

//        this.setAllowMatchesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_matches_gcm), 1));
//        this.setAllowLikesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_likes_gcm), 1));

//        this.setAdmob(sharedPref.getInt(getString(R.string.settings_account_admob), 1));

//        this.setBalance(sharedPref.getInt(getString(R.string.settings_account_balance), 0));

//        this.setCircleItems(sharedPref.getBoolean(getString(R.string.settings_account_circle_items), true));

        if (this.getInstance().getLat() == 0.000000 && this.getInstance().getLng() == 0.000000) {

            this.setLat(Double.parseDouble(sharedPref.getString("lat", "0.000000")));
            this.setLng(Double.parseDouble(sharedPref.getString("lng", "0.000000")));
        }
    }

    public void saveData() {

        sharedPref.edit().putInt("free_time", this.getFree_time()).apply();
        sharedPref.edit().putString("free_time_msg", this.getFree_time_msg()).apply();

        sharedPref.edit().putString("sku_1", this.getSku_1()).apply();

        sharedPref.edit().putFloat("btc",  this.getBtc()).apply();
        sharedPref.edit().putString("wallet",this.getWallet()).apply();

        sharedPref.edit().putString("sku_2", this.getSku_2()).apply();
        sharedPref.edit().putString("sku_3", this.getSku_3()).apply();
        sharedPref.edit().putString("sku_4", this.getSku_4()).apply();
        sharedPref.edit().putString("sku_5", this.getSku_5()).apply();



        sharedPref.edit().putString("sku_1_title", this.getSku_1_title()).apply();
        sharedPref.edit().putString("sku_2_title", this.getSku_2_title()).apply();
        sharedPref.edit().putString("sku_3_title", this.getSku_3_title()).apply();
        sharedPref.edit().putString("sku_4_title", this.getSku_4_title()).apply();
        sharedPref.edit().putString("sku_5_title", this.getSku_5_title()).apply();

        sharedPref.edit().putString("sku_1_save", this.getSku_1_save()).apply();
        sharedPref.edit().putString("sku_2_save", this.getSku_2_save()).apply();
        sharedPref.edit().putString("sku_3_save", this.getSku_3_save()).apply();
        sharedPref.edit().putString("sku_4_save", this.getSku_4_save()).apply();
        sharedPref.edit().putString("sku_5_save", this.getSku_5_save()).apply();


        sharedPref.edit().putFloat("sku_1_price", this.getSku_1_price()).apply();
        sharedPref.edit().putFloat("sku_2_price", this.getSku_2_price()).apply();
        sharedPref.edit().putFloat("sku_3_price", this.getSku_3_price()).apply();
        sharedPref.edit().putFloat("sku_4_price", this.getSku_4_price()).apply();
        sharedPref.edit().putFloat("sku_5_price", this.getSku_5_price()).apply();

        sharedPref.edit().putString("sku_1_price_title", this.getSku_1_price_title()).apply();
        sharedPref.edit().putString("sku_2_price_title", this.getSku_2_price_title()).apply();
        sharedPref.edit().putString("sku_3_price_title", this.getSku_3_price_title()).apply();
        sharedPref.edit().putString("sku_4_price_title", this.getSku_4_price_title()).apply();
        sharedPref.edit().putString("sku_5_price_title", this.getSku_5_price_title()).apply();


        sharedPref.edit().putString("username", this.getUsername()).apply();


        sharedPref.edit().putString("expires_at", this.getExpires_at()).apply();
        sharedPref.edit().putString("account_mode", this.getAccount_mode()).apply();

        sharedPref.edit().putString("email", this.getEmail()).apply();
        sharedPref.edit().putString("gender", this.getGender()).apply();
        sharedPref.edit().putString("token", this.getAccessToken()).apply();
//        sharedPref.edit().putInt("all", this.getAllowMessagesGCM()).apply();

        sharedPref.edit().putString("fullname", this.getFullname()).apply();
        sharedPref.edit().putString("profile_pic", this.getPhotoUrl()).apply();
//        sharedPref.edit().putString(getString(R.string.settings_account_cover_url), this.getCoverUrl()).apply();

        sharedPref.edit().putInt("email_verified", this.getVerify()).apply();

//        sharedPref.edit().putInt(getString(R.string.settings_account_free_messages_count), this.getFreeMessagesCount()).apply();
        sharedPref.edit().putBoolean("subscription", this.getSubscribed()).apply();

//        sharedPref.edit().putInt(getString(R.string.settings_account_allow_matches_gcm), this.getAllowMatchesGCM()).apply();
//        sharedPref.edit().putInt(getString(R.string.settings_account_allow_likes_gcm), this.getAllowLikesGCM()).apply();

//        sharedPref.edit().putInt(getString(R.string.settings_account_admob), this.getAdmob()).apply();

//        sharedPref.edit().putInt(getString(R.string.settings_account_balance), this.getBalance()).apply();

//        sharedPref.edit().putBoolean(getString(R.string.settings_account_circle_items), this.getCircleItems()).apply();

        sharedPref.edit().putString("lat", Double.toString(this.getLat())).apply();
        sharedPref.edit().putString("lng", Double.toString(this.getLng())).apply();
    }

    public void removeData() {


        sharedPref.edit().putString("username", "").apply();
        sharedPref.edit().putString("token", "").apply();
//        sharedPref.edit().putInt(getString(R.string.settings_account_allow_messages_gcm), 1).apply();
    }

//    public static void updateMainActivityBadges(Context context, String message) {
//
//        Intent intent = new Intent(TAG_UPDATE_BADGES);
//        intent.putExtra("message", message); // if need message
//        context.sendBroadcast(intent);
//    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public static synchronized ICSOpenVPNApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

//    public ImageLoader getImageLoader() {
//        getRequestQueue();
//        if (mImageLoader == null) {
//            mImageLoader = new ImageLoader(this.mRequestQueue,
//                    new LruBitmapCache());
//        }
//        return this.mImageLoader;
//    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public static void invite(Context context){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String sAux = "\n"+context.getString(R.string.msg_invite)+"\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="+context.getPackageName()+"\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            Log.e("Error",e.toString());
            //e.toString();
        }
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
//        Log.e("Justadd","Newwew");
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


   /* @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Background message
        CharSequence name = getString(R.string.channel_name_background);
        NotificationChannel mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_BG_ID,
                name, NotificationManager.IMPORTANCE_MIN);

        mChannel.setDescription(getString(R.string.channel_description_background));
        mChannel.enableLights(false);

        mChannel.setLightColor(Color.DKGRAY);
        mNotificationManager.createNotificationChannel(mChannel);

        // Connection status change messages

        name = getString(R.string.channel_name_status);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_NEWSTATUS_ID,
                name, NotificationManager.IMPORTANCE_LOW);

        mChannel.setDescription(getString(R.string.channel_description_status));
        mChannel.enableLights(true);

        mChannel.setLightColor(Color.BLUE);
        mNotificationManager.createNotificationChannel(mChannel);
    }*/
}
