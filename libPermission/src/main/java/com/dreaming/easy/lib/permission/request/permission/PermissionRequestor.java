package com.dreaming.easy.lib.permission.request.permission;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dreaming.easy.lib.permission.request.PermissionAction;
import com.dreaming.easy.lib.permission.request.PermissionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionRequestor extends IRequestContract.AbcRequestor{
    private static final String TAG = PermissionRequestor.class.getSimpleName();
    private static void log(String msg, Object...args){
        Log.d(TAG, String.format(msg,args));
    }

    private Activity mActivity;
    private final IRequestContract.AbcChecker mChecker = new PermissionChecker();
    private final IRequestContract.AbcExecutor mExecutor = new PermissionExecutor();
    private final IRequestContract.AbcViewer mViewer = new PermissionViewer();
    public PermissionRequestor(){
        mActivity = PermissionContext.getCurrentActivity();
        //这里可以应用工厂方法模式实现依赖倒置
        //由于项目较小以及开发时间未进行优化
        mChecker.attachActivity(mActivity);
        mExecutor.attachRequestor(this);
        mExecutor.attachChecker(mChecker);
        mExecutor.attachViewer(mViewer);
        mViewer.attachExecutor(mExecutor);
        mViewer.attachRequestor(this);
    }

    private final ArrayList<String> mPermissions = new ArrayList<>();
    private final ArrayList<String> mOriginPermissions = new ArrayList<>();
    public PermissionRequestor permission(String... permissions) {
        mPermissions.addAll(Arrays.asList(permissions));
        mOriginPermissions.addAll(mPermissions);
        mChecker.filterPermission(mPermissions);
        return this;
    }
    public PermissionRequestor permission(List<String> permissions) {
        mPermissions.addAll(permissions);
        mOriginPermissions.addAll(mPermissions);
        mChecker.filterPermission(mPermissions);
        return this;
    }

    protected PermissionAction<List<String>> mGranted;
    protected PermissionAction<List<String>> mDenied;
    protected PermissionAction<List<String>> mDeniedOnce;
    protected PermissionAction<List<String>> mDeniedAlways;

    protected boolean bIsForceMode = false;
    protected boolean isForceMode(){
        return bIsForceMode;
    }
    public PermissionRequestor setForceMode(boolean isForce) {
        bIsForceMode = isForce;
        return this;
    }

    protected boolean bIsEnableDialogShown = true;
    protected boolean isEnableDialogShown(){
        return bIsEnableDialogShown;
    }
    public PermissionRequestor setDialogEnable(boolean isEnable) {
        bIsEnableDialogShown = isEnable;
        return this;
    }

    protected Boolean bIsAccuratelyCallback = null;
    boolean isAccuratelyCallback(){
        return bIsAccuratelyCallback;
    }
    public PermissionRequestor setAccuratelyCallbackEnable(boolean enable) {
        bIsAccuratelyCallback = enable;
        return this;
    }
    public PermissionRequestor onGranted(@NonNull PermissionAction<List<String>> granted) {
        this.mGranted = granted;
        return this;
    }
    public PermissionRequestor onDenied(@NonNull PermissionAction<List<String>> denied) {
        this.mDenied = denied;
        return this;
    }
    public PermissionRequestor onDeniedOnce(@NonNull PermissionAction<List<String>> denied) {
        this.mDeniedOnce = denied;
        return this;
    }
    public PermissionRequestor onDeniedAlways(@NonNull PermissionAction<List<String>> denied) {
        this.mDeniedAlways = denied;
        return this;
    }

    final void onCallbackGranted(List<String> grantedList) {
        if (mGranted != null) {
            mGranted.onAction(grantedList);
            mGranted = null;
        }
    }

    final void onCallbackDenied(List<String> deniedList){
        if(mDenied != null){
            mDenied.onAction(deniedList);
            mDenied = null;
            mDeniedOnce = null;
            mDeniedAlways = null;
        }
    }

    final void onCallbackDeniedOnce(List<String> deniedList){
        if(mDeniedAlways!=null){
            mDeniedAlways.onAction(deniedList);
            mDenied = null;
            mDeniedOnce = null;
            mDeniedAlways = null;
        }
    }

    final void onCallbackDeniedAlways(List<String> deniedList){
        if(mDeniedOnce != null){
            mDeniedOnce.onAction(deniedList);
            mDenied = null;
            mDeniedOnce = null;
            mDeniedAlways = null;
        }
    }

    @Override
    Activity getActivity() {
        return mActivity;
    }

    @Override
    List<String> getRequestedPermissions() {
        return mOriginPermissions;
    }

    @Override
    List<String> getNeedGrantPermissions() {
        return mPermissions;
    }

    public PermissionRequestor start(){
        mExecutor.execute();
        return this;
    }
}
