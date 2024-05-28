package com.valvrare.littlekai.valvraretranslation.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.ImageModel;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class FileDownloader {
    private String novelName;
    private String chapterName;
    private boolean isSdCardAvailable;
    String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Android/data/com.valvrare.littlekai.valvraretranslation" + "/files/";
    private ValvrareDatabaseHelper db;
    Context context;
    NovelDescriptionActivity activity;

    public NovelDescriptionActivity getActivity() {
        return activity;
    }

    public void setActivity(NovelDescriptionActivity activity) {
        this.activity = activity;
    }

    public FileDownloader() {
    }

    public FileDownloader(String n, String c, Context ctx) {
        novelName = n;
        context = ctx;
        chapterName = c;
        if (canWriteOnExternalStorage()) isSdCardAvailable = true;
        db = new ValvrareDatabaseHelper(context);
    }


    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[\\|\\\\?*<\\\":>]", "_");
    }

    public void deleteNovel(Novel novel) throws IOException {
        String iRoot = root;
        File dir = new File(iRoot + sanitizeFilename(novel.getNovelName()));
        FileUtils.deleteDirectory(dir);
//        if (dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(dir, children[i]).delete();
//            }
//        }
    }

    public void deleteChapter(Chapter chapter) {
        String iRoot = root;
        File dir = new File(iRoot + sanitizeFilename(chapter.getNovelName()) + "/" + sanitizeFilename(chapter.getName()) + "/");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Kai", "Permission is granted");
                return true;
            } else {
                Log.v("Kai", "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Kai", "Permission is granted");
            return true;
        }
    }

    public boolean SaveImage(Bitmap finalBitmap, String url) {
        if (finalBitmap == null)
            return false;
        String source = "Snk";
        if (url.contains(Constants.VAL_URL_ROOT))
            source = "Val";
        String iRoot = root + "/" + source + "/" +
                //Constants.class.getPackage().getName()+
                sanitizeFilename(novelName) + "/" + sanitizeFilename(chapterName) + "/";
        if (isStoragePermissionGranted()) {
            File myDir = new File(iRoot);
            myDir.mkdirs();

            String fname = url.substring(url.lastIndexOf("/") + 1);
            fname = sanitizeFilename(fname);
            File file = new File(myDir, fname);
            Log.d("Kai", "SaveImage: " + iRoot + fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            db.insertImage(new ImageModel(chapterName, novelName, iRoot + fname, url));
            return true;
        }
        return false;
    }


    private static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            System.out.println("writing to sd card");
            return true;
        }
        return false;
    }
}
