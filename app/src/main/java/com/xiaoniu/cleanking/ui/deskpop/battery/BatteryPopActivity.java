package com.xiaoniu.cleanking.ui.deskpop.battery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.ui.deskpop.base.DeskPopConfig;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.widget.CircleRoundingAnim;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author zzh
 * @date 2020/7/24 19
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class BatteryPopActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatImageView sceneClose;
    private AppCompatTextView sceneTitle;
    private AppCompatButton sceneButton;
    private LottieAnimationView view_power_lottie;
    private TextView tvCurrentValue;
    private TextView tvFullTime;
    private CircleRoundingAnim chargeState01;
    private CircleRoundingAnim chargeState02;
    private CircleRoundingAnim chargeState03;
    private TextView tvPowerCapacity;
    private TextView tvPowerVoltage;
    private TextView tvPowerTemp;
    private TextView tvPowerApp;
    private FrameLayout adContainer;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_battery_pop_layer;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
        initView();
    }

    private void initView() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true);

        MmkvUtil.saveLong(PositionId.PAGE_DESK_BATTERY_INFO_TIME, System.currentTimeMillis());
        DeskPopConfig.getInstance().saveAndDecreaseBatteryPopNum();

        StatisticsUtils.customTrackEvent("charging_plug_screen_custom", "充电插屏曝光", "charging_plug_screen", "charging_plug_screen");
        sceneClose = findViewById(R.id.scene_close);
        sceneTitle = findViewById(R.id.scene_title);
        sceneButton = findViewById(R.id.scene_button);
        sceneClose.setOnClickListener(this::onClick);
        sceneButton.setOnClickListener(this::onClick);
        tvCurrentValue = findViewById(R.id.tv_current_value);
        tvFullTime = findViewById(R.id.tv_full_time);
        chargeState01 = findViewById(R.id.charge_state01);
        chargeState02 = findViewById(R.id.charge_state02);
        chargeState03 = findViewById(R.id.charge_state03);
        adContainer = findViewById(R.id.ad_container);
        tvPowerCapacity = findViewById(R.id.tv_power_capacity);
        tvPowerVoltage = findViewById(R.id.tv_power_voltage);
        tvPowerTemp = findViewById(R.id.tv_power_temp);
        tvPowerApp = findViewById(R.id.tv_power_app);
        view_power_lottie = findViewById(R.id.view_power_lottie);
        EasyBatteryMod easyBatteryMod = new EasyBatteryMod(this);
        tvCurrentValue.setText(String.valueOf(easyBatteryMod.getBatteryPercentage()));

        tvCurrentValue.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DIN-Medium.otf"));
        tvPowerCapacity.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DIN-Medium.otf"));
        tvPowerVoltage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DIN-Medium.otf"));
        tvPowerTemp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DIN-Medium.otf"));
        tvPowerApp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DIN-Medium.otf"));

        if (easyBatteryMod.getBatteryPercentage() == 100) {
            tvFullTime.setVisibility(View.GONE);
            chargeState03.setSelected();
            view_power_lottie.setAnimation("charging/charging_state03/data.json");
            view_power_lottie.setImageAssetsFolder("charging/charging_state03/images");
            view_power_lottie.playAnimation();
        } else if (easyBatteryMod.getBatteryPercentage() < 100) {
            tvFullTime.setVisibility(View.VISIBLE);
            long lh = easyBatteryMod.getFullTime() / 60;
            long lmin = easyBatteryMod.getFullTime() % 60;
            tvFullTime.setText(getString(R.string.power_time_content, String.valueOf(lh), String.valueOf(lmin)));
            if (easyBatteryMod.getBatteryPercentage() < 80) {
                chargeState01.setSelected();
                view_power_lottie.setAnimation("charging/charging_state01/data.json");
                view_power_lottie.setImageAssetsFolder("charging/charging_state01/images");
                view_power_lottie.playAnimation();
            } else {
                chargeState02.setSelected();
                view_power_lottie.setAnimation("charging/charging_state02/data.json");
                view_power_lottie.setImageAssetsFolder("charging/charging_state02/images");
                view_power_lottie.playAnimation();
            }
        }
        tvPowerCapacity.setText(getString(R.string.power_num_capacity, String.valueOf(easyBatteryMod.getCapacity())));
        String voltageValue = "4.0";
        try {
            float voltage  = Float.valueOf(easyBatteryMod.getBatteryVoltage()) / 1000f;
            voltageValue =  new DecimalFormat(".0").format(voltage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvPowerVoltage.setText(getString(R.string.power_num_voltage, voltageValue));
        tvPowerTemp.setText(getString(R.string.power_num_temp, String.valueOf(easyBatteryMod.getBatteryTemperature()) ));
        if(easyBatteryMod.getBatteryTemperature()>35f){
            tvPowerTemp.setTextColor(getResources().getColor(R.color.home_content_red));
        }
        tvPowerApp.setText(getString(R.string.power_num_app, String.valueOf(NumberUtils.mathRandomInt(5, 15))));
        sceneTitle.setText(getString(R.string.tv_title_power_pop));

        if (easyBatteryMod.getBatteryTemperature() > 37) {
            sceneButton.setText(getString(R.string.power_cut_temp));
        } else {
            sceneButton.setText(getString(R.string.power_charging));
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            finish();
        } else {
            initAd();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scene_close:
                StatisticsUtils.trackClick("close_click", "充电插屏关闭按钮点击", "charging_plug_screen", "charging_plug_screen");
                finish();
                break;
            case R.id.scene_button:
                StatisticsUtils.trackClick("charging_plug_screen_button_click", "充电插屏按钮点击", "charging_plug_screen", "charging_plug_screen");
                if (sceneButton.getText().toString().equals(getString(R.string.power_cut_temp))) {
                    ARouter.getInstance().build(RouteConstants.PHONE_COOLING_ACTIVITY).navigation();
                } else if (sceneButton.getText().toString().equals(getString(R.string.power_charging))) {
                    startActivity(new Intent(this, PhoneSuperPowerActivity.class));
                }
                finish();
                break;
        }
    }

    /**
     * 广告展示
     */
    public void initAd(){
            if (isFinishing() || !AppHolder.getInstance().checkAdSwitch(PositionId.KEY_PAGE_DESK_BATTERY_AD))
                return;
//            StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "acceleration_page", "acceleration_page");
            MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.KEY_PAGE_DESK_BATTERY_AD, PositionId.DRAW_ONE_CODE), new SimpleViewCallBack(adContainer){
                @Override
                public void onAdClick(AdInfoModel adInfoModel) {
                    super.onAdClick(adInfoModel);
                    BatteryPopActivity.this.finish();
                }
            });


    }


}
