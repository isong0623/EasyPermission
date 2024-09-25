package com.dreaming.easy.lib.permission;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dreaming.easy.lib.permission.request.FeatureConfigure;
import com.dreaming.easy.lib.permission.request.PermissionAction;
import com.dreaming.easy.lib.permission.request.PermissionConfigure;
import com.dreaming.easy.lib.permission.request.PermissionContext;
import com.dreaming.easy.lib.permission.request.feature.Feature;
import com.dreaming.easy.lib.permission.request.feature.FeatureRequestor;
import com.dreaming.easy.lib.permission.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EasyFeature {
    private static final String TAG = EasyFeature.class.getSimpleName();
    private static void log(String msg, Object...args){
        Log.d(TAG, String.format(msg,args));
    }

    public static EasyFeature features(Feature...features){
        return new EasyFeature(features);
    }

    protected PermissionAction<List<Feature>> mGranted;
    protected PermissionAction<List<Feature>> mDenied;

    private final List<Feature> mRequestedFeatures = new ArrayList<>();
    private final List<Feature> mNeedGrantFeatures = new ArrayList<>();
    public EasyFeature(Feature...features){
        for(Feature feature: features){
            if(mRequestedFeatures.contains(feature)) continue;
            mRequestedFeatures.add(feature);
            boolean hasFeature = hasFeature(feature);
            if(hasFeature) continue;
            mNeedGrantFeatures.add(feature);
        }
    }

    private boolean hasFeature(Feature feature){
        boolean hasFeature = false;
        switch (feature){
            case Boot:
                hasFeature= PermissionUtils.isBootSelf();
                break;
            case Battery:
                hasFeature= PermissionUtils.isOptimizeBattery();
                break;
            case Background:
                hasFeature= PermissionUtils.isOptimizeBackground();
                break;
        }
        log("hasFeature: feature=%s, reuslt=%b", feature, hasFeature);
        return hasFeature;
    }

    public EasyFeature onGranted(@NonNull PermissionAction<List<Feature>> granted) {
        this.mGranted = granted;
        return this;
    }

    public EasyFeature onDenied(@NonNull PermissionAction<List<Feature>> denied) {
        this.mDenied = denied;
        return this;
    }

    protected boolean bIsForceMode = false;
    protected boolean isForceMode(){
        return bIsForceMode;
    }
    public EasyFeature setForceMode(boolean isForce) {
        bIsForceMode = isForce;
        return this;
    }

    private void onShowDialog(){
        StringBuilder sb = new StringBuilder();
        sb.append(PermissionContext.get().getResources().getString(R.string.permission_dialog_better_performance)).append("\n");
        for(Feature p: mRequestedFeatures){
            sb  .append("[")
                    .append(FeatureConfigure.getFeatureName(p.toString()))
                    .append("]: ")
                    .append(FeatureConfigure.getFeatureMessage(p.toString()))
                    .append("\n");
        }
        String sFooterText = PermissionConfigure.getFooterContent();
        if(sFooterText != null && sFooterText.length()>0){
            sb.append("\n");
            sb.append(sFooterText);
        }

        String textTitle = PermissionContext.get().getResources().getString(R.string.permission_dialog_titile_denied_always);
        String textAlertText = sb.toString();
        String textConfirm = PermissionContext.get().getResources().getString(R.string.permission_dialog_confrim_denied_always);
        String textCancel = PermissionContext.get().getResources().getString(isForceMode()?R.string.permission_dialog_exit_denied_always:R.string.permission_dialog_cancel_denied_always);
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isCalled.set(true);
                execute();
            }
        };
        DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isCalled.set(true);
                if(isForceMode()) {
                    System.exit(0);
                    return;
                }
                cancel();
            }
        };
        DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if(isCalled.get()) return;
                if(isForceMode()) {
                    System.exit(0);
                    return;
                }
                cancel();
            }
        };
        Dialog dialog = PermissionConfigure
                .getCreator()
                .onCreateDialog(
                        PermissionContext.getCurrentActivity(),
                        textTitle,textAlertText,textConfirm,textCancel,
                        onConfirm,onCancel,
                        onDismiss
                );
        dialog.show();
    }

    public void request(){
        if(mNeedGrantFeatures.size() == 0){
            onCallbackGranted(mRequestedFeatures);
            return;
        }
        onShowDialog();
    }

    public void requestForce(){
        setForceMode(true);
        onDenied(new PermissionAction<List<Feature>>() {
            @Override
            public void onAction(List<Feature> data) {
                request();
            }
        });
        request();
    }

    private void execute(){
        if(mNeedGrantFeatures.size() == 0){
            onCallbackGranted(mRequestedFeatures);
            return;
        }

        Feature feature = mNeedGrantFeatures.get(0);
        new FeatureRequestor(feature)
                .onGranted(new PermissionAction<Feature>() {
                    @Override
                    public void onAction(Feature data) {
                        mNeedGrantFeatures.remove(data);
                        execute();
                    }
                })
                .onDenied(new PermissionAction<Feature>() {
                    @Override
                    public void onAction(Feature data) {
                        if(isForceMode())
                            onShowDialog();
                        else
                            onCallbackDenied(mNeedGrantFeatures);
                    }
                })
                .start();
    }

    private void onCallbackGranted(List<Feature> grantedList) {
        if (mGranted != null) {
            mGranted.onAction(grantedList);
            mGranted = null;
            mDenied  = null;
        }
    }

    private void onCallbackDenied(List<Feature> deniedList){
        if(mDenied != null){
            mDenied.onAction(deniedList);
            mGranted = null;
            mDenied  = null;
        }
    }

    private void cancel(){
        onCallbackDenied(mNeedGrantFeatures);
    }

    public static void requestNormal(PermissionAction<List<Feature>> onGranted, PermissionAction<List<Feature>> onDenied, Feature...features){
        new EasyFeature(features)
                .onGranted(onGranted)
                .onDenied(onDenied)
                .request();
    }

    public static void requestForce(PermissionAction<List<Feature>> onGranted, Feature...features){
        new EasyFeature(features)
                .onGranted(onGranted)
                .requestForce();
    }
}
