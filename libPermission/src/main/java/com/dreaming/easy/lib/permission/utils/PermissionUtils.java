package com.dreaming.easy.lib.permission.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.dreaming.easy.lib.permission.request.PermissionContext;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

public class PermissionUtils {
    private static final String MARK = Build.MANUFACTURER.toLowerCase();
    private static final String TAG = "OptimizeUtils";
    public static final int RequestCodeOptimizeBattery = 60000;
    public static final int RequestCodeOptimizeBackground = 60001;
    public static final int RequestCodeOptimizeBootSelf = 60002;

    //region 权限
    public static Activity startSettingPage(Activity activity, int requestCode) {
        Intent intent = null;
        if (MARK.contains("huawei")) {
            intent = huaweiSettingPage(activity);
        } else if (MARK.contains("xiaomi")) {
            intent = xiaomiSettingPage(activity);
        } else if (MARK.contains("oppo")) {
            intent = oppoSettingPage(activity);
        } else if (MARK.contains("vivo")) {
            intent = vivoSettingPage(activity);
        } else if (MARK.contains("meizu")) {
            intent = meizuSettingPage(activity);
        }
        else{
            intent = defaultSettingPage(activity);
        }
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            intent = defaultSettingPage(activity);
            activity.startActivityForResult(intent, requestCode);
        }
        return activity;
    }

    private static Intent defaultSettingPage(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private static Intent huaweiSettingPage(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultSettingPage(context);
    }

    private static Intent xiaomiSettingPage(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultSettingPage(context);
    }

    private static Intent vivoSettingPage(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultSettingPage(context);
    }

    private static Intent oppoSettingPage(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultSettingPage(context);
    }

    private static Intent meizuSettingPage(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultSettingPage(context);
    }

    private static boolean hasActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
    //endregion

    //region 电池优化权限
    public static boolean isOptimizeBattery(){
        return isOptimizeBattery(PermissionContext.getCurrentActivity());
    }
    /**
     * 是否在白名单内
     * @param activity
     * @return
     */
    @SuppressLint("LongLogTag")
    public static boolean isOptimizeBattery(Activity activity) {
        PowerManager pm = (PowerManager) activity.getSystemService(activity.POWER_SERVICE);
        String packageName = activity.getPackageName();
        boolean isWhite = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isWhite = pm.isIgnoringBatteryOptimizations(packageName);
        }
        else{
            isWhite = true;
        }
        Log.i(TAG, "SystemUtil.isSystemWhiteList.packageName=" + packageName + ",isWhite=" + isWhite);
        return isWhite;
    }

    public static void startOptimizeBattery(Activity activity) {
        try{
            if (isHuawei()) {
                goHuaweiSetting(activity);
            } else if (isXiaomi()) {
                goXiaomiSetting(activity);
            } else if (isOPPO()) {
                goOPPOSetting(activity);
            } else if (isVIVO()) {
                goVIVOSetting(activity);
            } else if (isMeizu()) {
                goMeizuSetting(activity);
            } else if (isSamsung()) {
                goSamsungSetting(activity);
            } else if (isLeTV()) {
                goLetvSetting(activity);
            } else if (isSmartisan()) {
                goSmartisanSetting(activity);
            }
            else{
                throw new Exception("");
            }
        }
        catch (Exception e){
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivityForResult(intent, RequestCodeOptimizeBattery);
        }
    }

    private static boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    private static void goHuaweiSetting(Activity activity) {
        try {
            showActivity(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
                    activity
            );
        } catch (Exception e) {
            showActivity(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity",
                    activity
            );
        }
    }

    private static boolean isXiaomi() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
    }

    private static void goXiaomiSetting(Activity activity) {
        showActivity(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity",
                activity
        );
    }

    private static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }


    private static void goOPPOSetting(Activity activity) {
        try {
            showActivity("com.coloros.phonemanager",activity);
        } catch (Exception e1) {
            try {
                showActivity("com.oppo.safe",activity);
            } catch (Exception e2) {
                try {
                    showActivity("com.coloros.oppoguardelf",activity);
                } catch (Exception e3) {
                    showActivity("com.coloros.safecenter",activity);
                }
            }
        }
    }

    private static boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }

    private static void goVIVOSetting(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = ComponentName.unflattenFromString("com.android.settings/.Settings$HighPowerApplicationsActivity");
        intent.setComponent(cn);
        activity.startActivityForResult(intent, RequestCodeOptimizeBackground);
    }

    private static boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
    }

    private static void goMeizuSetting(Activity activity) {
        showActivity("com.meizu.safe",activity);
    }

    private static boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
    }

    private static void goSamsungSetting(Activity activity) {
        try {
            showActivity("com.samsung.android.sm_cn",activity);
        } catch (Exception e) {
            showActivity("com.samsung.android.sm",activity);
        }
    }

    private static boolean isLeTV() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("letv");
    }

    private static void goLetvSetting(Activity activity) {
        showActivity(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity",
                activity
        );
    }

    private static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }

    private static void goSmartisanSetting(Activity activity) {
        showActivity("com.smartisanos.security",activity);
    }

    /**
     * 跳转到指定应用的首页
     */
    private static void showActivity(String packageName, Activity activity) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        activity.startActivityForResult(intent,RequestCodeOptimizeBattery);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private static void showActivity(String packageName, String activityDir, Activity activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent,RequestCodeOptimizeBattery);
    }
    //endregion

    //region 后台运行权限
    public static boolean isOptimizeBackground() {
        return isOptimizeBackground(PermissionContext.getCurrentActivity());
    }
    public static boolean isOptimizeBackground(Activity activity) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) activity.getSystemService(activity.POWER_SERVICE);
        if (powerManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
            }
            else{
                isIgnoring = true;
            }
        }
        return isIgnoring;
    }

    public static Activity startOptimizeBackground() {
        Activity activity = PermissionContext.getCurrentActivity();
        return startOptimizeBackground(activity);
    }

    public static Activity startOptimizeBackground(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent,RequestCodeOptimizeBackground);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }
    //endregion
    
    //region 自启动
    public static boolean isBootSelf(){
        try {
            @SuppressLint("PrivateApi")
            ComponentName localComponentName = new ComponentName(PermissionContext.get(), Class.forName("com.android.stk.BootCompletedReceiver"));
            int i = PermissionContext.get().getPackageManager().getComponentEnabledSetting(localComponentName);
            return i == COMPONENT_ENABLED_STATE_ENABLED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Activity gotoOptimizeBootSelf() {
        return gotoOptimizeBootSelf(PermissionContext.getCurrentActivity());
    }
    public static Activity gotoOptimizeBootSelf(Activity activity) {
        Intent autostartSettingIntent;
        try {
            autostartSettingIntent = getOptimizeBootSelfIntent(activity);
            activity.startActivityForResult(autostartSettingIntent,RequestCodeOptimizeBootSelf);
        } catch (Exception e) {
            autostartSettingIntent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivityForResult(autostartSettingIntent, RequestCodeOptimizeBootSelf);
        }
        return activity;
    }

    /**
     * 获取自启动管理页面的Intent
     */
    private static Intent getOptimizeBootSelfIntent(Activity activity) {
        ComponentName componentName = null;
        String brand = Build.MANUFACTURER;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (brand.toLowerCase()) {
            case "samsung"://三星
                componentName = new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case "huawei"://华为
                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity");
                break;
            case "xiaomi"://小米 mix3测试通过
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case "vivo"://vivo z5测试通过
                if (Build.VERSION.SDK_INT >= 23) {
                    componentName = new ComponentName("com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.PurviewTabActivity");
                } else {
                    componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity");
                }
                break;
            case "oppo"://OPPO Android9的一台设备通过
                componentName = new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                break;
            case "yulong":
            case "360"://360
                componentName = new ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case "meizu"://魅族
                componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");
                break;
            case "oneplus"://一加
                componentName = new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            case "letv"://乐视
                intent.setAction("com.letv.android.permissionautoboot");
            default://其他
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                break;
        }
        intent.setComponent(componentName);
        return intent;
    }
    //endregion
}
