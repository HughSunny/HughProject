package com.hugh.work.handler;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hugh.work.utils.ConvertUtil;
import com.hugh.work.utils.EquipmentInfo;
import com.hugh.work.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

/**
 * 异常捕捉
 * Created by Hugh on 2016/8/30.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler  {
    private static String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultUEH;
    public static String LOG_FILE_PATH = EquipmentInfo.getSDCardSavePath() + "CRASH_LOG" + File.separator;
    public static String FILE_TEMP = ".log";
    private Context mContext;
    private CrashExceptionListener mCatchListen;
    private CrashHandler() {
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx,CrashExceptionListener listener) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = ctx;
        mCatchListen = listener;
    }

    public interface CrashExceptionListener{
        void onExceptionResult(Throwable ex);
        String getErrorExInfo(String exStr);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, ex.getMessage(), ex);
        LogUtil.Error(TAG,ex.getMessage());
        if (!handleException(ex) && mDefaultUEH != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultUEH.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            if (mCatchListen != null) {
                mCatchListen.onExceptionResult(ex);
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        //保存日志文件
        saveCatchInfo2File(ex);
        return true;
    }

    /**
     * 保存错误信息到文件中
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCatchInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        if (mCatchListen != null && mCatchListen.getErrorExInfo(result) != null) {
            String extInfo = mCatchListen.getErrorExInfo(result);
            sb.append(extInfo);
        } else {
            sb.append(EquipmentInfo.getMobileInfo());
            sb.append(EquipmentInfo.getVersionInfo(mContext));
            sb.append(result);
        }

        try {
            long timestamp = System.currentTimeMillis();
            String time = ConvertUtil.dateYMDHMS2.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + FILE_TEMP;
            String filePath = LOG_FILE_PATH;
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}
