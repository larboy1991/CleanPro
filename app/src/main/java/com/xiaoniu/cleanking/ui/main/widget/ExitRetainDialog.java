package com.xiaoniu.cleanking.ui.main.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.hellogeek.permission.widget.floatwindow.IFloatingWindow;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

/**
 * 锁屏退出dialog<p>
 */
public class ExitRetainDialog extends AlertDialog implements View.OnClickListener {
    private FrameLayout container;
    private Activity activity;

    public ExitRetainDialog(Activity context) {
        super(context, R.style.Dialog);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);
        }
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_retain_layout);
        initView();
    }

    private void initView() {
        AppCompatTextView exit = findViewById(R.id.btn_exit);
        AppCompatTextView cancel = findViewById(R.id.btn_cancel);
        //此container即为显示广告的容器
        container = findViewById(R.id.container);
        if (exit != null) {
            exit.setOnClickListener(this);
        }
        if (cancel != null) {
            cancel.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                //更新按返回键退出程序的次数
                PreferenceUtil.updatePressBackExitAppCount();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                activity.startActivity(home);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
