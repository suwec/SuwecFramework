package com.suwec.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * 设备唯一识别码管理（双重备份，防止变化）
 */
class Installation {
    private static String sID = null;
    private static final String DEVICE_SID_SP = "SUWEC_DEVICE.SP";
    private static final String DEVICE_SID_KEY = "SUWEC_DEVICE_SID";
    private static final String DEVICE_SID_FILE_NAME = "SUWEC_DEVICE.SID";
    private static final String strSIDBasePath;

    static {
        strSIDBasePath = Environment.getExternalStorageDirectory()+"/.INSTALLATION";
    }

    /**
     * 获取设备识别码
     * @param context
     * @return
     */
    public synchronized static String id(Context context) {
        if (sID == null) {
            sID = readInstallationSP(context);
            if (TextUtils.isEmpty(sID)) {
                File installation = openInstallationFile(context);
                try {
                    if (!installation.exists()) {
                        String androidID = makeAndroidID(context);
                        writeInstallationFile(installation, androidID);
                    }
                    sID = readInstallationFile(installation);
                    writeInstallationSP(context, sID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                File installation = openInstallationFile(context);
                try {
                    writeInstallationFile(installation, sID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sID;
    }

    private static String readInstallationSP(Context context) {
        SharedPreferences mShare = context.getSharedPreferences(DEVICE_SID_SP, Context.MODE_PRIVATE);
        return mShare.getString(DEVICE_SID_KEY, "");
    }

    private static void writeInstallationSP(Context context, String androidID) {
        SharedPreferences mShare = context.getSharedPreferences(DEVICE_SID_SP, Context.MODE_PRIVATE);
        mShare.edit().putString(DEVICE_SID_KEY, androidID).commit();
    }

    private static File openInstallationFile(Context context) {
        String strSIDPath = strSIDBasePath + File.separator + AndroidUtil.getApplicationId(context);
        File fPath = new File(strSIDPath);
        if (!fPath.exists()) {
            fPath.mkdirs();
        }

        return new File(strSIDPath, DEVICE_SID_FILE_NAME);
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation, String androidID) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        out.write(androidID.getBytes());
        out.close();
    }

    private static String makeAndroidID(Context context) {
        String androidId = AndroidUtil.getDeviceId(context);
        if (TextUtils.isEmpty(androidId)) {
            androidId = UUID.randomUUID().toString();
        }
        return androidId;
    }
}