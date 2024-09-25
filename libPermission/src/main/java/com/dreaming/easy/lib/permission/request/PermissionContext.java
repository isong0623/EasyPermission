package com.dreaming.easy.lib.permission.request;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;

public class PermissionContext {
    private static String TAG = PermissionContext.class.getSimpleName();
    private static Context mContext;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static Activity mActivity;

    public static void set(Context context) {
        if(mContext != null) return;
        synchronized (PermissionContext.class){
            if(mContext == null){
                Context ctx = context.getApplicationContext();
                if(ctx == null) return;
                mContext = context.getApplicationContext();
                Application app = (Application) mContext;
                app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { mActivity = activity; }
                    @Override
                    public void onActivityStarted(Activity activity) { }
                    @Override
                    public void onActivityResumed(Activity activity) { mActivity = activity; }
                    @Override
                    public void onActivityPaused(Activity activity) { }
                    @Override
                    public void onActivityStopped(Activity activity) { }
                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }
                    @Override
                    public void onActivityDestroyed(Activity activity) { }
                });
                Log.d(TAG,"initialize context success!");
            }
        }
    }

    public static Context get() {
        if(mContext == null){
            synchronized (PermissionContext.class){
                if(mContext == null){
                    final long start = System.currentTimeMillis();
                    Log.d(TAG, "initialize context with reflect start!");
                    //最好是在Application.onCreate里调用set
                    try {
                        @SuppressLint("PrivateApi")
                        Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                        Method mtdCurrentActivityThread = ActivityThread.getMethod("currentActivityThread");
                        Object currentActivityThread = mtdCurrentActivityThread.invoke(ActivityThread);
                        Method mtdGetApplication = currentActivityThread.getClass().getMethod("getApplication");
                        set((Context)mtdGetApplication.invoke(currentActivityThread));
                        final long end = System.currentTimeMillis();
                        Log.d(TAG, String.format("initialize context with reflect success cost %d ms!", end-start));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "initialize context with reflect failure!");
                        Log.e(TAG,e.getMessage());
                    }
                }
            }
        }
        if(mContext == null){
            throw new NullPointerException("PermissionContext not init, you must call [PermissionContext.set(this);] in [Application.onCreate]!");
        }
        return mContext;
    }

    public static Activity getCurrentActivity(){
        return mActivity;
    }

    public static void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayInMillis) {
        mHandler.postDelayed(runnable, delayInMillis);
    }

}
