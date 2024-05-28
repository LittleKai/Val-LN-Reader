package com.valvrare.littlekai.valvraretranslation.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.valvrare.littlekai.valvraretranslation.dao.NovelApplications;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.ImageModel;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.DbBitmapUtility;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;
import com.valvrare.littlekai.valvraretranslation.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kai on 9/24/2016.
 */
//public class ValvrareDatabaseHelper extends SQLiteAssetHelper {
public class ValvrareDatabaseHelper extends SQLiteOpenHelper {

    private String DATABASE_CREATE_TABLE_ALL_NOVEL = "create table if not exists " + TABLE_ALL_NOVEL + "(\"_id\" INTEGER, \"name\" TEXT, \"url\" TEXT, \"image\" TEXT, \"summary\" TEXT, \"chapter_list\" TEXT, \"img_blob\" BLOB, \"type\" TEXT)";
    private String DATABASE_CREATE_TABLE_ALL_SNK_NOVEL = "create table if not exists " + TABLE_ALL_SNK_NOVEL + "(\"_id\" INTEGER, \"name\" TEXT, \"url\" TEXT, \"image\" TEXT" +
            ", \"second_image\" TEXT" + ", \"summary\" TEXT, \"chapter_list\" TEXT, \"img_blob\" BLOB, \"type\" TEXT, \"categories\" TEXT, \"last_update\" TEXT)";
    private String DATABASE_CREATE_TABLE_NOVEL = "create table if not exists " + TABLE_NOVEL + " (\n" +
            "\t`_id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`name`\tTEXT,\n" +
            "\t`image`\tTEXT,\n" +
            "\t`url`\tTEXT,\n" +
            "\t`latest`\tTEXT\n" +
            ", \"rate\" DOUBLE, \"rate_count\" INTEGER, \"updateDate\" TEXT, \"summary\" TEXT, \"notify\" BOOL" +
            ", \"last_update\" TEXT" +
            ")";
    private String DATABASE_CREATE_TABLE_CHAPTER = "create table if not exists " + TABLE_CHAPTER + "(\"_id\" INTEGER PRIMARY KEY ,\"name\" TEXT,\"novel_name\" TEXT DEFAULT (null) , \"lastYScroll\" INTEGER, \"lastHeight\" FLOAT, \"url\" TEXT, lastZoom float)";
    private String DATABASE_CREATE_TABLE_IMAGE = "create table if not exists " + TABLE_IMAGE + "(\"id\" INTEGER, \"name\" TEXT, \"path\" TEXT, \"url\" TEXT, \"chapter_name\" TEXT, \"novel_name\" TEXT)";
    private String DATABASE_CREATE_TABLE_DOWNLOAD_CHAPTER = "create table if not exists " + TABLE_DOWNLOAD_CHAPTER + "(\"_id\" INTEGER,\"novel_name\" TEXT,\"novel_image\" TEXT,\"novel_url\" TEXT,\"chapter_content\" TEXT,\"chapter_name\" TEXT,\"chapter_url\" TEXT,\"chapter_list\" TEXT,\"rate\" DOUBLE DEFAULT (null) ,\"rate_count\" INTEGER,\"updateDate\" TEXT,\"date\" TEXT,\"time\" TEXT, \"summary\" TEXT)";
    private String DATABASE_CREATE_TABLE_FAV_CHAPTER = "create table if not exists " + TABLE_FAV_CHAPTER + "(\"_id\" INTEGER PRIMARY KEY ,\"name\" TEXT,\"novel_name\" TEXT DEFAULT (null) , \"url\" TEXT, \"novel_url\" TEXT, \"time\" TEXT, \"date\" TEXT, \"chapter_list\" TEXT, \"novel_image\" TEXT)";
    private String DATABASE_CREATE_TABLE_RECENT = "create table if not exists " + TABLE_RECENT + "(\"_id\" INTEGER PRIMARY KEY ,\"novel_image\" TEXT DEFAULT (null) ,\"novel_name\" TEXT DEFAULT (null) ,\"novel_url\" TEXT DEFAULT (null) ,\"chapter_name\" TEXT,\"chapter_url\" TEXT,\"time\" TEXT,\"date\" TEXT,\"chapter_list\" TEXT,\"rate_count\" INTEGER,\"updateDate\" TEXT, \"rate\" DOUBLE, \"summary\" TEXT)";
//    String DATABASE_CREATE_TABLE_USER = "create table if not exists " + TABLE_ALL_NOVEL + "";
//    String DATABASE_CREATE_TABLE_ALL_NOVEL = "create table if not exists " + TABLE_ALL_NOVEL + "";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: db");
        db.execSQL(DATABASE_CREATE_TABLE_ALL_NOVEL);
        db.execSQL(DATABASE_CREATE_TABLE_NOVEL);
        db.execSQL(DATABASE_CREATE_TABLE_CHAPTER);
        db.execSQL(DATABASE_CREATE_TABLE_IMAGE);
        db.execSQL(DATABASE_CREATE_TABLE_DOWNLOAD_CHAPTER);
        db.execSQL(DATABASE_CREATE_TABLE_FAV_CHAPTER);
        db.execSQL(DATABASE_CREATE_TABLE_RECENT);
        db.execSQL(DATABASE_CREATE_TABLE_ALL_SNK_NOVEL);
    }

    private static final String DATABASE_NAME = "valvrare_database.db";
    private static final String TAG = "Kai";
    // Use /files/database to standardize with newer android.

    private final Object lock = new Object();

    public ValvrareDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_NOVEL = "novel";
    private static final String TABLE_CHAPTER = "chapter";
    private static final String TABLE_RECENT = "recent";
    private static final String TABLE_IMAGE = "image";
    private static final String TABLE_DOWNLOAD_CHAPTER = "download";
    private static final String TABLE_FAV_CHAPTER = "fav_chapter";
    private static final String TABLE_ALL_NOVEL = "all_novel";
    private static final String TABLE_ALL_SNK_NOVEL = "all_snk_novel";
    private static final String TABLE_CATEGORY = "category";

    private static final String TABLE_SUBSCRIPTION = "subscription";

    //TABLE_USER COLUMNS
    private static final String TABLE_USER_ID = "_id";
    private static final String TABLE_USER_FACEBOOK = "fb";

    //TABLE_NOVEL COLUMNS
    private static final String TABLE_NOVEL_ID = "_id";
    private static final String TABLE_NOVEL_NAME = "name";
    private static final String TABLE_NOVEL_IMAGE = "image";
    private static final String TABLE_NOVEL_URL = "url";
    private static final String TABLE_NOVEL_LASTEST_CHAPTER = "latest";
    private static final String TABLE_NOVEL_RATE = "rate";
    private static final String TABLE_NOVEL_RATE_COUNT = "rate_count";
    private static final String TABLE_NOVEL_UPDATE_DATE = "updateDate";
    private static final String TABLE_NOVEL_SUMMARY = "summary";
    private static final String TABLE_NOVEL_NOTIFY = "notify";
    private static final String TABLE_NOVEL_LAST_UPDATE = "last_update";

    //TABLE_CHAPTER COLUMNS
    private static final String TABLE_CHAPTER_ID = "_id";
    private static final String TABLE_CHAPTER_NAME = "name";
    private static final String TABLE_CHAPTER_URL = "url";
    private static final String TABLE_CHAPTER_NOVEL_NAME = "novel_name";
    private static final String TABLE_CHAPTER_LAST_Y_SCROLL = "lastYScroll";
    private static final String TABLE_CHAPTER_LAST_HEIGHT = "lastHeight";
    private static final String TABLE_CHAPTER_LAST_ZOOM = "lastZoom";

    //TABLE_RECENT COLUMNS
    private static final String TABLE_RECENT_ID = "_id";
    private static final String TABLE_RECENT_NOVEL_NAME = "novel_name";
    private static final String TABLE_RECENT_NOVEL_IMAGE = "novel_image";
    private static final String TABLE_RECENT_NOVEL_URL = "novel_url";
    private static final String TABLE_RECENT_CHAPTER_NAME = "chapter_name";
    private static final String TABLE_RECENT_CHAPTER_URL = "chapter_url";
    private static final String TABLE_RECENT_CHAPTER_TIME = "time";
    private static final String TABLE_RECENT_CHAPTER_DATE = "date";
    private static final String TABLE_RECENT_CHAPTER_LIST = "chapter_list";
    private static final String TABLE_RECENT_RATE = "rate";
    private static final String TABLE_RECENT_RATE_COUNT = "rate_count";
    private static final String TABLE_RECENT_UPDATE_DATE = "updateDate";
    private static final String TABLE_RECENT_SUMMARY = "summary";

    //TABLE_DOWNLOAD_CHAPTER COLUMNS
    private static final String TABLE_DOWNLOAD_CHAPTER_ID = "_id";
    private static final String TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME = "novel_name";
    private static final String TABLE_DOWNLOAD_CHAPTER_NOVEL_IMAGE = "novel_image";
    private static final String TABLE_DOWNLOAD_CHAPTER_NOVEL_URL = "novel_url";
    private static final String TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT = "chapter_content";
    private static final String TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME = "chapter_name";
    private static final String TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL = "chapter_url";
    private static final String TABLE_DOWNLOAD_CHAPTER_LIST = "chapter_list";
    private static final String TABLE_DOWNLOAD_RATE = "rate";
    private static final String TABLE_DOWNLOAD_RATE_COUNT = "rate_count";
    private static final String TABLE_DOWNLOAD_UPDATE_DATE = "updateDate";
    private static final String TABLE_DOWNLOAD_CHAPTER_DATE = "date";
    private static final String TABLE_DOWNLOAD_CHAPTER_TIME = "time";
    private static final String TABLE_DOWNLOAD_SUMMARY = "summary";

    //TABLE_IMAGE COLUMNS
    private static final String TABLE_IMAGE_ID = "id";
    private static final String TABLE_IMAGE_NAME = "name";
    private static final String TABLE_IMAGE_PATH = "path";
    private static final String TABLE_IMAGE_URL = "url";
    private static final String TABLE_IMAGE_CHAPTER_NAME = "chapter_name";
    private static final String TABLE_IMAGE_NOVEL_NAME = "novel_name";

    //TABLE_FAV_CHAPTER COLUMNS
    private static final String TABLE_FAV_CHAPTER_ID = "_id";
    private static final String TABLE_FAV_CHAPTER_NAME = "name";
    private static final String TABLE_FAV_CHAPTER_URL = "url";
    private static final String TABLE_FAV_CHAPTER_NOVEL_NAME = "novel_name";
    private static final String TABLE_FAV_CHAPTER_NOVEL_URL = "novel_url";
    private static final String TABLE_FAV_CHAPTER_NOVEL_IMAGE = "novel_image";
    private static final String TABLE_FAV_CHAPTER_TIME = "time";
    private static final String TABLE_FAV_CHAPTER_DATE = "date";
    private static final String TABLE_FAV_CHAPTER_CHAPTER_LIST = "chapter_list";

    //TABLE_ALL_NOVEL COLUMNS
    private static final String TABLE_ALL_NOVEL_ID = "_id";
    private static final String TABLE_ALL_NOVEL_NAME = "name";
    private static final String TABLE_ALL_NOVEL_URL = "url";
    private static final String TABLE_ALL_NOVEL_IMAGE = "image";
    private static final String TABLE_ALL_NOVEL_SUMMARY = "summary";
    private static final String TABLE_ALL_NOVEL_CHAPTER_LIST = "chapter_list";
    private static final String TABLE_ALL_NOVEL_IMG_BLOB = "img_blob";
    private static final String TABLE_ALL_NOVEL_TYPE = "type";

    //TABLE_ALL_SNK_NOVEL COLUMNS
    private static final String TABLE_ALL_SNK_NOVEL_ID = "_id";
    private static final String TABLE_ALL_SNK_NOVEL_NAME = "name";
    private static final String TABLE_ALL_SNK_NOVEL_URL = "url";
    private static final String TABLE_ALL_SNK_NOVEL_IMAGE = "image";
    private static final String TABLE_ALL_SNK_NOVEL_2ND_IMAGE = "second_image";
    private static final String TABLE_ALL_SNK_NOVEL_SUMMARY = "summary";
    private static final String TABLE_ALL_SNK_NOVEL_CHAPTER_LIST = "chapter_list";
    private static final String TABLE_ALL_SNK_NOVEL_IMG_BLOB = "img_blob";
    private static final String TABLE_ALL_SNK_NOVEL_TYPE = "type";
    private static final String TABLE_ALL_SNK_NOVEL_CATEGORY = "categories";
    private static final String TABLE_ALL_SNK_NOVEL_LAST_UPDATE = "last_update";

    //TABLE_CATEGORY COLUMNS
    private static final String TABLE_CATEGORY_ID = "_id";
    private static final String TABLE_CATEGORY_NAME = "name";
    private static final String TABLE_CATEGORY_URL = "url";
    private static final String TABLE_CATEGORY_PAGE = "page";

    //TABLE_SUBSCRIPTION COLUMNS
    private static final String TABLE_SUBSCRIPTION_ID = "_id";
    private static final String TABLE_SUBSCRIPTION_NOVEL_NAME = "novel_name";
    private static final String TABLE_SUBSCRIPTION_NOVEL_IMAGE = "novel_image";
    private static final String TABLE_SUBSCRIPTION_NOVEL_URL = "novel_url";
    private static final String TABLE_SUBSCRIPTION_CHAPTER_NAME = "chapter_name";
    private static final String TABLE_SUBSCRIPTION_CHAPTER_URL = "chapter_url";

    private static final int DATABASE_VERSION = 3;


    //    @Override
//    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "oldVersion: " + oldVersion);
//        super.onUpgrade(db, oldVersion, newVersion);
        Context ctx = LNReaderApplication.getInstance().getApplicationContext();
        String filename = UIHelper.getBackupRoot(ctx) + "/backup_" + oldVersion + "_to_" + newVersion + ".db";
//        String str = ctx.getResources().getString(R.string.db_upgrade_backup_notification, filename);
        try {
            ctx.getMainLooper().prepare();
//            Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
//            copyDB(ctx, true, filename);
        } catch (Exception ex) {
//            Log.i(TAG, str);
        }
        Log.d(TAG, "Begin upgrade:");
        if (oldVersion == 2) {
            Log.d(TAG, "DB version is less than 2, recreate DB");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTION);
//            onCreate(db);
        }
        if (oldVersion < 3) {
            db.execSQL(DATABASE_CREATE_TABLE_ALL_SNK_NOVEL);
//            db.execSQL("ALTER TABLE " + TABLE_ALL_SNK_NOVEL + " ADD COLUMN " + TABLE_ALL_SNK_NOVEL_2ND_IMAGE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NOVEL + " ADD COLUMN " + TABLE_NOVEL_LAST_UPDATE + " TEXT");

        }

        Log.d(TAG, oldVersion + "Upgrade is Completed:" + newVersion);
    }

    public ArrayList<Novel> getAllFavoritedNovels() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOVEL;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            ArrayList<Novel> novels = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_NAME));
                String image = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_IMAGE));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_URL));
                String latest = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_LASTEST_CHAPTER));
                String summary = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_SUMMARY));
                String updateDate = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_UPDATE_DATE));
                Double rate = cursor.getDouble(cursor.getColumnIndex(TABLE_NOVEL_RATE));
                int rateCount = cursor.getInt(cursor.getColumnIndex(TABLE_NOVEL_RATE_COUNT));
                boolean isNotify = cursor.getInt(cursor.getColumnIndex(TABLE_NOVEL_NOTIFY)) > 0;

                String lastUpdate = cursor.getString(cursor.getColumnIndex(TABLE_NOVEL_LAST_UPDATE));

                Novel novel = new Novel();
                novel.setNotify(isNotify);
                novel.setUpdateDate(updateDate);
                novel.setRate(rate);
                novel.setRateCount(rateCount);
                novel.setNovelName(name);
                novel.setImage(image);
                novel.setUrl(url);
                novel.setSummary(summary);
                novel.setLatestChapName(latest);
                novel.setFav(true);
                if (lastUpdate != null) {
                    String[] date_parts = lastUpdate.split("-");
                    if (date_parts.length > 0) {
                        novel.setTime(date_parts[0]);
                        novel.setDate(date_parts[1]);
                    }
                }

                novels.add(novel);
            }
            cursor.close();
            return novels;
        }
        cursor.close();
        return null;
    }

    public boolean isNovelFavorited(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NOVEL + " WHERE "
                + TABLE_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''") + "' AND " + TABLE_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean unfavoritedNovel(String novelName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NOVEL, TABLE_NOVEL_NAME + " = ?",
                new String[]{novelName});
        return rowsAffected > 0;
    }

    public boolean isNotified(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NOVEL + " WHERE "
                + TABLE_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''") + "' AND " + TABLE_NOVEL_NOTIFY + " = 'true'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean unNotifiedNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (novel.isNotify())
            novel.setNotify(false);
        else novel.setNotify(true);
        contentValues.put(TABLE_NOVEL_NOTIFY, novel.isNotify());
        int rowsAffected = db.update(TABLE_NOVEL, contentValues, TABLE_NOVEL_NAME + " = ?",
                new String[]{novel.getNovelName()});
        return rowsAffected > 0;
    }

    public boolean updateLastestChapter(Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_NOVEL_LASTEST_CHAPTER, chapter.getName());
        int rowsAffected = db.update(TABLE_NOVEL, contentValues, TABLE_NOVEL_NAME + " = ?",
                new String[]{chapter.getNovelName()});
        return rowsAffected > 0;
    }

    public boolean insertFavNovel(Novel novel) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NOVEL + " WHERE " + TABLE_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TABLE_NOVEL_NAME, novel.getNovelName());
            contentValues.put(TABLE_NOVEL_URL, novel.getUrl());
            contentValues.put(TABLE_NOVEL_IMAGE, novel.getImage());
            contentValues.put(TABLE_NOVEL_LASTEST_CHAPTER, novel.getLatestChapName());
            contentValues.put(TABLE_NOVEL_UPDATE_DATE, novel.getUpdateDate());
            contentValues.put(TABLE_NOVEL_RATE_COUNT, novel.getRateCount());
            contentValues.put(TABLE_NOVEL_RATE, novel.getRate());
            contentValues.put(TABLE_NOVEL_SUMMARY, novel.getSummary());
            contentValues.put(TABLE_NOVEL_NOTIFY, true);
            if (novel.getTime() != null & novel.getDate() != null)
                contentValues.put(TABLE_NOVEL_LAST_UPDATE, novel.getTime() + "-" + novel.getDate());
            try {
                db.insertOrThrow(TABLE_NOVEL, null, contentValues);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
                System.out.println("Failed to insert");
                cursor.close();
                return false;
            }
            System.out.println("inserted");
            cursor.close();
            return true;
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_NOVEL_IMAGE, novel.getImage());
            if (novel.getLatestChapName() != null)
                contentValues.put(TABLE_NOVEL_LASTEST_CHAPTER, novel.getLatestChapName());
            if (novel.getUpdateDate() != null)
                contentValues.put(TABLE_NOVEL_UPDATE_DATE, novel.getUpdateDate());
            if (novel.getRateCount() > 0)
                contentValues.put(TABLE_NOVEL_RATE_COUNT, novel.getRateCount());
            if (novel.getRate() > 0)
                contentValues.put(TABLE_NOVEL_RATE, novel.getRate());
//            if (novel.getSummary() != null)
//                contentValues.put(TABLE_NOVEL_SUMMARY, novel.getSummary());
            int rowsAffected = db.update(TABLE_NOVEL, contentValues, TABLE_NOVEL_NAME + " = ? AND " + TABLE_NOVEL_URL + " = ?",
                    new String[]{novel.getNovelName(), novel.getUrl()});
            cursor.close();
            return rowsAffected > 0;
        }
//            cursor.close(); return false;
    }

    public boolean updateSnkFavNovelDate(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (novel.getTime() != null & novel.getDate() != null)
            contentValues.put(TABLE_NOVEL_LAST_UPDATE, novel.getTime() + "-" + novel.getDate());
        int rowsAffected = db.update(TABLE_NOVEL, contentValues, TABLE_NOVEL_NAME + " = ? AND " + TABLE_NOVEL_URL + " = ?",
                new String[]{novel.getNovelName(), novel.getUrl()});
        return rowsAffected > 0;
    }

    public Chapter getRecentChapter(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_RECENT + " WHERE " + TABLE_RECENT_NOVEL_NAME + "= '" + novel.getNovelName() + "' AND " + TABLE_RECENT_NOVEL_URL + " = '" + novel.getUrl() + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String novel_name = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_NOVEL_NAME));
                String novel_image = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_NOVEL_IMAGE));
                String novel_url = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_NOVEL_URL));
                String chapter_name = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_NAME));
                String chapter_url = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_URL));

                String time = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_TIME));
                String date = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_DATE));

                Chapter chapter = new Chapter(chapter_name, chapter_url, time, date, "a");

                cursor.close();
                return chapter;
            }
        }
        cursor.close();
        return null;
    }

    public ArrayList<Novel> getRecentChapters() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_RECENT + " ORDER BY " + TABLE_RECENT_ID
                + " DESC LIMIT 25";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            ArrayList<Novel> novels = new ArrayList<>();
            while (cursor.moveToNext()) {
                String novel_name = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_NOVEL_NAME));
                String novel_image = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_NOVEL_IMAGE));
                String novel_url = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_NOVEL_URL));
                String chapter_name = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_NAME));
                String chapter_url = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_URL));

                String time = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_TIME));
                String date = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_DATE));
                String chapter_list = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_CHAPTER_LIST));
                double rate = cursor.getDouble(cursor.getColumnIndex(TABLE_RECENT_RATE));
                int rate_count = cursor.getInt(cursor.getColumnIndex(TABLE_RECENT_RATE_COUNT));
                String update_date = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_UPDATE_DATE));
                String summary = cursor.getString(cursor.getColumnIndex(TABLE_RECENT_SUMMARY));
                Chapter chapter = new Chapter(chapter_name, chapter_url, time, date, "a");
                Novel novel = new Novel(novel_name, novel_url, novel_image, chapter);
//                novel.setUrl(novel_url);
                novel.setDate(date);
                novel.setTime(time);
                novel.setRate(rate);
                novel.setRateCount(rate_count);
                novel.setUpdateDate(update_date);
                novel.setChapterList(chapter_list);
                novel.setSummary(summary);
                novels.add(novel);
            }

            cursor.close();
            return novels;
        }
        cursor.close();
        return null;
    }

    public boolean insertRecentChapter(Novel novel, Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteRecentChapter(novel);

        ContentValues contentValues = new ContentValues();

        contentValues.put(TABLE_RECENT_NOVEL_NAME, novel.getNovelName());
        contentValues.put(TABLE_RECENT_NOVEL_IMAGE, novel.getImage());
        contentValues.put(TABLE_RECENT_NOVEL_URL, novel.getUrl());
        contentValues.put(TABLE_RECENT_RATE, novel.getRate());
        contentValues.put(TABLE_RECENT_RATE_COUNT, novel.getRateCount());
//        contentValues.put(TABLE_RECENT_CHAPTER_LIST, novel.getChapterList());
        contentValues.put(TABLE_RECENT_UPDATE_DATE, novel.getUpdateDate());
        contentValues.put(TABLE_RECENT_SUMMARY, novel.getSummary());
        contentValues.put(TABLE_RECENT_CHAPTER_NAME, chapter.getName());
        contentValues.put(TABLE_RECENT_CHAPTER_URL, chapter.getUrl());
        contentValues.put(TABLE_RECENT_CHAPTER_TIME, novel.getTime());
        contentValues.put(TABLE_RECENT_CHAPTER_DATE, novel.getDate());

        try {
            db.insertOrThrow(TABLE_RECENT, null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            System.out.println("recent not inserted");
            return false;
        }
        System.out.println("recent inserted");
        return true;
    }

    public boolean deleteAllNovel() {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("delete from "+ TABLE_ALL_NOVEL);
        int rowsAffected = db.delete(TABLE_ALL_NOVEL, null, null);
        return rowsAffected > 0;
    }

    private boolean deleteRecentChapter(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RECENT, TABLE_RECENT_NOVEL_NAME + " = ? AND " + TABLE_RECENT_NOVEL_URL + " = ?", new String[]{novel.getNovelName(), novel.getUrl()});
        return rowsAffected > 0;
    }

    public boolean deleteAllRecentChapter() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RECENT, null, null);
        return rowsAffected > 0;
    }

    public ArrayList<String> getAllNovelName() {
        ArrayList<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_ALL_NOVEL;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_NAME));
                names.add(name);
            }
            cursor.close();
            return names;
        }
        cursor.close();
        return null;
    }

    public boolean updateNovelImage(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_ALL_NOVEL_IMG_BLOB, novel.getImg_file());

        int rowsAffected = db.update(TABLE_ALL_NOVEL, contentValues, TABLE_ALL_NOVEL_NAME + " = ?",
                new String[]{novel.getNovelName()});

        return rowsAffected > 0;
    }

    public Bitmap getNovelImage(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ALL_NOVEL + " WHERE " + TABLE_ALL_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''") + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        Bitmap bitmap = null;
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                byte[] img = cursor.getBlob(cursor.getColumnIndex(TABLE_ALL_NOVEL_IMG_BLOB));
                bitmap = DbBitmapUtility.getImage(img);
            }
            cursor.close();
            return bitmap;
        }
        cursor.close();
        return null;
    }

    public ArrayList<Novel> getAllNovels() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ALL_NOVEL;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            ArrayList<Novel> novels = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_NAME));
                String image = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_IMAGE));
                String summary = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_SUMMARY));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_URL));
                String type = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_TYPE));
                byte[] img = cursor.getBlob(cursor.getColumnIndex(TABLE_ALL_NOVEL_IMG_BLOB));
                Novel novel = new Novel();
                novel.setNovelName(name);
                novel.setSummary(summary);
                novel.setImg_file(img);
                novel.setUrl(url);
                novel.setImage(image);
                novel.setType(type);
                novels.add(novel);
            }
            cursor.close();
            return novels;
        }
        cursor.close();
        return null;
    }

    public boolean insertAllNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_ALL_NOVEL_NAME, novel.getNovelName());
        contentValues.put(TABLE_ALL_NOVEL_URL, novel.getUrl());
        contentValues.put(TABLE_ALL_NOVEL_IMAGE, novel.getImage());
        contentValues.put(TABLE_ALL_NOVEL_SUMMARY, novel.getSummary());
        contentValues.put(TABLE_ALL_NOVEL_IMG_BLOB, novel.getImg_file());
        contentValues.put(TABLE_ALL_NOVEL_TYPE, novel.getType());

        try {
            db.insertOrThrow(TABLE_ALL_NOVEL, null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            System.out.println("Failed to insert");
            return false;
        }
        System.out.println("inserted NOVEL");
        return true;
    }

    public String getChapterList(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String chapterList = null;
        String table = TABLE_ALL_NOVEL;
        if (novel.getUrl().contains(Constants.VAL_URL_ROOT))
            table = TABLE_ALL_NOVEL;
        else if (novel.getUrl().contains(Constants.SNK_URL_ROOT)) table = TABLE_ALL_SNK_NOVEL;

        String selectQuery = "SELECT  * FROM " + table + " WHERE " + TABLE_ALL_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_ALL_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Log.d(TAG, "getChapterList (db): " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                chapterList = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_CHAPTER_LIST));
            }
            cursor.close();
            return chapterList;
        }
        cursor.close();
        return null;
    }

    public String getSummary(Novel novel) {

        SQLiteDatabase database = this.getReadableDatabase();
        String summary = null;
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_NOVEL + " WHERE " + TABLE_ALL_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_ALL_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                summary = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_SUMMARY));
            }
            cursor.close();
            return summary;
        }
        cursor.close();
        return null;
    }

    public boolean insertChapterList(Novel novel) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_NOVEL + " WHERE " + TABLE_ALL_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_ALL_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_ALL_NOVEL_CHAPTER_LIST, novel.getChapterList());

            int rowsAffected = db.update(TABLE_ALL_NOVEL, contentValues, TABLE_ALL_NOVEL_NAME + " = ?",
                    new String[]{novel.getNovelName()});

            cursor.close();
            return rowsAffected > 0;
        }
        return false;
    }

    public boolean insertImage(ImageModel image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

//        contentValues.put(TABLE_IMAGE_NAME, image.getName());
        contentValues.put(TABLE_IMAGE_PATH, image.getPath());
        contentValues.put(TABLE_IMAGE_URL, image.getUrl());
        contentValues.put(TABLE_IMAGE_CHAPTER_NAME, image.getChapterName());
        contentValues.put(TABLE_IMAGE_NOVEL_NAME, image.getNovelName());
        try {
            db.insertOrThrow(TABLE_IMAGE, null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            System.out.println("Failed to insert");
            return false;
        }
        System.out.println("inserted");
        return true;
    }

    public ArrayList<ImageModel> getDownloadedImage(Chapter chapter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGE + " WHERE " + TABLE_IMAGE_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "' AND " + TABLE_IMAGE_NOVEL_NAME + " = '" + chapter.getNovelName().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ImageModel> imgArr = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String chapter_name = cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_CHAPTER_NAME));
                String novel_name = cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_NOVEL_NAME));
                String path = cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_PATH));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_URL));
                ImageModel imageModel = new ImageModel(chapter_name, novel_name, path, url);
                imgArr.add(imageModel);
            }
            cursor.close();
            return imgArr;
        }
        cursor.close();
        return null;
    }

    public boolean setStateChapter(Chapter chapter) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CHAPTER + " WHERE " + TABLE_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "' AND " + TABLE_CHAPTER_NOVEL_NAME + " = '" + chapter.getNovelName().replace("'", "''") + "'" + "AND " + TABLE_CHAPTER_URL + " = '" + chapter.getUrl().replace("'", "''") + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TABLE_CHAPTER_NAME, chapter.getName());
            contentValues.put(TABLE_CHAPTER_URL, chapter.getUrl());
            contentValues.put(TABLE_CHAPTER_NOVEL_NAME, chapter.getNovelName());
            contentValues.put(TABLE_CHAPTER_LAST_Y_SCROLL, chapter.getLastY());
            contentValues.put(TABLE_CHAPTER_LAST_HEIGHT, chapter.getLastYHeight());
//            contentValues.put(TABLE_CHAPTER_LAST_ZOOM, chapter.getLastZoom());

            try {
                db.insertOrThrow(TABLE_CHAPTER, null, contentValues);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
                System.out.println("Failed to insert");
                cursor.close();
                return false;
            }
            System.out.println("inserted");
            cursor.close();
            return true;
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_CHAPTER_LAST_HEIGHT, chapter.getLastYHeight());
            contentValues.put(TABLE_CHAPTER_LAST_Y_SCROLL, chapter.getLastY());
//            contentValues.put(TABLE_CHAPTER_LAST_ZOOM, chapter.getLastZoom());
            Log.d(TAG, "setStateChapter: posY: " + chapter.getLastY() + ", Total Height: " + chapter.getLastYHeight() + ", Zoom: " + chapter.getLastZoom());
            int rowsAffected = db.update(TABLE_CHAPTER, contentValues, TABLE_CHAPTER_NAME + " = ?",
                    new String[]{chapter.getName()});

            cursor.close();
            return rowsAffected > 0;
        }
    }

    public boolean setChapterFav(Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_FAV_CHAPTER_NAME, chapter.getName());
        contentValues.put(TABLE_FAV_CHAPTER_URL, chapter.getUrl());
        contentValues.put(TABLE_FAV_CHAPTER_NOVEL_URL, chapter.getUrl());
        contentValues.put(TABLE_FAV_CHAPTER_NOVEL_NAME, chapter.getNovelName());
        contentValues.put(TABLE_FAV_CHAPTER_NOVEL_IMAGE, chapter.getNovelImageUrl());
        try {
            db.insertOrThrow(TABLE_FAV_CHAPTER, null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            System.out.println("Failed to insert");
            return false;
        }
        System.out.println("inserted");
        return true;
    }

    public ArrayList<Chapter> getAllFavChapters() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FAV_CHAPTER + " ORDER BY " + TABLE_FAV_CHAPTER_ID
                + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Chapter> chapters = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String chapter_name = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_NAME));
                String novel_name = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_NOVEL_NAME));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_URL));
                String novel_url = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_NOVEL_URL));
                String novel_image = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_NOVEL_IMAGE));
                String date = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_DATE));
                String time = cursor.getString(cursor.getColumnIndex(TABLE_FAV_CHAPTER_TIME));
                Chapter cc = new Chapter(chapter_name, url);
                cc.setNovelUrl(novel_url);
                cc.setNovelName(novel_name);
                cc.setNovelImageUrl(novel_image);
                cc.setDate(date);
                cc.setTime(time);
                chapters.add(cc);
            }
            cursor.close();
            return chapters;
        }
        cursor.close();
        return null;
    }

    public boolean deleteFavChapter(Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_FAV_CHAPTER, TABLE_FAV_CHAPTER_NAME + " = ? AND " + TABLE_FAV_CHAPTER_URL + " = ?", new String[]{chapter.getName(), chapter.getUrl()});
        return rowsAffected > 0;
    }

    public boolean isChapterFav(Chapter chapter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FAV_CHAPTER + " WHERE " + TABLE_FAV_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "' AND " + TABLE_FAV_CHAPTER_URL + " = '" + chapter.getUrl().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        cursor.close();
        return true;
    }

    public boolean isChapterRead(Chapter chapter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CHAPTER + " WHERE " + TABLE_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "'" + "AND " + TABLE_CHAPTER_URL + " = '" + chapter.getUrl().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean getLastYPos(Chapter chapter) {
        SQLiteDatabase db = this.getReadableDatabase();

//        String selectQuery = "SELECT  * FROM " + TABLE_CHAPTER + " WHERE " + TABLE_CHAPTER_NAME + " = '" + chapterName.replace("'", "''")
//                + "' AND " + TABLE_CHAPTER_NOVEL_NAME + " = '" + novelName.replace("'", "''") + "'";
        String selectQuery = "SELECT  * FROM " + TABLE_CHAPTER + " WHERE " + TABLE_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
//                + "' AND " + TABLE_CHAPTER_NOVEL_NAME + " = '" + chapter.getNovelName().replace("'", "''")
                + "' AND " + TABLE_CHAPTER_URL + " = '" + chapter.getUrl().replace("'", "''") + "'";
        ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            chapter.setLastY(0);
            chapter.setLastYHeight(0);
//            chapter.setLastZoom(0);
            cursor.close();
            return false;
        } else if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int lastYPos = cursor.getInt(cursor.getColumnIndex(TABLE_CHAPTER_LAST_Y_SCROLL));
                float height = cursor.getFloat(cursor.getColumnIndex(TABLE_CHAPTER_LAST_HEIGHT));
//                float zoom = cursor.getFloat(cursor.getColumnIndex(TABLE_CHAPTER_LAST_ZOOM));
                chapter.setLastY(lastYPos);
                chapter.setLastYHeight(height);
//                chapter.setLastZoom(zoom);
            }
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public String getDownloadedChapterContent(Chapter chapter) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DOWNLOAD_CHAPTER + " WHERE " + TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "' AND " + TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL + " = '" + chapter.getUrl().replace("'", "''") + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String content = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT));
                cursor.close();
                return content;
            }
        }
        cursor.close();
        return null;
    }

    public boolean isDownloadedChapterContentExist(Chapter chapter) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DOWNLOAD_CHAPTER + " WHERE " + TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "' AND " + TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL + " = '" + chapter.getUrl().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean deleteDownloadedNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_DOWNLOAD_CHAPTER, TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME + " = ?", new String[]{novel.getNovelName()});
        return rowsAffected > 0;
    }

    public boolean deleteDownloadedChapter(Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_DOWNLOAD_CHAPTER, TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME + " = ? AND " + TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = ?", new String[]{chapter.getNovelName(), chapter.getName()});
        return rowsAffected > 0;
    }

    public ArrayList<Novel> getAllDownloadedNovel() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DOWNLOAD_CHAPTER;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            ArrayList<Novel> novels = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME));
                String image = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_CHAPTER_NOVEL_IMAGE));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_CHAPTER_NOVEL_URL));

                double rate = cursor.getDouble(cursor.getColumnIndex(TABLE_DOWNLOAD_RATE));
                int rate_count = cursor.getInt(cursor.getColumnIndex(TABLE_DOWNLOAD_RATE_COUNT));
                String date = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_CHAPTER_DATE));
                String summary = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_SUMMARY));
                String time = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_CHAPTER_TIME));
                String update_date = cursor.getString(cursor.getColumnIndex(TABLE_DOWNLOAD_UPDATE_DATE));
                if (novels.size() == 0) {
                    Novel novel = new Novel(name, url, image, 1);
                    novel.setDate(date);
                    novel.setTime(time);
                    novel.setUpdateDate(update_date);
                    novel.setRate(rate);
                    novel.setRateCount(rate_count);
                    novel.setSummary(summary);
                    novels.add(novel);
                } else {
                    boolean isContained = false;
                    for (int i = 0; i < novels.size(); i++)
                        if (novels.get(i).getNovelName().equals(name) & novels.get(i).getUrl().equals(url)) {
                            novels.get(i).setDaoChapters(novels.get(i).getDaoChapters() + 1);
                            novels.get(i).setDate(date);
                            novels.get(i).setTime(time);
                            novels.get(i).setUpdateDate(update_date);
                            novels.get(i).setRate(rate);
                            novels.get(i).setSummary(summary);
                            novels.get(i).setRateCount(rate_count);
                            isContained = true;
                            break;
                        }
                    if (!isContained) {
                        Novel novel = new Novel(name, url, image, 1);
                        novel.setDate(date);
                        novel.setTime(time);
                        novel.setUpdateDate(update_date);
                        novel.setRate(rate);
                        novel.setRateCount(rate_count);
                        novels.add(novel);
                    }
                }
            }
            cursor.close();
            return novels;
        }
        cursor.close();
        return null;
    }

    public boolean saveChapterContent(Novel novel, Chapter chapter, String content) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_DOWNLOAD_CHAPTER + " WHERE " + TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = '" + chapter.getName().replace("'", "''")
                + "' AND " + TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME + " = '" + chapter.getNovelName().replace("'", "''") + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TABLE_DOWNLOAD_CHAPTER_NOVEL_IMAGE, chapter.getNovelImageUrl());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_NOVEL_URL, chapter.getNovelUrl());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME, chapter.getName());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL, chapter.getUrl());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT, content);
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME, chapter.getNovelName());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_DATE, chapter.getDate());
            contentValues.put(TABLE_DOWNLOAD_UPDATE_DATE, novel.getUpdateDate());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_TIME, chapter.getTime());
            contentValues.put(TABLE_DOWNLOAD_SUMMARY, novel.getSummary());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_LIST, novel.getLatestChapName());
            contentValues.put(TABLE_DOWNLOAD_RATE, novel.getRate());
            contentValues.put(TABLE_DOWNLOAD_RATE_COUNT, novel.getRateCount());

            try {
                db.insertOrThrow(TABLE_DOWNLOAD_CHAPTER, null, contentValues);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
                System.out.println("Failed to insert");
                cursor.close();
                return false;
            }
            System.out.println("inserted");
            cursor.close();
            return true;
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TABLE_DOWNLOAD_CHAPTER_NOVEL_IMAGE, chapter.getNovelImageUrl());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_NOVEL_URL, chapter.getNovelUrl());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL, chapter.getUrl());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT, content);
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME, chapter.getNovelName());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_DATE, chapter.getDate());
            contentValues.put(TABLE_DOWNLOAD_UPDATE_DATE, novel.getUpdateDate());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_TIME, chapter.getTime());
            contentValues.put(TABLE_DOWNLOAD_SUMMARY, novel.getSummary());
            contentValues.put(TABLE_DOWNLOAD_CHAPTER_LIST, novel.getLatestChapName());
            contentValues.put(TABLE_DOWNLOAD_RATE, novel.getRate());
            contentValues.put(TABLE_DOWNLOAD_RATE_COUNT, novel.getRateCount());

            int rowsAffected = db.update(TABLE_DOWNLOAD_CHAPTER, contentValues, TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = ?",
                    new String[]{chapter.getName()});
            cursor.close();
            return rowsAffected > 0;
        }
    }


    public int update(SQLiteDatabase db, String table, ContentValues cv, String whereClause, String[] whereParams) {
        synchronized (lock) {
            if (!db.isOpen())
                db = getWritableDatabase();
            int affectedRows = db.updateWithOnConflict(table, cv, whereClause, whereParams, SQLiteDatabase.CONFLICT_IGNORE);
            // Log.d(TAG, "Affected Rows: " + affectedRows);
            return affectedRows;
            //         cursor.close(); return db.update(table, cv, whereClause, whereParams);
        }
    }

    public long insertOrThrow(SQLiteDatabase db, String table, String nullColumnHack, ContentValues cv) {
        synchronized (lock) {
            if (!db.isOpen())
                db = getWritableDatabase();
            long affectedRows = db.insertOrThrow(table, nullColumnHack, cv);
            // Log.d(TAG, "Affected Rows: " + affectedRows);
            return affectedRows;
        }
    }

    public int delete(SQLiteDatabase db, String table, String whereClause, String[] whereParams) {
        synchronized (lock) {
            if (!db.isOpen())
                db = getWritableDatabase();
            int affectedRows = db.delete(table, whereClause, whereParams);
            // Log.d(TAG, "Affected Rows: " + affectedRows);
            return affectedRows;
        }
    }


    //
//    public String checkDB(SQLiteDatabase db) {
//        try {
//            String sqlQuery = "pragma integrity_check";
//            db.rawQuery(sqlQuery, null);
//
//            // ensure all table are there
//            onCreate(db);
//
//                    cursor.close(); return "DB OK";
//        } catch (Exception ex) {
//            Log.e(TAG, "DB Check failed.", ex);
//                    cursor.close(); return ex.getMessage();
//        }
//    }


    public ArrayList<Novel> getSnkAllNovels() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ALL_NOVEL;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            ArrayList<Novel> novels = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_NAME));
                String image = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_IMAGE));
                String summary = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_SUMMARY));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_URL));
                String chapter_list = cursor.getString(cursor.getColumnIndex(TABLE_ALL_NOVEL_CHAPTER_LIST));
                byte[] img = cursor.getBlob(cursor.getColumnIndex(TABLE_ALL_NOVEL_IMG_BLOB));
                Novel novel = new Novel();
                novel.setNovelName(name);
                novel.setSummary(summary);
                novel.setImg_file(img);
                novel.setUrl(url);
                novel.setImage(image);
                novel.setChapterList(chapter_list);
                novels.add(novel);
            }
            cursor.close();
            return novels;
        }
        cursor.close();
        return null;
    }

    public boolean deleteAllSnkNovels() {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("delete from "+ TABLE_ALL_NOVEL);
        int rowsAffected = db.delete(TABLE_ALL_SNK_NOVEL, null, null);
        return rowsAffected > 0;
    }

    public boolean insertSnkAllNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TABLE_ALL_SNK_NOVEL_NAME, novel.getNovelName());
        contentValues.put(TABLE_ALL_SNK_NOVEL_URL, novel.getUrl());
        contentValues.put(TABLE_ALL_SNK_NOVEL_IMAGE, novel.getImage());
        contentValues.put(TABLE_ALL_SNK_NOVEL_TYPE, novel.getType());
//        contentValues.put(TABLE_ALL_SNK_NOVEL_IMG_BLOB, novel.getImg_file());
//        contentValues.put(TABLE_ALL_SNK_NOVEL_SUMMARY, novel.getSummary());
        try {
            db.insertOrThrow(TABLE_ALL_SNK_NOVEL, null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            System.out.println("Failed to insert");
            return false;
        }
        System.out.println("inserted NOVEL");
        return true;
    }

    public ArrayList<Novel> getAllSnkNovels() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ALL_SNK_NOVEL;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            ArrayList<Novel> novels = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_NAME));
                String image = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_IMAGE));
//                String second_image = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_2ND_IMAGE));
                String url = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_URL));
                String chapter_list = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_CHAPTER_LIST));
//                byte[] img = cursor.getBlob(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_IMG_BLOB));
                String type = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_TYPE));
                Novel novel = new Novel();
                novel.setNovelName(name);
//                novel.setImg_file(img);
//                novel.setSecond_image(second_image);
                novel.setUrl(url);
                novel.setType(type);
                novel.setImage(image);
                novel.setChapterList(chapter_list);
                novels.add(novel);
            }
            cursor.close();
            return novels;
        }
        cursor.close();
        return null;
    }

    public boolean getInfoSnkNovel(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_SNK_NOVEL + " WHERE " + TABLE_ALL_SNK_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_ALL_SNK_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String summary = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_SUMMARY));
                String categories = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_CATEGORY));
//                String summary =  cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_));
                String chapterList = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_CHAPTER_LIST));
                novel.setCatelogies(categories);
                novel.setChapterList(chapterList);
                novel.setSummary(summary);
                if (chapterList != null)
                    return true;
                break;
            }
        }
        cursor.close();
        return false;
    }

    public boolean updateSnkNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_ALL_SNK_NOVEL_SUMMARY, novel.getSummary());
        contentValues.put(TABLE_ALL_SNK_NOVEL_2ND_IMAGE, novel.getSecond_image());
        contentValues.put(TABLE_ALL_SNK_NOVEL_CHAPTER_LIST, novel.getChapterList());
        contentValues.put(TABLE_ALL_SNK_NOVEL_CATEGORY, novel.getCatelogies());
        contentValues.put(TABLE_ALL_SNK_NOVEL_LAST_UPDATE, novel.getTime() + "-" + novel.getDate());
        int rowsAffected = db.update(TABLE_ALL_SNK_NOVEL, contentValues, TABLE_ALL_SNK_NOVEL_NAME + " = ? AND " + TABLE_ALL_SNK_NOVEL_URL + " = ?",
                new String[]{novel.getNovelName(), novel.getUrl()});
        return rowsAffected > 0;
    }

    public void getSnkNovelLastCheck(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_SNK_NOVEL + " WHERE " + TABLE_ALL_SNK_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_ALL_SNK_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String lastUpdate = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_LAST_UPDATE));
                if (lastUpdate != null) {
                    String[] date_parts = lastUpdate.split("-");
                    if (date_parts.length > 0) {
                        novel.setTime(date_parts[0]);
                        novel.setDate(date_parts[1]);
                    }
                }
                break;
            }
        }
        cursor.close();
    }

    public String getSnkSecondImage(Novel novel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_SNK_NOVEL + " WHERE " + TABLE_ALL_SNK_NOVEL_NAME + " = '" + novel.getNovelName().replace("'", "''")
                + "' AND " + TABLE_ALL_SNK_NOVEL_URL + " = '" + novel.getUrl().replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String second_image = cursor.getString(cursor.getColumnIndex(TABLE_ALL_SNK_NOVEL_2ND_IMAGE));
                cursor.close();
                return second_image;
            }
        }
        cursor.close();
        return null;
    }

    public boolean deleteNovelHistory(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RECENT, TABLE_RECENT_NOVEL_NAME + " = ? AND " + TABLE_RECENT_NOVEL_URL + " = ?",
                new String[]{novel.getNovelName(), novel.getUrl()});
        return rowsAffected > 0;
    }

    public boolean deleteChaptersHistory(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String url_contain = null;
        if (novel.getUrl().contains(Constants.VAL_URL_ROOT))
            url_contain = Constants.VAL_URL_ROOT;
        else if (novel.getUrl().contains(Constants.SNK_URL_ROOT))
            url_contain = Constants.SNK_URL_ROOT;
        int rowsAffected = db.delete(TABLE_CHAPTER, TABLE_CHAPTER_NOVEL_NAME + " = ? AND " + TABLE_CHAPTER_URL + " LIKE '%" + url_contain + "%'",
                new String[]{novel.getNovelName()});
        Log.d(TAG, "deleteChaptersHistory: " + url_contain + ", " + (rowsAffected > 0));
        return rowsAffected > 0;
    }

    public boolean deleteAllNovelHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CHAPTER, null, null);
        return rowsAffected > 0;
    }
}
