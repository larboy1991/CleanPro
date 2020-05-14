package com.xiaoniu.cleanking.base;

import com.xiaoniu.cleanking.app.ApplicationDelegate;

import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
import com.xiaoniu.cleanking.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 保存埋点来源
 */
public class AppHolder {
    private static AppHolder appHolder;

    //广告配置内存缓存
    public static Map<String, SwitchInfoList.DataBean> mAdsMap = CollectionUtils.createMap();

    private AppHolder() {
    }

    private static class Holder {
        // 这里的私有没有什么意义
        static AppHolder appHolder = new AppHolder();
    }

    public static AppHolder getInstance() {
        // 外围类能直接访问内部类（不管是否是静态的）的私有变量
        return Holder.appHolder;
    }

    /**
     * 保存上级页面id
     */
    private String sourcePageId = "home_page";

    /**
     * 保存二级上级页面id
     */
    private String otherSourcePageId = "home_page";

    public String getSourcePageId() {
        return sourcePageId;
    }

    public void setSourcePageId(String sourcePageId) {
        this.sourcePageId = sourcePageId;
    }

    public void setOtherSourcePageId(String otherSourcePageId) {
        this.otherSourcePageId = otherSourcePageId;
    }

    public String getOtherSourcePageId() {
        return otherSourcePageId;
    }

    /**
     * 广告配置内存缓存 数据库缓存
     *
     * @param switchInfoList
     */
    public synchronized void setSwitchInfoMap(List<SwitchInfoList.DataBean> switchInfoList) {

        if (CollectionUtils.isEmpty(switchInfoList)) {
            return;
        }
        mAdsMap.clear();
        for (SwitchInfoList.DataBean adInfo : switchInfoList) {
            mAdsMap.put(adInfo.getConfigKey()+adInfo.getAdvertPosition(), adInfo);
            /*< XD added for sync feed config begin */
            if (PositionId.KEY_HOME_NEWS.equals(adInfo.getConfigKey()) && PositionId.AD_POSITION_HOME_NEWS.equals(adInfo.getAdvertPosition())) {
                NewsUtils.setIsHomeFeedOpen(adInfo.isOpen());
            } else if(PositionId.KEY_MAIN_TAB_NEWS.equals(adInfo.getConfigKey()) && PositionId.AD_POSITION_MAIN_TAB_NEWS.equals(adInfo.getAdvertPosition())) {
                NewsUtils.setIsMainTabNewsOpen(adInfo.isOpen());
            } else if(PositionId.KEY_CLEAN_FINISH_NEWS.equals(adInfo.getConfigKey()) && PositionId.AD_POSITION_CLEAN_FINISH_NEWS.equals(adInfo.getAdvertPosition())) {
                NewsUtils.setIsCleanFinishFeedOpen(adInfo.isOpen());
            }
            /* XD added for sync feed config End >*/
        }

        if (null == ApplicationDelegate.getAppDatabase() || null == ApplicationDelegate.getAppDatabase().adInfotDao())
            return;

        ApplicationDelegate.getAppDatabase().adInfotDao().deleteAll();
        ApplicationDelegate.getAppDatabase().adInfotDao().insertAll(switchInfoList);
    }

    public Map<String, SwitchInfoList.DataBean> getSwitchInfoMap() {
        return mAdsMap;
    }

    private String cleanFinishSourcePageId = "";
    private boolean isPush = false;

    public boolean isPush() {
        return isPush;
    }

    public void setPush(boolean push) {
        isPush = push;
    }

    public String getCleanFinishSourcePageId() {
        return cleanFinishSourcePageId;
    }

    public void setCleanFinishSourcePageId(String cleanFinishSourcePageId) {
        this.cleanFinishSourcePageId = cleanFinishSourcePageId;
    }

    /**
     * 广告是否打开
     * @param configKey
     * @param positionId
     * @return
     */
    public boolean isOpen(String configKey, String positionId) {
        boolean isOpen = false;
        if (!CollectionUtils.isEmpty(mAdsMap)) {
            SwitchInfoList.DataBean adinfoBean = AppHolder.getInstance().getSwitchInfoMap().get(configKey+positionId);
            if (null != adinfoBean) {
                isOpen = adinfoBean.isOpen();
            }
        } else {
            if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().adInfotDao()) {
                List<SwitchInfoList.DataBean> adinfoBean = ApplicationDelegate.getAppDatabase().adInfotDao().getAdInfo(configKey, positionId);
                if (!CollectionUtils.isEmpty(adinfoBean)) {
                    isOpen = adinfoBean.get(0).isOpen();
                }
            }
        }
        return isOpen;
    }
}
