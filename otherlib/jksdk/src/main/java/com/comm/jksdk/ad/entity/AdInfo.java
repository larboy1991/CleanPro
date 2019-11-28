package com.comm.jksdk.ad.entity;

import android.os.Parcel;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.entity
 * @ClassName: AdInfo
 * @Description: 广告信息（用来回调给业务线）
 * @Author: fanhailong
 * @CreateDate: 2019/11/21 13:40
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/21 13:40
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class AdInfo extends BaseEntity{
    /**
     * 广告对应的appid
     */
    private String adAppid;
    /**
     * 广告id
     */
    private String adId;

    /**
     * 广告源
     */
    private String adSource;

    /**
     * 广告title（有些有，有些没有）
     */
    private String adTitle;

    /**
     * 插屏样式
     */
    private String adStyle;

    /**
     * 点击后的类型：1=下载；2=详情
     */
    private int adClickType;

    public String getAdAppid() {
        return adAppid;
    }

    public void setAdAppid(String adAppid) {
        this.adAppid = adAppid;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdSource() {
        return adSource;
    }

    public void setAdSource(String adSource) {
        this.adSource = adSource;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdStyle() {
        return adStyle;
    }

    public void setAdStyle(String adStyle) {
        this.adStyle = adStyle;
    }

    public int getAdClickType() {
        return adClickType;
    }

    public void setAdClickType(int adClickType) {
        this.adClickType = adClickType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.adAppid);
        dest.writeString(this.adId);
        dest.writeString(this.adSource);
        dest.writeString(this.adTitle);
        dest.writeString(this.adStyle);
        dest.writeInt(this.adClickType);
    }

    public AdInfo() {
    }

    protected AdInfo(Parcel in) {
        this.adAppid = in.readString();
        this.adId = in.readString();
        this.adSource = in.readString();
        this.adTitle = in.readString();
        this.adStyle = in.readString();
        this.adClickType = in.readInt();
    }

    public static final Creator<AdInfo> CREATOR = new Creator<AdInfo>() {
        @Override
        public AdInfo createFromParcel(Parcel source) {
            return new AdInfo(source);
        }

        @Override
        public AdInfo[] newArray(int size) {
            return new AdInfo[size];
        }
    };

    @Override
    public String toString() {
        return "AdInfo{" +
                "adAppid='" + adAppid + '\'' +
                ", adId='" + adId + '\'' +
                ", adSource='" + adSource + '\'' +
                ", adTitle='" + adTitle + '\'' +
                ", adStyle='" + adStyle + '\'' +
                ", adClickType=" + adClickType +
                '}';
    }
}