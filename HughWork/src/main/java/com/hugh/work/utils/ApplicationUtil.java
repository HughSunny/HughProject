package com.hugh.work.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import java.util.List;

/**
 * Created by Hugh on 2016/11/9.
 * 应用工具
 */
public class ApplicationUtil {
    /**
     * 跳转到应用
     * @param context
     * @param packagename
     */
    public static void openApplicationByPackageName(Context context, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = getApplicationInfo(context,packagename);
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    public static PackageInfo getApplicationInfo(Context context, String packageName) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageinfo;
    }

    /**
     * 启动应用的某个app
     * @param context
     * @param packageName
     * @param className
     * @param bundle
     * @return 成功或者失败
     */
    public static boolean openAppActivity(Context context, String packageName, String className, Bundle bundle) {
        PackageInfo packageinfo = getApplicationInfo(context,packageName);
        if (packageinfo == null) {
            return false;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(packageName, className);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return false;
    }
}
