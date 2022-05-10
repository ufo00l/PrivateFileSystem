package com.ljq.privatefiledemo.privatefile;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.arialyy.aria.core.download.DownloadEntity;
import com.ljq.privatefiledemo.privatefile.sql.CryptoFileSQLManager;
import com.ljq.privatefiledemo.privatefile.sql.HideFileSupportFileSQLManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Description : 隐私文件事务操作类
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class HideFileManager implements IHideFileManager<DownloadEntity> {

    private volatile static HideFileManager sSingleton;

    private Context mContext;

    private HideFileManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static HideFileManager getInstance(Context context) {
        if (sSingleton == null) {
            synchronized (HideFileManager.class) {
                if (sSingleton == null) {
                    sSingleton = new HideFileManager(context);
                }
            }
        }
        return sSingleton;
    }

    /**
     * 转化DownloadItemBean到PrivateFileItemBean
     * 填充一部分数据
     *
     * @param bean
     * @return
     */
    @Override
    public PrivateFileItemBean makePrivateFileItemBean(@NonNull DownloadEntity bean) {
        PrivateFileItemBean privateFileItemBean = new PrivateFileItemBean();

//        bean.getTotalBytes();

        privateFileItemBean.setFileName(bean.getFileName());
        privateFileItemBean.setFileOriginalPath(bean.getFilePath());
        privateFileItemBean.setFileSize(bean.getFileSize());
        //通过INDEX_IS_VISIBLE_IN_DOWNLOADS_UI字段对原下载文件的记录进行隐藏
        //还原的时候通过ID去改变INDEX_IS_VISIBLE_IN_DOWNLOADS_UI字段进行显示和还原
        privateFileItemBean.setExtraStr(bean.getId() + "");
        privateFileItemBean.setFileAddTime(bean.getCompleteTime());
        privateFileItemBean.setOriginalId(bean.getId());

        return privateFileItemBean;
    }

    @Override
    public void hideSingleFile(@NonNull PrivateFileItemBean bean,
            @Nullable HideFileListener listener) {
        try {
            //1.转移文件
            FileUtil.pushFile(mContext, bean);
            ArrayList<PrivateFileItemBean> result = new ArrayList<>();
            result.add(bean);
            //2.插入或者更新数据库数据
            CryptoFileSQLManager.insertOrUpdateBeanSingle(mContext,bean);
            //3.插入或者更新辅助数据库数据
            HideFileSupportFileSQLManager.insertOrUpdateHideFileSupportSingle(mContext,bean.getOriginalId()+"",true);
            //4.回调结果
            if (listener != null) {
                listener.OnSuccess(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailed(e, "hideSingleFile error");
            }
        }
    }

    @Override
    public void hideFiles(@NonNull List<PrivateFileItemBean> beans,
            @Nullable HideFileListener listener) {
        for (PrivateFileItemBean bean : beans) {
            try {
                FileUtil.pushFile(mContext, bean);
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onFailed(e, "hideFiles error");
                    return;
                }
            }
        }
        CryptoFileSQLManager.insertBeanList(mContext, beans);
        if (listener != null) {
            listener.OnSuccess(beans);
        }
    }

    @Override
    public void unHideSingleFile(PrivateFileItemBean bean, HideFileListener listener) {
        List<PrivateFileItemBean> beans = new ArrayList<>();
        beans.add(bean);
        unHideFiles(beans, listener);
    }

    @Override
    public void unHideFiles(List<PrivateFileItemBean> beans, HideFileListener listener) {
        for (PrivateFileItemBean bean : beans) {
            try {
                //1.转移文件
                FileUtil.pullFile(bean);
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onFailed(e, "hideFiles error");
                    return;
                }
            }
        }
        List<Long> ids = new ArrayList<>();
        for (PrivateFileItemBean bean : beans) {
            try {
                ids.add(Long.valueOf(bean.getExtraStr()));
            } catch (Exception e) {
                if (listener != null) {
                    listener.onFailed(e, "hideFiles error");
                    return;
                }
            }
        }
        //2.标记记录
        unhideDownloadFile(ids);
        //3.删除数据库数据
        CryptoFileSQLManager.deleteBeanList(mContext, beans);
        //4.回调结果
        if (listener != null) {
            listener.OnSuccess(beans);
        }
    }

    public boolean unhideDownloadFile(@NonNull List<Long> ids) {
        for (Long id : ids) {
            HideFileSupportFileSQLManager.updateHideFileSupportSingle(mContext,id+"",false);
        }
        return true;
    }

    /**
     * 在重新下载的时候 考虑到隐私文件功能 将原来的记录显示性重置为显示
     *
     * @param id 文件数据库ID
     */
    public static void setReDownloadFileStatus(@NotNull Context context, long id) {
        HideFileSupportFileSQLManager.updateHideFileSupportSingle(context,id+"",false);
    }


    @Override
    public List<PrivateFileItemBean> getAllCryptoFile() {
        return CryptoFileSQLManager.queryAllCryptoFileBean(mContext);
    }

    @Override
    public void delSingeFile(PrivateFileItemBean bean, HideFileListener listener) {
        try {
            FileUtil.deleteFileSingle(bean);
            ArrayList<PrivateFileItemBean> result = new ArrayList<>();
            result.add(bean);
            CryptoFileSQLManager.deleteBeanSingleByID(mContext, bean.getId());
            if (listener != null) {
                listener.OnSuccess(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailed(e, "unHideSingleFile error");
            }
        }
    }

    @Override
    public void delFileList(List<PrivateFileItemBean> beans, HideFileListener listener) {
        try {
            FileUtil.deleteFileList(beans);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailed(e, "hideFiles error");
                return;
            }
        }
        CryptoFileSQLManager.deleteBeanList(mContext, beans);
        if (listener != null) {
            listener.OnSuccess(beans);
        }
    }

    public boolean renameFileToOpen(PrivateFileItemBean bean) {
        String path = bean.getFilePath();
        String suffix = bean.getFileSuffix();
        if (path.endsWith(suffix)) {
            //已经处理了 不用进入处理
            return true;
        }
        try {
            FileUtil.addFileSuffix(mContext, bean);
            return CryptoFileSQLManager.updateBeanPathSingle(mContext, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void fixRenamePrivateFile(Context context) {
        List<PrivateFileItemBean> beans = CryptoFileSQLManager.queryAllCryptoFileBean(context);
        for (PrivateFileItemBean bean : beans) {
            String path = bean.getFilePath();
            String suffix = bean.getFileSuffix();
            if (path.endsWith(suffix)) {
                try {
                    FileUtil.removeFileSuffix(mContext, bean);
                    CryptoFileSQLManager.updateBeanPathSingle(mContext, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
