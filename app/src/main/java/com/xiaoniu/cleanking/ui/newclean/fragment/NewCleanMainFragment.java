package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppConfig;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.GameActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.NewsActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.widget.MyRelativeLayout;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewCleanMainPresenter;
import com.xiaoniu.cleanking.ui.news.adapter.ComFragmentAdapter;
import com.xiaoniu.cleanking.ui.news.adapter.HomeRecommendAdapter;
import com.xiaoniu.cleanking.ui.news.fragment.NewsListFragment;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.InternalStoragePremEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.ScreenUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.MeasureViewPager;
import com.xiaoniu.cleanking.widget.OperatorNestedScrollView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.common.widget.viewpagerindicator.IScrollBar;
import com.xiaoniu.common.widget.viewpagerindicator.IndicatorAdapter;
import com.xiaoniu.common.widget.viewpagerindicator.LineScrollBar;
import com.xiaoniu.common.widget.viewpagerindicator.ViewPagerIndicator;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * 1.2.1 新版本清理主页
 */
public class NewCleanMainFragment extends BaseFragment<NewCleanMainPresenter> implements HomeRecommendAdapter.onCheckListener , NestedScrollView.OnScrollChangeListener{

    private static final String TAG = "NewCleanMainFragment";

    private long firstTime;
    /*< XD added for feed begin */
    @BindView(R.id.layout_root)
    MyRelativeLayout layoutRoot;
    @BindView(R.id.v_home_top)
    RelativeLayout vHomeTop;
    @BindView(R.id.v_home_top_normal)
    RelativeLayout vTopTitleNormal;    // normal
    @BindView(R.id.ll_top_xiding)
    LinearLayout vTopTitleXiding;
    /* XD added for feed End >*/

    @BindView(R.id.tv_clean_type)
    TextView mTvCleanType;
    @BindView(R.id.tv_clean_type01)
    TextView mTvCleanType01;

    @BindView(R.id.line_shd)
    LinearLayout lineShd;
    @BindView(R.id.text_wjgl)
    LinearLayout textWjgl;
    @BindView(R.id.view_phone_thin)
    View viewPhoneThin;
    @BindView(R.id.view_news)
    View viewNews;
    @BindView(R.id.v_game_clean)
    View viewGame;
    @BindView(R.id.tv_acc)
    TextView mAccTv;
    @BindView(R.id.tv_noti_clear)
    TextView mNotiClearTv;
    @BindView(R.id.tv_electricity)
    TextView mElectricityTv;
    @BindView(R.id.iv_acc)
    ImageView mAccIv;
    @BindView(R.id.iv_noti_clear)
    ImageView mNotiClearIv;
    @BindView(R.id.iv_electricity)
    ImageView mElectricityIv;
    @BindView(R.id.iv_acc_g)
    ImageView mAccFinishIv;
    @BindView(R.id.iv_noti_g)
    ImageView mNotiClearFinishIv;
    @BindView(R.id.iv_electricity_g)
    ImageView mElectricityFinishIv;
    @BindView(R.id.iv_interaction)
    ImageView mInteractionIv;
    @BindView(R.id.image_ad_bottom_first)
    ImageView mImageFirstAd;
    @BindView(R.id.image_ad_bottom_second)
    ImageView mImageSecondAd;
    @BindView(R.id.view_lottie_home)
    LottieAnimationView mLottieHomeView;
    @BindView(R.id.tv_now_clean)
    ImageView tvNowClean;
    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_scroll)
    OperatorNestedScrollView mNestedScrollView;   //  XD modify
    @BindView(R.id.v_no_net)
    View mNoNetView;

    /*< XD added for feed begin */
    @BindView(R.id.close_feed_empty_view)
    View close_feed_empty_view;
    @BindView(R.id.home_feeds)
    LinearLayout homeFeeds;    // 信息流
    @BindView(R.id.feed_view_pager)
    MeasureViewPager feedViewPager;   // feed pager
    @BindView(R.id.feed_indicator)
    ViewPagerIndicator feedIndicator;
    @BindView(R.id.fl_top_nav)
    LinearLayout mFLTopNav;
    @BindView(R.id.iv_back)
    ImageView mIvBack;

    private String mType = "white";
    private static final String KEY_TYPE = "TYPE";
    private NewsType[] mNewTypes = {NewsType.TOUTIAO, NewsType.SHEHUI, NewsType.GUONEI, NewsType.GUOJI, NewsType.YULE};
    private ComFragmentAdapter mNewsListFragmentAdapter;
    private List<com.xiaoniu.common.base.BaseFragment> mNewsListFragments;  // NewsListFragments
    private boolean canXiding = true;
    public LayoutInflater mInflater;
    private int mStatusBarHeight;
    /* XD added for feed End >*/

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private int mInteractionPoistion; //互动式广告position、
    private int mShowCount;

    private List<InteractionSwitchList.DataBean.SwitchActiveLineDTOList> mInteractionList;
    private HomeRecommendAdapter mRecommendAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_clean_main;
    }

    /*< XD added for feed begin */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable(savedInstanceState);
    }

    protected void initVariable(Bundle arguments) {
        mNewsListFragments = new ArrayList<>();
        if (arguments != null) {
            mType = arguments.getString(KEY_TYPE);
            Log.d(TAG, "!--->initVariable----mType:"+mType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    /* XD added for feed End >*/

    @Override
    protected void initView() {
        mStatusBarHeight = ScreenUtil.getStatusBarHeight(getActivity());
        Log.d(TAG, "!--->initView--mStatusBarHeight:" + mStatusBarHeight);

        tvNowClean.setVisibility(View.VISIBLE);
        EventBus.getDefault().register(this);
        showHomeLottieView();
        initRecyclerView();
        initFeedView();  // XD added 20200509

        mPresenter.getRecommendList();
        mPresenter.requestBottomAd();
        mPresenter.getInteractionSwitch();
        mPresenter.getAccessListBelow();
        if (PreferenceUtil.isFirstForHomeIcon()) {
            PreferenceUtil.saveFirstForHomeIcon(false);
        } else {
            if (!PreferenceUtil.getCleanTime()) {
                mAccFinishIv.setVisibility(View.VISIBLE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");
            } else {
                mShowCount++;
                if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                    mAccFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                    mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                    mAccTv.setText(getString(R.string.tool_one_key_speed));
                } else {
                    mAccFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                    mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
                }
            }

            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                mNotiClearFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.getNotificationCleanTime()) {
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    mNotiClearFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                }
            }

            if (mShowCount < 2 && AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.getPowerCleanTime()) {
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                } else {
                    mShowCount++;
                    mElectricityFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                }
            }
        }
        //状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(getActivity(), AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            viewNews.setVisibility(View.GONE);
        } else {
            viewNews.setVisibility(VISIBLE);
        }
    }

    /**
     * @author xd.he
     */
    private void initFeedView() {
        mNestedScrollView.setOnScrollChangeListener(this);
        feedViewPager.setOffscreenPageLimit(10);
        homeFeeds.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!AppConfig.isFeedClosed()) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) homeFeeds.getLayoutParams();
                        params.height = layoutRoot.getHeight() - mFLTopNav.getHeight();  //  mStatusBarHeight
                        homeFeeds.setLayoutParams(params);
                        mNestedScrollView.scrollTo(mNestedScrollView.getScrollX(), 0);
                        mNestedScrollView.requestLayout();
                    } else {
                        homeFeeds.setVisibility(View.GONE);
                        close_feed_empty_view.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        feedViewPager.setNeedScroll(false);
    }

    private void initRecyclerView() {
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecommendAdapter = new HomeRecommendAdapter(getActivity());
        mRecommendAdapter.setmOnCheckListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecommendAdapter);
        mNestedScrollView.smoothScrollTo(0, 0);
        mRecyclerView.setFocusable(false);
    }

    /**
     * 显示广告 position = 0 第一个 position = 1  第二个
     *
     * @param dataBean
     */
    public void showFirstAd(ImageAdEntity.DataBean dataBean, int position) {
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.BANNER);
        if (position == 0) {
            mImageFirstAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageFirstAd);
            clickDownload(mImageFirstAd, dataBean.getDownloadUrl(), position);
//            mTextBottomTitle.setVisibility(GONE);
        } else if (position == 1) {
            mImageSecondAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageSecondAd);
            clickDownload(mImageSecondAd, dataBean.getDownloadUrl(), position);
//            mTextBottomTitle.setVisibility(GONE);
        }
        StatisticsUtils.trackClickAD("ad_show", "\"广告展示曝光", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
    }

    /**
     * 获取互动式广告成功
     *
     * @param switchInfoList
     */
    public void getInteractionSwitchSuccess(InteractionSwitchList switchInfoList) {
        if (null == switchInfoList || null == switchInfoList.getData() || switchInfoList.getData().size() <= 0)
            return;
        if (switchInfoList.getData().get(0).isOpen()) {
            mInteractionList = switchInfoList.getData().get(0).getSwitchActiveLineDTOList();
            Glide.with(this).load(switchInfoList.getData().get(0).getSwitchActiveLineDTOList().get(0).getImgUrl()).into(mInteractionIv);
        }
    }

    /**
     * 互动式广告
     */
    @OnClick(R.id.iv_interaction)
    public void interactionClick() {
        if (mInteractionPoistion > 2) {
            mInteractionPoistion = 0;
        }
        StatisticsUtils.trackClick("Interaction_ad_click", "用户在首页点击互动式广告按钮", "clod_splash_page", "home_page");
        if (null != mInteractionList && mInteractionList.size() > 0) {

            if (mInteractionList.size() == 1) {
                startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                        .putExtra(ExtraConstant.WEB_URL, mInteractionList.get(0).getLinkUrl()));
            } else {
                if (mInteractionList.size() - 1 >= mInteractionPoistion) {
                    startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                            .putExtra(ExtraConstant.WEB_URL, mInteractionList.get(mInteractionPoistion).getLinkUrl()));
                }

            }
            mInteractionPoistion++;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
        mPresenter.getSwitchInfoList();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();

        //有通知时改变通知栏清理状态
       /* if (mNotifySize > 0 && !PreferenceUtil.isCleanNotifyUsed() && mShowCount < 2) {
            mShowCount++;
            mNotiClearFinishIv.setVisibility(View.GONE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
            mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
            mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
        }*/

        if (null != mInteractionList && mInteractionList.size() > 0) {
            if (mInteractionPoistion > 2) {
                mInteractionPoistion = 0;
            }
            if (mInteractionList.size() == 1) {
                GlideUtils.loadGif(getActivity(), mInteractionList.get(0).getImgUrl(), mInteractionIv, 10000);
            } else {
                if (mInteractionList.size() - 1 >= mInteractionPoistion) {
                    GlideUtils.loadGif(getActivity(), mInteractionList.get(mInteractionPoistion).getImgUrl(), mInteractionIv, 10000);
                }

            }
        }

        lineShd.setEnabled(true);
        textWjgl.setEnabled(true);
        viewPhoneThin.setEnabled(true);
        viewNews.setEnabled(true);
        viewGame.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
    }

    /**
     * 清理完成回调
     *
     * @param event
     */
    @Subscribe
    public void fromHomeCleanFinishEvent(FromHomeCleanFinishEvent event) {
        if (null == event || TextUtils.isEmpty(event.getTitle())) return;
        mShowCount = 0;
        if (getString(R.string.tool_one_key_speed).contains(event.getTitle())) { //一键加速
//            mShowCount--;
            mAccFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs, mAccIv);
            mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");

            //通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                mNotiClearFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    mNotiClearFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                } else if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.tool_notification_clean);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
//                    mShowCount--;
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                }
            }

            //超强省电
            if (AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.isCleanPowerUsed() && PreferenceUtil.getPowerCleanTime()) {
                    mShowCount++;
                    mElectricityFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
//                    mShowCount--;
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                }
            }
        } else if (getString(R.string.tool_notification_clean).contains(event.getTitle())) {//通知栏清理
//            mShowCount--;
            mNotiClearFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
            mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            mNotiClearTv.setText(R.string.finished_clean_notify_hint);

            //一键加速
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mAccTv.setText(getString(R.string.tool_one_key_speed));
            } else if (!PreferenceUtil.isCleanJiaSuUsed() && PreferenceUtil.getCleanTime()) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            }

            //超强省电
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                mElectricityFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_o, mElectricityIv);
                mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mElectricityTv.setText(getString(R.string.tool_super_power_saving));
            } else if (AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.isCleanPowerUsed() && PreferenceUtil.getPowerCleanTime()) {
                    mShowCount++;
                    mElectricityFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
//                    mShowCount--;
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                }
            }

        } else if (getString(R.string.tool_super_power_saving).contains(event.getTitle())) { //超强省电
//            mShowCount--;
            mElectricityFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
            mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
            } else {
                mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
            }
            //一键加速
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mAccTv.setText(getString(R.string.tool_one_key_speed));
            } else if (!PreferenceUtil.isCleanJiaSuUsed() && PreferenceUtil.getCleanTime()) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            }

            //通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                mNotiClearFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    mNotiClearFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                } else if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.tool_notification_clean);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
//                    mShowCount--;
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                }
            }
        }
        if (mShowCount <= 0) {
            mTvCleanType01.setText(getString(R.string.recommend_count_hint_all));
        } else {
            mTvCleanType01.setText(getString(R.string.recommend_count_hint, String.valueOf(mShowCount)));
        }
    }

    /**
     * 一键加速获取权限后通知首页一键加速状态改变
     */
    @Subscribe
    public void internalStoragePremEvent(InternalStoragePremEvent event) {
        if (!PreferenceUtil.isCleanJiaSuUsed()) {
            mAccFinishIv.setVisibility(View.GONE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
            mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
            mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
        }
    }

    @Subscribe
    public void changeLifecyEvent(LifecycEvent lifecycEvent) {
        if (lifecycEvent.isActivity()) {
            tvNowClean.setVisibility(VISIBLE);
            mTvCleanType.setVisibility(VISIBLE);
            mTvCleanType01.setVisibility(View.GONE);
            showTextView();
            mLottieHomeView.useHardwareAcceleration(true);
            mLottieHomeView.setAnimation("clean_home_top.json");
            mLottieHomeView.setImageAssetsFolder("images_home");
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    /**
     * 立即清理
     */
    @OnClick(R.id.tv_now_clean)
    public void nowClean() {
        StatisticsUtils.trackClick("home_page_clean_click", "用户在首页点击【立即清理】", "home_page", "home_page");
        //PreferenceUtil.getNowCleanTime() || TextUtils.isEmpty(Constant.APP_IS_LIVE
        ((MainActivity) getActivity()).commitJpushClickTime(1);
        if (true) {
            startActivity(NowCleanActivity.class);
        } else {
            AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
            boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_CLEAN_ALL,PositionId.DRAW_THREE_CODE);
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_suggest_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putString("home", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 文件管理
     */
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        textWjgl.setEnabled(false);
        ((MainActivity) getActivity()).commitJpushClickTime(4);
        StatisticsUtils.trackClick("file_clean_click", "用户在首页点击【文件清理】按钮", "home_page", "home_page");
        startActivity(FileManagerHomeActivity.class);
    }

    /**
     * 一键加速
     */
    @OnClick(R.id.text_acce)
    public void text_acce() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        ((MainActivity) getActivity()).commitJpushClickTime(2);
        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (!PreferenceUtil.getCleanTime()) {
            boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_JIASU,PositionId.DRAW_THREE_CODE);
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
            startActivity(PhoneAccessActivity.class, bundle);
        }
    }

    /**
     * 超强省电
     */
    @OnClick(R.id.line_shd)
    public void line_shd() {
        lineShd.setEnabled(false);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(9);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
        if (PreferenceUtil.getPowerCleanTime()) {
            startActivity(PhoneSuperPowerActivity.class);
        } else {
            boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_CQSD,PositionId.DRAW_THREE_CODE);
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }

    }

    /**
     * 新闻点击
     */
    @OnClick(R.id.view_news)
    public void ViewNewsClick() {
        viewNews.setEnabled(false);
        StatisticsUtils.trackClick("news_click", "用户在首页点击【头条新闻热点】按钮", "home_page", "home_page");
        startActivity(NewsActivity.class);
    }

    /**
     * 软件管理
     */
    @OnClick(R.id.view_phone_thin)
    public void ViewPhoneThinClick() {
        viewPhoneThin.setEnabled(false);
        Intent intent = new Intent(getActivity(), PhoneThinActivity.class);
        intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_soft_manager));
        startActivity(intent);
        StatisticsUtils.trackClick("app_manage_click", "用户在首页点击【软件管理】按钮", "home_page", "home_page");
    }

    /**
     * 游戏加速
     */
    @OnClick(R.id.v_game_clean)
    public void ViewThinClick() {
        viewGame.setEnabled(false);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.game_quicken));
        startActivity(intent);
        StatisticsUtils.trackClick("gameboost_click", "游戏加速点击", "home_page", "home_page");
    }

    /*    *//**
     * 权限设置
     *//*
    @OnClick(R.id.iv_permission)
    public void onClick() {
        startActivity(new Intent(getContext(), PermissionActivity.class));
        StatisticsUtils.trackClick("Triangular_yellow_mark_click", "三角黄标", AppHolder.getInstance().getSourcePageId(), "permission_page");
    }*/

    /**
     * 微信专清
     */
    @OnClick(R.id.line_wx)
    public void mClickWx() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);

        ((MainActivity) getActivity()).commitJpushClickTime(5);
        StatisticsUtils.trackClick("wxclean_click", "用户在首页点击【微信专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_WECHAT,PositionId.DRAW_THREE_CODE);
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_chat_clear), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 通知栏清理
     */
    @OnClick(R.id.line_super_power_saving)
    public void mClickQq() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(8);
        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
        if (!NotifyUtils.isNotificationListenerEnabled() || PreferenceUtil.getNotificationCleanTime() || mNotifySize > 0) {
            NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0);
        } else {
            boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_NOTIFY,PositionId.DRAW_THREE_CODE);
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_notification_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 手机降温
     */
    @OnClick(R.id.line_jw)
    public void mClickJw() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(6);
        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");

        if (PreferenceUtil.getCoolingCleanTime()) {
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        } else {
            boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_COOL,PositionId.DRAW_THREE_CODE);
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_phone_temperature_low), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }

    public void onKeyBack() {
//        long currentTimeMillis = System.currentTimeMillis();
//        if (currentTimeMillis - firstTime > 1500) {
//            Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            firstTime = currentTimeMillis;
//        } else {
//            SPUtil.setInt(getContext(), "turnask", 0);
//            AppManager.getAppManager().AppExit(getContext(), false);
//        }
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_28d1a6), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_28d1a6), false);
            }
        } else {
            NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
        }
    }

    /**
     * EventBus 立即清理完成后，更新首页显示文案
     */
    @Subscribe
    public void onEventClean(CleanEvent cleanEvent) {
        if (cleanEvent != null) {
            if (cleanEvent.isCleanAminOver()) {
                showTextView01();
                tvNowClean.setVisibility(View.GONE);
                mLottieHomeView.useHardwareAcceleration(true);
                mLottieHomeView.setAnimation("clean_home_top2.json");
                mLottieHomeView.setImageAssetsFolder("images_home_finish");
                mLottieHomeView.playAnimation();
                mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (null != mLottieHomeView) {
                            mLottieHomeView.playAnimation();
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }
    }

    /**
     * 点击下载app
     *
     * @param view
     * @param downloadUrl
     */
    public void clickDownload(View view, String downloadUrl, int position) {
        view.setOnClickListener(v -> {
            //广告埋点
            StatisticsUtils.trackClickAD("ad_click", "\"广告点击", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
           /* Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, downloadUrl);
            bundle.putBoolean(Constant.NoTitle, false);
            startActivity(UserLoadH5Activity.class, bundle);*/
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(downloadUrl);
            intent.setData(content_url);
            startActivity(intent);
        });
    }

    /**
     * 静止时动画
     */
    private void showHomeLottieView() {
        showTextView();
        mLottieHomeView.useHardwareAcceleration(true);
        mLottieHomeView.setAnimation("clean_home_top.json");
        mLottieHomeView.setImageAssetsFolder("images_home");
        mLottieHomeView.playAnimation();
        mLottieHomeView.setVisibility(VISIBLE);
        mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void showTextView() {
        String hintText = getString(R.string.tool_home_hint);
        SpannableString msp = new SpannableString(hintText);
//        msp.setSpan(new AbsoluteSizeSpan(ScreenUtils.dpToPx(mContext, 18)), hintText.indexOf("存在大量垃圾"), hintText.indexOf("，"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), hintText.indexOf("存在大量垃圾"), hintText.indexOf("，"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTvCleanType != null && msp != null) {
                    mTvCleanType.setText(msp);
                    mTvCleanType.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .setListener(null);
                }
            }
        }, 1000);
    }

    public void showTextView01() {
        String showText = getString(R.string.tool_phone_already_clean);
        String showText01 = "";
        if (mShowCount <= 0) {
            showText01 = getString(R.string.recommend_count_hint_all);
        } else {
            showText01 = getString(R.string.recommend_count_hint, String.valueOf(mShowCount));
        }
        SpannableString msp = new SpannableString(showText);
        SpannableString msp01 = new SpannableString(showText01);
        msp01.setSpan(new AbsoluteSizeSpan(ScreenUtils.dpToPx(mContext, 17)), 0, showText01.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp01.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, showText01.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvCleanType.setText(msp);
        mTvCleanType.setVisibility(VISIBLE);
        mTvCleanType01.setText(msp01);
        mTvCleanType01.setVisibility(VISIBLE);
    }

    /**
     * 获取推荐列表成功
     *
     * @param entity
     */
    public void getRecommendListSuccess(HomeRecommendEntity entity) {
        if (null == mRecommendAdapter || null == entity || null == entity.getData() || entity.getData().size() <= 0)
            return;
        PreferenceUtil.saveFirstHomeRecommend(false);
        mRecyclerView.setVisibility(VISIBLE);
        mNoNetView.setVisibility(View.GONE);
        mRecommendAdapter.setData(entity.getData());
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d("XiLei", "subscribe:" + Thread.currentThread().getName());
                if (null == ApplicationDelegate.getAppDatabase() || null == ApplicationDelegate.getAppDatabase().homeRecommendDao())
                    return;
                ApplicationDelegate.getAppDatabase().homeRecommendDao().deleteAll();
                ApplicationDelegate.getAppDatabase().homeRecommendDao().insertAll(entity.getData());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 获取推荐列表失败 取本地数据
     */
    public void getRecommendListFail() {
        if (PreferenceUtil.isFirstHomeRecommend()) {
            mRecyclerView.setVisibility(View.GONE);
            mNoNetView.setVisibility(VISIBLE);
            return;
        }
        if (null == ApplicationDelegate.getAppDatabase() || null == ApplicationDelegate.getAppDatabase().homeRecommendDao()
                || null == ApplicationDelegate.getAppDatabase().homeRecommendDao().getAll() || ApplicationDelegate.getAppDatabase().homeRecommendDao().getAll().size() <= 0)
            return;
        Observable<List<HomeRecommendListEntity>> observable = Observable.create(new ObservableOnSubscribe<List<HomeRecommendListEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HomeRecommendListEntity>> emitter) throws Exception {
                Log.d("XiLei", "subscribe2222:" + Thread.currentThread().getName());
                emitter.onNext(ApplicationDelegate.getAppDatabase().homeRecommendDao().getAll());
            }
        });
        Consumer<List<HomeRecommendListEntity>> consumer = new Consumer<List<HomeRecommendListEntity>>() {
            @Override
            public void accept(List<HomeRecommendListEntity> list) throws Exception {
                Log.d("XiLei", "accept:" + list.size() + ":" + Thread.currentThread().getName());
                if (null == mRecommendAdapter) return;
                mRecommendAdapter.setData(list);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    @Override
    public void onCheck(List<HomeRecommendListEntity> list, int pos) {
        if (null == getActivity() || null == list || list.size() <= 0) return;
        if (list.get(pos).getLinkType().equals("1")) {
            if (list.get(pos).getName().equals(getString(R.string.game_quicken))) { //游戏加速
                StatisticsUtils.trackClick("gameboost_click", "游戏加速点击", "home_page", "home_page");
                AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
                if (PreferenceUtil.getGameTime()) {
                    SchemeProxy.openScheme(getActivity(), list.get(pos).getLinkUrl());
                } else {
                    boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_GAME,PositionId.DRAW_THREE_CODE);
                    if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.game_quicken), mRamScale, mNotifySize, mPowerSize) < 3) {
                        Bundle bundle = new Bundle();
                        bundle.putString("title", getString(R.string.game_quicken));
                        startActivity(CleanFinishAdvertisementActivity.class, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("title", getString(R.string.game_quicken));
                        bundle.putString("num", PreferenceUtil.getGameCleanPer());
                        startActivity(NewCleanFinishActivity.class, bundle);
                    }
                }
                return;
            } else if (list.get(pos).getName().equals(getString(R.string.tool_one_key_speed))) {
                StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
            }
            SchemeProxy.openScheme(getActivity(), list.get(pos).getLinkUrl());
        } else if (list.get(pos).getLinkType().equals("2")) {
            startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                    .putExtra(ExtraConstant.WEB_URL, list.get(pos).getLinkUrl()));
        } else if (list.get(pos).getLinkType().equals("3")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(list.get(pos).getLinkUrl());
            intent.setData(content_url);
            startActivity(intent);
        }
    }


    /*< XD added for feed begin */
    @Override
    protected void loadData() {
        loadFeedData();
    }

    private void showFeedView() {
        homeFeeds.setVisibility(View.VISIBLE);   // 显示信息流
        feedViewPager.setVisibility(View.VISIBLE);
    }

    protected void loadFeedData() {
        showFeedView();
        for (int i = 0; i < mNewTypes.length; i++) {
            NewsListFragment listFragment = NewsListFragment.getInstance(mNewTypes[i]);
            mNewsListFragments.add(listFragment);
        }
        mNewsListFragmentAdapter = new ComFragmentAdapter(getChildFragmentManager(), mNewsListFragments);
        feedViewPager.setAdapter(mNewsListFragmentAdapter);
        feedIndicator.bindViewPager(feedViewPager);
        feedIndicator.setIndicatorAdapter(new IndicatorAdapter() {
            @Override
            public View getTabView(Context context, int position) {
                TextView textView = (TextView) mInflater.inflate(R.layout.layout_news_tab_item, null);
                textView.setText(mNewTypes[position].getName());
                if ("white".equals(mType)) {
                    feedIndicator.setBackgroundColor(Color.WHITE); // WHITE color_06C581
                    mFLTopNav.setBackgroundColor(Color.WHITE);
                    mIvBack.setColorFilter(Color.BLACK);
                } else {
                    textView.setTextColor(Color.WHITE);
                }
                return textView;
            }

            @Override
            public IScrollBar getScrollBar(Context context) {
                LineScrollBar scrollBar = new LineScrollBar(context);
                scrollBar.setColor(Color.WHITE);//滚动块颜色
                scrollBar.setHeight(DisplayUtils.dip2px(context, 2));//滚动块高度，不设置默认和每个tabview高度一致
                scrollBar.setRadius(DisplayUtils.dip2px(context, 1));//滚动块圆角半径
                scrollBar.setGravity(Gravity.BOTTOM);//可设置上中下三种
                scrollBar.setWidth(DisplayUtils.dip2px(context, 12));//滚动块宽度，不设置默认和每个tabview宽度一致
                return scrollBar;
            }

            @Override
            public int getTabCount() {
                return mNewsListFragments.size();
            }

            @Override
            public void onTabChange(View tabView, int position, float selectPercent) { // callback many times ！
            }
        });

        feedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                feedViewPager.setNeedScroll(false);
                Rect rect = new Rect();
                homeFeeds.getGlobalVisibleRect(rect);
                int dy = rect.top - vHomeTop.getHeight() - mStatusBarHeight;
                doXiDingStickyAnim(mNestedScrollView.getScrollY() + dy, true);

                StatisticsUtils.trackClickNewsTab("content_cate_click", "“分类”点击", "selected_page", "information_page", i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onScrollChange(NestedScrollView nestedScrollView, int x, int y, int lastx, int lasty) {
        if (!AppConfig.isFeedClosed() && canXiding) {
            //处理吸顶操作
            Rect rect = new Rect();
            homeFeeds.getGlobalVisibleRect(rect);
            int dy = rect.top - vHomeTop.getHeight() - mStatusBarHeight;  // flow top - titleTop Height  - statusBarHeight
            int changeY = y - lasty;
            int mStickyHeight = (int) (ScreenUtil.getScreenHeightPx(NewCleanMainFragment.this.getActivity()) * 0.35f);
            if (dy> 0 && dy <= mStickyHeight && changeY > 0) {
                if (changeY < 20) {
                    if (dy > 0) {
                        doXiDingStickyAnim(y + dy, true, 300);
                    }
                } else {
                    doXiDingStickyAnim(y + dy, false);
                }
            }
        }
    }

    private void doXiDingStickyAnim(int scrolltoY, boolean isAnimation) {
        doXiDingStickyAnim(scrolltoY, isAnimation, 400);
    }

    private void doXiDingStickyAnim(int scrolltoY, boolean isAnimation, int duration) {
        mNestedScrollView.setNeedScroll(false);
        canXiding = false;
        if (isAnimation) {
            scrollAnima(mNestedScrollView.getScrollY(), scrolltoY, duration);
        } else {
            mNestedScrollView.scrollTo(mNestedScrollView.getScrollX(), scrolltoY);
            canXiding = true;
        }
        updateTitle(true);
    }

    @OnClick(R.id.fl_xiding_top_back)
    public void onClickGoBackToClean() {
        goBackToClean(true);
    }

    private void goBackToClean(boolean isAnimation) {
        canXiding = false;
        if (mNewsListFragmentAdapter != null) {
            mNewsListFragmentAdapter.resetScrollToTop();
        }
        mNestedScrollView.setNeedScroll(true);
        if (isAnimation) {
            scrollAnima(mNestedScrollView.getScrollY(), 0, 400);
        } else {
            mNestedScrollView.scrollTo(mNestedScrollView.getScrollX(), 0);
            canXiding = true;
        }
        updateTitle(false);
    }


    private void updateTitle(boolean xiding) {
        if (xiding) {
            vTopTitleNormal.setVisibility(View.GONE);  // lltop_narmal
            vTopTitleXiding.setVisibility(View.VISIBLE);
        } else {
            vTopTitleNormal.setVisibility(View.VISIBLE);
            vTopTitleXiding.setVisibility(View.GONE);
        }
    }

    private void scrollAnima(int start, int end, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取动画过程中的渐变值
                int animatedValue = (int) animation.getAnimatedValue();
                if (mNestedScrollView != null) {
                    mNestedScrollView.scrollTo(0, animatedValue);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                canXiding = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                canXiding = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
    /* XD added for feed End >*/
}
