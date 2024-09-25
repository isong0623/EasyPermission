package com.dreaming.easy.lib.permission.request.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.dreaming.easy.lib.permission.request.PermissionRequestState;
import com.dreaming.easy.lib.permission.request.Version;
import com.dreaming.easy.lib.permission.utils.PermissionUtils;

public class PermissionExecutor extends IRequestContract.AbcExecutor implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = PermissionExecutor.class.getSimpleName();
    private static void log(String msg, Object...args){
        Log.d(TAG, String.format(msg,args));
    }

    private IRequestContract.AbcRequestor mRequestor;
    @Override
    void attachRequestor(IRequestContract.AbcRequestor requestor) {
        mRequestor = requestor;
        mRequestor.getActivity().getApplication().registerActivityLifecycleCallbacks(this);
    }

    private IRequestContract.AbcChecker mChecker;
    @Override
    void attachChecker(IRequestContract.AbcChecker checker) {
        mChecker = checker;
    }

    private IRequestContract.AbcViewer mViewer;
    @Override
    void attachViewer(IRequestContract.AbcViewer viewer) {
        mViewer = viewer;
    }

    private PermissionRequestState mState = null;
    void onTestPermissionDeniedAlways(){
        log("onTestPermissionDeniedAlways");
        mState = PermissionRequestState.DeniedAlways;
        mViewer.onShowDeniedAlwaysDialog();
    }

    @Override
    void execute() {
        if(bIsDestroyed) return;
        log("execute: state=%s",mState);

        if(mRequestor.getNeedGrantPermissions().size() == 0){
            bIsRequesting = true;
            onFinished();
            return;
        }

        //弹窗回调
        if(mState != null){
            if(mState == PermissionRequestState.DeniedAlways){
                PermissionUtils.startSettingPage(mRequestor.getActivity(), 0);
            }
            else{
                //包含DeniedOnce回调 和 测试回调
                requestPermissionsInternal();
            }
            return;
        }

        if(Build.VERSION.SDK_INT < Version.API.Android_6_0.level){
            //安卓6以下不需要用户授权
            if(mRequestor.getNeedGrantPermissions().size() == 0){
                mRequestor.onCallbackGranted(mRequestor.getRequestedPermissions());
            }
            else{
                if(mRequestor.isAccuratelyCallback()){
                    mRequestor.onCallbackDeniedAlways(mRequestor.getRequestedPermissions());
                }
                else{
                    mRequestor.onCallbackDenied(mRequestor.getRequestedPermissions());
                }
            }
        }
        else{
            boolean isShouldShowDialog = mChecker.shouldShowDeniedOnceDiloag(mRequestor.getNeedGrantPermissions());
            if(isShouldShowDialog){
                //如果要显示那么就一定是被拒绝过一次
                mState = PermissionRequestState.DeniedOnce;
                mViewer.onShowDeniedOnceDialog();
            }
            else{
                //否则就是 未请求 或 永久拒绝
                mState = PermissionRequestState.NeverRequest;
                //测试是否能够直接请求
                //如果不能则提示跳转设置弹窗
                requestPermissionsInternal();
            }
        }
    }

    private boolean bIsRequesting = false;
    private void requestPermissionsInternal(){
        log("requestPermissionsInternal");
        String[] permissions = new String[mRequestor.getNeedGrantPermissions().size()];
        for(int i=0,ni=permissions.length;i<ni;++i){
            permissions[i] = mRequestor.getNeedGrantPermissions().get(i);
        }
        bIsRequesting = true;
        ActivityCompat.requestPermissions(mRequestor.getActivity(), permissions, 0);
    }

    @Override
    void cancel() {
        log("cancel");
        mRequestor.onCallbackDenied(mRequestor.getNeedGrantPermissions());
        destroy();
    }


    void onCallbackSuccess(){
        mRequestor.onCallbackGranted(mRequestor.getRequestedPermissions());
    }

    void onCallbackFailure(){
        if(mRequestor.isAccuratelyCallback()){
            switch (mState){
                case NeverRequest:
                    mRequestor.onCallbackDenied(mRequestor.getNeedGrantPermissions());
                    break;
                case DeniedOnce:
                    mRequestor.onCallbackDeniedOnce(mRequestor.getNeedGrantPermissions());
                    break;
                case DeniedAlways:
                    mRequestor.onCallbackDeniedAlways(mRequestor.getNeedGrantPermissions());
                    break;
            }
        }
        else{
            mRequestor.onCallbackDenied(mRequestor.getNeedGrantPermissions());
        }
    }

    private boolean bIsRequestedManageFile = false;
    @SuppressLint("NewApi")
    void onFinished(){
        if(!bIsRequesting) return;
        log("onFinished");
        bIsRequesting = false;

        mChecker.filterPermission(mRequestor.getNeedGrantPermissions());
        if(mRequestor.getNeedGrantPermissions().size() == 0){
            if(Build.VERSION.SDK_INT >= Version.API.Android_10_0_Plus.level &&
                mRequestor.getRequestedPermissions().contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                !Environment.isExternalStorageManager()) {
                if(!bIsRequestedManageFile){
                    bIsRequestedManageFile = true;
                    bIsRequesting = true;
                    Intent intent =  new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + mRequestor.getActivity().getPackageName()));
                    mRequestor.getActivity().startActivity(intent);
                    return;
                }
                mRequestor.getNeedGrantPermissions().add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                onCallbackFailure();
                return;
            }
            onCallbackSuccess();
        }
        else{
            onCallbackFailure();
        }
        destroy();
        mState = null;
    }

    private boolean bIsDestroyed = false;
    private void destroy(){
        if(bIsDestroyed) return;
        log("destroy");
        bIsDestroyed = true;
        mRequestor.getActivity().getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    private long bRequestStartTimestamp = 0;
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if(activity != mRequestor.getActivity()) return;
        log("onActivityStarted: state=%s",mState);
        if(bIsRequestedManageFile){
            onFinished();
            return;
        }
        if(mState != PermissionRequestState.DeniedAlways) return;
        onFinished();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(activity != mRequestor.getActivity()) return;
        if(!bIsRequesting) return;
        if(mState == null) return;
        log("onActivityResumed: state=%s",mState);
        switch (mState){
            case NeverRequest:
                if(System.currentTimeMillis() - bRequestStartTimestamp < 300L){
                    //如果权限永久拒绝，
                    // 那么这个activity会先onActivityPaused
                    // 然后迅速调用onActivityResumed
                    onTestPermissionDeniedAlways();
                }
                else{
                    if(bIsRequesting){
                        onFinished();
                        return;
                    }
                }
                break;
            case DeniedAlways:
                break;
            default:
                if(bIsRequesting){
                    onFinished();
                    return;
                }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if(activity != mRequestor.getActivity()) return;
        log("onActivityPaused: state=%s",mState);
        if(mState != PermissionRequestState.NeverRequest) return;
        bRequestStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if(activity != mRequestor.getActivity()) return;
        log("onActivityStopped: state=%s",mState);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if(activity == mRequestor.getActivity()){
            destroy();
        }
    }

}
