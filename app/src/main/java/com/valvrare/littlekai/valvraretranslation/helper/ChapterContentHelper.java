package com.valvrare.littlekai.valvraretranslation.helper;

import android.content.Context;

import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;

/**
 * Created by Kai on 12/21/2016.
 */

public class ChapterContentHelper {
    private Context context;
    private ValvrareDatabaseHelper dbh;

    public ChapterContentHelper(Context cxt) {
        dbh = new ValvrareDatabaseHelper(context);
        context = cxt;
    }
//
//    public String getDownloadedChapterContent(String chapterName, String novelName) {
//
//        SQLiteDatabase db = dbh.getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER + " WHERE " + ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = '" + chapterName.replace("'", "''")
//                + "' AND " + ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME + " = '" + novelName.replace("'", "''") + "'";
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                String content = cursor.getString(cursor.getColumnIndex(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT));
//                return content;
//            }
//        }
//        return null;
//    }
//
//    public boolean isDownloadedChapterContentExist(ListChapter chapter) {
//
//        SQLiteDatabase db = dbh.getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER + " WHERE " + ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME + " = '" + chapter.getListChapterName().replace("'", "''")
//                + "' AND " + ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME + " = '" + chapter.getNovelName().replace("'", "''") + "'";
//        ;
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.getCount() > 0) {
//            return true;
//
//        }
//        return false;
//    }
//    public boolean saveChapterContent(ListChapter chapter, String content) {
//
//        SQLiteDatabase db = dbh.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME, chapter.getListChapterName());
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL, chapter.getChapterUrl());
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT, content);
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME, chapter.getNovelName());
//        try {
//            db.insertOrThrow(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER, null, contentValues);
//        } catch (SQLiteConstraintException e) {
//            e.printStackTrace();
//            System.out.println("Failed to insert");
//            return false;
//        }
//        System.out.println("inserted");
//        return true;
//    }
//
//    public boolean saveChapterContent(ListChapter chapter, Novel novel, String content) {
//
//        SQLiteDatabase db = dbh.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_NAME, novel.getNovelName());
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_URL, chapter.getChapterUrl());
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_CHAPTER_CONTENT, content);
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_NOVEL_NAME, novel.getNovelName());
//        contentValues.put(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER_NOVEL_URL, novel.getUrl());
//        try {
//            db.insertOrThrow(ValvrareDatabaseHelper.TABLE_DOWNLOAD_CHAPTER, null, contentValues);
//        } catch (SQLiteConstraintException e) {
//            e.printStackTrace();
//            System.out.println("Failed to insert");
//            return false;
//        }
//        System.out.println("inserted");
//        return true;
//    }
}

