package com.xiaoniu.common.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

public class CustomToolbar extends Toolbar {
    public CustomToolbar(Context context) {
        super(context);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public LayoutParams generateDefaultLayoutParams() {
//        LayoutParams params = new LayoutParams(DisplayUtils.dip2px(35), LayoutParams.WRAP_CONTENT);
//        return params;
//    }
}
