package com.dreaming.easy.lib.permission.request;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;


import com.dreaming.easy.lib.permission.R;

import java.util.HashMap;
import java.util.Map;

public class PermissionConfigure {
    private static String sFooterContent = "";
    private static final Map<String,String> mPermissionNames = new HashMap<>();
    private static final Map<String, String> mPermissionMessages = new HashMap<>();
    private static final Map<String,String> mGroupedPermissionMessages = new HashMap<>();
    public interface IDialogCreator{
        Dialog onCreateDialog(Context context, String titleText, String alertText, String confirmText, String cancelText, DialogInterface.OnClickListener onConfirm, DialogInterface.OnClickListener onCancel, DialogInterface.OnDismissListener onDismiss);
    }
    private static final IDialogCreator defaultCreator = new IDialogCreator() {
        @Override
        public Dialog onCreateDialog(
                Context context,
                String titleText, String alertText, String confirmText, String cancelText,
                DialogInterface.OnClickListener onConfirm, DialogInterface.OnClickListener onCancel,
                DialogInterface.OnDismissListener onDismiss
        ) {
            return new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog)
                    .setTitle(titleText)
                    .setMessage(alertText)
                    .setCancelable(false)
                    .setNeutralButton (confirmText, onConfirm)
                    .setNegativeButton(cancelText, onCancel)
                    .setOnDismissListener(onDismiss)
                    .show();
        }
    };
    private static IDialogCreator creator = defaultCreator;
    public static IDialogCreator getCreator() {
        return creator;
    }

    /**
     * Set the dialog creator
     *
     * Show timer:
     * @see #setPermissionMessage(String, String)
     * @see #setGroupedPermissionMessage(String, String)
     *
     * @param creator
     */
    public static void setDialogCreator(IDialogCreator creator){
        PermissionConfigure.creator = creator==null?defaultCreator:creator;
    }

    /**
     * Set the permission name
     *
     * @param permission {@link Manifest.permission}
     * @param name the name to show
     */
    public static void setPermissionName(String permission, String name){
        mPermissionNames.put(permission, name);
    }
    public static String getPermissionName(String permission){
        return mPermissionNames.get(permission);
    }

    /**
     * Set permission request message
     *
     * Call timer:
     *  1.when denied once then request
     *  2.when denied always then request
     *
     * @param permission  {@link Manifest.permission}
     * @param message
     */
    public static void setPermissionMessage(String permission, String message){
        mPermissionMessages.put(permission, message);
    }
    public static String getPermissionMessage(String permission){
        return mPermissionMessages.get(permission);
    }

    /**
     * Set group request message
     *
     * Call timer:
     *  1.when at lest one permission was denied once but no always, then request
     *  2.when at lest one permission was denied alwasys then request
     *
     * @param group {@link Manifest.permission_group}
     * @param message
     */
    public static void setGroupedPermissionMessage(String group, String message) {
        mGroupedPermissionMessages.put(group, message);
    }

    public static void setFooterContent(String content){
        sFooterContent = content;
    }

    public static String getFooterContent() {
        return sFooterContent;
    }
}
