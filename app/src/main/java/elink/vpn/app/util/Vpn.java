package elink.vpn.app.util;

import java.io.Serializable;

public class Vpn implements Serializable {
    private String name;
    private String url;
    private String flag;
    private String location;
    private String username;
    private String password;
    private String config_file;
    private String server_name;
    private String search_domain;
    private int premium;
    private int vip;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPremium() {
        return premium;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getVip() {
        return vip;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    public String getFlag()
    {
        return this.flag;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
    public String getLocation()
    {
        return this.location;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getUsername()
    {
        return this.username;
    }

    public void setServer_name(String server_name)
    {
        this.server_name = server_name;
    }

    public String getServer_name()
    {
        return this.server_name;
    }

    public void setSearch_domain(String search_domain) {
        this.search_domain = search_domain;
    }

    public String getSearch_domain() {
        return search_domain;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getPassword()
    {
        return this.password;
    }
    public void setConfig_file(String config_file)
    {
        this.config_file = config_file;
    }
    public String getConfig_file()
    {
        return this.config_file;
    }
}
