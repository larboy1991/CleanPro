package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.VirusKillActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tie on 2017/5/15.
 */
public class VirusKillPresenter extends RxPresenter<VirusKillActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public VirusKillPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void reduceSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }

                @Override
                public void totalSize(int p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
                    mView.getAccessListBelow(strings);
                });
    }
}
