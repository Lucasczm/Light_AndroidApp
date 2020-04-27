package br.com.maplus.light;

public class Configuration {

    private String authToken;
    private String deviceIP;
    private boolean useInternet;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public boolean isUseInternet() {
        return useInternet;
    }

    public void setUseInternet(boolean useInternet) {
        this.useInternet = useInternet;
    }

}
