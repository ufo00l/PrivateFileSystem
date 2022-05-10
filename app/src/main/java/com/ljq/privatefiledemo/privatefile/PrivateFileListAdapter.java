/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ljq.privatefiledemo.privatefile;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ljq.privatefiledemo.R;
import com.ljq.privatefiledemo.base.adapter.AbsHolder;
import com.ljq.privatefiledemo.base.adapter.AbsRVAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Description : 隐私文件列表适配器
 * Author      : ljq
 * Date        : 2022/04/25
 */
final class PrivateFileListAdapter extends AbsRVAdapter<PrivateFileItemBean, PrivateFileListAdapter.FileListHolder> {
  private IHideFileManager mHideFileManager;
  private onChangeListener mOnChangeListener;

  public PrivateFileListAdapter(Context context, List<PrivateFileItemBean> data) {
    super(context, data);
    //对隐私文件的操作都集中在 HideFileManager
    mHideFileManager = HideFileManager.getInstance(mContext);
  }

  @Override protected FileListHolder getViewHolder(View convertView, int viewType) {
    return new FileListHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_private_list;
  }

  @Override
  protected void bindData(FileListHolder holder, int position, final PrivateFileItemBean item) {
    holder.name.setText("文件名：" + item.getFileName());
    holder.size.setText("文件大小：" + Float.valueOf(item.getFileSize())/1024/1024+"MB");

    holder.back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PrivateFileItemBean bean = mData.get(position);
        unHide(bean);
      }
    });

    holder.del.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PrivateFileItemBean bean = mData.get(position);
        delete(bean);
      }
    });

  }

  public interface onChangeListener{
    void backSuc();
    void backFail();
    void delSuc();
    void delFail();
  }

  private void unHide(PrivateFileItemBean bean) {
    List<PrivateFileItemBean> list = new ArrayList<>();
    list.add(bean);
    mHideFileManager.unHideFiles(list, new HideFileListener() {
      @Override
      public void OnSuccess(List<PrivateFileItemBean> beans) {
        Log.d(TAG, "unHideFiles OnSuccess");
        if(mOnChangeListener != null){
          mOnChangeListener.backSuc();
        }
      }

      @Override
      public void onFailed(Exception e, String errorMsg) {
        Log.d(TAG, "unHideFiles onFailed");
        if(mOnChangeListener != null){
          mOnChangeListener.backFail();
        }
      }
    });
  }

  private void delete(PrivateFileItemBean bean) {
//        showDeleteDialog();
    List<PrivateFileItemBean> list = new ArrayList<>();
    list.add(bean);
    mHideFileManager.delFileList(list,
            new HideFileListener() {
              @Override
              public void OnSuccess(
                      List<PrivateFileItemBean> beans) {
                Log.d(TAG, "delFileList OnSuccess");
                if(mOnChangeListener != null){
                  mOnChangeListener.delSuc();
                }
              }

              @Override
              public void onFailed(Exception e,
                                   String errorMsg) {
                Log.d(TAG, "delFileList onFailed");
                if(mOnChangeListener != null){
                  mOnChangeListener.delFail();
                }
              }
            });
  }

  public void setOnChangeListener(onChangeListener listener){
    mOnChangeListener = listener;
  }

  public void setData(List<PrivateFileItemBean> beans) {
    mData = beans;
    notifyDataSetChanged();
  }

  class FileListHolder extends AbsHolder {
    TextView name;
    TextView size;
    Button back;
    Button del;

    FileListHolder(View itemView) {
      super(itemView);
      name = findViewById(R.id.name);
      size = findViewById(R.id.fileSize);
      back = findViewById(R.id.back);
      del = findViewById(R.id.del);
    }
  }
}
