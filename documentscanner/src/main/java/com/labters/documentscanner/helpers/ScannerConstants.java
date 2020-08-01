/*
 * *
 *  * Created by Ali YÜCE on 3/2/20 11:18 PM
 *  * https://github.com/mayuce/
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/2/20 11:10 PM
 *
 */

package com.labters.documentscanner.helpers;

import android.graphics.Bitmap;

public class ScannerConstants {
    public static Bitmap selectedImageBitmap, bitmap;
    public static String cropText="CROP",backText="BACK",
            imageError="Cannot Load Image",
            cropError="Error while cropping";
    public static String cropColor="#6666ff",backColor="#ff0000",progressColor="#331199";
    public static boolean saveStorage=false;
    public static int position = -1;
}
