package com.xiaoniu.cleanking.ui.tool.qq.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanAudActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by z on 2017/5/15.
 */
public class QQCleanAudPresenter extends RxPresenter<QQCleanAudActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public QQCleanAudPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public AlertDialog alertBanLiveDialog(Context context, int deleteNum, final ImageListPresenter.ClickListener okListener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return dlg;
        }
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_redp_send_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnOk = window.findViewById(R.id.btnOk);

        TextView btnCancle = window.findViewById(R.id.btnCancle);
        TextView tipTxt = window.findViewById(R.id.tipTxt);
        TextView content = window.findViewById(R.id.content);
        content.setText("永久从设备中删除这些文件，删除后将不可恢复。");
        tipTxt.setText("确定删除这" + deleteNum + "个文件？");
        btnOk.setOnClickListener(v -> {
            dlg.dismiss();
            okListener.clickOKBtn();
        });
        btnCancle.setOnClickListener(v -> {
            dlg.dismiss();
            okListener.cancelBtn();
        });
        return dlg;
    }

    //删除本地文件
    public void delFile(List<CleanWxItemInfo> list) {
        mView.showLoadingDialog();
        List<CleanWxItemInfo> files = list;
        Observable.create((ObservableOnSubscribe<String>) emitter -> {

            for (CleanWxItemInfo appInfoBean : files) {
                File file = appInfoBean.getFile();
                if (null != file) {
                    file.delete();
                }
            }
            emitter.onNext("");
            emitter.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.cancelLoadingDialog();
                    }
                });

    }
}
