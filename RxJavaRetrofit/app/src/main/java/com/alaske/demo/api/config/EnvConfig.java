package com.alaske.demo.api.config;

/**
 * EnvConfig
 * Created by zhaocheng on 16/3/7.
 */
public class EnvConfig {

    public static EnvMode envMode = EnvMode.TEST;

    public static enum EnvMode {
        DEV,
        INT,
        TEST,
        PRE,
        ONLINE
    }

    public static String getWebApiBaseUrl() {
        switch (envMode) {
            case DEV:
                return "http://simg.91xuexibao.com/";
            case INT:
                return "http://simg.91xuexibao.com/";
            case TEST:
                return "http://simg.91xuexibao.com/";
            case PRE:
                return "http://simg.91xuexibao.com/";
            case ONLINE:
                return "http://simg.91xuexibao.com/";
            default:
                return "http://simg.91xuexibao.com/";

        }
    }

}
