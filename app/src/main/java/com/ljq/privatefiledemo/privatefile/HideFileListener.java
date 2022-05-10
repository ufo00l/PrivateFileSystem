package com.ljq.privatefiledemo.privatefile;

import java.util.List;

/**
 * Description : 隐藏文件返回结果监听
 * Author      : ljq
 * Date        : 2022/04/25
 */
public interface HideFileListener {

    void OnSuccess(List<PrivateFileItemBean> beans);

    void onFailed(Exception e, String errorMsg);
}
