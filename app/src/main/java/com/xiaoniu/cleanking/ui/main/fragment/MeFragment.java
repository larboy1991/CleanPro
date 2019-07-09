package com.xiaoniu.cleanking.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的
 * Created on 2018/3/21.
 */
public class MeFragment extends SimpleFragment {
    @BindView(R.id.iv_top)
    ImageView iv_top;
    @BindView(R.id.line_about)
    LinearLayout line_about;
    @BindView(R.id.line_permisson)
    LinearLayout line_permisson;
//    @BindView(R.id.line_feedback)
//    LinearLayout line_feedback;

    public static MeFragment getIntance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView() {
        line_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AboutActivity.class);
            }
        });

//        line_feedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(FeedBackActivity.class);
//            }
//        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
//        } else {
//            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
//        }
//        StatusBarCompat.translucentStatusBar(getActivity(), false, iv_top, true);
    }


    @OnClick({R.id.ll_setting,R.id.line_permisson, R.id.ll_question_report})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.ll_setting) {
            startActivity(new Intent(getContext(), WhiteListSettingActivity.class));
        } else if (ids == R.id.ll_question_report) {
            startActivity(new Intent(getContext(), QuestionReportActivity.class));
        }else if (ids == R.id.line_permisson) {
            startActivity(new Intent(getContext(), PermissionActivity.class));
        }
    }

}
