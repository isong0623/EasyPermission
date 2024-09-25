package com.dreaming.easy.lib.permission.request.feature;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dreaming.easy.lib.permission.R;
import com.dreaming.easy.lib.permission.request.PermissionAction;
import com.dreaming.easy.lib.permission.request.PermissionContext;
import com.dreaming.easy.lib.permission.utils.PermissionUtils;

public class FeatureRequestor implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = FeatureRequestor.class.getSimpleName();
    private static void log(String msg, Object...args){
        Log.d(TAG, String.format(msg,args));
    }

    private final Activity mActivity;
    private final Feature mFeature;
    public FeatureRequestor(Feature feature){
        mActivity = PermissionContext.getCurrentActivity();
        mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        mFeature = feature;
    }

    protected PermissionAction<Feature> mGranted;
    protected PermissionAction<Feature> mDenied;
    public FeatureRequestor onGranted(@NonNull PermissionAction<Feature> granted) {
        this.mGranted = granted;
        return this;
    }

    public FeatureRequestor onDenied(@NonNull PermissionAction<Feature> denied) {
        this.mDenied = denied;
        return this;
    }

    final void onCallbackGranted(Feature granted) {
        if (mGranted != null) {
            mGranted.onAction(granted);
            mGranted = null;
        }
    }

    final void onCallbackDenied(Feature denied){
        if(mDenied != null){
            mDenied.onAction(denied);
            mDenied = null;
        }
    }

    public void start(){
        if(bIsDestroyed) return;

        switch (mFeature){
            case Boot:
                if(PermissionUtils.isBootSelf())
                    onCallbackGranted(mFeature);
                else
                    PermissionUtils.gotoOptimizeBootSelf(mActivity);
                break;
            case Battery:
                if(PermissionUtils.isOptimizeBattery(mActivity))
                    onCallbackGranted(mFeature);
                else
                    PermissionUtils.startOptimizeBattery(mActivity);
                break;
            case Background:
                if(PermissionUtils.isOptimizeBackground(mActivity))
                    onCallbackGranted(mFeature);
                else
                    PermissionUtils.startOptimizeBackground(mActivity);
                break;
        }
    }

    private void onFinished(){
        if(bIsDestroyed) return;
        log("onFinished");

        boolean isSuccess = false;
        switch (mFeature){
            case Boot:
                isSuccess= PermissionUtils.isBootSelf();
                break;
            case Battery:
                isSuccess= PermissionUtils.isOptimizeBattery(mActivity);
                break;
            case Background:
                isSuccess= PermissionUtils.isOptimizeBackground(mActivity);
                break;
        }

        log("onCallback: feature=%s, result:%b", mFeature, isSuccess);
        if(isSuccess) onCallbackGranted(mFeature);
        else onCallbackDenied(mFeature);

        destroy();
    }

    private boolean bIsDestroyed = false;
    private void destroy(){
        if(bIsDestroyed) return;
        bIsDestroyed = true;
        mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if(activity!=mActivity) return;
        PermissionContext.postDelayed(new Runnable() {
            @Override
            public void run() {
                log("onActivityStarted");
                onFinished();
            }
            //不加这个延时会判断错误
        }, 1000);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) { }

    @Override
    public void onActivityPaused(@NonNull Activity activity) { }

    @Override
    public void onActivityStopped(@NonNull Activity activity) { }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if(activity != mActivity) return;
        destroy();
    }
}
