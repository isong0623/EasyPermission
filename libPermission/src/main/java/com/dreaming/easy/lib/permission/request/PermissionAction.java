package com.dreaming.easy.lib.permission.request;

public interface PermissionAction<T>{
    void onAction(T data);
}
