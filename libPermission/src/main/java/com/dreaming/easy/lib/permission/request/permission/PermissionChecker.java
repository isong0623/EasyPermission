package com.dreaming.easy.lib.permission.request.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.dreaming.easy.lib.permission.request.Version;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionChecker extends IRequestContract.AbcChecker {

    private static final String TAG = PermissionChecker.class.getSimpleName();
    private static void log(String msg, Object...args){
        Log.d(TAG, String.format(msg,args));
    }

    private static Set<String> mAllPermissions = new HashSet<String>();
    private static Set<String> getAllPermissions(){
        if(mAllPermissions.size() == 0){
            synchronized (mAllPermissions){
                if(mAllPermissions.size() == 0){
                    try {
                        Field[] fields = Manifest.permission.class.getDeclaredFields();
                        for(Field f : fields){
                            Object value = null;
                            try {
                                f.setAccessible(true);
                                value = f.get(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(value == null) continue;
                            if(value instanceof String){
                                mAllPermissions.add((String)f.get(null));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mAllPermissions;
    }

    private Activity mActivity;
    @Override
    void attachActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    void filterPermission(List<String> mPermissions){
        for(int i=mPermissions.size()-1;i>-1;--i){
            String permission = mPermissions.get(i);
            if(mPermissions.indexOf(permission) != i){
                mPermissions.remove(permission);
                continue;
            }
            if(!getAllPermissions().contains(permission)) {
                mPermissions.remove(permission);
                continue;
            }
            if(ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED){
                mPermissions.remove(permission);
            }
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean shouldShowDeniedOnceDiloag(List<String> mPermissions){
        if (Build.VERSION.SDK_INT >= Version.API.Android_6_0.level) {
            for(String permission : mPermissions){
                if(mActivity.shouldShowRequestPermissionRationale(permission)){
                    //1、用户之前仅拒绝了权限请求
                    return true;
                }
            }
        }

        return false;
    }

}
