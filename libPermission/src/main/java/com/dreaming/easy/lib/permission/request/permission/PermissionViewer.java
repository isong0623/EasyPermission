package com.dreaming.easy.lib.permission.request.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import com.dreaming.easy.lib.permission.R;
import com.dreaming.easy.lib.permission.request.PermissionConfigure;

import java.util.concurrent.atomic.AtomicBoolean;

public class PermissionViewer extends IRequestContract.AbcViewer {
    private static final String TAG = PermissionViewer.class.getSimpleName();
    private static void log(String msg, Object...args){
        Log.d(TAG, String.format(msg,args));
    }

    private IRequestContract.AbcRequestor mRequestor;
    private Activity mActivity;
    @Override
    void attachRequestor(IRequestContract.AbcRequestor requestor) {
        mRequestor = requestor;
        mActivity = mRequestor.getActivity();
    }

    private IRequestContract.AbcExecutor mExecutor;
    @Override
    void attachExecutor(IRequestContract.AbcExecutor executor) {
        mExecutor = executor;
    }

    @Override
    void onShowDeniedOnceDialog(){
        StringBuilder sb = new StringBuilder();
        sb.append(mActivity.getResources().getString(R.string.permission_dialog_content_denied_once)).append("\n");
        for(String p : mRequestor.getNeedGrantPermissions()){
            sb  .append("[")
                    .append(PermissionConfigure.getPermissionName(p))
                    .append("]: ")
                    .append(PermissionConfigure.getPermissionMessage(p))
                    .append("\n");
        }
        String sFooterText = PermissionConfigure.getFooterContent();
        if(sFooterText != null && sFooterText.length()>0){
            sb.append("\n");
            sb.append(sFooterText);
        }

        String textTitle = mActivity.getResources().getString(R.string.permission_dialog_titile_denied_once);
        String textAlertText = sb.toString();
        String textConfirm = mActivity.getResources().getString(R.string.permission_dialog_confrim_denied_once);
        String textCancel = mActivity.getResources().getString(R.string.permission_dialog_cancel_denied_once);
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isCalled.set(true);
                mExecutor.execute();
            }
        };
        DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isCalled.set(true);
                mExecutor.cancel();
            }
        };
        DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if(isCalled.get()) return;
                mExecutor.cancel();
            }
        };
        Dialog dialog = PermissionConfigure
                .getCreator()
                .onCreateDialog(
                        mActivity,
                        textTitle,textAlertText,textConfirm,textCancel,
                        onConfirm,onCancel,
                        onDismiss
                );
        dialog.show();
    }

    @Override
    void onShowDeniedAlwaysDialog(){
        StringBuilder sb = new StringBuilder();
        sb.append(mActivity.getResources().getString(R.string.permission_dialog_content_denied_always)).append("\n");
        for(String p: mRequestor.getNeedGrantPermissions()){
            sb  .append("[")
                    .append(PermissionConfigure.getPermissionName(p))
                    .append("]: ")
                    .append(PermissionConfigure.getPermissionMessage(p))
                    .append("\n");
        }
        String sFooterText = PermissionConfigure.getFooterContent();
        if(sFooterText != null && sFooterText.length()>0){
            sb.append("\n");
            sb.append(sFooterText);
        }

        String textTitle = mActivity.getResources().getString(R.string.permission_dialog_titile_denied_always);
        String textAlertText = sb.toString();
        String textConfirm = mActivity.getResources().getString(R.string.permission_dialog_confrim_denied_always);
        String textCancel = mActivity.getResources().getString(mRequestor.isForceMode()?R.string.permission_dialog_exit_denied_always:R.string.permission_dialog_cancel_denied_always);
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isCalled.set(true);
                mExecutor.execute();
            }
        };
        DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isCalled.set(true);
                if(mRequestor.isForceMode()) {
                    System.exit(0);
                    return;
                }
                mExecutor.cancel();
            }
        };
        DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if(isCalled.get()) return;
                if(mRequestor.isForceMode()) {
                    System.exit(0);
                    return;
                }
                mExecutor.cancel();
            }
        };
        Dialog dialog = PermissionConfigure
                .getCreator()
                .onCreateDialog(
                        mActivity,
                        textTitle,textAlertText,textConfirm,textCancel,
                        onConfirm,onCancel,
                        onDismiss
                );
        dialog.show();
    }

}
