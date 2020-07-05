package com.xiaoniu.cleanking.utils.user;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.ActionListener;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.chuanglan.shanyan_sdk.listener.OpenLoginAuthListener;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.utils.DisplayUtils;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/5
 * Describe:闪验调用管理类
 */
public class ShanYanManager {
    /* 集成文档
     * https://shanyan.253.com/document/details?lid=519&cid=93&pc=28&pn=%25E9%2597%25AA%25E9%25AA%258CSDK
     * 设置授权页协议复选框是否选中
     * OneKeyLoginManager.getInstance().setCheckBoxValue(false);
     * 销毁授权页
     * OneKeyLoginManager.getInstance().finishAuthActivity();
     */

    /**
     * 预取号
     * 建议在判断当前用户属于未登录状态时使用，已登录状态用户请不要调用该方法
     * 建议在执行拉取授权登录页的方法前，提前一段时间调用预取号方法，中间最好有2-3秒的缓冲（因为预取号方法需要1~3s的时间取得临时凭证），比如放在启动页的onCreate（）方法中，或者app启动的application中的onCreate（）方法中去调用，不建议放在用户登录时和拉取授权登录页方法一起调用，会影响用户体验和成功率。
     * 请勿频繁的多次调用、请勿与拉起授权登录页同时和之后调用。
     * 避免大量资源下载时调用，例如游戏中加载资源或者更新补丁的时候要顺序执行
     */
    public static void getPhoneInfo(ShanYanCallBack shanYanCallBack) {
        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
            @Override
            public void getPhoneInfoStatus(int code, String result) {
                //code为1022：成功；其他：失败
                // result 返回信息
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(-1, code, result);
                }
            }
        });
    }

    /**
     * 拉起授权页
     * 调用拉起授权页方法后将会调起运营商授权页面。已登录状态请勿调用 。
     * 每次调用拉起授权页方法前均需先调用授权页配置方法，否则授权页可能会展示异常。
     * 1秒之内只能调用一次，而且必须保证上一次拉起的授权页已经销毁再调用，否则SDK会返回请求频繁。
     */
    public static void openLoginAuth(ShanYanCallBack shanYanCallBack) {
        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
            @Override
            public void getOpenLoginAuthStatus(int code, String result) {
                //code为1000：授权页成功拉起 其他：失败
                // result 返回信息
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(102, code, result);
                }
            }
        }, new OneKeyLoginListener() {
            @Override
            public void getOneKeyLoginStatus(int code, String result) {
                // code为1000：成功  其他：失败 (包含点击返回键 code=1011)
                // result 返回信息
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(103, code, result);
                }
            }
        });
    }

    /**
     * 授权页点击事件监听
     * <p>
     * <p>
     * 需要对授权页点击事件监听的用户，可调用此方法监听授权页点击事件，无此需求可以不写。
     */
    public static void setActionListener(ShanYanCallBack shanYanCallBack) {
        OneKeyLoginManager.getInstance().setActionListener(new ActionListener() {
            @Override
            public void ActionListner(int type, int code, String message) {
               /* type=1 ，隐私协议点击事件
                  type=2 ，checkbox点击事件
                  type=3 ，一键登录按钮点击事件
                code
                type=1 ，隐私协议点击事件，code分为0,1,2,3（协议页序号）
                type=2 ，checkbox点击事件，code分为0,1（0为未选中，1为选中）
                type=3 ，一键登录点击事件，code分为0,1（0为协议未勾选时，1为协议勾选时）

                message String点击事件的详细信息*/
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(type, code, message);
                }
            }
        });
    }

    /**
     * 设置一键授权页面信息
     * OneKeyLoginManager.getInstance().setAuthThemeConfig(ShanYanManager.getCJSConfig(getApplicationContext()));
     *
     * @param context
     * @return
     */
    public static ShanYanUIConfig getCJSConfig(final Context context) {
        /************************************************自定义控件**************************************************************/
        Drawable authNavHidden = context.getResources().getDrawable(R.drawable.bg_white_fillet_06);
        Drawable logoImgPath = context.getResources().getDrawable(R.mipmap.applogo);
        Drawable logBtnImgPath = context.getResources().getDrawable(R.mipmap.sysdk_login_safe_bg);
        Drawable uncheckedImgPath = context.getResources().getDrawable(R.mipmap.icon_login_no_check);
        Drawable checkedImgPath = context.getResources().getDrawable(R.mipmap.icon_login_check);
        Drawable navReturnImgPath = context.getResources().getDrawable(R.mipmap.back);

        TextView otherTV = new TextView(context);
        otherTV.setText(context.getResources().getString(R.string.app_name));
        otherTV.setTextColor(Color.parseColor("#333333"));
        otherTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        otherTV.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams1.setMargins(0, DisplayUtils.dip2px(190), 0, 0);
        mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        otherTV.setLayoutParams(mLayoutParams1);

        /****************************************************设置授权页*********************************************************/
        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                //授权页导航栏：
                .setNavColor(Color.parseColor("#ffffff"))  //设置导航栏颜色
                .setNavText("")  //设置导航栏标题文字
                .setNavTextSize(18)
                .setNavTextColor(Color.parseColor("#262626")) //设置标题栏文字颜色
                .setAuthBGImgPath(authNavHidden)
                .setNavReturnImgPath(navReturnImgPath)
                .setNavReturnBtnWidth(10)
                .setNavReturnBtnHeight(16)
                .setNavReturnBtnOffsetX(10)

                //.setFullScreen(true)
                //.setAuthBgVideoPath("android.resource://" + context.getPackageName() + "/" + R.raw.testvideo)

                //授权页logo（logo的层级在次底层，仅次于自定义控件）
                .setLogoImgPath(logoImgPath)  //设置logo图片
                .setLogoWidth(78)   //设置logo宽度
                .setLogoHeight(78)   //设置logo高度
                .setLogoOffsetY(106)  //设置logo相对于标题栏下边缘y偏移
                .setLogoHidden(false)   //是否隐藏logo
//                .setLogoOffsetX(20)

                //授权页手机号码栏：
                .setNumberColor(Color.parseColor("#575757"))  //设置手机号码字体颜色
                .setNumFieldOffsetY(244)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumFieldWidth(257)
                .setNumberSize(18)
//                .setNumFieldOffsetX(120)


                //授权页登录按钮：
                .setLogBtnText("")  //设置登录按钮文字
                .setLogBtnTextColor(Color.parseColor("#252222"))   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnOffsetY(266)   //设置登录按钮相对于标题栏下边缘y偏移
                .setLogBtnTextSize(15)
                .setLogBtnHeight(68)
                .setLogBtnWidth(256)

                //授权页隐私栏：
//                .setAppPrivacyOne("闪验用户协议", "https://api.253.com/api_doc/yin-si-zheng-ce/wei-hu-wang-luo-an-quan-sheng-ming.html")  //设置开发者隐私条款1名称和URL(名称，url)
//                .setAppPrivacyTwo("闪验隐私政策", "https://api.253.com/api_doc/yin-si-zheng-ce/ge-ren-xin-xi-bao-hu-sheng-ming.html")  //设置开发者隐私条款2名称和URL(名称，url)
//                .setAppPrivacyThree("用户服务条款", "https://api.253.com/api_doc/yin-si-zheng-ce/ge-ren-xin-xi-bao-hu-sheng-ming.html")
                .setPrivacyText("同意安全绑定则代表同意", "", "", "",
                        "授权"+context.getResources().getString(R.string.app_name)+"获得本机号码")
                .setPrivacyTextSize(13)
                .setPrivacyState(true)
                .setCheckBoxMargin(0, 0, 4, 20)
                .setAppPrivacyColor(Color.parseColor("#A4A4A4"), Color.parseColor("#5CD0FF"))   //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setPrivacyOffsetBottomY(50)//设置隐私条款相对于屏幕下边缘y偏
                .setUncheckedImgPath(uncheckedImgPath)
                .setCheckedImgPath(checkedImgPath)

                //授权页slogan：
                .setSloganTextColor(Color.parseColor("#A4A4A4"))  //设置slogan文字颜色
                .setSloganTextSize(11)
                .setSloganOffsetBottomY(20)  //设置slogan相对于标题栏下边缘y偏移
                .setSloganHidden(false)

                // 添加自定义控件:
                .addCustomView(otherTV, false, false, null)
//                .addCustomView(relativeLayout, false, false, null)
                .build();
        return uiConfig;

    }
}
