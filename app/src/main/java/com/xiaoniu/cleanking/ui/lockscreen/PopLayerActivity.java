package com.xiaoniu.cleanking.ui.lockscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.common.base.BaseActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author zhengzhihao
 * @date 2019/11/16 18
 * @mail：zhengzhihao@hellogeek.com
 * 弹出层Activity
 */
public class PopLayerActivity extends AppCompatActivity {
    RelativeLayout flayoutAdContainer;
    private AdManager adManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
        ActivityCollector.addActivity(this,PopLayerActivity.class);
        setContentView(R.layout.activity_pop_layer);
        flayoutAdContainer = (RelativeLayout)findViewById(R.id.flayout_ad_container);
        adInit();
        //todo_zzh
        //loadCustomInsertScreenAd2("external_advertising_ad_1");
    }




    private void setLockerWindow(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT > 18) {
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.setAttributes(lp);
        window.getDecorView().setSystemUiVisibility(0x0);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    public void adInit() {
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, "success_page_ad_3", new AdListener() {

            @Override
            public void adSuccess(AdInfo info) {
                View adView = adManager.getAdView();
                if (adView != null) {
                    flayoutAdContainer.removeAllViews();
                    flayoutAdContainer.addView(adView);
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                LogUtils.e("adExposed");
            }

            @Override
            public void adClicked(AdInfo info) {

            }

            @Override
            public void adError(int errorCode, String errorMsg) {

            }
        });
    }

    /**
     * 获取全屏插屏广告并加载
     */
    private void loadCustomInsertScreenAd2(String position) {
        LogUtils.d("position:" + position + " isFullScreen:");
        try {
            adManager.loadCustomInsertScreenAd(this, position, 3, new AdListener() {
                @Override
                public void adClose(AdInfo info) {
                    ActivityCollector.removeActivity(PopLayerActivity.this);
                    PopLayerActivity.this.finish();

                }

                @Override
                public void adError(int errorCode, String errorMsg) {
                    LogUtils.d("-----adError-----" + errorMsg);

                }

                @Override
                public void adSuccess(AdInfo info) {

                }

                @Override
                public void adExposed(AdInfo info) {
                    LogUtils.d("-----adExposed-----");
                }

                @Override
                public void adClicked(AdInfo info) {
                    LogUtils.d("-----adClicked-----");
                }
            }, String.valueOf(NumberUtils.mathRandom(25,50)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
