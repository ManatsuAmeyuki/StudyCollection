package com.csh.studycollection.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FilePathUtils {
    /**
     * 获取拍照的图片的存放目录
     *
     * @return
     */
    public static String getCameraPicDir(Context context) {
        String saveDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "customcamera";
        File path = new File(saveDir);
        if (!path.exists()) {
            path.mkdirs();
        }
        return path.getAbsolutePath();
    }
}
