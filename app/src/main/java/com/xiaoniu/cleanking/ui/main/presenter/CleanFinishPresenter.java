package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.BubbleDouble;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.model.GoldCoinDoubleModel;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common3Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tie on 2017/5/15.
 */
public class CleanFinishPresenter extends RxPresenter<NewCleanFinishActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public CleanFinishPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 插屏广告开关
     */
    public void getScreenSwitch() {
        mModel.getScreentSwitch(new Common4Subscriber<InsertAdSwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InsertAdSwitchInfoList switchInfoList) {
                AppHolder.getInstance().setInsertAdSwitchInfoList(switchInfoList);
                mView.getScreenSwitchSuccess();
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
            }
        });
    }

    /**
     * 获取金币
     */
    public void getGoldCoin() {
        mModel.getGoleGonfigs(new Common3Subscriber<BubbleConfig>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleConfig bubbleConfig) {
                if (bubbleConfig != null && bubbleConfig.getData().size() > 0) {
                    for (BubbleConfig.DataBean item : bubbleConfig.getData()) {
                        if (item.getLocationNum() == 5) {
                            addGoldCoin(item.getGoldCount());
                            break;
                        }
                    }
                }

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView));
    }

    private void addGoldCoin(int goldNum) {
        mModel.goleCollect(new Common3Subscriber<BubbleCollected>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                // ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleCollected bubbleConfig) {
                //实时更新金币信息
                RequestUserInfoUtil.getUserCoinInfo();
                Map<String, Object> map = getStatisticsMap();
                map.put("gold_number", goldNum);
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", "功能完成页领取弹窗金币发放数", "", "success_page_gold_coin_pop_up_window", map);
                showGetGoldCoinDialog(bubbleConfig);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView), 5);
    }

    private void addDoubleGoldCoin(BubbleCollected bubbleCollected) {
        mModel.goleDouble(new Common3Subscriber<BubbleDouble>() {
                              @Override
                              public void showExtraOp(String code, String message) {  //关心错误码；
                                  ToastUtils.showShort(message);
                                  GoldCoinDialog.dismiss();
                              }

                              @Override
                              public void getData(BubbleDouble bubbleDouble) {

                                  String adId = "";
                                  if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS)) {
                                      adId = MidasConstants.GET_DOUBLE_GOLD_COIN_SUCCESS;
                                  }
                                  startGoldSuccess(adId, bubbleCollected.getData().getGoldCount(), mView.getActivityTitle());
                                  GoldCoinDialog.dismiss();
                              }

                              @Override
                              public void showExtraOp(String message) {
                                  ToastUtils.showShort(message);
                                  GoldCoinDialog.dismiss();
                              }

                              @Override
                              public void netConnectError() {
                                  ToastUtils.showShort(R.string.notwork_error);
                              }
                          }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView), bubbleCollected.getData().getUuid(), bubbleCollected.getData().getLocationNum(),
                bubbleCollected.getData().getGoldCount());
    }

    private void startGoldSuccess(String adId, int num, String functionName) {
        GoldCoinDoubleModel model = new GoldCoinDoubleModel(adId, num, Points.FunctionGoldCoin.SUCCESS_PAGE, functionName);
        GoldCoinSuccessActivity.Companion.start(mActivity, model);
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
//                    mView.cancelLoadingDialog();
                    mView.getAccessListBelow(strings);
                });
    }


    //显示内部插屏广告
    public void showInsideScreenDialog() {
        if (mActivity == null) {
            return;
        }
        StatisticsUtils.customTrackEvent("ad_request_sdk_4", "功能完成页广告位4发起请求", "", "success_page");
        AdRequestParams params = new AdRequestParams.Builder()
                .setActivity(mActivity).setAdId(MidasConstants.FINISH_INSIDE_SCREEN_ID).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdShow(AdInfo adInfo) {
                super.onAdShow(adInfo);
                LogUtils.e("====完成页内部插屏广告展出======");
            }
        });
    }

    private Map<String, Object> getStatisticsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("gold_coin_position_id", 5);
        map.put("function_name", mView.getActivityTitle());
        return map;
    }

    private JSONObject getStatisticsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gold_coin_position_id", 5);
            jsonObject.put("function_name", mView.getActivityTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //金币领取广告弹窗
    public void showGetGoldCoinDialog(BubbleCollected bubbleCollected) {
        GoldCoinDialogParameter bean = new GoldCoinDialogParameter();
        bean.dialogType = 3;
        bean.obtainCoinCount = bubbleCollected.getData().getGoldCount();
        bean.totalCoinCount = bubbleCollected.getData().getTotalGoldCount();
        bean.adId = MidasConstants.FINISH_GET_GOLD_COIN;
        bean.context = mActivity;
        bean.isRewardOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GOLD_DIALOG_SHOW_VIDEO);
        bean.advCallBack = new AbsAdCallBack() {

        };
        bean.closeClickListener = view -> StatisticsUtils.trackClick("close_click", "金币翻倍按钮点击", "", "success_page_gold_coin_pop_up_window", getStatisticsJson());
        bean.onDoubleClickListener = (v) -> {
            StatisticsUtils.trackClick("double_the_gold_coin_click", "金币翻倍按钮点击", "", "success_page_gold_coin_pop_up_window", getStatisticsJson());
            StatisticsUtils.customTrackEvent("ad_request_sdk_2", "功能完成页翻倍激励视频广告发起请求", "", "success_page_gold_coin_pop_up_window", getStatisticsMap());
            ViewGroup viewGroup = (ViewGroup) mView.getWindow().getDecorView();
            AdRequestParams params = new AdRequestParams.Builder().
                    setActivity(mActivity).
                    setViewContainer(viewGroup).
                    setAdId(MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON).build();
            MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
                @Override
                public void onShowError(int i, String s) {
                    super.onShowError(i, s);
                    ToastUtils.showLong("网络异常");
                    GoldCoinDialog.dismiss();
                }

                @Override
                public void onAdError(AdInfo adInfo, int i, String s) {
                    super.onAdError(adInfo, i, s);
                    ToastUtils.showLong("网络异常");
                    GoldCoinDialog.dismiss();
                }

                @Override
                public void onAdClose(AdInfo adInfo) {
                    super.onAdClose(adInfo);
                    StatisticsUtils.trackClick("incentive_video_ad_click", "功能完成页金币翻倍激励视频广告关闭点击", "", "success_page_gold_coin_pop-up_window_incentive_video_page", getStatisticsJson());
                }

                @Override
                public void onAdVideoComplete(AdInfo adInfo) {
                    super.onAdVideoComplete(adInfo);
                    addDoubleGoldCoin(bubbleCollected);
                }
            });

        };
        StatisticsUtils.customTrackEvent("success_page_gold_coin_pop_up_window_custom", "功能完成页金币领取弹窗曝光", "", "success_page_gold_coin_pop_up_window");
        StatisticsUtils.customTrackEvent("ad_request_sdk_1", "功能完成页金币领取弹窗上广告发起请求", "", "success_page_gold_coin_pop_up_window", getStatisticsMap());
        GoldCoinDialog.showGoldCoinDialog(bean);
    }


}
