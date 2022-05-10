package com.ljq.privatefiledemo.privatefile;

import java.util.List;

/**
 * Description : 隐私文件事务操作接口定义
 * Author      : ljq
 * Date        : 2022/04/25
 */
public interface IHideFileManager<T> {

    PrivateFileItemBean makePrivateFileItemBean(T bean);

    void hideSingleFile(PrivateFileItemBean bean, HideFileListener listener);

    void hideFiles(List<PrivateFileItemBean> bean, HideFileListener listener);

    void unHideSingleFile(PrivateFileItemBean bean, HideFileListener listener);

    void unHideFiles(List<PrivateFileItemBean> beans, HideFileListener listener);

    List<PrivateFileItemBean> getAllCryptoFile();

    void delSingeFile(PrivateFileItemBean bean, HideFileListener listener);

    void delFileList(List<PrivateFileItemBean> beans, HideFileListener listener);

}
