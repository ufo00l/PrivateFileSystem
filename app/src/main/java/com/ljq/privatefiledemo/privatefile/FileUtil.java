package com.ljq.privatefiledemo.privatefile;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * Description : 文件操作类
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private static final Pattern sNotValidName = Pattern.compile("[?@#$&()\\|;\\'\"<>]+");

    private static final int STORAGE_INIT_IN_THOUSAND = 1000 * 1000 * 1000;
    private static final int STORAGE_INIT_IN_1024 = 1024 * 1024 * 1024;
    private static final int MATH_LOG_TWO = 2;

    /**
     * 根据原地址生成存储地址 //todo 分离成两个方法
     *
     * @param context
     * @param path
     * @return 数组第一个值为存储地址 第二值为扩展名
     */
    public static String[] genderCryptoFilePath(@NonNull Context context, @NonNull String path) {
        String destFolder = context.getExternalFilesDir(".CryptoFiles").toString();
        Log.d(TAG, "genderCryptoFilePath destFolder " + destFolder);

        String fileAllName = path.substring(path.lastIndexOf("/") + 1, path.length());
        String fileName = fileAllName;
        String fileSuffix = "";
        if (fileAllName.contains(".")) {
            fileName = fileAllName.substring(0, fileAllName.lastIndexOf("."));
            fileSuffix =
                    fileAllName.substring(fileAllName.lastIndexOf(".") + 1, fileAllName.length());
        }
        File file = new File(destFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        String destPath = destFolder + "/" + fileName.hashCode();
        Log.d(TAG, "genderCryptoFilePath destPath " + destPath);
        String[] strings = new String[]{destPath, fileSuffix};
        return strings;
    }

    public static boolean deleteFileSingle(PrivateFileItemBean bean) {
        File storeFile = new File(bean.getFilePath());
        if (storeFile.exists()) {
            return storeFile.delete();
        }
        return false;
    }

    public static void deleteFileList(List<PrivateFileItemBean> beans) {
        for (PrivateFileItemBean bean : beans) {
            deleteFileSingle(bean);
        }
    }

    public static void pullFile(PrivateFileItemBean bean) throws Exception {
        String source = bean.getFilePath();
        String store = bean.getFileOriginalPath();

        String storeFolder = source.substring(0, store.indexOf("/"));
        File storeFolderFile = new File(storeFolder);
        if (!storeFolderFile.exists()) {
            storeFolderFile.mkdirs();
        }

        File file = new File(source);
        file.renameTo(new File(store));
    }

    public static void addFileSuffix(Context context, PrivateFileItemBean bean) throws Exception {

//        long start = System.currentTimeMillis();
        String source = bean.getFilePath();
        String dest = bean.getFilePath() + "." + bean.getFileSuffix();
        File file = new File(source);
        file.renameTo(new File(dest));
        bean.setFilePath(dest);

        long end = System.currentTimeMillis();
//        Log.d(TAG,
//                "copyFileUsingFileChannels  " + " spend time " + (end - start) + " source " +
//                        source + " dest " + dest);

//        bean.setFileAddTime(end);
    }

    public static void removeFileSuffix(Context context, PrivateFileItemBean bean)
            throws Exception {

//        long start = System.currentTimeMillis();
        String source = bean.getFilePath();
        String dest = bean.getFilePath().substring(0, bean.getFilePath().lastIndexOf("."));
        File file = new File(source);
        file.renameTo(new File(dest));
        bean.setFilePath(dest);

        long end = System.currentTimeMillis();
//        Log.d(TAG,
//                "copyFileUsingFileChannels  " + " spend time " + (end - start) + " source " +
//                        source + " dest " + dest);

//        bean.setFileAddTime(end);
    }

    public static void pushFile(Context context, PrivateFileItemBean bean) throws Exception {

//        long start = System.currentTimeMillis();
        String[] result = genderCryptoFilePath(context, bean.getFileOriginalPath());
        bean.setFilePath(result[0]);
        bean.setFileSuffix(result[1]);

        String source = bean.getFileOriginalPath();
        String dest = bean.getFilePath();
        File file = new File(source);
        file.renameTo(new File(dest));

        long end = System.currentTimeMillis();
//        Log.d(TAG,
//                "copyFileUsingFileChannels  " + " spend time " + (end - start) + " source " +
//                        source + " dest " + dest);

//        bean.setFileAddTime(end);
    }

//    /**
//     * 返回外部存储可用空间
//     * @return first 为 total second 为 available 单位为GB
//     */
//    public static Pair<Float,Float> getExternalStorageSpaceGB(Context context){
//        File sdcardDir = Environment.getExternalStorageDirectory();
//        StatFs sf = new StatFs(sdcardDir.getPath());
//        boolean isUnitNormal = FeatureOption.getSIsStorageUnitNormal(context);
//        float unit = isUnitNormal ? STORAGE_INIT_IN_1024 : STORAGE_INIT_IN_THOUSAND;
//        float totalGb = sf.getTotalBytes() / unit;
//        float availGb = 0;
//        if (FeatureOption.isAboveCoOS7_0(context)){
//            availGb = sf.getAvailableBytes()*1f / unit;
//        }else {
//            availGb = sf.getAvailableBytes()*1f / STORAGE_INIT_IN_1024;
//        }
//        if (totalGb < 1){
//            return new Pair<>(1f,0f);
//        }
//        if (!isUnitNormal){
//            totalGb = formatInThousand(totalGb);
//        }
//        return new Pair<>(totalGb,availGb);
//    }


    private static int formatInThousand(double size) {
        int formatSize = 0;
        int n = (int) (Math.log(size) / Math.log(MATH_LOG_TWO));
        formatSize = (int) Math.pow(MATH_LOG_TWO, n + 1);

        return formatSize;
    }

    public static boolean isValidFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        if (fileName.trim().length() <= 0){
            return false;
        }
        //文件名过长
        if (fileName.getBytes().length > 255) {
            return false;
        }
        //文件名包含特殊符合
        Matcher m = sNotValidName.matcher(fileName);
        return !m.find();
    }
}
