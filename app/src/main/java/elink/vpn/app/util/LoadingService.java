package elink.vpn.app.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.Constants;
import io.reactivex.Observable;

public final class LoadingService {
    private static final LoadingService instance;

    static {
        instance = new LoadingService();
    }

    private final        HashMap<String, VpnProfile> cache;

    private LoadingService() {
        cache = new HashMap<>();
    }

    public static LoadingService getInstance() {
        return instance;
    }

    public Observable<List<Vpn>> loadVpn() {
        return Observable.create(emitter -> {

            Log.e("I cam ","here 1");


            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.API_DOMAIN+"get_servers", null,
                    response -> {

                        Log.e("I cam ","here 2");
                        Log.e("just",response.toString());

                        try {

                            if (response.getString("action").equals("success")) {

                                Log.e("I cam ","here 3");

                                JSONArray arr = response.getJSONArray("servers");
                                Log.e("I cam ","here 4");


                                List<Vpn> list = new ArrayList<Vpn>();

                                for(int i = 0; i<=arr.length()-1; i++)
                                {
                                    JSONObject ob = arr.getJSONObject(i);

                                    String title = ob.getString("title");
                                    String location = ob.getString("location");
                                    String username = ob.getString("username");
                                    String password = ob.getString("password");
                                    String flag = ob.getString("image");
                                    String config_file = ob.getString("config_file");

                                    String server_name = ob.getString("server_name");
                                    String search_domain = ob.getString("search_domain");
                                    int premium = ob.getInt("premium");
                                    int vip = ob.getInt("vip");


                                    Vpn vpn = new Vpn();
                                    vpn.setName(title);
                                    vpn.setLocation(location);
                                    vpn.setUsername(username);
                                    vpn.setPassword(password);
                                    vpn.setFlag(flag);
                                    vpn.setConfig_file(config_file);
                                    vpn.setServer_name(server_name);
                                    vpn.setSearch_domain(search_domain);
                                    vpn.setPremium(premium);
                                    vpn.setVip(vip);

                                    vpn.setUrl(Constants.API_SERVER+"resources/uploads/servers/"+config_file);

                                    list.add(vpn);

                                }

                                if (!emitter.isDisposed()) {
                                    emitter.onNext(list);
                                    emitter.onComplete();
                                }



//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            else {

                                Log.e("I cam ","here 5");

                            }

                        } catch (JSONException e) {

                            Log.e("I cam ","here 6");
                            e.printStackTrace();
                        }
                    }, error -> Log.e("setGcmToken()", error.toString())) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", ICSOpenVPNApplication.getInstance().getAccessToken());
                    params.put("email",ICSOpenVPNApplication.getInstance().getEmail());
                    params.put("apiKey",Constants.API_KEY);
                    params.put("encryptionKey",Constants.ENCRYPTION_KEY);
                    Log.e("I send this token: ",ICSOpenVPNApplication.getInstance().getAccessToken());
                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            ICSOpenVPNApplication.getInstance().addToRequestQueue(jsonReq);

//            Vpn vpn = new Vpn();
//            vpn.setName("GODFATHER");
//            vpn.setUrl("https://www.dropbox.com/s/mhi1n0vgwv6xwnv/file1.ovpn?dl=1");
//
//            Vpn vpn1 = new Vpn();
//            vpn1.setName("GODFATHER 2");
//            vpn1.setUrl("https://www.dropbox.com/s/qwok2yfmr9a10am/file2.ovpn?dl=1");
//
//            Vpn vpn2 = new Vpn();
//            vpn2.setName("SCARFACE");
//            vpn2.setUrl("https://www.dropbox.com/s/zpu0g1zcc8jomea/file3.ovpn?dl=1");
//
//            Vpn vpn3 = new Vpn();
//            vpn3.setName("FULL METAL JACKET");
//            vpn3.setUrl("https://www.dropbox.com/s/svq8peigm9fq5m3/file4.ovpn?dl=1");
//
//            Vpn vpn4 = new Vpn();
//            vpn4.setName("PULP FICTION");
//            vpn4.setUrl("https://www.dropbox.com/s/82b1caz320ejejj/file5.ovpn?dl=1");
//
//            List<Vpn> list = new ArrayList<Vpn>() {{
//                add(vpn);
//                add(vpn1);
//                add(vpn2);
//                add(vpn3);
//                add(vpn4);
//            }};
//            if (!emitter.isDisposed()) {
//                emitter.onNext(list);
//                emitter.onComplete();
//            }
        });
    }

    public Observable<VpnProfile> getProfile(@NonNull Vpn vpn) {
        return Observable.create(emitter -> {
            VpnProfile saved = cache.get(vpn.getName());
            if (saved != null) {
                if (!emitter.isDisposed()) {
                    emitter.onNext(saved);
                    emitter.onComplete();
                }
                return;
            }
            String     data   = getStringFromUrl(vpn.getUrl());
            VpnProfile config = parseConfig(data, vpn.getName(),vpn.getUsername(),vpn.getPassword());
            if (config != null) {
                config.mServerName=vpn.getServer_name();
                config.mSearchDomain=vpn.getSearch_domain();
                //config.mServerName="69.55.61.97";
                if (!emitter.isDisposed()) {
                    cache.put(vpn.getName(), config);
                    emitter.onNext(config);
                    emitter.onComplete();
                }
            } else {
                if (!emitter.isDisposed()) {
                    emitter.onError(new IllegalArgumentException("failed to parse file"));
                }
            }
        });
    }

    private VpnProfile parseConfig(String data, String name,String username,String password) throws Exception {
        ConfigParser configParser = new ConfigParser();
        configParser.parseConfig(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        VpnProfile vpnProfile = configParser.convertProfile();
        vpnProfile.mName = name;
        vpnProfile.mUsername = username;
        vpnProfile.mPassword = password;
        return vpnProfile;
    }

    private String getStringFromUrl(String string) {
        try {


            URL url1 = new URL(string);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            line = stringBuilder.toString();
            httpURLConnection.disconnect();
            stringBuilder.delete(0, line.length());
            return line;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
}
