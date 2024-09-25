package com.dreaming.easy.lib.permission;

import androidx.annotation.NonNull;

import com.dreaming.easy.lib.permission.request.PermissionAction;
import com.dreaming.easy.lib.permission.request.permission.PermissionRequestor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by songxusheng 2024/0920
 */
public class EasyPermission {

    private final List<String> mPermissions;

    public static EasyPermission permissions(String...permissions){
        return new EasyPermission(permissions);
    }

    public static EasyPermission groups(String[]...groups){
        return new EasyPermission(groups);
    }

    public EasyPermission(@NonNull String...permissions){
        mPermissions = new ArrayList<>();
        mPermissions.addAll(Arrays.asList(permissions));
    }

    public EasyPermission(@NonNull String[]...groups){
        mPermissions = new ArrayList<>();
        for (String[] group : groups) {
            mPermissions.addAll(Arrays.asList(group));
        }
    }

    private boolean bIsForceMode = false;
    private boolean isForceMode(){
        return bIsForceMode;
    }

    public EasyPermission setForceMode(boolean isForce) {
        bIsForceMode = isForce;
        return this;
    }
    private boolean bIsEnableDialogShown = true;
    private boolean isEnableDialogShown(){
        return bIsEnableDialogShown;
    }
    
    public EasyPermission setDialogEnable(boolean isEnable) {
        bIsEnableDialogShown = isEnable;
        return this;
    }
    private boolean bIsAccuratelyCallback = false;
    private boolean isAccuratelyCallback(){
        return bIsAccuratelyCallback;
    }
    
    public EasyPermission setAccuratelyCallbackEnable(boolean enable) {
        bIsAccuratelyCallback = enable;
        return this;
    }

    private PermissionAction<List<String>> mGranted;
    public EasyPermission onGranted(@NonNull PermissionAction<List<String>> granted) {
        this.mGranted = granted;
        return this;
    }
    
    private PermissionAction<List<String>> mDenied;
    public EasyPermission onDenied(@NonNull PermissionAction<List<String>> denied) {
        this.mDenied = denied;
        return this;
    }

    private PermissionAction<List<String>> mDeniedOnce;
    public EasyPermission onDeniedOnce(@NonNull PermissionAction<List<String>> denied) {
        this.mDeniedOnce = denied;
        return this;
    }

    private PermissionAction<List<String>> mDeniedAlways;
    public EasyPermission onDeniedAlways(@NonNull PermissionAction<List<String>> denied) {
        this.mDeniedAlways = denied;
        return this;
    }

    /**
     * request permission once
     * @see #requestPermissionsOnce
     */
    public EasyPermission requestOnce(){
        new PermissionRequestor()
                .permission(mPermissions)
                .setForceMode(isForceMode())
                .setDialogEnable(isEnableDialogShown())
                .setAccuratelyCallbackEnable(isAccuratelyCallback())
                .onGranted(mGranted)
                .onDenied(mDenied)
                .onDeniedOnce(mDeniedOnce)
                .onDeniedAlways(mDeniedAlways)
                .start();
        return this;
    }

    /**
     * force request permissions until granted or user shutdown application
     *
     * @example
        new PermissionsWith()
            .onGranted(d->{})
            .requestForce();
     */
    public EasyPermission requestForce(){
        new PermissionRequestor()
                .permission(mPermissions)
                .setForceMode(true)
                .setDialogEnable(true)
                .setAccuratelyCallbackEnable(true)
                .onGranted(mGranted)
                .onDenied(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestForce();
                    }
                })
                .onDeniedOnce(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestForce();
                    }
                })
                .onDeniedAlways(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestForce();
                    }
                })
                .start();
        return this;
    }


    public static final int STATE_INIT          = 0;
    public static final int STATE_DENIED        = 1;
    public static final int STATE_DENIED_ONCE   = 2;
    public static final int STATE_DENIED_ALWAYS = 3;
    public static final int STATE_END           = 4;
    /**
     * request permissions until user denied always and back from system settings page
     * @example
        new PermissionsWith()
            .onGranted(d->{})
            .onDenied(d->{})
            .requestFully();
     */
    public EasyPermission requestFully(){
        return requestFully(STATE_INIT, STATE_END);
    }

    /**
     * @example
         new PermissionsWith()
             .onGranted(d->{})
             .onDenied(d->{})
             .requestFully(PermissionsWith.STATE_INIT, PermissionsWith.STATE_DENIED_ALWAYS);
     */
    public EasyPermission requestFully(final int startState, final int endState){
        if(startState>=endState) {
            mDenied.onAction(mPermissions);
            return this;
        }
        new PermissionRequestor()
                .permission(mPermissions)
                .setAccuratelyCallbackEnable(true)
                .onGranted(mGranted)
                .onDenied(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestFully(STATE_DENIED, STATE_END);
                    }
                })
                .onDeniedOnce(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestFully(STATE_DENIED_ONCE, STATE_END);
                    }
                })
                .onDeniedAlways(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if(startState == STATE_DENIED_ALWAYS){
                            mDenied.onAction(data);
                        }
                        else{
                            requestFully(STATE_DENIED_ALWAYS, STATE_END);
                        }
                    }
                })
                .start();
        return this;
    }
    /**
     * force request permissions until granted or user shutdown application
     */
    public static void requestForcePermissions(final PermissionAction<List<String>> onGranted, final String... permissions){
        new PermissionRequestor()
                .permission(permissions)
                .setAccuratelyCallbackEnable(true)
                .onGranted(onGranted)
                .onDenied(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestForcePermissions(onGranted, permissions);;
                    }
                })
                .onDeniedOnce(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestForcePermissions(onGranted,  permissions);
                    }
                })
                .onDeniedAlways(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestForcePermissions(onGranted,  permissions);
                    }
                })
                .start();
    }

    public static void requestFullyPermissions(PermissionAction<List<String>> onGranted, PermissionAction<List<String>> onDenied, String... permissions){
        requestFullyPermissions(onGranted, onDenied, STATE_INIT, STATE_END, permissions);
    }
    public static void requestFullyPermissions(final PermissionAction<List<String>> onGranted, final PermissionAction<List<String>> onDenied, final int startState, final int endState, final String... permissions){
        if(startState>=endState) {
            onDenied.onAction(Arrays.asList(permissions));
            return;
        }
        new PermissionRequestor()
                .permission(permissions)
                .setAccuratelyCallbackEnable(true)
                .onGranted(onGranted)
                .onDenied(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestFullyPermissions(onGranted, onDenied, STATE_DENIED, endState, permissions);
                    }
                })
                .onDeniedOnce(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestFullyPermissions(onGranted, onDenied, STATE_DENIED_ONCE, endState, permissions);
                    }
                })
                .onDeniedAlways(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if(startState == STATE_DENIED_ALWAYS){
                            onDenied.onAction(data);
                        }
                        else{
                            requestFullyPermissions(onGranted, onDenied, STATE_DENIED_ALWAYS, endState, permissions);
                        }
                    }
                })
                .start();
    }

    public static void requestPermissionsOnce(PermissionAction<List<String>> onGranted, PermissionAction<List<String>> onDenied, String... permissions){
        requestPermissionsOnce(onGranted, onDenied, true, permissions);
    }

    public static void requestPermissionsOnce(PermissionAction<List<String>> onGranted, PermissionAction<List<String>> onDenied, boolean isEnableDialogShown, String... permissions){
        new PermissionRequestor()
                .permission(permissions)
                .setDialogEnable(isEnableDialogShown)
                .onGranted(onGranted)
                .onDenied(onDenied)
                .start();
    }
}
