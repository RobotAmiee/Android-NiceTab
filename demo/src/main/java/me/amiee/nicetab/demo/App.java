package me.amiee.nicetab.demo;


public class App {
    private String mName;
    private String mIconUrl;
    private String mAppUrl;

    public App(String name, String iconUrl, String appUrl) {
        mName = name;
        mIconUrl = iconUrl;
        mAppUrl = appUrl;
    }

    public String getName() {
        return mName;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public String getAppUrl() {
        return mAppUrl;
    }
}
