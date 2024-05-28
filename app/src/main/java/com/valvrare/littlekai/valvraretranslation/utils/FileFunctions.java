package com.valvrare.littlekai.valvraretranslation.utils;


import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class which is used to handle all sort of file operations.
 */
public class FileFunctions {
    private String novelName;
    private String chapterName;

    public boolean isChapterDownloaded(String m, String c) {
        File sdcard = Environment.getExternalStorageDirectory();
        File directory = new File(sdcard.getAbsolutePath() + "/.Val Light Novel/" + m + "/" + c);
        if (directory.exists() && directory.isDirectory() && directory.listFiles().length > 0) {
            novelName = m;
            chapterName = c;
            return true;
        }
        return false;
    }

    public ArrayList<String> getFilePaths() {
        ArrayList<String> filePaths = new ArrayList<String>();

        File sdcard = Environment.getExternalStorageDirectory();
        File directory = new File(sdcard.getAbsolutePath() + "/.Val Light Novel/" + novelName + "/" + chapterName);

        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles.length > 0) {

                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {

                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();

                    // Add image path to array list
                    filePaths.add(filePath);
                }
            }
        }
        return filePaths;
    }

    public boolean hasPoster(String novelName) {
        File sdcard = Environment.getExternalStorageDirectory();
        File directory = new File(sdcard.getAbsolutePath() + "/.Val Light Novel/" + novelName + "/");
        if (directory.exists() && directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f.isFile()) return true;
            }
        }
        return false;
    }

    public boolean deleteChapter(String novelName, String chapterName) {
        File sdcard = Environment.getExternalStorageDirectory();
        File directory = new File(sdcard.getAbsolutePath() + "/.Val Light Novel/" + novelName + "/" + chapterName);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        return directory.delete();
    }

    public boolean delete(File path) {
        if (path.isDirectory() && path.exists())
            for (File f : path.listFiles())
                delete(f);
        return path.delete();
    }

    public long getTotalSize() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard.getAbsolutePath() + "/.Val Light Novel/");
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }

        return result / (1024 * 1024);
    }
}
