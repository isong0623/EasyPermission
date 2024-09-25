package com.dreaming.easy.lib.permission.request;

import com.dreaming.easy.lib.permission.request.feature.Feature;

import java.util.HashMap;
import java.util.Map;

public class FeatureConfigure {
    private static final Map<String,String> mFeatureNames = new HashMap<>();
    private static final Map<String, String> mFeatureMessages = new HashMap<>();

    public static void setFeatureName(String Feature, String name){
        mFeatureNames.put(Feature, name);
    }
    public static String getFeatureName(String Feature){
        return mFeatureNames.get(Feature);
    }

    public static void setFeatureMessage(String Feature, String message){
        mFeatureMessages.put(Feature, message);
    }
    public static String getFeatureMessage(String Feature){
        return mFeatureMessages.get(Feature);
    }

    static {
        mFeatureNames.put(Feature.Boot.toString()      , "自启动");
        mFeatureNames.put(Feature.Background.toString(), "后台运行");
        mFeatureNames.put(Feature.Battery.toString()   , "电池优化");

        mFeatureMessages.put(Feature.Boot.toString()      , "手机开机后，程序会随后启动。");
        mFeatureMessages.put(Feature.Background.toString(), "让程序允许在后台运行。");
        mFeatureMessages.put(Feature.Battery.toString()   , "让程序在后台运行时能够运行正常。");
    }
}
