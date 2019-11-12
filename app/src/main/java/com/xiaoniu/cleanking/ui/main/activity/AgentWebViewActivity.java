package com.xiaoniu.cleanking.ui.main.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.BaseAgentWebActivity;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：
 */
public class AgentWebViewActivity extends BaseAgentWebActivity {

    private TextView mTitleTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        LinearLayout mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        Toolbar mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("");
        mTitleTextView = (TextView) this.findViewById(R.id.toolbar_title);
        this.setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsUtils.trackClick("Interaction_ad_return_click", "用户在首页点击互动式广告返回", "home_page", "Interaction_ad_page");
                finish();
            }
        });
    }


    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.findViewById(R.id.container);
    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("Interaction_ad_return_click", "用户在首页点击互动式广告返回", "home_page", "Interaction_ad_page");
        super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getIndicatorColor() {
        return Color.parseColor("#ff0000");
    }

    @Override
    protected void setTitle(WebView view, String title) {
        super.setTitle(view, title);
        if (!TextUtils.isEmpty(title)) {
            if (title.length() > 10) {
                title = title.substring(0, 10).concat("...");
            }
        }
        mTitleTextView.setText(title);
    }

    @Override
    protected int getIndicatorHeight() {
        return 3;
    }

    @Nullable
    @Override
    protected String getUrl() {
        return getIntent().getStringExtra(ExtraConstant.WEB_URL);
    }
}
