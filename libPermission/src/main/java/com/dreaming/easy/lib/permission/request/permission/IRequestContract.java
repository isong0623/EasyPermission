package com.dreaming.easy.lib.permission.request.permission;

import android.app.Activity;

import java.util.List;

public interface IRequestContract {
    abstract class AbcChecker{
        abstract void attachActivity(Activity activity);
        abstract void filterPermission(List<String> mPermissions);
        abstract boolean shouldShowDeniedOnceDiloag(List<String> mPermissions);
    }

    abstract class AbcRequestor{
        abstract Activity getActivity();

        abstract List<String> getRequestedPermissions();
        abstract List<String> getNeedGrantPermissions();

        abstract boolean isAccuratelyCallback();
        abstract boolean isForceMode();

        abstract void onCallbackGranted(List<String> grantedList);
        abstract void onCallbackDenied(List<String> deniedList);
        abstract void onCallbackDeniedOnce(List<String> deniedList);
        abstract void onCallbackDeniedAlways(List<String> deniedList);
    }

    abstract class AbcViewer{
        abstract void attachRequestor(AbcRequestor requestor);
        abstract void attachExecutor(AbcExecutor executor);
        abstract void onShowDeniedOnceDialog();
        abstract void onShowDeniedAlwaysDialog();
    }

    abstract class AbcExecutor{
        abstract void attachRequestor(AbcRequestor requestor);
        abstract void attachViewer(AbcViewer viewer);
        abstract void attachChecker(AbcChecker checker);
        abstract void execute();
        abstract void cancel();
    }
}
