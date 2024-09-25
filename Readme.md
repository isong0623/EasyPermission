# EasyPermission
简易权限调用

# 功能&特性
* 1、自动判断权限所在的请求周期，自动回调
	* 从未请求，调用ActivityCompat.requestPermissions。
	* 仅被拒绝，请求权限任意次，但每次都拒绝，调用ActivityCompat.requestPermissions。
	* 已授权，无操作。
	* 永久拒绝，调用相关厂商的接口去该应用的权限配置页。
* 2、根据所处周期自动弹出提示窗口，自动回调
	* 在用户首次请求时，不弹窗
	* 用户首次请求后拒绝又请求，弹窗
	* 永久拒绝后又请求，弹窗 
* 3、支持自定义权限请求周期，自动回调
* 4、支持强制请求权限，直到用户点击弹窗上的退出按钮，自动回调
* 5、支持请求一些特性，自动回调
	* 后台运行
	* 电池优化
* 6、不需要在Activity里配置回调方法
* 7、原理是动态注册和监听Activity生命周期
# 初始化
在Application的onCreate方法中调用如下函数

```java
import android.Manifest;

private void initEasyPermission(){
      PermissionContext.set(this);
      
      //以下内容用于 展示当用户拒绝而又请求时 或 用户永久拒绝时
      //弹出的提示元信息

      //配置权限名称
      PermissionConfigure.setPermissionName(Manifest.permission.CAMERA,"相机");
      PermissionConfigure.setPermissionMessage(Manifest.permission.CAMERA, "为了拍照");
//配置权限提示信息
      PermissionConfigure.setPermissionName(Manifest.permission.WRITE_EXTERNAL_STORAGE,"读取文件");
      PermissionConfigure.setPermissionMessage(Manifest.permission.WRITE_EXTERNAL_STORAGE, "为了好玩");
  }
```

# 请求各种权限
## 普通执行一次

```java
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
```

## 执行到某个周期
以下代码将会从[任意状态]开始到用户永久拒绝将会执行到跳转[设置页返回]结束
```java
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
```

## 强制执行，直到用户退出程序

```java
  EasyPermission.permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new PermissionAction<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                    }
                })
                .requestForce();
```

# 请求某些特性
注意：暂不支持自启动，因为没有办法判断程序是否能够自启动

## 请求某些特性一次

```java
EasyFeature.features(Feature.Background, Feature.Battery)
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
```

## 强制请求某些特性，直到用户退出程序

```java
     EasyFeature.features(Feature.Background, Feature.Battery)
                .onGranted(new PermissionAction<List<Feature>>() {
                     @Override
                     public void onAction(List<Feature> data) {
                         Toast.makeText(MainActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                     }
                 })
                 .requestForce();
```

