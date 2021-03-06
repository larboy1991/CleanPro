package com.xiaoniu.cleanking.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.WebUrlEntity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.FileUploadInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.Patch;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author tie
 * @date 2017/5/15.
 */
public interface UserApiService {

    /**
     * 查询app版本更新
     *
     * @return
     */
    @POST("/appVersion/query")
    Flowable<AppVersion> queryAppVersion(@Body RequestBody body);

    /**
     * 过审开关
     *
     * @return
     */
    @POST("/auditSwitch/query")
    Flowable<AuditSwitch> queryAuditSwitch(@Body RequestBody body);

    /**
     * 广告开关
     *
     * @return
     */
    @POST("/switcherNew/getSwitchInfoList")
    Flowable<SwitchInfoList> getSwitchInfoList(@Body RequestBody body);

    /**
     * 分享成功增加领券接口
     * @param body
     * @return
     */
    @POST("/activity/share/succeed")
    Flowable<BaseEntity> shareSuccess(@Body RequestBody body);

    /**
     * 获取后台WebView URL
     * @param body
     * @return
     */
    @POST("/finish/page/query")
    Flowable<WebUrlEntity> getWebUrl(@Body RequestBody body);

    /**
     * 保存JPush Alias
     * @param body
     * @return
     */
    @POST("/deviceActivation/save")
    Flowable<BaseEntity> commitJPushAlias(@Body RequestBody body);

    /**
     * 操作记录(PUSH消息)
     * @param body
     * @return
     */
    @POST("/operating/save")
    Flowable<BaseEntity> commitJPushClickTime(@Body RequestBody body);

    @POST("/appVersion/query")
    Flowable<AppVersion> sendVoiceSmsCode(@Body RequestBody body);
    /**
     * 查询app是否有补丁版本
     *
     * @param baseVersionName 基线版本
     * @param clientType
     * @param patchVersion    当前补丁版本
     * @return
     */
    @GET("/gateway/config/patch")
    Flowable<Patch> queryPatch(@Query("baseVersionName") String baseVersionName, @Query("clientType") String clientType, @Query("patchVersion") String patchVersion);


    /**
     * 意见反馈
     */
    @POST("/feedback/save")
    @JSONField
    Flowable<BaseEntity> submitQuestionReport(@Body RequestBody requestBody);


    /**
     * 上传用户头像
     **/
    @Multipart
    @POST("/file/upload")
    Flowable<FileUploadInfoBean> uploadFile(@Part MultipartBody.Part file);


    /**
     * 首页的底部清理广告
     */
    @POST("/banner/query")
    @JSONField
    Flowable<ImageAdEntity> queryBottomAd(@Body RequestBody requestBody);

}
