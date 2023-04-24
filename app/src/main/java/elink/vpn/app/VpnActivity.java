

package elink.vpn.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.VpnService;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import elink.vpn.app.R;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;
import elink.vpn.app.util.VpnStateListener;


public class VpnActivity extends AppCompatActivity implements VpnStateListener.VpnState {

    private static final int RC_DISCONNECT = 100;

    public SharedPreferences prefs;

    @BindView(R.id.btnManageState)
    MaterialButton btnManageState;

    @BindView(R.id.btnChangeServer)
    MaterialButton btnChangeServer;


    @BindView(R.id.tvVpnState)
    TextView tvVpnState;

    @BindView(R.id.btnCheckMyIP)
    Button btnCheckMyIP;
    @BindView(R.id.progressBar)
    ProgressBar loadingView;

    private VpnProfile vpnProfile;
    private VpnStateListener vpnStateListener;
    private ProfileManager profileManager;
    private boolean paused;

    @SuppressLint("RestrictedApi")
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn);
        ButterKnife.bind(this);
        vpnProfile = (VpnProfile) getIntent().getSerializableExtra("vpn");
        if (vpnProfile == null) {
            finish();
            return;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        prefs = this.getSharedPreferences(
                "elink.vpn.app", Context.MODE_PRIVATE);
//        Toast.makeText(this,vpnProfile.getName(),Toast.LENGTH_LONG).show();

        prefs.edit().putString("last_con", vpnProfile.getName()).apply();


        if (ICSOpenVPNApplication.getInstance().getAccount_mode().equals("Free")) {


        } else {
        }


//        VpnStatus.ByteCountListener
        setTitle(vpnProfile.getName());
        profileManager = ProfileManager.getInstance(this);
        profileManager.addProfile(vpnProfile);
        profileManager.saveProfileList(this);
        vpnStateListener = new VpnStateListener(this, this);
        toggleVpnStatus();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnManageState, R.id.btnChangeServer, R.id.btnCheckMyIP})
    public void onClick(View view) {
        if (view.getId() == R.id.btnManageState) {
            toggleVpnStatus();
        }
        if (view.getId() == R.id.btnChangeServer) {
            VpnActivity.super.onBackPressed();
        }

        if (view.getId() == R.id.btnCheckMyIP) {
            String url = "https://trejj.net/whois.php";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

    }


    private void toggleVpnStatus() {
        if (vpnProfile != null) {
            startOrStopVPN(vpnProfile);
        }
    }


    private void startOrStopVPN(VpnProfile profile) {
//        profile.mPassword = "faisal";
//        profile.mUsername = "faisal";
        if (paused) {
            paused = false;
            Intent pauseVPN = new Intent(this, OpenVPNService.class);
            pauseVPN.setAction(OpenVPNService.RESUME_VPN);
            startService(pauseVPN);
            return;
        }
        if (VpnStatus.isVPNActive() && profile.getUUIDString().equals(VpnStatus.getLastConnectedVPNProfile())) {
            Intent disconnectVPN = new Intent(this, DisconnectVPN.class);
            prefs.edit().putString("last_con", "Successfully!").apply();
            startActivityForResult(disconnectVPN, RC_DISCONNECT);
        } else {
            VpnStatus.setConnectedVPNProfile(profile.getUUIDString());
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 0);
            } else {
                startVPN(profile);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_DISCONNECT && resultCode == RESULT_OK) {
            stateDisconnected();
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            startVPN(vpnProfile);
        }
    }

    private void startVPN(VpnProfile profile) {
        VPNLaunchHelper.startOpenVpn(profile, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (vpnProfile != null) {
            vpnStateListener.listen();
        }
    }

    @Override
    protected void onStop() {
        if (vpnProfile != null) {
            vpnStateListener.stopListening();
        }
        super.onStop();
    }


    @Override
    public void stateConnecting() {
        loadingView.setVisibility(View.VISIBLE);
        btnManageState.setEnabled(false);
        tvVpnState.setText(R.string.state_connecting);
    }

    @Override
    public void stateConnected() {
        paused = false;
        loadingView.setVisibility(View.GONE);
        btnManageState.setEnabled(true);
        btnManageState.setText(R.string.state_disconnected);
        tvVpnState.setText(R.string.vpn_connected);
    }

    @Override
    public void stateDisconnected() {
        loadingView.setVisibility(View.GONE);
        btnManageState.setEnabled(true);
        btnManageState.setText(R.string.connect);
        tvVpnState.setText(R.string.vpn_disconnected);

       /* final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(VpnActivity.this, MainActivity.class));

            }
        }, 500);*/
    }

    @Override
    public void statePaused() {
        paused = true;
        loadingView.setVisibility(View.GONE);
        btnManageState.setText(R.string.resumevpn);
        btnManageState.setEnabled(true);
        tvVpnState.setText(R.string.paused);
    }

}
