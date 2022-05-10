package com.ljq.privatefiledemo.privatefile.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Description : 隐私文件数据库创建和升级类
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class CryptoFileSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = CryptoFileSQLiteHelper.class.getSimpleName();
    public static final String DB_NAME = "file_crypto_db";
    public static final String TABLE_CRYPTO_FILE = "crypto_file";
    public static final String TABLE_HIDE_SUPPORT_FILE = "hide_support_file";

    //数据库版本号
    private static final Integer VERSION = 1;

    private volatile static CryptoFileSQLiteHelper mInstance;

    private CryptoFileSQLiteHelper(Context context, String name,
            SQLiteDatabase.CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    private CryptoFileSQLiteHelper(Context context, String name, int version) {
        this(context.getApplicationContext(), name, null, version);
    }

    public static CryptoFileSQLiteHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (CryptoFileSQLiteHelper.class) {
                if (mInstance == null) {
                    mInstance = new CryptoFileSQLiteHelper(context, DB_NAME, VERSION);
                }
            }
        }
        return mInstance;
    }

    //当数据库创建的时候被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
//      创建数据库和表
        createCryptoFileTable(db);
        createHideSupportFileTable(db);
    }

    public static class COLUMN_TABLE_CRYPTO_FILE {
        public static final String ID = "id";
        public static final String EXTRA_STR = "extra_str";
        public static final String FILE_NAME = "file_name";
        public static final String FILE_ORIG_PATH = "file_orig_path";
        public static final String FILE_STORE_PATH = "file_store_path";
        public static final String FILE_SUFFIX = "file_suffix";
        public static final String FILE_ADD_TIME = "file_add_time";
        public static final String FILE_SIZE = "file_size";
    }

    public static class COLUMN_TABLE_HIDE_SUPPORT_FILE {
        public static final String ID = "id";
        public static final String FILE_ID = "file_id";
        public static final String IS_HIDE = "is_hide";
    }

    private void createCryptoFileTable(SQLiteDatabase db) {
        String sql = "create table " + TABLE_CRYPTO_FILE + "(" +
                "id INTEGER primary key autoincrement," +
                "extra_str varchar(500)," +
                "file_name varchar(255)," +
                "file_orig_path varchar(255)," +
                "file_store_path varchar(255)," +
                "file_suffix varchar(20)," +
                "file_add_time INT," +
                "file_size INT" +
                ")";
        db.execSQL(sql);
    }

    private void createHideSupportFileTable(SQLiteDatabase db) {
        String sql = "create table " + TABLE_HIDE_SUPPORT_FILE + "(" +
                "id INTEGER primary key autoincrement," +
                "file_id varchar(255)," +
                "is_hide INT" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.d(TAG, "onUpgrade" + newVersion);
    }
}

