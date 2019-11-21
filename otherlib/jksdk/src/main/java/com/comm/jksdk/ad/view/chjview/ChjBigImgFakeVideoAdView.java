package com.comm.jksdk.ad.view.chjview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.DownloadStatusController;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
  *
  * @ProjectName:    ${PROJECT_NAME}
  * @Package:        ${PACKAGE_NAME}
  * @ClassName:      ${NAME}
  * @Description:    假视频大图_01
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */


public class ChjBigImgFakeVideoAdView extends CommAdView {
    // 广告实体数据
    private TTFeedAd mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;

    RelativeLayout nativeAdContainer;
    TextView adTitleTv; //广告的title
    TextView adDescribeTv; //广告描述
    ImageView adIm; //广告主体图片
    TextView downTb; //广告下载按钮

    private AnimationDrawable mAnimationDrawable;

    public ChjBigImgFakeVideoAdView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.chj_ad_big_fake_video_layout;
    }

    @Override
    public void initView() {

        nativeAdContainer = findViewById(R.id.rl_ad_item_root);
        adTitleTv = findViewById(R.id.ad_title_tv);
        adDescribeTv = findViewById(R.id.ad_describe_tv);
        adIm = findViewById(R.id.ad_im);
        downTb = findViewById(R.id.down_bt);
        if (mContext == null) {
            return;
        }
        int adlogoWidth = DisplayUtil.dp2px(mContext, 30);
        int adlogoHeight = DisplayUtil.dp2px(mContext, 12);
        adlogoParams = new FrameLayout.LayoutParams(adlogoWidth, adlogoHeight);
        adlogoParams.gravity = Gravity.BOTTOM;
        adlogoParams.bottomMargin = DisplayUtil.dp2px(mContext, 8);
        adlogoParams.leftMargin = (int) (getContext().getResources().getDimension(R.dimen.common_ad_img_width_98dp) - adlogoWidth);
        requestOptions = new RequestOptions()
                .transforms(new RoundedCorners(DisplayUtil.dp2px(mContext, 3)))
                .error(R.color.returncolor);//图片加载失败后，显示的图片

    }

    /**
     * 解析广告
     *
     * @param nativeAdList
     */
    @Override
    public void parseChjAd(List<TTFeedAd> nativeAdList) {
        // 如果没有特定需求，随机取一个
        if (nativeAdList == null || nativeAdList.isEmpty()) {
            firstAdError(1, "请求结果为空");
            return;
        }
        int size = nativeAdList.size();
        int index = new Random().nextInt(size);
        TTFeedAd adData = nativeAdList.get(index);
        if (adData == null) {
            firstAdError(1, "请求结果为空");
            return;
        }

        this.mNativeADData = adData;



        initAdData(adData);
    }

    /**
     * 初始化广告数据
     *
     * @param adData
     */
    private void initAdData(TTFeedAd adData) {
        if ( mContext == null) {
            firstAdError(1, "mContext 为空");
            return;
        }

        if (adData.getImageMode() != TTAdConstant.IMAGE_MODE_LARGE_IMG) {
            firstAdError(1, "返回结果不是大图");
            return;
        }
        nativeAdContainer.setVisibility(VISIBLE);

        bindData(nativeAdContainer,adData);

    }


    private void bindData(View convertView, TTFeedAd ad) {
        adTitleTv.setText(ad.getTitle());
        adDescribeTv.setText(ad.getDescription());
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adIm);
        clickViewList.add(downTb);
        clickViewList.add(nativeAdContainer);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(nativeAdContainer);
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction((ViewGroup) convertView, clickViewList, creativeViewList,adListener );



////默认0
//        long downloadCount = 0;
////         预览描述
//        String browseDesc = getBrowseDesc(downloadCount);
//        tvAdBrowseCount.setText(browseDesc);

        TTImage image = ad.getImageList().get(0);
        if (image != null && image.isValid()) {
            try {
                Glide.with(mContext).load(image.getImageUrl())
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(requestOptions)
                        .into(adIm);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                downTb.setText("下载");
                bindDownloadListener(ad);
                //绑定下载状态控制器
                bindDownLoadStatusController(ad);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                downTb.setText("详情");
            default:
//                nativeAdContainer.setVisibility(View.GONE);
//                ToastUtils.setToastStrShort("交互类型异常");
        }

    }
    private void bindDownLoadStatusController( final TTFeedAd ad) {
        final DownloadStatusController controller = ad.getDownloadStatusController();
        nativeAdContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.changeDownloadStatus();
//                    TToast.show(mContext, "改变下载状态");
                    LogUtils.d(TAG, "改变下载状态");
                }
            }
        });

    }

    private void bindDownloadListener( TTFeedAd ad) {
        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                if (!isValid()) {
                    return;
                }
                downTb.setText("下载");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (totalBytes <= 0L) {
//                    tvCreativeContent.setText("下载中 percent: 0");
                } else {
//                    tvCreativeContent.setText("下载中 percent: " + (currBytes * 100 / totalBytes));
                }
//                adViewHolder.mStopButton.setText("下载中");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (totalBytes <= 0L) {
//                    tvCreativeContent.setText("下载中 percent: 0");
                } else {
//                    tvCreativeContent.setText("下载暂停 percent: " + (currBytes * 100 / totalBytes));
                }
//                adViewHolder.mStopButton.setText("下载暂停");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                downTb.setText("下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                downTb.setText("立即打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                downTb.setText("立即安装");
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            private boolean isValid() {
                return true;
            }
        };
        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
        ad.setDownloadListener(downloadListener); // 注册下载监听器

    }

    TTNativeAd.AdInteractionListener adListener=new TTNativeAd.AdInteractionListener() {
        @Override
        public void onAdClicked(View view, TTNativeAd ad) {
            if (ad != null) {
                LogUtils.w(TAG, "deployAditem onAdClicked");
                adClicked(mAdInfo);
            }
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ad) {
            if (ad != null) {
                LogUtils.w(TAG, "deployAditem onAdCreativeClick");
                adClicked(mAdInfo);
            }
        }

        @Override
        public void onAdShow(TTNativeAd ad) {
            if (ad != null) {
                LogUtils.w(TAG, "广告" + ad.getTitle() + "展示");
                adExposed(mAdInfo);
            }
        }
    };

}
