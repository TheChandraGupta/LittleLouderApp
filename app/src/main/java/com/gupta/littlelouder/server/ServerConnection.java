package com.gupta.littlelouder.server;

/**
 * Created by GUPTA on 14-May-16.
 */
public class ServerConnection {

    // http://localhost:8088/LittleLouderServices/

    private String IP = "192.168.0.9";
    private String PORT = "8088";
    private String APP = "LittleLouderServices";

    public String getIP() {
        return IP;
    }

    public String getPORT() {
        return PORT;
    }

    public String getAPP() {
        return APP;
    }
}
