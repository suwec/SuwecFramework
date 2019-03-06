package com.suwec.framework.utils;

import android.app.Activity;
import android.view.WindowManager;

/**
 * android activity 截屏控制
 */
public class ScreenShotSettings {

    /**
     * 开启截屏功能
     * @param activity
     */
    public static void enableScreenShot(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    /**
     * 关闭截屏功能
     * @param activity
     */
    public static void disableScreenShot(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}
