package elink.vpn.app.App;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import elink.vpn.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import elink.vpn.app.Constants;
import elink.vpn.app.util.CustomRequest;

//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.RetryPolicy;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.Volley;
//import com.google.firebase.FirebaseApp;
//import com.klitim.chat.R;
//import com.klitim.chat.constants.Constants;
//import com.klitim.chat.util.CustomRequest;
//import com.klitim.chat.util.LruBitmapCache;

public class App extends Application implements Constants {

    public static final String TAG = App.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static App mInstance;

    private SharedPreferences sharedPref;

    private String username,email,gender,usernamesinch, phone, fullname, accessToken, gcmToken = "", fb_id = "", photoUrl, coverUrl, area = "", country = "", city = "";
    private Double lat = 0.000000, lng = 0.000000;
    private long id;
    private int state, admob = 1, verify, balance = 0, vip, ghost, pro, freeMessagesCount, allowShowMyAge, allowShowMySexOrientation, allowShowOnline, allowShowMyInfo, allowShowMyGallery, allowShowMyFriends, allowShowMyLikes, allowShowMyGifts, allowPhotosComments, allowComments, allowMessages, allowMatchesGCM, allowLikesGCM, allowCommentsGCM, allowFollowersGCM, allowMessagesGCM, allowGiftsGCM, allowCommentReplyGCM, errorCode, currentChatId = 0, notificationsCount = 0, messagesCount = 0, guestsCount = 0, newFriendsCount = 0, newMatchesCount = 0, seenTyping = 1, allowRewardedAds = 1;

    private int photoCreateAt = 0, photoModerateAt = 0, accountModerateAt = 0;

    private Boolean circle_items = true;
    public Boolean subscription = false;
    private String photoModerateUrl = "", coverModerateUrl = "";
    private String flutterwave_key = "";
    private String flutterwave_encryption = "";

    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;

//        FirebaseApp.initializeApp(this);

        sharedPref = this.getSharedPreferences("account", Context.MODE_PRIVATE);

        this.readData();
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

            if (authObj.has("error_code")) {

                this.setErrorCode(authObj.getInt("error_code"));
            }

            if (!authObj.has("error")) {

                return false;
            }

            if (authObj.getBoolean("error")) {

                return false;
            }

            if (!authObj.has("account")) {

                return false;
            }

            JSONArray accountArray = authObj.getJSONArray("account");

            if (accountArray.length() > 0) {

                JSONObject accountObj = (JSONObject) accountArray.get(0);

                if (accountObj.has("pro")) {

                    this.setPro(accountObj.getInt("pro"));

                } else {

                    this.setPro(0);
                }

                if (accountObj.has("free_messages_count")) {

                    this.setFreeMessagesCount(accountObj.getInt("free_messages_count"));

                } else {

                    this.setFreeMessagesCount(0);
                }

                this.setUsername(accountObj.getString("username"));
                this.setEmail(accountObj.getString("email"));
                this.setPhone(accountObj.getString("phone"));
                this.setFullname(accountObj.getString("fullname"));
                this.setState(accountObj.getInt("state"));
                this.setAdmob(accountObj.getInt("admob"));
                this.setGhost(accountObj.getInt("ghost"));
                this.setVip(accountObj.getInt("vip"));
                this.setBalance(accountObj.getInt("balance"));
                this.setVerify(accountObj.getInt("verify"));
                this.setFacebookId(accountObj.getString("fb_id"));
                this.setAllowPhotosComments(accountObj.getInt("allowPhotosComments"));
                this.setAllowComments(accountObj.getInt("allowComments"));
                this.setAllowMessages(accountObj.getInt("allowMessages"));
                this.setAllowLikesGCM(accountObj.getInt("allowLikesGCM"));
                this.setAllowCommentsGCM(accountObj.getInt("allowCommentsGCM"));
                this.setAllowFollowersGCM(accountObj.getInt("allowFollowersGCM"));
                this.setAllowMessagesGCM(accountObj.getInt("allowMessagesGCM"));
                this.setAllowGiftsGCM(accountObj.getInt("allowGiftsGCM"));
                this.setAllowCommentReplyGCM(accountObj.getInt("allowCommentReplyGCM"));

                this.setAllowShowMyInfo(accountObj.getInt("allowShowMyInfo"));
                this.setAllowShowMyGallery(accountObj.getInt("allowShowMyGallery"));
                this.setAllowShowMyFriends(accountObj.getInt("allowShowMyFriends"));
                this.setAllowShowMyLikes(accountObj.getInt("allowShowMyLikes"));
                this.setAllowShowMyGifts(accountObj.getInt("allowShowMyGifts"));

                if (accountObj.has("allowShowMyAge")) {

                    this.setAllowShowMyAge(accountObj.getInt("allowShowMyAge"));
                }

                if (accountObj.has("allowShowMySexOrientation")) {

                    this.setAllowShowMySexOrientation(accountObj.getInt("allowShowMySexOrientation"));
                }

                if (accountObj.has("allowShowOnline")) {

                    this.setAllowShowOnline(accountObj.getInt("allowShowOnline"));
                }

                this.setPhotoUrl(accountObj.getString("lowPhotoUrl"));
                this.setCoverUrl(accountObj.getString("coverUrl"));

                if (accountObj.has("photoModerateUrl")) {

                    this.setPhotoModerateUrl(accountObj.getString("photoModerateUrl"));
                }

                if (accountObj.has("coverModerateUrl")) {

                    this.setCoverModerateUrl(accountObj.getString("coverModerateUrl"));
                }

                this.setNotificationsCount(accountObj.getInt("notificationsCount"));
                this.setGuestsCount(accountObj.getInt("guestsCount"));
                this.setMessagesCount(accountObj.getInt("messagesCount"));
                this.setNewFriendsCount(accountObj.getInt("newFriendsCount"));

                if (accountObj.has("newMatchesCount")) {

                    this.setNewMatchesCount(accountObj.getInt("newMatchesCount"));
                }

                if (accountObj.has("allowMatchesGCM")) {

                    this.setAllowMatchesGCM(accountObj.getInt("allowMatchesGCM"));
                }

                if (App.getInstance().getLat() == 0.000000 && App.getInstance().getLng() == 0.000000) {

                    this.setLat(accountObj.getDouble("lat"));
                    this.setLng(accountObj.getDouble("lng"));
                }

                if (accountObj.has("photoCreateAt")) {

                    this.setPhotoCreateAt(accountObj.getInt("photoCreateAt"));
                }

                if (accountObj.has("photoModerateAt")) {

                    this.setPhotoModerateAt(accountObj.getInt("photoModerateAt"));
                }

                if (accountObj.has("accountModerateAt")) {

                    this.setAccountModerateAt(accountObj.getInt("accountModerateAt"));
                }

                Log.d("Account", accountObj.toString());
            }

            this.setId(authObj.getLong("accountId"));
            this.setAccessToken(authObj.getString("accessToken"));

            this.saveData();

            this.getSettings();

            if (getGcmToken().length() != 0) {

                setGcmToken(getGcmToken());
            }

            return true;

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

    public void setGuestsCount(int guestsCount) {

        this.guestsCount = guestsCount;
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

    public void setFlutterwave_key(String key){
        this.flutterwave_key=key;
    }

    public void setFlutterwave_encryption(String enc){
        this.flutterwave_encryption = enc;
    }

    public String getFlutterwave_key(){
        return this.flutterwave_key;
    }
    public String getFlutterwave_encryption(){
        return this.flutterwave_encryption;
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
        this.setEmail(sharedPref.getString("email", ""));
        this.setGender(sharedPref.getString("gender", ""));
        this.setAccessToken(sharedPref.getString("token", ""));
//        this.setAllowMessagesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_messages_gcm), 1));

        this.setFullname(sharedPref.getString("fullname", ""));
        this.setPhotoUrl(sharedPref.getString("profile_pic", ""));
//        this.setCoverUrl(sharedPref.getString(getString(R.string.settings_account_cover_url), ""));

        this.setVerify(sharedPref.getInt("email_verified", 0));

//        this.setFreeMessagesCount(sharedPref.getInt(getString(R.string.settings_account_free_messages_count), 150));
        this.setSubscribed(sharedPref.getBoolean("subscription", false));

//        this.setAllowMatchesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_matches_gcm), 1));
//        this.setAllowLikesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_likes_gcm), 1));

//        this.setAdmob(sharedPref.getInt(getString(R.string.settings_account_admob), 1));

//        this.setBalance(sharedPref.getInt(getString(R.string.settings_account_balance), 0));

//        this.setCircleItems(sharedPref.getBoolean(getString(R.string.settings_account_circle_items), true));

        if (App.getInstance().getLat() == 0.000000 && App.getInstance().getLng() == 0.000000) {

            this.setLat(Double.parseDouble(sharedPref.getString("lat", "0.000000")));
            this.setLng(Double.parseDouble(sharedPref.getString("lng", "0.000000")));
        }
    }

    public void saveData() {


        sharedPref.edit().putString("username", this.getUsername()).apply();
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

    public static synchronized App getInstance() {
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
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}