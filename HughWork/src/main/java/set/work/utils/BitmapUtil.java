package set.work.utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * bitmap操作的工具类
 *
 */
public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getSimpleName();
    private static final int DEFAULT_COMPRESS_QUALITY = 90;
    private static final int INDEX_ORIENTATION = 0;
    private static final String[] IMAGE_PROJECTION = new String[] {
            ImageColumns.ORIENTATION
    };

    /**
     * Drawable转化成bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }
    /**
     * Creates a mutable bitmap from subset of source bitmap, transformed by the optional matrix.
     */
    private static Bitmap createBitmap(
            Bitmap source, int x, int y, int width, int height, Matrix m) {
        // Re-implement Bitmap createBitmap() to always return a mutable bitmap.
        Canvas canvas = new Canvas();

        Bitmap bitmap;
        Paint paint;
        if ((m == null) || m.isIdentity()) {
            bitmap = Bitmap.createBitmap(width, height, source.getConfig());
            paint = null;
        } else {
            RectF rect = new RectF(0, 0, width, height);
            m.mapRect(rect);
            bitmap = Bitmap.createBitmap(
                    Math.round(rect.width()), Math.round(rect.height()), source.getConfig());

            canvas.translate(-rect.left, -rect.top);
            canvas.concat(m);

            paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            if (!m.rectStaysRect()) {
                paint.setAntiAlias(true);
            }
        }
        bitmap.setDensity(source.getDensity());
        canvas.setBitmap(bitmap);

        Rect srcBounds = new Rect(x, y, x + width, y + height);
        RectF dstBounds = new RectF(0, 0, width, height);
        canvas.drawBitmap(source, srcBounds, dstBounds, paint);
        return bitmap;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Rect getBitmapBounds(byte[] data){
        Rect bounds = new Rect();
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            bounds.right = options.outWidth;
            bounds.bottom = options.outHeight;
            Log.i(TAG, "options.outWidth="+options.outWidth+" , "+"options.outHeight="+options.outHeight);
        } catch (Exception e) {
        }finally {
        }
        return bounds;
    }

    public static Rect getBitmapBounds(Context context, Uri uri) {
        Rect bounds = new Rect();
        InputStream is = null;

        try {
            is = context.getContentResolver().openInputStream(uri);
            return getBitmapBounds(is,false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(is);
        }

        return bounds;
    }

    public static Rect getBitmapBounds(InputStream is, boolean isClose) {
        Rect bounds = new Rect();

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);

            bounds.right = options.outWidth;
            bounds.bottom = options.outHeight;
            Log.i(TAG, "options.outWidth="+options.outWidth+" , "+"options.outHeight="+options.outHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( isClose )
                closeStream(is);
        }

        return bounds;
    }

    private int getOrientation(Context context, Uri uri) {
        int orientation = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, IMAGE_PROJECTION, null, null, null);
            if ((cursor != null) && cursor.moveToNext()) {
                orientation = cursor.getInt(INDEX_ORIENTATION);
            }
        } catch (Exception e) {
            // Ignore error for no orientation column; just use the default orientation value 0.
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return orientation;
    }

    /**
     * Decodes bitmap (maybe immutable) that keeps aspect-ratio and spans most within the bounds.
     */
    public Bitmap decodeBitmapByStream(InputStream is, Rect bounds, int width, int height) {
        Log.i(TAG, "width = " + width + " , " + "height = " + height);
        Bitmap bitmap = null;
        try {
            // TODO: Take max pixels allowed into account for calculation to avoid possible OOM.
//    		Rect bounds = getBitmapBounds(is, false);
            int sampleSize = Math.max(bounds.width() / width, bounds.height() / height);
            sampleSize = Math.min(sampleSize,
                    Math.max(bounds.width() / height, bounds.height() / width));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(sampleSize, 1);
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Log.i(TAG, "sampleSize = " + sampleSize + " , " + "options.inSampleSize = " + options.inSampleSize);
            bitmap = BitmapFactory.decodeStream(is, null, options);//!!!!溢出
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            closeStream(is);
        }

        // Ensure bitmap in 8888 format, good for editing as well as GL compatible.
        if ((bitmap != null) && (bitmap.getConfig() != Bitmap.Config.ARGB_8888)) {
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap.recycle();
            bitmap = copy;
        }

        if (bitmap != null) {
            // Scale down the sampled bitmap if it's still larger than the desired dimension.
            float scale = Math.min((float) width / bitmap.getWidth(),
                    (float) height / bitmap.getHeight());
            scale = Math.max(scale, Math.min((float) height / bitmap.getWidth(),
                    (float) width / bitmap.getHeight()));
            if (scale < 1) {
                Matrix m = new Matrix();
                m.setScale(scale, scale);
                Bitmap transformed = createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m);
                bitmap.recycle();
                return transformed;
            }
        }
        return bitmap;
    }

    /**
     * Decodes bitmap (maybe immutable) that keeps aspect-ratio and spans most within the bounds.
     */
    public Bitmap decodeBitmap(byte[] data, int width, int height){
        Log.i(TAG, "width = " + width + " , " + "height = " + height);
        Bitmap bitmap = null;
        try {
            // TODO: Take max pixels allowed into account for calculation to avoid possible OOM.
            Rect bounds = getBitmapBounds(data);
            int sampleSize = Math.max(bounds.width() / width, bounds.height() / height);
            sampleSize = Math.min(sampleSize,
                    Math.max(bounds.width() / height, bounds.height() / width));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(sampleSize, 1);
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Log.i(TAG, "sampleSize = " + sampleSize + " , " + "options.inSampleSize = " + options.inSampleSize);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);//!!!!溢出
        } catch (Exception e) {
            Log.e(TAG,   e.getMessage());
        } finally {
            data = null;
        }

        // Ensure bitmap in 8888 format, good for editing as well as GL compatible.
        if ((bitmap != null) && (bitmap.getConfig() != Bitmap.Config.ARGB_8888)) {
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap.recycle();
            bitmap = copy;
        }

        if (bitmap != null) {
            // Scale down the sampled bitmap if it's still larger than the desired dimension.
            float scale = Math.min((float) width / bitmap.getWidth(),
                    (float) height / bitmap.getHeight());
            scale = Math.max(scale, Math.min((float) height / bitmap.getWidth(),
                    (float) width / bitmap.getHeight()));
            if (scale < 1) {
                Matrix m = new Matrix();
                m.setScale(scale, scale);
                Bitmap transformed = createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m);
                bitmap.recycle();
                return transformed;
            }
        }
        return bitmap;
    }

    /**
     * Decodes bitmap (maybe immutable) that keeps aspect-ratio and spans most within the bounds.
     */
    public static Bitmap decodeBitmap(Context context, Uri uri, int width, int height) {
        Log.i(TAG, "width = " + width + " , " + "height = " + height);
        InputStream is = null;
        Bitmap bitmap = null;

        try {
            // TODO: Take max pixels allowed into account for calculation to avoid possible OOM.
            Rect bounds = getBitmapBounds(context, uri);
            int sampleSize = Math.max(bounds.width() / width, bounds.height() / height);
            sampleSize = Math.min(sampleSize,
                    Math.max(bounds.width() / height, bounds.height() / width));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(sampleSize, 1);
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            is = context.getContentResolver().openInputStream(uri);
            Log.i(TAG, "sampleSize = " + sampleSize + " , " + "options.inSampleSize = " + options.inSampleSize);
            bitmap = BitmapFactory.decodeStream(is, null, options);//!!!!溢出
        } catch (Exception e) {

        } finally {
            closeStream(is);
        }

        // Ensure bitmap in 8888 format, good for editing as well as GL compatible.
        if ((bitmap != null) && (bitmap.getConfig() != Bitmap.Config.ARGB_8888)) {
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap.recycle();
            bitmap = copy;
        }

        if (bitmap != null) {
            // Scale down the sampled bitmap if it's still larger than the desired dimension.
            float scale = Math.min((float) width / bitmap.getWidth(),
                    (float) height / bitmap.getHeight());
            scale = Math.max(scale, Math.min((float) height / bitmap.getWidth(),
                    (float) width / bitmap.getHeight()));
            if (scale < 1) {
                Matrix m = new Matrix();
                m.setScale(scale, scale);
                Bitmap transformed = createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m);
                bitmap.recycle();
                return transformed;
            }
        }
        return bitmap;
    }

    /**
     * Gets decoded bitmap that keeps orientation as well.
     */
    public Bitmap getBitmap(Context context, Uri uri, int width, int height) {
        Bitmap bitmap = decodeBitmap(context ,uri, width, height);

        // Rotate the decoded bitmap according to its orientation if it's necessary.
        if (bitmap != null) {
            int orientation = getOrientation(context, uri);
            if (orientation != 0) {
                Matrix m = new Matrix();
                m.setRotate(orientation);
                Bitmap transformed = createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m);
                bitmap.recycle();
                return transformed;
            }
        }
        return bitmap;
    }

    /**
     * Saves the bitmap by given directory, filename, and format; if the directory is given null,
     * then saves it under the cache directory.
     */
    public File saveBitmap(
            Bitmap bitmap, String directory, String filename, CompressFormat format) {

        if (directory == null) {
            directory = EquipmentInfo.getSDCardSavePath();
        } else {
            // Check if the given directory exists or try to create it.
            File file = new File(directory);
            if (!file.isDirectory() && !file.mkdirs()) {
                return null;
            }
        }

        File file = null;
        OutputStream os = null;

        try {
            filename = (format == CompressFormat.PNG) ? filename + ".png" : filename + ".jpg";
            file = new File(directory, filename);
            os = new FileOutputStream(file);
            bitmap.compress(format, DEFAULT_COMPRESS_QUALITY, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeStream(os);
        }
        return file;
    }

    /**
     * 缩放bitmap
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float w, float h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleW = (w / width);
        float scaleH = (h / height);
        matrix.postScale(scaleW, scaleH);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return newBmp;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees){
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degrees);
//             m.setRotate(degrees,
//                     (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                bitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//                 if (bitmap != b2) {
//                	 bitmap.recycle();  //Android开发网再次提示Bitmap操作完应该显示的释放
//                	 bitmap = b2;
//                 }
            } catch (OutOfMemoryError ex) {
                // Android建议大家如果出现了内存不足异常，最好return 原始的bitmap对象。.
            }
        }
        return bitmap;
    }

    public static Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
        Log.i(TAG, "drawTextToBitmap = " + gText);
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (12 * scale));
        // text shadow
//		  paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight())/2 + (int)scale*2;

        canvas.drawText(gText,  x, y, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bitmap;
    }

    /**
     * 图片反转
     *
     * @param bmp
     * @param flag
     *            0为水平反转，1为垂直反转
     * @return
     */
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {
        float[] floats = null;
        switch (flag) {
            case 0: // 水平反转
                floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
                break;
            case 1: // 垂直反转
                floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
                break;
        }

        if (floats != null) {
            Matrix matrix = new Matrix();
            matrix.setValues(floats);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        return null;
    }

    /**
     * 获取圆角的bitmap
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static Bitmap getGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 锟睫革拷图片alpha值
     * @param sourceImg
     * @param number
     * @return
     */
    public static Bitmap setAlphaByBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// 锟斤拷锟酵计拷锟紸RGB值
        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);// 锟睫革拷锟斤拷锟�2位锟斤拷值
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }

    /**
     * 锟睫革拷图片rgb值
     * @param sourceImg
     * @param Beta
     * @return
     */
    public static Bitmap effectBitmapBeta(Bitmap sourceImg, int Beta) {
        int srcW = sourceImg.getWidth();
        int srcH = sourceImg.getHeight();
        int[] srcPixels = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(srcPixels, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// 锟斤拷锟酵计拷锟紸RGB值
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;
        int argb;
        for (int i = 0; i < srcH; i++) {
            for (int ii = 0; ii < srcW; ii++) {
                argb = srcPixels[i * srcW + ii];
                a = ((argb & 0xff000000) >> 24); // alpha channel
                r = Beta + ((argb & 0x00ff0000) >> 16); // red channel
                g = Beta + ((argb & 0x0000ff00) >> 8); // green channel
                b = Beta + (argb & 0x000000ff); // blue channel

                r = r > 255 ? 255 : r;
                g = g > 255 ? 255 : g;
                b = b > 255 ? 255 : b;
                srcPixels[i * srcW + ii] = ((a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        sourceImg = Bitmap.createBitmap(srcPixels, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }

    /**
     * 获取视频的缩略图
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createVideoThumbnali(String file, int width, int height) {
        Bitmap bitmap = null;
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(file);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = mp.getDuration();
        mp.release();
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        retriver.setDataSource(file);
        bitmap = retriver.getFrameAtTime(i);
        retriver.release();
        if (bitmap != null) {
            int imageHeight = bitmap.getHeight();
            int imageWidth = bitmap.getWidth();
            if (imageHeight == height && imageWidth == width) {
                return bitmap;
            }
            Matrix matix = new Matrix();
            float scaleX = (width * 1.0f) / imageWidth;
            float scaleY = (height * 1.0f) / imageHeight;
            matix.setScale(scaleX, scaleY);
            Bitmap retBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth,
                    imageHeight, matix, true);
            bitmap.recycle();
            return retBitmap;
        }
        return bitmap;
    }


    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w"+bitmap.getWidth());
        System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取居中的的bitmap
     * @param input
     * @return
     */
    public static Bitmap getFillCenterBitmap(InputStream input){

        Bitmap bitmap = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null ,opt);
        int imageHeight = opt.outWidth;
        int imageWidth = opt.outHeight;
        int screenWidth = EquipmentInfo.getScreenWidth();
        int screenHeight = EquipmentInfo.getScreenHeight();
        if (imageHeight >= screenHeight || imageWidth >=  screenWidth) {
            float scale = 1.0f;
            float areaRate = 1.0f * screenHeight / screenWidth;
            float imgRate = 1.0f * imageWidth / imageHeight;
            if (areaRate < imgRate) {
                scale = (EquipmentInfo.getScreenWidth() * 1.0f) / imageWidth;
            } else {
                scale = (EquipmentInfo.getScreenHeight() * 1.0f) / imageHeight;
            }
            opt.inSampleSize = (int) (1 / scale);
            // 杩欐鍐嶇湡姝ｅ湴鐢熸垚涓�涓湁鍍忕礌鐨勶紝缁忚繃缂╂斁浜嗙殑bitmap
            opt.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(input,null, opt);
        } else {
            bitmap = BitmapFactory.decodeStream(input);
        }
        return bitmap;
    }



    /**
     * 图片倒影
     * @param resId
     * @param context
     * @return
     */
    public static Bitmap getReverseBitmapById(int resId, Context context){
        Bitmap sourceBitmap= BitmapFactory.decodeResource(context.getResources(),resId);
        //绘制原图的下一半图片
        Matrix matrix=new Matrix();
        //倒影翻转
        matrix.setScale(1,-1);

        Bitmap inverseBitmap=Bitmap.createBitmap(sourceBitmap,0,sourceBitmap.getHeight()/2,sourceBitmap.getWidth(),sourceBitmap.getHeight()/3,matrix,false);
        //合成图片
        Bitmap groupbBitmap=Bitmap.createBitmap(sourceBitmap.getWidth(),sourceBitmap.getHeight()+sourceBitmap.getHeight()/3+60,sourceBitmap.getConfig());
        //以合成图片为画布
        Canvas gCanvas=new Canvas(groupbBitmap);
        //将原图和倒影图片画在合成图片上
        gCanvas.drawBitmap(sourceBitmap,0,0,null);
        gCanvas.drawBitmap(inverseBitmap,0,sourceBitmap.getHeight()+50,null);
        //添加遮罩
        Paint paint=new Paint();
        Shader.TileMode tileMode= Shader.TileMode.CLAMP;
        LinearGradient shader=new LinearGradient(0,sourceBitmap.getHeight()+50,0,
                groupbBitmap.getHeight(), Color.BLACK,Color.TRANSPARENT,tileMode);
        paint.setShader(shader);
        //这里取矩形渐变区和图片的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0,sourceBitmap.getHeight()+50,sourceBitmap.getWidth(),groupbBitmap.getHeight(),paint);
        return groupbBitmap;
    }

}
