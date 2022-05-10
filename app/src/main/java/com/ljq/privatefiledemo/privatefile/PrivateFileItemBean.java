package com.ljq.privatefiledemo.privatefile;

/**
 * Description : 隐私文件bean
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class PrivateFileItemBean {
    //提供给外部存储格外信息的字符串
    private String extraStr;
    //原地址
    private String fileOriginalPath;
    //实际存储
    private String filePath;
    //文件名
    private String fileName;
    //文件拓展名
    private String fileSuffix;
    //文件加入隐藏功能时间
    private long fileAddTime;

    private long fileSize;

    //唯一ID
    private long id;

    //转换前bean ID
    private long original_id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExtraStr() {
        return extraStr;
    }

    public void setExtraStr(String extraStr) {
        this.extraStr = extraStr;
    }

    public String getFileOriginalPath() {
        return fileOriginalPath;
    }

    public void setFileOriginalPath(String fileOriginalPath) {
        this.fileOriginalPath = fileOriginalPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public long getFileAddTime() {
        return fileAddTime;
    }

    public void setFileAddTime(long fileAddTime) {
        this.fileAddTime = fileAddTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public PrivateFileItemBean(long id, long original_id,String extraStr, String fileOriginalPath, String filePath,
            String fileName, String fileSuffix, long fileAddTime, long size) {
        this.extraStr = extraStr;
        this.fileOriginalPath = fileOriginalPath;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSuffix = fileSuffix;
        this.fileAddTime = fileAddTime;
        this.id = id;
        this.fileSize = size;
        this.original_id = original_id;
    }

    public long getOriginalId() {
        return original_id;
    }

    public void setOriginalId(long original_id) {
        this.original_id = original_id;
    }

    public PrivateFileItemBean() {

    }

    @Override
    public String toString() {
        return "PrivateFileItemBean{" +
                "extraStr='" + extraStr + '\'' +
                ", fileOriginalPath='" + fileOriginalPath + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSuffix='" + fileSuffix + '\'' +
                ", fileAddTime=" + fileAddTime +
                ", fileSize=" + fileSize +
                ", id=" + id +
                ", original_id=" + original_id +
                '}';
    }
}
