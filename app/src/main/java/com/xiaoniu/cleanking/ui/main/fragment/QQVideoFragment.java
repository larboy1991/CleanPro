package com.xiaoniu.cleanking.ui.main.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WXVideoChatAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.event.WXImgCameraEvent;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelFileSuccessFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.FileCopyProgressDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.MFullDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.VideoPlayFragment;
import com.xiaoniu.cleanking.ui.main.presenter.QQVideoPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.MusicFileUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信拍摄图片清理
 * Created by lang.chen on 2019/8/1
 */
public class QQVideoFragment extends BaseFragment<QQVideoPresenter> {


    private  static  final  int REQUEST_CODE_IMG_VIEW=0x1022;
    @BindView(R.id.list_view_camera)
    ExpandableListView mListView;
    private WXVideoChatAdapter mAdapter;

    @BindView(R.id.ll_empty_view)
    LinearLayout mEmptyView;
    @BindView(R.id.txt_empty_title)
    TextView mTxtEmptyTile;

    @BindView(R.id.btn_del)
    Button mBtnDel;
    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    private  boolean mIsCheckAll;

    private  int mGroupPosition;

    private CleanFileLoadingDialogFragment mLoading;
    private FileCopyProgressDialogFragment mProgress;;

    public static QQVideoFragment newInstance() {
        QQVideoFragment instance = new QQVideoFragment();

        return instance;
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {

        fragmentComponent.inject(this);
        mPresenter.init(getContext());
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.qq_img_camera;
    }

    @Override
    protected void initView() {
        mLoading=CleanFileLoadingDialogFragment.newInstance();
        String title="聊天视频";
        String content=getString(R.string.msg_save_video);
        mProgress = FileCopyProgressDialogFragment.newInstance(title,content);
        mAdapter=new WXVideoChatAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                List<FileTitleEntity> lists=mAdapter.getList();
                for(int i=0;i<lists.size();i++){
                    if(i==groupPosition){
                        FileTitleEntity fileTitleEntity=lists.get(groupPosition);
                        if(fileTitleEntity.isExpand){
                            fileTitleEntity.isExpand=false;
                        }else {
                            fileTitleEntity.isExpand=true;
                        }
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();

                return false;
            }
        });

        mLLCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsCheckAll){
                    mIsCheckAll=false;
                }else {
                    mIsCheckAll=true;
                }
                mLLCheckAll.setSelected(mIsCheckAll);
                setSelectStatus(mIsCheckAll);
                setDelBtnSize();
                StatisticsUtils.trackClick("video_cleaning_all_election_click","\"全选\"按钮点击"
                        ,"qq_cleaning_page","qq_video_cleaning_page");
            }
        });

        mAdapter.setOnCheckListener(new WXVideoChatAdapter.OnCheckListener() {
            @Override
            public void onCheck(int groupPosition, int position, boolean isCheck) {
                setSelectChildStatus(groupPosition);
                setDelBtnSize();
            }

            @Override
            public void onCheckImg(int groupPosition, int position) {
                mGroupPosition=groupPosition;
                Intent intent = new Intent(mActivity, PreviewImageActivity.class);
                intent.putExtra(ExtraConstant.PREVIEW_IMAGE_POSITION, position);
                CleanAllFileScanUtil.clean_image_list = wrapperImg(groupPosition,position);
                startActivityForResult(intent, REQUEST_CODE_IMG_VIEW);
            }

            @Override
            public void onCheckVideo(int groupPosition, int position) {
                List<FileTitleEntity> lists= mAdapter.getList();
                if(groupPosition<lists.size()){
                    FileTitleEntity fileTitleEntity=lists.get(groupPosition);
                    //获取子集
                    List<FileChildEntity> flieChilds=fileTitleEntity.lists;
                    if(position<flieChilds.size()){
                        FileChildEntity fileChildEntity=flieChilds.get(position);
                        play(fileChildEntity.name,fileChildEntity.path,fileChildEntity.size);
                    }
                }
            }
        });

    }
    public void play(String name,String path,long lenth) {
        VideoPlayFragment videoPlayFragment=VideoPlayFragment.newInstance(name,FileSizeUtils.formatFileSize(lenth)
                ,"时长: "+ MusicFileUtils.getPlayDuration2(path),"未知");
        FragmentManager fm = getActivity().getFragmentManager();
        videoPlayFragment.show(fm,"");
        videoPlayFragment.setDialogClickListener(new VideoPlayFragment.DialogClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                playAudio(path);
            }
        });
    }

    public void playAudio(String audioPath) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //解决8.0以上问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file:///" + audioPath);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /**
     * 照片查看选择集合包装
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    private List<FileEntity> wrapperImg(int groupPosition, int childPosition) {
        List<FileEntity> wrapperLists = new ArrayList<>();

        List<FileTitleEntity> lists = mAdapter.getList();
        if(lists.size()>0){
            List<FileChildEntity> listChilds=lists.get(groupPosition).lists;
            for(FileChildEntity fileChildEntity: listChilds){
                FileEntity fileEntity=new FileEntity(String.valueOf(fileChildEntity.size),fileChildEntity.path);
                fileEntity.isSelect=fileChildEntity.isSelect;
                wrapperLists.add(fileEntity);
            }
        }
        return wrapperLists;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMG_VIEW) {
            List<FileEntity> listTemp = new ArrayList<>();
            listTemp.addAll(CleanAllFileScanUtil.clean_image_list);
            refreshData(listTemp);
        }

    }


    /**
     * 查看图片后刷新数据
     * @param fileEntities
     */
    private  void  refreshData(List<FileEntity> fileEntities){

        List<FileTitleEntity> lists=mAdapter.getList();

        List<FileChildEntity> listsNew=new ArrayList<>();
        if(lists.size()>0){
            List<FileChildEntity> fileChildEntities=  lists.get(mGroupPosition).lists;

            for(FileChildEntity fileChildEntity:fileChildEntities){

                boolean isAdd=false;
                for(FileEntity fileEntity:fileEntities){
                    if(fileEntity.path.equals(fileChildEntity.path)){
                        fileChildEntity.isSelect=fileEntity.isSelect;
                        isAdd=true;
                    }
                }
                if(isAdd){
                    listsNew.add(fileChildEntity);
                }
            }
            fileChildEntities.clear();;

            fileChildEntities.addAll(listsNew);
            mPresenter.totalFileSize(lists);
            mAdapter.notifyDataSetChanged();
            setSelectChildStatus(mGroupPosition);
            setDelBtnSize();


        }
    }


    /**
     * 改变子视图的选择状态
     */
    private void setSelectChildStatus(int groupPosition) {
        List<FileTitleEntity> lists = mAdapter.getList();


        for (int i = 0; i < lists.size(); i++) {
            if (i == groupPosition) {
                //是否选择所有
                boolean isCheckAll = true;
                FileTitleEntity fileTitleEntity = lists.get(groupPosition);
                if(fileTitleEntity.lists.size()==0){
                    fileTitleEntity.isSelect = false;
                    mAdapter.notifyDataSetChanged();
                    break;
                }else {
                    for (FileChildEntity file : fileTitleEntity.lists) {
                        if (file.isSelect == false) {
                            isCheckAll = false;
                        }
                    }

                    fileTitleEntity.isSelect = isCheckAll;
                    mAdapter.notifyDataSetChanged();
                    break;

                }
            }
        }

    }

    /**
     * 改变父类文件选择状态
     */
    private void setSelectChildStatus() {
        List<FileTitleEntity> lists = mAdapter.getList();

        for (int i = 0; i < lists.size(); i++) {
            //是否选择所有
            boolean isCheckAll = true;
            FileTitleEntity fileTitleEntity = lists.get(i);
            if (fileTitleEntity.lists.size() == 0) {
                fileTitleEntity.isSelect = false;
            } else {
                for (FileChildEntity file : fileTitleEntity.lists) {
                    if (file.isSelect == false) {
                        isCheckAll = false;
                    }
                }

                fileTitleEntity.isSelect = isCheckAll;
            }

        }
        mAdapter.notifyDataSetChanged();

    }


    private void setSelectStatus(boolean isCheck){
        List<FileTitleEntity> lists = mAdapter.getList();
        for(FileTitleEntity fileTitleEntity :lists){
            if(fileTitleEntity.lists.size()>0){
                fileTitleEntity.isSelect=isCheck;

                for(FileChildEntity file: fileTitleEntity.lists){
                    file.isSelect=isCheck;
                }
            }

        }
        mAdapter.notifyDataSetChanged();
    }

    private long totalSelectSize() {
        long size = 0L;
        List<FileTitleEntity> lists = mAdapter.getList();
        for(FileTitleEntity fileTitleEntity :lists){
            for(FileChildEntity file:fileTitleEntity.lists){
                if(file.isSelect){
                    size+=file.size;
                }
            }
        }
        return size;
    }

    private  void setDelBtnSize(){
        long size=totalSelectSize();
        if(size>0){
            mBtnDel.setSelected(true);
            mBtnDel.setEnabled(true);
            mBtnDel.setText("确认选中"+ FileSizeUtils.formatFileSize(size));
        }else {
            mBtnDel.setSelected(false);
            mBtnDel.setEnabled(false);
            mBtnDel.setText("未选中");
        }

    }

    /**
     * 移除列表中元素
     * @param paths 根据路径和父类id是否相等
     */
    public void updateDelFileView(List<FileChildEntity> paths){
        List<FileTitleEntity> listsNew=new ArrayList<>();

        List<FileTitleEntity> lists=mAdapter.getList();
        for(int i=0;i<lists.size();i++){
            FileTitleEntity fileTitleEntity=lists.get(i);

            FileTitleEntity fileTitle=FileTitleEntity.copyObject(fileTitleEntity.id,fileTitleEntity.title
                    ,fileTitleEntity.type,fileTitleEntity.size,fileTitleEntity.isExpand,fileTitleEntity.isSelect);
            for(FileChildEntity fileChildEntity: fileTitleEntity.lists){
                if(fileChildEntity.isSelect==false){
                    fileTitle.lists.add(fileChildEntity);
                }
            }
            listsNew.add(fileTitle);
        }
        mLoading.dismissAllowingStateLoss();
        mPresenter.totalFileSize(listsNew);
        mAdapter.clear();
        mAdapter.modifyData(listsNew);
        setDelBtnSize();
        setSelectChildStatus();

        FragmentManager fm = getActivity().getFragmentManager();
        String totalSize=FileSizeUtils.formatFileSize(getDelTotalFileSize(paths));
        String fileSize=String.valueOf(paths.size());
        DelFileSuccessFragment.newInstance(totalSize,fileSize).show(fm,"");
    }

    public long getDelTotalFileSize(List<FileChildEntity> paths){
        long size=0L;
        for(FileChildEntity fileChildEntity:paths){
            size+=fileChildEntity.size;

        }
        return  size;
    }
    /**
     * 获取选中删除的元素
     * @return
     */
    public List<FileChildEntity> getDelFile(){
        List<FileChildEntity> files=new ArrayList<>();
        List<FileTitleEntity> lists=mAdapter.getList();
        for(FileTitleEntity fileTitleEntity:lists){
            for(FileChildEntity file:fileTitleEntity.lists){
                if(file.isSelect){
                    files.add(file);
                }
            }
        }
        return  files;
    }
    @OnClick({R.id.btn_del,R.id.btn_save})
    public void onClickView(View view){
        int ids=view.getId();
        switch (ids){
            case R.id.btn_del:

//                String title=String.format("确定删除这%s个视频?",getSelectSize());
//                String cotnent=getString(R.string.msg_del_video);
//                DelDialogStyleFragment dialogFragment = DelDialogStyleFragment.newInstance(title,cotnent);
//                FragmentManager fm = getActivity().getFragmentManager();
//                dialogFragment.showShort(fm,"");
//                dialogFragment.setDialogClickListener(new DelDialogStyleFragment.DialogClickListener() {
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onConfirm() {
//                        mLoading.showShort(getActivity().getSupportFragmentManager(),"");
//                        List<FileChildEntity> files=getDelFile();
//                        mPresenter.delFile(files);
//                    }
//                });

                StatisticsUtils.trackClick("qq_video_confirm_the_selection_click","\"确认选中\"点击"
                        ,"qq_cleaning_page","qq_video_cleaning_page");


                ArrayList<FileTitleEntity> lists=(ArrayList<FileTitleEntity>) mAdapter.getList();
                Bundle bundle=new Bundle();
                bundle.putSerializable(Constant.PARAMS_QQ_VIDEO_LIST, lists);
                Intent intent=new Intent();
                intent.putExtras(bundle);
                getActivity().setResult(0x11,intent);
                getActivity().finish();
                break;
            case R.id.btn_save:

//                List<File> lists=getSelectFiles();
//                if(lists.size()==0){
//                    ToastUtils.showShort("未选中照片");
//                }else {
//                    FragmentManager fmProgress= getActivity().getFragmentManager();
//                    mProgress.showShort(fmProgress,"");
//                    //导入图片
//                    mPresenter.copyFile(lists);
//                }


                break;
        }
    }



    private  int getSelectSize(){
        int size = 0;
        List<FileTitleEntity> lists = mAdapter.getList();

        for(FileTitleEntity fileTitleEntity: lists){
            for(FileChildEntity file:fileTitleEntity.lists){
                if(file.isSelect){
                    size+=1;
                }
            }
        }
        return  size;
    }

    private  List<File> getSelectFiles(){
        List<File> files=new ArrayList<>();
        List<FileTitleEntity> lists = mAdapter.getList();

        for(FileTitleEntity fileTitleEntity: lists){
            for(FileChildEntity fileChildEntity:fileTitleEntity.lists){
                if(fileChildEntity.isSelect){
                    File File =new File(fileChildEntity.path);
                    files.add(File);
                }
            }
        }
        return  files;
    }



    @Subscribe
    public void onEnvent(WXImgCameraEvent wxImgCameraEvent){
        List<FileTitleEntity> lists=(List<FileTitleEntity>)wxImgCameraEvent.object;
        mAdapter.modifyData(lists);
    }

    /**
     * 导入成功
     * @param progress
     */
    public void copySuccess(int progress){

        mProgress.setValue(progress);
        if(progress>=100){
            ToastUtils.showShort("保存成功，请至手机相册查看");
            mProgress.dismissAllowingStateLoss();
        }
    }


    /**
     * 导出失败
     */
    public void onCopyFaile(){
        if(null!=mProgress){
            mProgress.dismissAllowingStateLoss();
        }
        FragmentManager fm = getActivity().getFragmentManager();
        MFullDialogStyleFragment.newInstance().show(fm,"");
    }


    public void updateImgCamera(List<FileTitleEntity> lists){
        mAdapter.modifyData(lists);
        if (lists.size() > 0) {
            mListView.expandGroup(lists.size() - 1);
            mListView.setSelectedGroup(0);
        }
        if(totalFileSizeL(lists)==0){
            mTxtEmptyTile.setText("暂无视频~");
            mEmptyView.setVisibility(View.VISIBLE);
        }

    }



    public   long totalFileSizeL(List<FileTitleEntity> lists){
        if(null==lists ||  lists.size()==0){
            return 0L;
        }

        long size=0L;

        for(FileTitleEntity fileTitleEntity: lists) {
            size+=fileTitleEntity.size;

        }

        return  size;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
