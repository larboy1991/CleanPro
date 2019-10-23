package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.xiaoniu.cleanking.R;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：
 */
public class GlideUtils {

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (null == context) return;
        Glide.with(context).load(url).into(imageView);
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView, int round) {
        if (null == context) return;
        Glide.with(context).load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(round)))
                .into(imageView);
    }

    public static void loadGif(Context context, String url, ImageView imageView, int count) {
        Glide.with(context).load(url).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载次数
                    ((GifDrawable) resource).setLoopCount(count);
                }
                return false;
            }
        }).into(imageView);
    }

    /**
     * 加载本地Gif
     * @param context
     * @param resourceId
     * @param imageView
     */
    public static void loadDrawble(Context context, int resourceId, ImageView imageView) {
        Glide.with(context).load(resourceId).into(imageView);
    }

}
