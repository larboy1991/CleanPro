package com.xiaoniu.cleanking.ui.main.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.CleanFinishActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.MyLinearLayout;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CleanMainFragment extends BaseFragment<CleanMainPresenter> {

    @BindView(R.id.text_count)
    TextView mTextCount;
    @BindView(R.id.layout_root)
    MyLinearLayout mLayoutRoot;
    @BindView(R.id.layout_clean_top)
    FrameLayout mLayoutCleanTop;
    @BindView(R.id.btn_ljql)
    Button mButtonCleanNow;
    @BindView(R.id.icon_outer)
    ImageView mIconOuter;
    @BindView(R.id.icon_inner)
    ImageView mIconInner;
    @BindView(R.id.circle_outer)
    View mCircleOuter;
    @BindView(R.id.circle_outer2)
    View mCircleOuter2;
    @BindView(R.id.text_scan_trace)
    TextView mTextScanTrace;
    @BindView(R.id.text_content)
    TextView mTextContent;
    @BindView(R.id.layout_scan)
    LinearLayout mLayoutScan;
    @BindView(R.id.view_arrow)
    ImageView mArrowRight;
    @BindView(R.id.layout_scroll)
    ScrollView mScrollView;
    @BindView(R.id.view_lottie)
    LottieAnimationView mAnimationView;
    @BindView(R.id.text_acce)
    LinearLayout mTextAcce;
    @BindView(R.id.line_ql)
    LinearLayout mLineQl;
    @BindView(R.id.text_wjgl)
    LinearLayout mTextWjgl;
    @BindView(R.id.iv_dun)
    ImageView mIvDun;
    @BindView(R.id.tv_size)
    TextView mTvSize;
    @BindView(R.id.tv_gb)
    TextView mTvGb;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.layout_clean_finish)
    ConstraintLayout mLayoutCleanFinish;

    private boolean isScanFinish = false;
    public static HashMap<Integer, JunkGroup> mJunkGroups;
    private CountEntity mCountEntity;
    private List<ImageView> mTopViews;
    private Handler mHandler;


    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clean_mainnew;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        ViewGroup.LayoutParams layoutParams = mLayoutCleanFinish.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenHeight(AppApplication.getInstance());
        mLayoutCleanFinish.setLayoutParams(layoutParams);

        new Handler().postDelayed(() -> {
            mPresenter.startScan();
            mPresenter.startCleanScanAnimation(mIconOuter, mCircleOuter, mCircleOuter2);
        }, 500);


    }


    //    @OnClick(R.id.text_cooling)
//    public void onCoolingViewClicked() {
//        //手机降温
//        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
//    }
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        //文件管理
        startActivity(FileManagerHomeActivity.class);
    }

    @OnClick(R.id.text_acce)
    public void text_acce() {
        //一键加速
        startActivity(PhoneAccessActivity.class);
    }

    @OnClick(R.id.line_ql)
    public void line_ql() {
        //手机清理
        startActivity(RouteConstants.CLEAN_BIG_FILE_ACTIVITY);
    }

    @OnClick(R.id.btn_ljql)
    public void btnLjql() {
        if (isScanFinish) {
            mPresenter.showTransAnim(mLayoutCleanTop);
            mPresenter.startCleanAnimation(mIconInner, mIconOuter, mLayoutScan, mTextCount, mCountEntity);
            mButtonCleanNow.setVisibility(View.GONE);
            mTextScanTrace.setText("垃圾清理中...");
            mArrowRight.setVisibility(View.GONE);
            mLayoutRoot.setIntercept(true);
            initWebView();
        } else {
            ToastUtils.show("正在扫描中");
        }
    }

    @OnClick(R.id.layout_scan)
    public void mClickLayoutScan() {
        //查看详情
        if (isScanFinish) {
            startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
        } else {
            ToastUtils.show("正在扫描中");
        }
    }

    /**
     * 扫描完成
     *
     * @param junkGroups
     */
    public void scanFinish(HashMap<Integer, JunkGroup> junkGroups) {
        isScanFinish = true;
        mJunkGroups = junkGroups;

        mTextScanTrace.setText("查看垃圾详情");
        mArrowRight.setVisibility(View.VISIBLE);

        mPresenter.stopCleanScanAnimation();

        if (mCountEntity != null) {
            mTvSize.setText(mCountEntity.getTotalSize());
            mTvGb.setText(mCountEntity.getUnit());
        }
    }

    /**
     * 统计总数
     *
     * @param total
     */
    public void showCountNumber(long total) {
        if (getActivity() == null) {
            return;
        }
        mCountEntity = CleanUtil.formatShortFileSize(total);
        getActivity().runOnUiThread(() -> mTextCount.setText(CleanUtil.formatShortFileSize(getActivity(), total)));
    }


    @Subscribe
    public void cleanFinish(String string) {
        if ("clean_finish".equals(string)) {
            //清理完成
            restoreLayout();
        }
    }

    public FrameLayout getCleanTopLayout() {
        return mLayoutCleanTop;
    }

    /**
     * 恢复布局
     */
    private void restoreLayout() {
        mLayoutRoot.setIntercept(false);
        mIconInner.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLayoutCleanTop.getLayoutParams();
        layoutParams.height = DeviceUtils.dip2px(400);
        mLayoutCleanTop.setLayoutParams(layoutParams);

        mIconOuter.setTranslationY(0);
        mIconInner.setTranslationY(0);
        mLayoutScan.setTranslationY(0);
        mTextCount.setTranslationY(0);

        mTextContent.setVisibility(View.VISIBLE);
        mTextCount.setVisibility(View.GONE);
        mLayoutScan.setVisibility(View.GONE);
        mButtonCleanNow.setText("完成");
        mButtonCleanNow.setVisibility(View.VISIBLE);
        mButtonCleanNow.setEnabled(false);
    }

    public void showBottomTab() {
        if (getActivity() != null) {
            FrameLayout frameLayout = getActivity().findViewById(R.id.frame_layout);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            marginParams.bottomMargin = DeviceUtils.dip2px(53);
            frameLayout.setLayoutParams(marginParams);
            getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.bottom_shadow).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void showScanFile(String p0) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if(mTextScanTrace != null) {
                mTextScanTrace.setText(p0);
            }
        });

    }

    public void endScanAnimation() {
        mCircleOuter2.setVisibility(View.GONE);
        mCircleOuter.setVisibility(View.GONE);
    }

    public void showCleanFinish(Long o) {
        Bundle bundle = new Bundle();
        bundle.putString("CLEAN_TYPE", CleanFinishActivity.TYPE_CLEAN_CACHE);
        bundle.putLong("clean_count", o);
        startActivity(RouteConstants.CLEAN_FINISH_ACTIVITY, bundle);
    }

    /**
     * 显示lottie动画
     */
    public void showLottieView() {
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data.json");
        mAnimationView.playAnimation();
    }

    /**
     * 清理完成后的页面
     *
     * @return
     */
    public View getCleanFinish() {
        return mLayoutCleanFinish;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        showBottomTab();
        mLayoutCleanFinish.setVisibility(View.GONE);
    }

    private long firstTime;

    public void onKeyBack() {
        if (mLayoutCleanFinish.getVisibility() == View.VISIBLE) {
            mLayoutCleanFinish.setVisibility(View.GONE);
            showBottomTab();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - firstTime > 1500) {
                Toast.makeText(getActivity(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = currentTimeMillis;
            } else {
                SPUtil.setInt(getContext(), "turnask", 0);
                AppManager.getAppManager().AppExit(getContext(), false);
            }
        }
    }

    /**
     * 状态栏颜色变化
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, false);
        }
    }

    public void initWebView() {
        String url = ApiModule.Base_H5_Host;
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                cancelLoadingDialog();
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }
        });
    }
}
