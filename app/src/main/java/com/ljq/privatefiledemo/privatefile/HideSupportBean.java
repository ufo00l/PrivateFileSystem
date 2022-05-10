package com.ljq.privatefiledemo.privatefile;

/**
 * @author :ljq
 * @des : 用于标记下载记录是否被隐藏
 * @date : 2022/4/25
 */
public class HideSupportBean {
   private long id;
   private long fileId;
   private boolean isHide;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }


   public long getFileId() {
      return fileId;
   }

   public void setFileId(long fileId) {
      this.fileId = fileId;
   }

   public boolean isHide() {
      return isHide;
   }

   public void setHide(boolean hide) {
      isHide = hide;
   }

}
