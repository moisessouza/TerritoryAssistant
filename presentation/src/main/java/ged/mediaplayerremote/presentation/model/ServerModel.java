package ged.mediaplayerremote.presentation.model;

/**
 * Class representing MPC-HC Server in presentation layer.
 */
public class ServerModel {
    private String ip;
    private String hostName;
    private String port;
    private int connectionTimeout;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public String toString() {
        return hostName + " : " + ip;
    }
}
