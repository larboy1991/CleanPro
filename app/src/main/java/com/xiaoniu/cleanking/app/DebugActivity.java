package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.DeviceUtils;

/**
 * deprecation:调试页面
 * author:ayb
 * time:2018/11/28
 */
public class DebugActivity extends BaseActivity {
    private TextView tv_hide_icon;
    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initView() {
        tv_hide_icon = findViewById(R.id.tv_hide_icon);
        tv_hide_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hideIcon();
                startFullInsertPage(DebugActivity.this);
            }
        });
    }

    public void close(View view) {
        finish();
    }

    public void toHomeClean(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=0"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=4";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeTools(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=1"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=1";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeNews(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=2"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=2";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeMine(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=3"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=3";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toH5(View view) {
        //jump 协议
//        "cleanking://com.xiaoniu.cleanking/jump?url=XXXX"
        String url = AppConstants.Base_H5_Host + "/userAgreement.html";
        String jump = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.JUMP + "?url=";
        String jumpParams = "&is_no_title=0&h5_title=协议";
        String scheme = jump + url + jumpParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toWeChatClean(View view) {
        //原生不带参数 native_no_params协议
//        "cleanking://com.xiaoniu.cleanking/native_no_params?a_name=包名.ui.后面的路径"

//        SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "tool.notify.activity.NotifyCleanGuideActivity";
        String packagePath = "tool.wechat.activity.WechatCleanHomeActivity";
        String schemeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE_NO_PARAMS +
                "?" + SchemeConstant.ANDROID_NAME + "=";
        String scheme = schemeHeader + packagePath;
        SchemeProxy.openScheme(this, scheme);
    }


    public void getImei(){
        //有没有传过imei
        String imei = DeviceUtils.getIMEI();
        LogUtils.i("--zzh-"+imei);
    }

    /**
     * Hide app's icon
     */
    public void hideIcon(){
        try {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(this, SplashADActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
            p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("zzh","--"+e.getMessage(),"");
        }
    }

  /**
     *back the app's icon.*/
    public void backIcon(){
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, SplashADActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    /**
     *  添加桌面快捷方式
     * @param cx
     * @param name 快捷方式名称
     *  调用示例：      ShortCutUtils.addShortcut(MainActivity.this, name.getText() != null ? name.getText().toString() : "1");
     */
    public static void addShortcut(Activity cx, String name) {
        // TODO: 2017/6/25  创建快捷方式的intent广播
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // TODO: 2017/6/25 添加快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //  快捷图标是允许重复
        shortcut.putExtra("duplicate", false);
        // 快捷图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(cx, R.mipmap.applogo);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // TODO: 2017/6/25 我们下次启动要用的Intent信息
        Intent carryIntent = new Intent(Intent.ACTION_MAIN);
        carryIntent.putExtra("name", name);
        carryIntent.setClassName(cx.getPackageName(),cx.getClass().getName());
        carryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //添加携带的Intent
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, carryIntent);
        // TODO: 2017/6/25  发送广播
        cx.sendBroadcast(shortcut);
    }


    //全局跳转全屏插屏页面
    private void startFullInsertPage(Context context) {
        if(ActivityCollector.isActivityExist(FullPopLayerActivity.class))
            return;
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra("ad_style", PositionId.AD_EXTERNAL_ADVERTISING_02);
        context.startActivity(screenIntent);
    }
}
