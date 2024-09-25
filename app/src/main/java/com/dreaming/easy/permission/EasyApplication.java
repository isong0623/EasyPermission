package com.dreaming.easy.permission;

import android.Manifest;
import android.app.Application;

import com.dreaming.easy.lib.permission.request.PermissionConfigure;
import com.dreaming.easy.lib.permission.request.PermissionContext;

public class EasyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initEasyPermission();
    }

    private void initEasyPermission(){
        PermissionContext.set(this);

        PermissionConfigure.setPermissionName(Manifest.permission.CAMERA,"相机");
        PermissionConfigure.setPermissionMessage(Manifest.permission.CAMERA, "为了拍照");

        PermissionConfigure.setPermissionName(Manifest.permission.WRITE_EXTERNAL_STORAGE,"读取文件");
        PermissionConfigure.setPermissionMessage(Manifest.permission.WRITE_EXTERNAL_STORAGE, "为了好玩");
    }
}
