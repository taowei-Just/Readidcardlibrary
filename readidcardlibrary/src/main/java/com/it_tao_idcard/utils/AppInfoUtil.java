package com.it_tao_idcard.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Administrator on 2017-09-28.
 */

public class AppInfoUtil {


    // 获取所有安装包信息

    public static List<PackageInfo> getPackageInfoList(Context context) {

        PackageManager pm = context.getPackageManager();
         

        return pm.getInstalledPackages(0);

    }

    //获取制定包信息

    public static PackageInfo getAppointPackageInfo(Context context,String packageName) {
        List<PackageInfo> packageInfoList = getPackageInfoList(context);
//        String packageName = context.getPackageName();

        for (PackageInfo info : packageInfoList) {

            if (packageName != null)
                if (info.packageName.equals(packageName)) {
 
                    return info;
                }

        }

        return null;
    }

    // 查看apk 信息

    public static PackageInfo getApkInfo(String apkPath, Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    }


}
