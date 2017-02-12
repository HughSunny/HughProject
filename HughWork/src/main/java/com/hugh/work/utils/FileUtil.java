package com.hugh.work.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 文件处理工具
 */
public class FileUtil {
    private static final String TAG  = "FileUtil";
    private static int MAX_WRITE_SIZE = 10240;
    private static char[] BUFFER = new char[MAX_WRITE_SIZE];
    /**
     * 把信息info 写到fileName文件中
     * @param filePath
     * @param fileName
     * @param info
     */
    public static void writeLocalFiles(String filePath, String fileName,String info) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        FileOutputStream fos = null;
        BufferedReader reader = null;
        StringReader strReader = null;
        File file = new File(path, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file,false);
//            fos.write(info.getBytes());
            //分段写
            strReader = new StringReader(info);
            reader = new BufferedReader(strReader);
            int len = 0;
            String tempStr = null;
            while ((len = reader.read(BUFFER)) != -1) {
                tempStr = new String(BUFFER,0,len);
                fos.write(tempStr.getBytes());
            }
//            int size = info.length();
//            //取得流文件需要分开的数量
//            int streamNum = (int)Math.floor(size/MAX_WRITE_SIZE);
//            //分开文件之后,剩余的数量
//            int leave = (int)size % MAX_WRITE_SIZE;
//            if (streamNum > 0) {
//                for (int i = 0; i < streamNum; i++) {
//                    fos.write(info.getBytes());
//                }
//            }
            Log.i(TAG, fileName + "  WRITE SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, fileName + "  WRITE EXCEPTION");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (strReader != null) {
                strReader.close();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取本地文件 以文本的形式返回
     * @param file
     * @return
     */
    public static String getFileString(File file) {
        String result = "";
        StringBuilder retBuilder = new StringBuilder();
        //以行读取
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                retBuilder.append(tempString  + "\n");
                tempString = null;
//                result = result + tempString + "\n";
            }
            reader.close();
            tempString = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        //读固定长度的流
//        FileInputStream input = null;
//        try {
//            input = new FileInputStream(file);
//            int len = 0;
//            while ((len = input.read(BUFFER)) != -1) {
//                result = result + "\n" + new String(BUFFER,0,len);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                }catch (IOException e1) {
//                }
//            }
//        }

        return retBuilder.toString();
    }

    /**
     * 文件有效期在daycount之内
     * @param file
     * @param dayCount
     * @return
     */
    public static boolean isModifyInDays(File file, int dayCount){
        if(!file.exists()) {
            return false;
        }
        long lastModify = file.lastModified();
        Date date = new Date(lastModify);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, dayCount);
        date= calendar.getTime();
        Date nowDate = new Date();
        return nowDate.before(date);
    }

    /**
     * 文件拷贝
     * @param fromFile
     * @param toFile
     * @param rewrite
     */
    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        Log.d(TAG, fromFile.getName());
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[MAX_WRITE_SIZE];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            Log.e("readfile", ex.toString());
            Log.e("readfile", ex.getMessage());
        } finally {

        }
    }

    /**
     * 获取文件列表
     * @param dir
     * @param filter
     * @return
     */
    public static String[] getFileList(String dir, final String filter) {
        File dirName = new File(dir);
        if (!dirName.exists()) {
            Log.d(TAG, "file not exists");
            return null;
        }
        if (!dirName.isDirectory()) {
            Log.d(TAG, "file is not a directory");
            return null;
        }

        return dirName.list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(filter);
            }
        });
    }


    /**
     * 获取文件夹的大小
     *
     * @param dir
     */
    public static long getDirSize(File dir) {
        FileInputStream inputStream = null;
        long fileSize = -1;
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return fileSize;
        }
        File[] files = dir.listFiles();
        try {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file == null || !file.exists()) {
                    continue;
                }
                inputStream = new FileInputStream(file);
                if (inputStream != null) {
                    fileSize += inputStream.available();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileSize;
    }

    /**
     * 获取文件大小
     * @param dir
     * @param fileName
     * @return
     */
    public static long getFileSize(String dir, String fileName) {
        long s = 0;
        if (dir == null || fileName == null) {
            return s;
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            try {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                s = fis.available();
            } catch (Exception e) {
            }
        } else {
            System.out.println("文件不存在");
            return -1;
        }
        return s;
    }

    public static void deleteFile(String path) {
        if (path == null) {
            Log.i(TAG, "deletFile path is" + path);
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

    }

    public static void deleteDir(String path) {
        if (path == null) {
            Log.i(TAG, "deletDir path is" + path);
            return;
        }
        File dir = new File(path);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
