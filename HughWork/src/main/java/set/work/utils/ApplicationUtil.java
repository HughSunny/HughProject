package set.work.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * 应用工具
 * Created by Hugh on 2016/11/9.
 */
public class ApplicationUtil {
    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    /**
     * 程序是否在前台运行
     * @return
     */
    public static  boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回成功或者失败
     * @param apkUri
     * @param filePath
     * @param fileName
     * @param context
     * @return
     */
    public static boolean installAPK(Uri apkUri, String filePath , String fileName, Context context) {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                Intent intents = new Intent();
                intents.setAction("android.intent.action.VIEW");
                intents.addCategory("android.intent.category.DEFAULT");
                intents.setType("application/vnd.android.package-archive");
                intents.setData(apkUri);
                intents.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intents);
            } else {
                File file = new File(filePath + fileName);
                if (!file.exists()) {
                    return false;
                }
                return openApkFile(file, context);
            }
        } catch (Exception var5){
            var5.printStackTrace();
            Toast.makeText(context, "打开升级文件异常", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /***
     *  返回打开apk文件成功或者失败
     * @param file
     * @param context
     * @return
     */
    public static boolean openApkFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static String getMIMEType(File file) {
        String var1 = "";
        String var2 = file.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    /**
     * 跳转到应用
     * @param context
     * @param packagename
     */
    public static void openApplicationByPackageName(Context context, String packagename, Bundle bundle) {
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
            if (bundle != null) {
                intent.putExtras(bundle);
            }

            context.startActivity(intent);
        }
    }

    /**
     * 判断有没有应用的信息
     * @param context
     * @param packageName
     * @return
     */
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
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ComponentName cn = new ComponentName(packageName, className);
//        intent.setComponent(cn);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        return false;
    }



    /**
     * 添加 桌面图标
     * @param context
     * @param name
     * @param resId
     */
    public static void addShortcut(Context context, String name, int resId) {
        Intent intent = new Intent(ACTION_ADD_SHORTCUT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
        intent.putExtra("duplicate", false);
        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_LAUNCHER);
        // 删除的应用程序的ComponentName，即应用程序包名+activity的名字
        intent2.setComponent(new ComponentName(context.getPackageName(), context.getPackageName() + ".Main"));

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, resId));
        context.sendBroadcast(intent);
    }

    /**
     * 删除桌面图标
     * @param context
     * @param name
     * @param cls
     */
    public static void removeShortcut(Context context, String name,Class<?> cls) {
        // remove shortcut的方法在小米系统上不管用，在三星上可以移除
        Intent intent = new Intent(ACTION_REMOVE_SHORTCUT);
        // 名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 设置关联程序
        Intent launcherIntent = new Intent(context, cls).setAction(Intent.ACTION_MAIN);

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        // 发送广播
        context.sendBroadcast(intent);
    }

    /**
     * 判断是否有桌面图标
     * @param context
     * @param name
     * @return
     */
    public static boolean hasInstallShortcut(Context context,String name) {
        boolean hasInstall = false;
        final String AUTHORITY = "com.android.launcher2.settings";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/favorites?notify=true");
        // 这里总是failed to find provider info
        // com.android.launcher2.settings和com.android.launcher.settings都不行
        Cursor cursor = context.getContentResolver().query(CONTENT_URI,
                new String[] { "title", "iconResource" }, "title=?",
                new String[] { name }, null);
        if (cursor != null && cursor.getCount() > 0) {
            hasInstall = true;
        }
        return hasInstall;
    }

    /**
     * 服务是否在跑
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean IsRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                IsRunning = true;
                break;
            }
        }
        return IsRunning;
    }

}
