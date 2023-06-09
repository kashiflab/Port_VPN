package de.blinkt.openvpn.core;

import android.content.Context;

import net.openvpn.ovpn3.ClientAPI_Config;
import net.openvpn.ovpn3.ClientAPI_EvalConfig;
import net.openvpn.ovpn3.ClientAPI_Event;
import net.openvpn.ovpn3.ClientAPI_ExternalPKICertRequest;
import net.openvpn.ovpn3.ClientAPI_ExternalPKISignRequest;
import net.openvpn.ovpn3.ClientAPI_LogInfo;
import net.openvpn.ovpn3.ClientAPI_OpenVPNClient;
import net.openvpn.ovpn3.ClientAPI_ProvideCreds;
import net.openvpn.ovpn3.ClientAPI_Status;
import net.openvpn.ovpn3.ClientAPI_TransportStats;

import de.blinkt.openvpn.VpnProfile;

public class OpenVPNThreadv3 extends ClientAPI_OpenVPNClient implements Runnable, OpenVPNManagement {

	static {
		/*System.loadLibrary("crypto");
		System.loadLibrary("ssl");*/
        //System.loadLibrary("polarssl-dynamic");
		System.loadLibrary("ovpn3");
	}

	private VpnProfile mVp;
	private OpenVPNService mService;

	public OpenVPNThreadv3(OpenVPNService openVpnService, VpnProfile vp) {
		init_process();
		mVp =vp;
		mService =openVpnService;
	}

	@Override
	public void run() {
		String configstr = mVp.getConfigFile((Context)mService,true);
		if(!setConfig(configstr))
			return;
		setUserPW();
		VpnStatus.logInfo(platform());
        VpnStatus.logInfo(copyright());


		StatusPoller statuspoller = new StatusPoller(OpenVPNManagement.mBytecountInterval*1000);
		new Thread(statuspoller,"Status Poller").start();

		ClientAPI_Status status = connect();
		if(status.getError()) {
            VpnStatus.logError(String.format("connect() error: %s: %s",status.getStatus(),status.getMessage()));
		} else {
            VpnStatus.logDebug("OpenVPN3 thread finished");
		}
		statuspoller.stop();
	}


    @Override
	public boolean tun_builder_set_remote_address(String address, boolean ipv6) {
		mService.setMtu(1500);
		return true;
	}

	@Override
	public boolean tun_builder_set_mtu(int mtu) {
		mService.setMtu(mtu);
		return true;
	}
	@Override
	public boolean tun_builder_add_dns_server(String address, boolean ipv6) {
		mService.addDNS(address);
		return true;
	}

    @Override
    public boolean tun_builder_add_route(String address, int prefix_length, int metric, boolean ipv6) {
		if (address.equals("remote_host"))
			return false;
		
		if(ipv6)
			mService.addRoutev6(address + "/" + prefix_length,"tun");
		else
			mService.addRoute(new CIDRIP(address, prefix_length), true);
		return true;
	}

    @Override
    public boolean tun_builder_exclude_route(String address, int prefix_length, int metric, boolean ipv6) {
        if(ipv6)
            mService.addRoutev6(address + "/" + prefix_length, "wifi0");
        else {
			CIDRIP route = new CIDRIP(address, prefix_length);
            mService.addRoute(route, false);
        }
        return true;
    }



    @Override
	public boolean tun_builder_add_search_domain(String domain) {
		mService.setDomain(domain);
		return true;
	}

	@Override
	public int tun_builder_establish() {
		return mService.openTun().detachFd();
	}

	@Override
	public boolean tun_builder_set_session_name(String name) {
        VpnStatus.logDebug("We should call this session" + name);
		return true;
	}




    @Override
    public boolean tun_builder_add_address(String address, int prefix_length, String gateway, boolean ipv6, boolean net30) {
        if(!ipv6)
            mService.setLocalIP(new CIDRIP(address, prefix_length));
        else
            mService.setLocalIPv6(address+ "/" + prefix_length);
        return true;
    }

	@Override
	public boolean tun_builder_new() {

		return true;
	}


    @Override
    public boolean tun_builder_set_layer(int layer) {
        return layer == 3;
    }


    @Override
    public boolean tun_builder_reroute_gw(boolean ipv4, boolean ipv6, long flags) {
		if (ipv4)
			mService.addRoute("0.0.0.0", "0.0.0.0", "127.0.0.1", OpenVPNService.VPNSERVICE_TUN);

		if (ipv6)
			mService.addRoutev6("::/0", OpenVPNService.VPNSERVICE_TUN);

        return true;
    }



	private boolean setConfig(String vpnconfig) {

		ClientAPI_Config config = new ClientAPI_Config();
		if(mVp.getPasswordPrivateKey()!=null)
			config.setPrivateKeyPassword(mVp.getPasswordPrivateKey());

		config.setContent(vpnconfig);
		config.setTunPersist(mVp.mPersistTun);
        config.setGuiVersion(mVp.getVersionEnvString(mService));
        //config.setPlatformVersion(mVp.getPlatformVersionEnvString());
		config.setExternalPkiAlias("extpki");

		ClientAPI_EvalConfig ec = eval_config(config);
		if(ec.getExternalPki()) {
            VpnStatus.logDebug("OpenVPN3 core assumes an external PKI config");
		}
		if (ec.getError()) {
            VpnStatus.logError("OpenVPN config file parse error: " + ec.getMessage());
			return false;
		} else {
			config.setContent(vpnconfig);
			return true;
		}
	}

	@Override
	public void external_pki_cert_request(ClientAPI_ExternalPKICertRequest certreq) {
        VpnStatus.logDebug("Got external PKI certificate request from OpenVPN core");
		String[] ks = mVp.getKeyStoreCertificates((Context) mService);
		if(ks==null) {
			certreq.setError(true);
			certreq.setErrorText("Error in pki cert request");
			return;
		}

        String supcerts = ks[0];
        /* FIXME: How to differentiate between chain and ca certs in OpenVPN 3? */
        if (ks[1]!=null)
            supcerts += "\n" + ks[1];
		certreq.setSupportingChain(supcerts);
		certreq.setCert(ks[2]);
		certreq.setError(false);
	}

	@Override
	public void external_pki_sign_request(ClientAPI_ExternalPKISignRequest signreq) {
		VpnStatus.logDebug("Got external PKI signing request from OpenVPN core");
		signreq.setSig(mVp.getSignedData(signreq.getData()));
	}

	void setUserPW() {
		if(mVp.isUserPWAuth()) {
			ClientAPI_ProvideCreds creds = new ClientAPI_ProvideCreds();
			creds.setCachePassword(true);
			creds.setPassword(mVp.getPasswordAuth());
			creds.setUsername(mVp.mUsername);
			provide_creds(creds);
		}
	}

	@Override
	public boolean socket_protect(int socket) {
		boolean b= mService.protect(socket);
		return b;

	}

	@Override
	public boolean stopVPN(boolean replaceConnection) {
		stop();
		return false;
	}

	@Override
	public void networkChange(boolean sameNetwork) {
		reconnect(1);
	}

	@Override
	public void setPauseCallback(PausedStateCallback callback) {
	}

	@Override
	public void log(ClientAPI_LogInfo arg0) {
		String logmsg =arg0.getText();
		while (logmsg.endsWith("\n"))
			logmsg = logmsg.substring(0, logmsg.length()-1);

        VpnStatus.logInfo(logmsg);
	}

	@Override
	public void event(ClientAPI_Event event) {
		VpnStatus.updateStateString(event.getName(), event.getInfo());
		if(event.getError())
            VpnStatus.logError(String.format("EVENT(Error): %s: %s",event.getName(),event.getInfo()));
	}

	@Override
	public boolean pause_on_connection_timeout() {
        VpnStatus.logInfo("pause on connection timeout?! ");
		return true;
	}


	// When a connection is close to timeout, the core will call this
	// method.  If it returns false, the core will disconnect with a
	// CONNECTION_TIMEOUT event.  If true, the core will enter a PAUSE
	// state.

	@Override
	public void stop() {
		super.stop();
		mService.openvpnStopped();
	}

	@Override
	public void reconnect() {
		reconnect(1);
	}

	@Override
	public void pause(pauseReason reason) {
		super.pause(reason.toString());
	}

	class StatusPoller implements  Runnable
	{
		boolean mStopped=false;
		private long mSleeptime;

		public StatusPoller(long sleeptime) {
			mSleeptime=sleeptime;
		}

		public void run() {
			while(!mStopped) {
				try {
					Thread.sleep(mSleeptime);
				} catch (InterruptedException e) {
				}
				ClientAPI_TransportStats t = transport_stats();
				long in = t.getBytesIn();
				long out = t.getBytesOut();
				VpnStatus.updateByteCount(in, out);
			}
		}

		public void stop() {
			mStopped=true;
		}
	}

}
