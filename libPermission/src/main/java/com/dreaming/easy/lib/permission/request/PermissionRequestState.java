package com.dreaming.easy.lib.permission.request;

public enum PermissionRequestState {
    NeverRequest(1),
    Granted     (2),
    DeniedOnce  (3),
    DeniedAlways(4),
    ;

    public final int state;
    private PermissionRequestState(int state){
        this.state = state;
    }

    public static PermissionRequestState parse(int state){
        switch (state){
            case 1: return NeverRequest;
            case 2: return Granted     ;
            case 3: return DeniedOnce  ;
        }
        return DeniedAlways;
    }
}
