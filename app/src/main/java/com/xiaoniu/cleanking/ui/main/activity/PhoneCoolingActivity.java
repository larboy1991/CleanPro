package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.callback.OnProgressUpdateListener;
import com.xiaoniu.cleanking.ui.main.adapter.ProcessIconAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HardwareInfo;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneCoolingPresenter;
import com.xiaoniu.cleanking.ui.main.widget.CustomerSpaceDecoration;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.widget.ArcProgressBar;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

/**
 * @author Administrator
 */
@Route(path = RouteConstants.PHONE_COOLING_ACTIVITY)
public class PhoneCoolingActivity extends BaseActivity<PhoneCoolingPresenter> {

    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.layout_title_bar)
    RelativeLayout mLayoutTitleBar;
    @BindView(R.id.text_temperature)
    TextView mTextTemperature;
    @BindView(R.id.layout_title)
    RelativeLayout mBgTitle;
    @BindView(R.id.bg_title)
    ImageView mImageTitle;
    @BindView(R.id.text_temperature_tips)
    TextView mTextTemperatureTips;
    @BindView(R.id.text_title_process)
    TextView mTextTitleProcess;
    @BindView(R.id.recycler_process)
    RecyclerView mRecyclerProcess;
    @BindView(R.id.layout_process)
    ConstraintLayout mLayoutProcess;
    @BindView(R.id.text_title_hardware)
    TextView mTextTitleHardware;
    @BindView(R.id.icon_cpu)
    ImageView mIconCpu;
    @BindView(R.id.layout_hardware)
    ConstraintLayout mLayoutHardware;
    @BindView(R.id.layout_bottom_container)
    LinearLayout mLayoutBottom;
    @BindView(R.id.progress_bar)
    ArcProgressBar mProgressBar;
    @BindView(R.id.image_point)
    ImageView mImagePoint;
    @BindView(R.id.text_temperature_number)
    TextView mTextTemperatureNumber;
    @BindView(R.id.layout_anim_cool)
    ConstraintLayout mLayoutAnimCool;
    @BindView(R.id.layout_content_cool)
    LinearLayout mLayoutContentCool;

    private ProcessIconAdapter mProcessIconAdapter;
    private HardwareInfo mHardwareInfo;
    public static ArrayList<FirstJunkInfo> mRunningProcess;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_cooling;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }

        mTextTitle.setText("手机降温");

        int phoneTemperature = mPresenter.getPhoneTemperature();
        mTextTemperature.setText(String.valueOf(phoneTemperature));
        if (phoneTemperature > 40) {
            mTextTemperatureTips.setText("手机温度较高");
            mBgTitle.setBackgroundColor(mContext.getResources().getColor(R.color.color_FD6F46));
            mLayoutTitleBar.setBackgroundColor(mContext.getResources().getColor(R.color.color_FD6F46));
            mImageTitle.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_bg_hot));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_FD6F46), true);
            } else {
                StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_FD6F46), false);
            }
        }

        initAdapter();

        mPresenter.getRunningProcess();

        mPresenter.getHardwareInfo();

        initCoolAnimation(phoneTemperature);
    }

    private void initCoolAnimation(int phoneTemperature) {
        mProgressBar.setArcBgColor(getResources().getColor(R.color.color_progress_bg));
        mProgressBar.setProgressColor(getResources().getColor(R.color.white));
        mProgressBar.setUpdateListener(new OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(float progress) {
                mImagePoint.setRotation(progress);
            }
        });
        mProgressBar.setProgress(phoneTemperature);
        initAnimator(phoneTemperature);
    }

    /**
     * 数字增加动画
     *
     * @param phoneTemperature
     */
    private void initAnimator(int phoneTemperature) {
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(0, phoneTemperature);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            mTextTemperatureNumber.setText(String.valueOf(animatedValue));
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setViewTrans();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    /**
     * 数字动画播放完后上移，布局高度缩小
     */
    public void setViewTrans() {
        int bottom = mBgTitle.getBottom();
        mLayoutContentCool.setVisibility(VISIBLE);
        int startHeight = DeviceUtils.getScreenHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight - bottom, 0);
        new Handler().postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mBgTitle, "alpha", 0, 1);
            mBgTitle.setVisibility(VISIBLE);
            alpha.setDuration(300);
            alpha.start();
        }, 700);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mLayoutContentCool.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.topMargin = currentValue;
            mLayoutContentCool.setLayoutParams(rlp);
        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
    }

    private void initAdapter() {
        mRecyclerProcess.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerProcess.addItemDecoration(new CustomerSpaceDecoration());
        mProcessIconAdapter = new ProcessIconAdapter();
        mRecyclerProcess.setAdapter(mProcessIconAdapter);
    }

    @OnClick({R.id.img_back})
    public void onBackPress(View view) {
        StatisticsUtils.trackClick("Operating_components_click", "\"手机降温\"返回", "tools_page", "temperature_result_display_page");
        finish();
    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("Operating_components_click", "\"手机降温\"返回", "tools_page", "temperature_result_display_page");
        super.onBackPressed();
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @OnClick(R.id.layout_process)
    public void onMLayoutProcessClicked() {
        StatisticsUtils.trackClick("Running_applications_click ", "\"运行的应用\"点击", "tools_page", "temperature_result_display_page");
        startActivity(RouteConstants.PROCESS_INFO_ACTIVITY);
    }

    @OnClick(R.id.layout_hardware)
    public void onMLayoutHardwareClicked() {
        StatisticsUtils.trackClick("Operating_components_click ", "\"运行的部件\"点击", "tools_page", "temperature_result_display_page");

        Bundle bundle = new Bundle();
        bundle.putSerializable("content", mHardwareInfo);
        startActivity(RouteConstants.HARDWARE_INFO_ACTIVITY, bundle);
    }

    @OnClick(R.id.text_cool_now)
    public void onMLayoutCoolClicked() {
        StatisticsUtils.trackClick("Cool_down_immediately_click", "\"立即降温\"点击", "tools_page", "temperature_result_display_page");

        //立即降温
        for (FirstJunkInfo firstJunkInfo : mRunningProcess) {
            CleanUtil.killAppProcesses(firstJunkInfo.getAppPackageName(), firstJunkInfo.getPid());
        }

        finish();

        Bundle bundle = new Bundle();
        bundle.putString("CLEAN_TYPE", CleanFinishActivity.TYPE_COOLING);
        bundle.putLong("clean_count", new Random().nextInt(3) + 1);
        startActivity(RouteConstants.CLEAN_FINISH_ACTIVITY, bundle);
    }

    /**
     * 显示进程
     *
     * @param runningProcess
     */
    public void showProcess(ArrayList<FirstJunkInfo> runningProcess) {
        mRunningProcess = runningProcess;
        mTextTitleProcess.setText(runningProcess.size() + "个运行的应用");
        mProcessIconAdapter.setData(runningProcess);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRunningProcess != null) {
            showProcess(mRunningProcess);
        }

    }

    /**
     * 硬件信息
     *
     * @param hardwareInfo
     */
    public void showHardwareInfo(HardwareInfo hardwareInfo) {
        mHardwareInfo = hardwareInfo;

        int totalHardware = 1;
        //电池
        if (hardwareInfo.isCharge()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_battery_normal, "电池"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_battery_not, "电池"));
        }
        //蓝牙
        if (hardwareInfo.isBluetoothOpen()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_bluetooth_normal, "蓝牙"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_bluetooth_not, "蓝牙"));
        }

        //GPS
        if (hardwareInfo.isGPSOpen()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_gps_normal, "GPS"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_gps_not, "GPS"));
        }

        //WIFI
        if (hardwareInfo.isWiFiOpen()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_wifi_normal, "WIFI"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_wifi_not, "WIFI"));
        }

        mHardwareInfo.setSize(totalHardware);

        //运行数量显示
        mTextTitleHardware.setText(totalHardware + "个运行的部位");
    }

    public View generateItem(int imageIcon, String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_cool_hardwear, mLayoutBottom, false);
        ImageView mIcon = view.findViewById(R.id.icon_hardware);
        TextView mTextName = view.findViewById(R.id.text_name);
        mIcon.setImageDrawable(AppApplication.getInstance().getResources().getDrawable(imageIcon));
        mTextName.setText(name);
        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatisticsUtils.trackClick("Cell_phone_cooling_view_page", "\"手机降温\"浏览", "tools_page", "temperature_result_display_page");
    }
}
