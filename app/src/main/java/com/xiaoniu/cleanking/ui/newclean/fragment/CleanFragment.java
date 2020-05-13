package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.callback.OnCleanListSelectListener;
import com.xiaoniu.cleanking.ui.main.adapter.DockingExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.CleanPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 清理操作页面
 */
public class CleanFragment extends BaseFragment<CleanPresenter> {

    @BindView(R.id.junk_list)
    ExpandableListView mExpandableListView;
    @BindView(R.id.do_junk_clean)
    TextView doJunkClean;

    @BindView(R.id.iv_clean_bg03)
    TextView ivCleanBg03;
    @BindView(R.id.iv_clean_bg02)
    TextView ivCleanBg02;
    @BindView(R.id.iv_clean_bg01)
    TextView ivCleanBg01;
    @BindView(R.id.rel_clean_content)
    RelativeLayout relCleanContent;
    @BindView(R.id.view_lottie_bottom)
    LottieAnimationView viewLottieBottom;
    @BindView(R.id.view_lottie_top)
    LottieAnimationView viewLottieTop;
    @BindView(R.id.btn_left_scan)
    ImageView btnLeftScan;
    @BindView(R.id.tv_clean_count)
    AppCompatTextView tvCleanCount;
    @BindView(R.id.tv_clean_unit)
    TextView tvCleanUnit;
    @BindView(R.id.layout_show_list)
    RelativeLayout layoutShowList;
    @BindView(R.id.clean_toolbar)
    Toolbar cleanToolbar;
    View mHeadView;
    TextView[] ivs;
    TextView tvCheckedSize;
    private DockingExpandableListViewAdapter mAdapter;
    private CountEntity totalCountEntity;
    private CountEntity checkCountEntity;
    private long checkedSize = 0;
    private long totalSize = 0;
    private HashMap<Integer, JunkGroup> mJunkGroups;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    int shouIndex = 2;

    @Inject
    NoClearSPHelper mSPHelper;

    public static CleanFragment newInstance() {
        return new CleanFragment();
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clean;
    }

    @Override
    protected void initView() {
        mHeadView = getLayoutInflater().inflate(R.layout.layout_head_now_clean, null);
        mPresenter.getAccessListBelow();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        TextView tvSize = mHeadView.findViewById(R.id.tv_size);
        TextView tvUnit = mHeadView.findViewById(R.id.tv_clear_finish_gb_title);

        tvSize.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        tvUnit.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/FuturaRound-Medium.ttf"));

        tvCheckedSize = mHeadView.findViewById(R.id.tv_checked_size);
//        mJunkGroups = ((NowCleanActivity) getActivity()).getJunkGroups();

        totalSize = checkedSize = CleanUtil.getTotalSize(mJunkGroups);
        checkCountEntity = totalCountEntity = CleanUtil.formatShortFileSize(totalSize);

        if (totalCountEntity != null) {
            tvSize.setText(totalCountEntity.getTotalSize());
            tvUnit.setText(totalCountEntity.getUnit());
            tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
            doJunkClean.setText(mContext.getString(R.string.text_clean) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
        }

        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setChildIndicator(null);
        mExpandableListView.setDividerHeight(0);
        mExpandableListView.addHeaderView(mHeadView);

        mExpandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            JunkGroup junkGroup = mJunkGroups.get(groupPosition);
            if (junkGroup != null) {
                junkGroup.isExpand = !junkGroup.isExpand;
                mAdapter.notifyDataSetChanged();
            }
            return false;
        });

        mAdapter = new DockingExpandableListViewAdapter(getActivity(), mExpandableListView);
        mAdapter.setOnCleanListSelectListener(new OnCleanListSelectListener() {
            @Override
            public void onGroupSelected(int groupPosition, boolean isChecked) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                junkGroup.isChecked = isChecked;
                mJunkGroups.put(groupPosition, junkGroup);
                checkedSize = CleanUtil.getTotalSize(mJunkGroups);
                checkCountEntity = CleanUtil.formatShortFileSize(checkedSize);
                if (checkCountEntity != null) {
                    tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                    doJunkClean.setText(mContext.getString(R.string.text_clean) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                }
            }

            @Override
            public void onFistChilSelected(int groupPosition, int childPosition, boolean isChecked) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                junkGroup.mChildren.get(childPosition).setAllchecked(isChecked);
                mJunkGroups.put(groupPosition, junkGroup);
                checkedSize = CleanUtil.getTotalSize(mJunkGroups);
                checkCountEntity = totalCountEntity = CleanUtil.formatShortFileSize(checkedSize);
                if (totalCountEntity != null) {
                    tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                    doJunkClean.setText(mContext.getString(R.string.text_clean) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());

                }
            }
        });

        mExpandableListView.setAdapter(mAdapter);

        mAdapter.setData(mJunkGroups);
        //solve umeng error -> 'int java.util.HashMap.size()' on a null object reference
        if (mJunkGroups != null) {
            for (int i = 0; i < mJunkGroups.size(); i++) {
                mExpandableListView.expandGroup(i);
            }
        }
    }

    @OnClick({R.id.layout_junk_clean,R.id.btn_left_scan})
    public void viewClick(View view) {
        switch (view.getId()){
            case R.id.layout_junk_clean:
                starClean();
                break;
            case R.id.btn_left_scan:
                ((NowCleanActivity)getActivity()).backClick(true);
                break;
        }

    }

    public void starClean(){
        //扫描中弹框_确认按钮
        StatisticsUtils.trackClick("cleaning_button_click", "用户在扫描结果页点击【清理】按钮", "clean_up_scan_page", "scanning_result_page");
        startClean();
    }

    /**
     * 清理完成
     */
    public void cleanFinish() {
        AppHolder.getInstance().setCleanFinishSourcePageId("clean_finish_annimation_page");
        if (PreferenceUtil.getNowCleanTime() || TextUtils.isEmpty(Constant.APP_IS_LIVE)) {
            PreferenceUtil.saveNowCleanTime();
        }

        //状态栏恢复正常
        NotificationEvent event = new NotificationEvent();
        event.setType("clean");
        event.setFlag(0);
        EventBus.getDefault().post(event);

        PreferenceUtil.saveCleanAllUsed(true);
        boolean isOpen = AppHolder.getInstance().isOpen(PositionId.KEY_CLEAN_ALL,PositionId.DRAW_THREE_CODE);
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        if (getActivity() != null && this.isAdded()) {
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_suggest_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", mContext.getString(R.string.tool_suggest_clean));
                bundle.putString("num", checkCountEntity.getTotalSize());
                bundle.putString("unit", checkCountEntity.getUnit());
                startActivity(NewCleanFinishActivity.class, bundle);
            }
            getActivity().finish();
        }
    }

    private void startClean() {
        cleanToolbar.setVisibility(View.GONE);
        relCleanContent.setVisibility(View.VISIBLE);
        ivCleanBg01.setVisibility(View.VISIBLE);
        ivCleanBg02.setVisibility(View.VISIBLE);
        ivCleanBg03.setVisibility(View.VISIBLE);
        ivs = new TextView[]{ivCleanBg01,ivCleanBg02,ivCleanBg03};
        viewLottieBottom.useHardwareAcceleration();
        viewLottieBottom.setAnimation("cleanbottom.json");
        viewLottieBottom.setImageAssetsFolder("cleanbottom");

        viewLottieTop.useHardwareAcceleration();
        viewLottieTop.setAnimation("cleantop.json");
        viewLottieTop.setImageAssetsFolder("cleantop");

        tvCleanCount.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        tvCleanUnit.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));


        if(!viewLottieBottom.isAnimating()){
            viewLottieBottom.playAnimation();
        }
        if(!viewLottieTop.isAnimating()){
            viewLottieTop.playAnimation();
        }
        tvCleanCount.setText(checkCountEntity.getTotalSize());
        tvCleanUnit.setText(checkCountEntity.getUnit());

        viewLottieBottom.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //清理完成动画开始
                NiuDataAPI.onPageStart("clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                NiuDataAPIUtil.onPageEnd("scanning_result_page", "clean_finish_annimation_page", "clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
                if (getActivity() != null) { //作为返回键判断条件
                    ((NowCleanActivity) getActivity()).setClean(false);
                }
                cleanFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        viewLottieBottom.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedFraction();
                if (animatedValue <= 0.74f) {
                    float currentValue = Float.valueOf(checkCountEntity.getTotalSize()) * (0.74f - animatedValue);
                    tvCleanCount.setText(String.format("%s", Math.round(currentValue)));
                    tvCleanUnit.setText(checkCountEntity.getUnit());
                }else if(animatedValue > 0.76f && animatedValue < 0.76f){
                    tvCleanCount.setText("0");
                    tvCleanUnit.setText(checkCountEntity.getUnit());
                }else{
                    tvCleanCount.setVisibility(View.GONE);
                    tvCleanUnit.setVisibility(View.GONE);

                }
            }
        });


        shouIndex = 2;
        showColorChange(ivs,shouIndex);
        clearAll();
    }

   public void  reStartClean(){
        if(viewLottieBottom!=null){
            viewLottieBottom.playAnimation();
        }
   }

    boolean isCacheCheckAll = true;  //运行内存是否全选
    boolean isCheckAll = true;  //运行内存是否全选

    @SuppressLint("CheckResult")
    private void clearAll() {
        if (mJunkGroups == null || mJunkGroups.size() < 1) {
            cleanFinish();
            return;
        }
        Observable.create(e -> {
            long total = 0;

            for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                JunkGroup value = entry.getValue();
                if (value.mChildren != null && value.mChildren.size() > 0) {
                    if ("TYPE_PROCESS".equals(value.mChildren.get(0).getGarbageType())) { //运行内存
                        for (FirstJunkInfo info : value.mChildren) {
                            if (info.isAllchecked()) {
                                total += info.getTotalSize();
                                CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                            } else {
                                isCheckAll = isCacheCheckAll = false;
                            }
                        }

                    } else if ("TYPE_CACHE".equals(value.mChildren.get(0).getGarbageType())) { //缓存
                        for (FirstJunkInfo info : value.mChildren) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }
                        long l = CleanUtil.freeJunkInfos(value.mChildren);
                        total += l;
                    } else if ("TYPE_APK".equals(value.mChildren.get(0).getGarbageType())) { //apk文件
                        for (FirstJunkInfo info : value.mChildren) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }
                        long l1 = CleanUtil.freeJunkInfos(value.mChildren);
                        total += l1;
                    } else if ("TYPE_LEAVED".equals(value.mChildren.get(0).getGarbageType())) {//残留垃圾
                        for (FirstJunkInfo info : value.mChildren) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }
                        long leavedCache = CleanUtil.freeJunkInfos(value.mChildren);
                        total += leavedCache;
                    }
                } else if (entry.getKey() == 4 && value.otherChildren.size() > 0) {//其他垃圾处理
                    if (!value.isChecked) {
                        isCheckAll = false;
                    }

                    long otherCache = CleanUtil.freeOtherJunkInfos(value.otherChildren);
                    total += otherCache;
                }
            }
            PreferenceUtil.saveIsCheckedAll(isCheckAll);
            PreferenceUtil.saveCacheIsCheckedAll(isCacheCheckAll);
            PreferenceUtil.saveMulCacheNum(PreferenceUtil.getMulCacheNum() * 0.3f);
            e.onNext(total);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            double memoryShow = NoClearSPHelper.getMemoryShow();
            if (memoryShow == 1) {
                //清理完成，存储时间点
                mSPHelper.saveCleanTime(System.currentTimeMillis());
            }
        });

    }

    /**
     * 停止清理
     */
    public void stopClean() {
        viewLottieBottom.pauseAnimation();
    }


    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("scanning_result_page_view_page", "用户在扫描结果页浏览");

    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd("clean_up_scan_page", "scanning_result_page", "scanning_result_page_view_page", "用户在扫描结果页浏览");
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }

    public void showColorChange(TextView[] ivs, int index) {
        if (ivs.length == 3 && index <= 2 && index > 0) {
            Drawable drawable = ivs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ObjectAnimator animatorHide = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 0));
                animatorHide.setTarget(drawable);
                animatorHide.setDuration(1000);
                if (!animatorHide.isRunning()) {
                    animatorHide.start();
                }
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        showColorChange(ivs, (index - 1));
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
}
