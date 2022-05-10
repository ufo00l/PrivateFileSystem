package com.ljq.privatefiledemo.privatefile.sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.ljq.privatefiledemo.privatefile.PrivateFileItemBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Description : 隐私文件数据库操作类
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class CryptoFileSQLManager {
    private static final String TAG = CryptoFileSQLManager.class.getSimpleName();

    public static int deleteBeanSingleByID(Context context, long id) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        int result = sqliteDatabase.delete(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE,
                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.ID + " = ?", new String[
                        ]{id + ""});
        sqliteDatabase.close();
        return result;
    }

    public static void deleteBeanList(Context context, List<PrivateFileItemBean> beans) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        sqliteDatabase.beginTransaction();
        try {
            for (PrivateFileItemBean bean : beans) {
                long id = bean.getId();
                int result = sqliteDatabase
                        .delete(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE,
                                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.ID + " = ?",
                                new String[
                                        ]{id + ""});
            }
            sqliteDatabase.setTransactionSuccessful();
        } finally {
            sqliteDatabase.endTransaction();
        }
        //关闭数据库
        sqliteDatabase.close();
    }

    public static int deleteBeanSingleByPath(Context context, String path) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        int result = sqliteDatabase.delete(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE,
                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_STORE_PATH + " = ?",
                new String[
                        ]{path});
        sqliteDatabase.close();
        return result;
    }

    private static @Nullable
    ContentValues bean2CV(PrivateFileItemBean bean) {
        if (bean == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_NAME, bean.getFileName());
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH,
                bean.getFileOriginalPath());
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_STORE_PATH,
                bean.getFilePath());
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_SUFFIX,
                bean.getFileSuffix());
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.EXTRA_STR, bean.getExtraStr());
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ADD_TIME,
                bean.getFileAddTime());
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_SIZE, bean.getFileSize());
        return values;
    }

    public static synchronized long insertBeanSingle(Context context,
            @NonNull PrivateFileItemBean bean) {
        Log.d(TAG, "insertBeanSingle bean.getFileName() " + bean.getFileName());

        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = bean2CV(bean);
        long result = sqliteDatabase.insert(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, null, contentValues);

        //关闭数据库
        sqliteDatabase.close();

        return result;
    }

    public static synchronized boolean updateBeanPathSingle(Context context,
            @NonNull PrivateFileItemBean bean) {
        Log.d(TAG, "updateBeanSingle bean.getFileName() " + bean.getFileName());

        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_STORE_PATH,
                bean.getFilePath());

        int count = sqliteDatabase.update(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, values,
                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.ID + "=?",
                new String[]{String.valueOf(bean.getId())});
        //关闭数据库
        sqliteDatabase.close();
        return count > 0;
    }

    public static void clearData(Context context) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        sqliteDatabase.delete(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, null, null);

        sqliteDatabase.close();
    }

    public static void insertBeanList(Context context, @NonNull List<PrivateFileItemBean> beans) {

        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        sqliteDatabase.beginTransaction();

        try {
            for (PrivateFileItemBean bean : beans) {
                ContentValues values = bean2CV(bean);
                long info =
                        sqliteDatabase
                                .insert(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, null, values);
            }
            sqliteDatabase.setTransactionSuccessful();
        } finally {
            sqliteDatabase.endTransaction();
        }
        //关闭数据库
        sqliteDatabase.close();
    }

    public static synchronized boolean insertOrUpdateBeanSingle(Context context,PrivateFileItemBean bean) {
        //如果存在就更新
        if(isExtraStrExist(context,bean.getExtraStr())){
            return updateBeanPathSingle(context,bean);
        }else{//不存在就插入
            return insertBeanSingle(context,bean)>0;
        }
    }

    public static boolean isPathExist(Context context, String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        // 创建DatabaseHelper对象
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        // 调用SQLiteDatabase对象的query方法进行查询
        // 返回一个Cursor对象：由数据库查询返回的结果集对象
//        Cursor cursor = sqliteDatabase.query(MediaSQLiteHelper.TABLE_VIDEO_NAME,null, "id=?",
//                new String[] { qid }, null, null, null);
        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, new String[]{
                        CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH},
                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH + "=?",
                new String[]{path}, null, null, null);

        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public static boolean isExtraStrExist(Context context, String extra_str) {
        if (TextUtils.isEmpty(extra_str)) {
            return false;
        }
        // 创建DatabaseHelper对象
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        // 调用SQLiteDatabase对象的query方法进行查询
        // 返回一个Cursor对象：由数据库查询返回的结果集对象
//        Cursor cursor = sqliteDatabase.query(MediaSQLiteHelper.TABLE_VIDEO_NAME,null, "id=?",
//                new String[] { qid }, null, null, null);
        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, new String[]{
                        CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.EXTRA_STR},
                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.EXTRA_STR + "=?",
                new String[]{extra_str}, null, null, null);

        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    @SuppressLint("Range")
    public static List<String> queryAllCryptoFilePath(Context context) {
        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, new String[]{
                        CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH}, null,
                null, null, null, null);
        List<String> paths = new ArrayList<String>();
        while (cursor.moveToNext()) {
             String path = cursor.getString(cursor.getColumnIndex(
                    CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH));
            paths.add(path);
        }
        cursor.close();
        sqliteDatabase.close();
        return paths;
    }

    @SuppressLint("Range")
    public static PrivateFileItemBean queryBeanSingleByID(Context context, long id) {
        Log.d(TAG, "queryBeanSingleByID id " + id);

        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, null,
                CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.ID,
                new String[]{id + ""}, null, null, null);

        String fileName;
        String filePath;
        String fileStorePath;
        String fileSuffix;
        long fileAddTime;
        long fileSize;
        String extra_str;

        PrivateFileItemBean bean = new PrivateFileItemBean();

        while (cursor.moveToNext()) {
            bean.setId(id);
            fileName =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_NAME));
            bean.setFileName(fileName);
            filePath =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH));
            bean.setFileOriginalPath(filePath);
            fileStorePath =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_STORE_PATH));
            bean.setFilePath(fileStorePath);
            fileSuffix =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_SUFFIX));
            bean.setFileSuffix(fileSuffix);

            fileAddTime =
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ADD_TIME));
            bean.setFileAddTime(fileAddTime);

            fileSize =
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_SIZE));
            bean.setFileSize(fileSize);

            extra_str =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.EXTRA_STR));
            bean.setExtraStr(extra_str);

        }
        cursor.close();
        //关闭数据库
        sqliteDatabase.close();

        return bean;
    }

    @SuppressLint("Range")
    public static List<PrivateFileItemBean> queryAllCryptoFileBean(Context context) {
        String sortOrder = CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ADD_TIME + " DESC";

        CryptoFileSQLiteHelper dbHelper = CryptoFileSQLiteHelper.getInstance(context);

        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = sqliteDatabase.query(CryptoFileSQLiteHelper.TABLE_CRYPTO_FILE, null, null,
                null, null, null, sortOrder);

        List<PrivateFileItemBean> beans = new ArrayList<PrivateFileItemBean>();

        long id;
        String fileName;
        String filePath;
        String fileStorePath;
        String fileSuffix;
        long fileAddTime;
        long fileSize;
        String extra_str;

        while (cursor.moveToNext()) {
            PrivateFileItemBean bean = new PrivateFileItemBean();

            id = cursor.getLong(
                    cursor.getColumnIndex(CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.ID));
            bean.setId(id);
            fileName =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_NAME));
            bean.setFileName(fileName);
            filePath =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ORIG_PATH));
            bean.setFileOriginalPath(filePath);
            fileStorePath =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_STORE_PATH));
            bean.setFilePath(fileStorePath);
            fileSuffix =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_SUFFIX));
            bean.setFileSuffix(fileSuffix);

            fileAddTime =
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_ADD_TIME));
            bean.setFileAddTime(fileAddTime);

            fileSize =
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.FILE_SIZE));
            bean.setFileSize(fileSize);

            extra_str =
                    cursor.getString(cursor.getColumnIndex(
                            CryptoFileSQLiteHelper.COLUMN_TABLE_CRYPTO_FILE.EXTRA_STR));
            bean.setExtraStr(extra_str);

            beans.add(bean);
        }

        cursor.close();
        sqliteDatabase.close();

        return beans;
    }

}
