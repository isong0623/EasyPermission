package com.dreaming.easy.permission;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.dreaming.easy.lib.permission.EasyFeature;
import com.dreaming.easy.lib.permission.EasyPermission;
import com.dreaming.easy.lib.permission.request.PermissionAction;
import com.dreaming.easy.lib.permission.request.feature.Feature;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test_feature_force).setOnClickListener(this);
        findViewById(R.id.btn_test_feature_once).setOnClickListener(this);
        findViewById(R.id.btn_test_permission_fully).setOnClickListener(this);
        findViewById(R.id.btn_test_permission_force).setOnClickListener(this);
        findViewById(R.id.btn_test_permission_once).setOnClickListener(this);
    }

    private void log(String msg, Object...args){
//        Log.e("MainActivity", String.format(msg, args));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        log("Main", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("Main", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("Main", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("Main", "onPause");
    }

    private Feature[] mFeatures = new Feature[]{Feature.Background, Feature.Battery};
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_feature_once:{
                EasyFeature.features(mFeatures)
                        .onDenied(new PermissionAction<List<Feature>>() {
                            @Override
                            public void onAction(List<Feature> data) {
                                Toast.makeText(MainActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onGranted(new PermissionAction<List<Feature>>() {
                            @Override
                            public void onAction(List<Feature> data) {
                                Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .request();
                break;
            }
            case R.id.btn_test_feature_force:{
                EasyFeature.features(mFeatures)
                        .onGranted(new PermissionAction<List<Feature>>() {
                            @Override
                            public void onAction(List<Feature> data) {
                                Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .requestForce();
                break;
            }
            case R.id.btn_test_permission_force:{
                EasyPermission.permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .requestForce();
                break;
            }
            case R.id.btn_test_permission_fully:{
                EasyPermission.permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onDenied(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .requestFully();
                break;
            }
            case R.id.btn_test_permission_once:{
                EasyPermission.permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setAccuratelyCallbackEnable(true)
                        .onGranted(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onDenied(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onDeniedOnce(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求失败，一次！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onDeniedAlways(new PermissionAction<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Toast.makeText(MainActivity.this, "请求失败，总是！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .requestOnce();
                break;
            }
        }
    }

    protected void toast(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}