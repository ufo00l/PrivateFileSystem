package com.ljq.privatefiledemo.privatefile.sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ljq.privatefiledemo.privatefile.HideSupportBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Description : 标记下载文件隐藏数据库操作类
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class HideFileSupportFileSQLManager {
    private static final String TAG = HideFileSupportFileSQLManager.class.getSimpleName();

    public static void clearData(Context context) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        sqliteDatabase.delete(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, null, null);

        sqliteDatabase.close();
    }

    public static synchronized boolean insertOrUpdateHideFileSupportSingle(Context context,String fileId,boolean isHide) {
        //如果存在就更新
        if(isHideFileSupportExist(context,fileId)){
            return updateHideFileSupportSingle(context,fileId,isHide);
        }else{//不存在就插入
            CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
            SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
            sqliteDatabase.beginTransaction();
            long count = 0;
            try {
                ContentValues values = new ContentValues();
                values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.FILE_ID, fileId);
                values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.IS_HIDE,
                        isHide);
                count = sqliteDatabase.insert(CryptoFileSQLiteHelper.TABLE_HIDE_SUPPORT_FILE, null,values);
                sqliteDatabase.setTransactionSuccessful();
            } finally {
                sqliteDatabase.endTransaction();
            }
            //关闭数据库
            sqliteDatabase.close();
            return count > 0;
        }
    }

    public static boolean isHideFileSupportExist(Context context, String fileId) {
        // 创建DatabaseHelper对象
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        // 调用SQLiteDatabase对象的query方法进行查询
        // 返回一个Cursor对象：由数据库查询返回的结果集对象
//        Cursor cursor = sqliteDatabase.query(MediaSQLiteHelper.TABLE_VIDEO_NAME,null, "id=?",
//                new String[] { qid }, null, null, null);
        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_HIDE_SUPPORT_FILE, new String[]{
                        CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.FILE_ID},
                CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.FILE_ID + "=?",
                new String[]{fileId}, null, null, null);

        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public static synchronized boolean updateHideFileSupportSingle(Context context,String fileId,boolean isHide) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.IS_HIDE,
                isHide?1:0);
        int count = sqliteDatabase.update(CryptoFileSQLiteHelper.TABLE_HIDE_SUPPORT_FILE, values,
                CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.FILE_ID + "=?",
                new String[]{fileId});
        //关闭数据库
        sqliteDatabase.close();
        return count > 0;
    }

    @SuppressLint("Range")
    public static List<HideSupportBean> queryHideFileSupportBean(Context context) {
        String sortOrder = CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.ID + " DESC";

        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_HIDE_SUPPORT_FILE, null, null,
                null, null, null, sortOrder);

        List<HideSupportBean> beans = new ArrayList<>();

        long id;
        long fileId;
        boolean isHide;

        while (cursor.moveToNext()) {
            HideSupportBean bean = new HideSupportBean();

            id = cursor.getLong(
                    cursor.getColumnIndex(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.ID));
            bean.setId(id);
            fileId =
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.FILE_ID));
            bean.setFileId(fileId);
            isHide =
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_HIDE_SUPPORT_FILE.IS_HIDE))==1;
            bean.setHide(isHide);
            beans.add(bean);
        }

        cursor.close();
        sqliteDatabase.close();

        return beans;
    }

    public static List<Long> queryHideFileSupportIdList(Context context) {
        List<HideSupportBean>  beans = queryHideFileSupportBean(context);
        List<Long> idList = new ArrayList<>();
        if(beans != null){
            for(HideSupportBean bean : beans){
                if(bean.isHide()){
                    idList.add(bean.getFileId());
                }
            }
        }
        return idList;
    }
}
