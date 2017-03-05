package com.set.ui.view.img;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import set.work.utils.PublicTools;

/**
 * 转场的image视图
 */
public final class TransImageView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "ImageView";
	private int random;
	private int image_x;
	private int image_y;
	private int image_w;
	private int image_h;
	private int image_half_w;      // 图片宽的一半
	private int image_half_h;      // 图片高的一半
	private Bitmap bitmapBackGround = null;
	private Bitmap bitmapCurrent = null;
	private Bitmap bitmapTmp = null;
	private Bitmap bitmapTmp2 = null;
	private int mMode;            // 当前图片专场类型
	private int transittime = 25; // 当前专场时间
	private int transittime_y = 25;
	private boolean hasDrawed = true;
	private boolean hasSetImage = false;
	private String mSourceFile;
	private int imagevleft_x;     // 竖屏两个蒙版的x位置
	private int imagevright_x; 
	private int imagehup_y;       // 竖屏蒙版的y位置
	private int imagehdown_y;
	private int modenumb;         // 平分模块
	private int modenumb_x;
	private int modenumb_y;
	private int mode_w;          // 图片平分模块的后的宽度
	private int mode_h;          // 图片平分模块的后的高度
	private int linearry[];      // 线条绘制前后顺序
	private Paint paint;
	private Random randomType;
	private int pixels[];

//------------------------------------------------------------------------------
	private boolean mRun = true;
	private boolean mHasCreated = false;
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	private Thread mThread;
	private int mSleep = 25 * 1;
//------------------------------------------------------------------------------


	public TransImageView(Context context, int width, int height) {
		super(context);
		image_h = height;
		image_w = width;
		init();
	}
	
	private void init() {
		mHolder = this.getHolder();
//		setZOrderOnTop(true);
//		mHolder.setFormat(PixelFormat.TRANSPARENT);
		mHolder.addCallback(this);
		randomType = new Random();
		paint = new Paint();
		mThread = new Thread(runnable);
		mThread.start();
		pixels = new int[image_w * image_h];
		bitmapTmp2 = Bitmap.createBitmap(image_w, 2 * image_h, Config.RGB_565);
		bitmapTmp = Bitmap.createBitmap(2 * image_w, image_h, Config.RGB_565);
	}
	private ImageParpareListener listener;
	
	public interface ImageParpareListener{
		void onImageParpared();
	}
	private Runnable runnable = new Runnable() {
		public void run() {
//			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			while (mRun) {
				try {
					Thread.sleep(mSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (hasSetImage) {
					prepareImage();
					hasSetImage = false;
					if (listener != null) {
						listener.onImageParpared();
					}
					//Log.i(TAG, "----------------------------- hasSetImage");
				}
				if (!hasDrawed && mHasCreated) {
					mCanvas = mHolder.lockCanvas();
					if(mCanvas != null){
						drawImage(mCanvas);
						mHolder.unlockCanvasAndPost(mCanvas);
					}
					moveImage();
					//Log.i(TAG, "----------------------------- hasDrawed");
				}
			}
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHasCreated = true;
		Log.d(TAG, "Surface Created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasCreated = false;
		Log.d(TAG, "Surface Destroyed");
	}

	@Override
	protected void onDetachedFromWindow() {
		mRun = false;
		try {
			mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (bitmapTmp != null) {
			bitmapTmp.recycle();
			bitmapTmp = null;
		}
		if (bitmapTmp2 != null) {
			bitmapTmp2.recycle();
			bitmapTmp2 = null;
		}
		
		if (bitmapBackGround != null) {
			bitmapBackGround.recycle();
			bitmapBackGround = null;
		}

		if (bitmapCurrent != null) {
			bitmapCurrent.recycle();
			bitmapCurrent = null;
		}
		super.onDetachedFromWindow();
	}

	private void drawImage(Canvas c) {
		
		// 第一张图直接刷，无效果
		if (bitmapBackGround == null) {
			if (bitmapCurrent != null) {
				c.drawBitmap(bitmapCurrent, 0, 0, null);
			}
			hasDrawed = true;
			return;
		}

		switch (mMode) {
		case Mode.SLIDE_UP:
			drawSlideUp(c);
			break;
		case Mode.SLIDE_DOWN:
			drawSlideDown(c);
			break;
		case Mode.SLIDE_LEFT:
			drawSlideLeft(c);
			break;
		case Mode.SLIDE_RIGHT:
			drawSlideRight(c);
			break;
		case Mode.VERTICAL_CLOSE:
			drawVerticalClose(c);
			break;
		case Mode.VERTICAL_OPEN:
			drawVerticalOpen(c);
			break;
		case Mode.HORIZONTAL_CLOSE:
			drawHorizontalClose(c);
			break;
		case Mode.HORIZONTAL_OPEN:
			drawHorizontalOpen(c);
			break;
		case Mode.TURN_RIGHT:
			drawTurnRight(c);
			break;
		case Mode.TURN_DWON:
			drawTurnDown(c);
			break;
		case Mode.ZOOM_IN:
			drawZoomIn(c);
			break;
		case Mode.ZOOM_OUT:
			drawZoomOut(c);
			break;
		case Mode.LINE_DOWN_RIGHT:
			drawLineDownRight(c);
			break;
		case Mode.LINE_DOWN_LEFT:
			drawLineDownLeft(c);
			break;
		case Mode.LINE_UP_RIGHT:
			drawLineUpRight(c);
			break;
		case Mode.LINE_UP_LEFT:
			drawLineUpLeft(c);
			break;
		case Mode.RECT_RADOM:
			drawRectRandom(c);
			break;
		case Mode.LINE_VERTICAL_RANDOM:
			drawLineVertical(c);
			break;
		case Mode.LINE_HORIZONTAL_RANDOM:
			drawLineHorizontal(c);
			break;
		case Mode.FADE:
			drawFade(c);
			break;
		case Mode.RANDOM_LINE:
			drawRandomLine(c);
			break;
		case Mode.TRANSLATE_RIGHT:
			drawTranslateRight(c);
			break;
		case Mode.TRANSLATE_LEFT:
			drawTranslateLeft(c);
			break;
		case Mode.TRANSLATE_UP:
			drawTranslateUp(c);
			break;
		case Mode.TRANSLATE_DOWN:
			drawTranslateDown(c);
			break;
		default:
			break;
		}
	}

	private void drawTranslateRight(Canvas c) {
		if (image_x < 0) {
			c.drawBitmap(bitmapTmp, image_x, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawTranslateLeft(Canvas c) {
		if (image_x > -image_w) {
			c.drawBitmap(bitmapTmp, image_x, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawTranslateUp(Canvas c) {
		if (image_y > - image_h) {
			c.drawBitmap(bitmapTmp2, 0, image_y, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawTranslateDown(Canvas c) {
		if (image_y < 0) {
			c.drawBitmap(bitmapTmp2, 0, image_y, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawRandomLine(Canvas c) {
		if (image_y + transittime < image_h) {
			for (int i = 0; i < modenumb; i++) {
				int x = i * mode_w;
				int w = (i == modenumb - 1) ? (image_w - x) :  mode_w;
				if (i % 2 == 0) {
					copyPixels(x, image_y, w, transittime);
				} else {
					copyPixels(x, (image_h   - 1 - transittime) - image_y, w, transittime);
				}
			}
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawFade(Canvas c) {
		paint.setAlpha(255 - transittime);
		c.drawBitmap(bitmapBackGround, 0, 0, paint);
		paint.setAlpha(transittime);
		c.drawBitmap(bitmapCurrent, 0, 0, paint);
	}

	private void drawRectRandom(Canvas c) {
		for (int i = random; i < random + 25 && i < linearry.length; i++) {
			int column = (linearry[i] % modenumb_x);
			int row = (linearry[i] / modenumb_x);
			int x = column * mode_w;
			int y = row * mode_h;
			int w = (column == modenumb_x - 1) ? (image_w - x) :  mode_w;
			int h = (row == modenumb_y - 1) ? (image_h - y) :  mode_h;
			copyPixels(x, y, w, h);
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawLineVertical(Canvas c) {
		for (int i = random; i < random + transittime && i < linearry.length; i++) {
			int x = linearry[i] * mode_w;
			int w = (linearry[i] == modenumb - 1) ? (image_w - x) : mode_w;
			copyPixels(x, 0, w, image_h);
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawLineHorizontal(Canvas c) {
		for (int i = random; i < random + transittime && i < linearry.length; i++) {
			int y = linearry[i] * mode_h;
			int h = (linearry[i] == modenumb - 1) ? (image_h - y) : mode_h;
			copyPixels(0, y, image_w, h);
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawLineDownRight(Canvas c) {
		for (int i = 0; i < modenumb_x; i++) {
			for (int j = 0; j < modenumb_y; j++) {
				if ((modenumb_x - i - 1) + j == modenumb) {
					int x = i * mode_w;
					int y = j * mode_h;
					int w = (i == modenumb_x - 1) ? (image_w - x) :  mode_w;
					int h = (j == modenumb_y - 1) ? (image_h - y) :  mode_h;
					copyPixels(x, y, w, h);
				}
			}
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawLineDownLeft(Canvas c) {
		for (int i = 0; i < modenumb_x; i++) {
			for (int j = 0; j < modenumb_y; j++) {
				if (i + j == modenumb) {
					int x = i * mode_w;
					int y = j * mode_h;
					int w = (i == modenumb_x - 1) ? (image_w - x) :  mode_w;
					int h = (j == modenumb_y - 1) ? (image_h - y) :  mode_h;
					copyPixels(x, y, w, h);
				}
			}
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawLineUpRight(Canvas c) {
		for (int i = 0; i < modenumb_x; i++) {
			for (int j = 0; j < modenumb_y; j++) {
				if (i + (modenumb_y - j - 1) == modenumb) {
					int x = i * mode_w;
					int y = j * mode_h;
					int w = (i == modenumb_x - 1) ? (image_w - x) :  mode_w;
					int h = (j == modenumb_y - 1) ? (image_h - y) :  mode_h;
					copyPixels(x, y, w, h);
				}
			}
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawLineUpLeft(Canvas c) {
		for (int i = 0; i < modenumb_x; i++) {
			for (int j = 0; j < modenumb_y; j++) {
				if ((modenumb_x - i - 1) + (modenumb_y - j - 1) == modenumb) {
					int x = i * mode_w;
					int y = j * mode_h;
					int w = (i == modenumb_x - 1) ? (image_w - x) :  mode_w;
					int h = (j == modenumb_y - 1) ? (image_h - y) :  mode_h;
					copyPixels(x, y, w, h);
				}
			}
		}
		c.drawBitmap(bitmapBackGround, 0, 0, null);
	}

	private void drawSlideUp(Canvas c) {
		if (image_y >= 0) {
			copyPixels(0, image_y, image_w, transittime);
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawSlideDown(Canvas c) {
		if (image_y + transittime < image_h) {
			copyPixels(0, image_y, image_w, transittime);
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawSlideLeft(Canvas c) {
		if (image_x >= 0) {
			copyPixels(image_x, 0, transittime, image_h);
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawSlideRight(Canvas c) {
		if (image_x + transittime < image_w) {
			copyPixels(image_x, 0, transittime, image_h);
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawVerticalClose(Canvas c) {
		boolean drawLeft = imagevleft_x + transittime < image_half_w;
		boolean drawRight = imagevright_x >= image_half_w;
		
		if (drawLeft) {
			copyPixels(imagevleft_x, 0, transittime, image_h);
		}
		
		if (drawRight) {
			copyPixels(imagevright_x, 0, transittime, image_h);
		}
		
		if (drawLeft || drawRight) {
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawVerticalOpen(Canvas c) {
		boolean drawLeft = imagevleft_x >= 0;
		boolean drawRight = imagevright_x + transittime < image_w;
		
		if (drawLeft) {
			copyPixels(imagevleft_x, 0, transittime, image_h);
		}
		
		if (drawRight) {
			copyPixels(imagevright_x, 0, transittime, image_h);
		}
		
		if (drawLeft || drawRight) {
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawHorizontalClose(Canvas c) {
		boolean drawUp = imagehup_y + transittime < image_half_h;
		boolean drawDown = imagehdown_y >= image_half_h;
		
		if (drawUp) {
			copyPixels(0, imagehup_y, image_w, transittime);
		}
		
		if (drawDown) {
			copyPixels(0, imagehdown_y, image_w, transittime);
		}
		
		if (drawUp || drawDown) {
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawHorizontalOpen(Canvas c) {
		boolean drawUp = imagehup_y >= 0;
		boolean drawDown = imagehdown_y + transittime < image_h;
		
		if (drawUp) {
			copyPixels(0, imagehup_y, image_w, transittime);
		}
		
		if (drawDown) {
			copyPixels(0, imagehdown_y, image_w, transittime);
		}
		
		if (drawUp || drawDown) {
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawTurnRight(Canvas c) {
		if (image_x + transittime < mode_w) {
			for (int i = 0; i < modenumb; i++) {
				copyPixels(image_x + (i * mode_w), 0, transittime, image_h);
			}
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawTurnDown(Canvas c) {
		if (image_y + transittime < mode_h) {
			for (int i = 0; i < modenumb; i++) {
				copyPixels(0, image_y + (i * mode_h), image_w, transittime);
			}
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawZoomIn(Canvas c) {
		boolean drawLeft = imagevleft_x + transittime < image_half_w;
		boolean drawRight = imagevright_x >= image_half_w;
		boolean drawUp = imagehup_y + transittime_y < image_half_h;
		boolean drawDown = imagehdown_y >= image_half_h;
		
		if (drawLeft) {
			copyPixels(imagevleft_x, 0, transittime, image_h);
		}
		
		if (drawRight) {
			copyPixels(imagevright_x, 0, transittime, image_h);
		}
		
		if (drawUp) {
			copyPixels(0, imagehup_y, image_w, transittime_y);
		}
		
		if (drawDown) {
			copyPixels(0, imagehdown_y, image_w, transittime_y);
		}
		
		if (drawLeft || drawRight || drawUp || drawDown) {
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}

	private void drawZoomOut(Canvas c) {
		if (image_x >=0 && image_y >= 0) {
			copyPixels(image_x, image_y, 2 * (image_half_w - image_x), 2 * (image_half_h - image_y));
			c.drawBitmap(bitmapBackGround, 0, 0, null);
		} else {
			c.drawBitmap(bitmapCurrent, 0, 0, null);
		}
	}
	
	private void copyPixels(int x, int y, int width, int height) {
		bitmapCurrent.getPixels(pixels, 0, image_w, x, y, width, height);
		bitmapBackGround.setPixels(pixels, 0, image_w, x, y, width, height);
	}

	// 图片的移动
	private void moveImage() {
		if(bitmapBackGround==null)
			return;
		
		switch (mMode) {
		case Mode.SLIDE_UP:
			if (image_y > 0) {
				hasDrawed = false;
				image_y -= transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.SLIDE_DOWN:
			if (image_y < image_h) {
				hasDrawed = false;
				image_y += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.SLIDE_LEFT:
			if (image_x > 0) {
				hasDrawed = false;
				image_x -= transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.SLIDE_RIGHT:
			if (image_x < image_w) {
				hasDrawed = false;
				image_x += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.VERTICAL_CLOSE:
			if (imagevleft_x < image_half_w) {
				hasDrawed = false;
				imagevleft_x += transittime;
			} else {
				hasDrawed = true;
			}
			if (imagevright_x > image_half_w) {
				hasDrawed = false;
				imagevright_x -= transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.VERTICAL_OPEN:
			if (imagevleft_x > 0) {
				hasDrawed = false;
				imagevleft_x -= transittime;
			} else {
				hasDrawed = true;
			}
			if (imagevright_x < image_w) {
				hasDrawed = false;
				imagevright_x += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.HORIZONTAL_CLOSE:
			if (imagehup_y < image_half_h) {
				hasDrawed = false;
				imagehup_y += transittime;
			} else {
				hasDrawed = true;
			}
			if (imagehdown_y > image_half_h) {
				hasDrawed = false;
				imagehdown_y -= transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.HORIZONTAL_OPEN:
			if (imagehup_y > 0) {
				hasDrawed = false;
				imagehup_y -= transittime;
			} else {
				hasDrawed = true;
			}
			if (imagehdown_y < image_h) {
				hasDrawed = false;
				imagehdown_y += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.TURN_RIGHT:
			if (image_x < mode_w) {
				hasDrawed = false;
				image_x += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.TURN_DWON:
			if (image_y < mode_h) {
				hasDrawed = false;
				image_y += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.ZOOM_IN:
			if (imagevleft_x < image_half_w) {
				hasDrawed = false;
				imagevleft_x += transittime;
			} else {
				hasDrawed = true;
			}
			if (imagevright_x > image_half_w) {
				hasDrawed = false;
				imagevright_x -= transittime;
			} else {
				hasDrawed = true;
			}
			if (imagehup_y < image_half_h) {
				hasDrawed = false;
				imagehup_y += transittime_y;
			} else {
				hasDrawed = true;
			}
			if (imagehdown_y > image_half_h) {
				hasDrawed = false;
				imagehdown_y -= transittime_y;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.ZOOM_OUT:
			if (image_x > 0) {
				hasDrawed = false;
				image_x -= transittime;
			} else {
				hasDrawed = true;
			}
			
			if (image_y > 0) {
				hasDrawed = false;
				image_y -= transittime_y;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.LINE_DOWN_RIGHT:
		case Mode.LINE_DOWN_LEFT:
		case Mode.LINE_UP_RIGHT:
		case Mode.LINE_UP_LEFT:
			if (modenumb < modenumb_x + modenumb_y - 1) {
				hasDrawed = false;
				modenumb++;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.LINE_VERTICAL_RANDOM:
		case Mode.LINE_HORIZONTAL_RANDOM:
			if (random + transittime  < linearry.length) {
				hasDrawed = false;
				random += transittime ;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.RECT_RADOM:
			if (random + 25 < linearry.length) {
				hasDrawed = false;
				random += 25;
			} else {
				hasDrawed = true;
			}
			
			break;
		case Mode.FADE:
			if (transittime < 255) {
				hasDrawed = false;
				transittime += 30;
				if (transittime > 255) {
					transittime = 255;
				}
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.RANDOM_LINE:
			if (image_y < image_h) {
				hasDrawed = false;
				image_y += transittime;
			} else {
				hasDrawed = true;
			}
			break;
		case Mode.TRANSLATE_RIGHT:
			if (image_x < 0) {
				hasDrawed = false;
				image_x += transittime;
			} else {
				hasDrawed = true;
//				if (bitmapTmp != null) {
//					bitmapTmp.recycle();
//					bitmapTmp = null;
//				}
			}
			break;
		case Mode.TRANSLATE_LEFT:
			if (image_x > -image_w) {
				hasDrawed = false;
				image_x -= transittime;
			} else {
				hasDrawed = true;
//				if (bitmapTmp != null) {
//					bitmapTmp.recycle();
//					bitmapTmp = null;
//				}
			}
			break;
		case Mode.TRANSLATE_UP:
			if (image_y > -image_h) {
				hasDrawed = false;
				image_y -= transittime;
			} else {
				hasDrawed = true;
//				if (bitmapTmp != null) {
//					bitmapTmp.recycle();
//					bitmapTmp = null;
//				}
			}
			break;
		case Mode.TRANSLATE_DOWN:
			if (image_y < 0) {
				hasDrawed = false;
				image_y += transittime;
			} else {
				hasDrawed = true;
//				if (bitmapTmp != null) {
//					bitmapTmp.recycle();
//					bitmapTmp = null;
//				}
			}
			break;
		default:
			hasDrawed = true;
			break;
		}
	}

	private int getMode(int mMode) {
		if (Mode.TRANSLATE_RANDOM == mMode) {
			return Math.abs(randomType.nextInt(4)) + Mode.TRANSLATE_RIGHT;
		}
		return mMode;
	}

	// 下张图片的数据
	public void setNextImage(String file, int mode) {
		if(mSourceFile != null && file.equals(mSourceFile)){
			return;
		}
		mSourceFile = file;
		mMode = mode;
		hasSetImage = true;
	}
	
	// 下张图片的数据
	public void setNextImage(String file, int mode,ImageParpareListener listener) {
		if(mSourceFile != null && file.equals(mSourceFile)){
			return;
		}
		mSourceFile = file;
		mMode = mode;
		hasSetImage = true;
		this.listener = listener;
	}
	public void clearSource() {
		mSourceFile = null;
	}
	public void clearBg(){
		mSourceFile = null;
		if (bitmapCurrent != null) {
			bitmapCurrent.recycle();
			bitmapCurrent = null;
		}
		if (bitmapBackGround != null) {
			bitmapBackGround.recycle();
			bitmapBackGround = null;
		}
	}
	
	private void prepareImage() {
		if (bitmapCurrent != null) {
			if (bitmapBackGround != null) {
				bitmapBackGround.recycle();
			}
			bitmapBackGround = bitmapCurrent;
		}

		bitmapCurrent = PublicTools.getBitmap(mSourceFile, image_w, image_h,2);
		if (bitmapCurrent == null) {
			Log.d(TAG, "setNextImage(): bitmapCurrent is null");
			hasDrawed = true;
			return;
		}
		mMode = getMode(mMode);
		Log.d(TAG, "THE IMAGE TRANSMODE IS : " + mMode);
//		mMode = 22;
		switch (mMode) {
		case Mode.SLIDE_UP:
			slideUpData();
			break;
		case Mode.SLIDE_DOWN:
			slideDownData();
			break;
		case Mode.SLIDE_LEFT:
			slideLeftData();
			break;
		case Mode.SLIDE_RIGHT:
			slideRightData();
			break;
		case Mode.VERTICAL_CLOSE:
			verticalCloseData();
			break;
		case Mode.VERTICAL_OPEN:
			verticalOpenData();
			break;
		case Mode.HORIZONTAL_CLOSE:
			horizontalCloseData();
			break;
		case Mode.HORIZONTAL_OPEN:
			horizontaleOpenData();
			break;
		case Mode.TURN_RIGHT:
			turnRightData();
			break;
		case Mode.TURN_DWON:
			turnDownData();
			break;
		case Mode.ZOOM_IN:
			zoomInData();
			break;
		case Mode.ZOOM_OUT:
			zoomOutData();
			break;
		case Mode.LINE_DOWN_RIGHT:
		case Mode.LINE_DOWN_LEFT:
		case Mode.LINE_UP_RIGHT:
		case Mode.LINE_UP_LEFT:
			LineInit();
			break;
		case Mode.LINE_VERTICAL_RANDOM:
			LineVerticalRandomData();
			break;
		case Mode.LINE_HORIZONTAL_RANDOM:
			LineHorizontalRandomData();
			break;
		case Mode.RECT_RADOM:
			rectRandomData();
			break;
		case Mode.FADE:
			fadeData();
			break;
		case Mode.RANDOM_LINE:
			randomLineData();
			break;
		case Mode.TRANSLATE_RIGHT:
			transtateRightData();
			break;
		case Mode.TRANSLATE_LEFT:
			transtateLeftData();
			break;
			
		case Mode.TRANSLATE_UP:
			transtateUpData();
			break;
		case Mode.TRANSLATE_DOWN:
			transtateDownData();
			break;
		}
		hasDrawed = false;
	}
	
	public void stopShow() {
		mRun = false;
	}

	private void transtateUpData() {
		//bitmapTmp = Bitmap.createBitmap(image_w, 2 * image_h, Config.RGB_565);
		if (bitmapTmp2 != null && bitmapBackGround != null && bitmapCurrent != null) {
			bitmapBackGround.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp2.setPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapCurrent.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp2.setPixels(pixels, 0, image_w, 0, image_h, image_w, image_h);
		}
		image_y = 0;
		transittime = image_h / 15;
		mSleep = 20;
	}

	private void transtateDownData() {
		//bitmapTmp = Bitmap.createBitmap(image_w, 2 * image_h, Config.RGB_565);
		if (bitmapTmp2 != null && bitmapBackGround != null && bitmapCurrent != null) {
			bitmapBackGround.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp2.setPixels(pixels, 0, image_w, 0, image_h, image_w, image_h);
			bitmapCurrent.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp2.setPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
		}
		image_y = -image_h;
		transittime = image_h / 15;
		mSleep = 20;
	}

	private void transtateLeftData() {
		//bitmapTmp = Bitmap.createBitmap(2 * image_w, image_h, Config.RGB_565);
		if (bitmapTmp != null && bitmapBackGround != null && bitmapCurrent != null) {
			bitmapBackGround.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp.setPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapCurrent.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp.setPixels(pixels, 0, image_w, image_w, 0, image_w, image_h);
		}
		image_x = 0;
		transittime = image_w / 15;
		mSleep = 20;
	}

	private void transtateRightData() {
		//bitmapTmp = Bitmap.createBitmap(2 * image_w, image_h, Config.RGB_565);
		if (bitmapTmp != null && bitmapBackGround != null && bitmapCurrent != null) {
			bitmapBackGround.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp.setPixels(pixels, 0, image_w, image_w, 0, image_w, image_h);
			bitmapCurrent.getPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
			bitmapTmp.setPixels(pixels, 0, image_w, 0, 0, image_w, image_h);
		}
		image_x = -image_w;
		transittime = image_w / 15;
		mSleep = 20;
	}

	private void randomLineData() {
		mode_w = 70;
		modenumb = image_w / mode_w;
		modenumb_y = 15;
		transittime = image_h / modenumb_y;
		image_y = 0;
		mSleep = 10;
	}

	private void fadeData() {
		transittime = 0;
		mSleep = 20;
	}

	private void rectRandomData() {
		random = 0;
		modenumb_x = 20;
		modenumb_y = 20;
		mode_w = image_w / modenumb_x;
		mode_h =  image_h / modenumb_y;
		linearry = new int[modenumb_x * modenumb_y];
		disorder(linearry);
		mSleep = 10;
	}

	private void LineHorizontalRandomData() {
		mode_h = 5;
		modenumb = image_h / mode_h;
		linearry = new int[modenumb];
		random = 0;
		if (modenumb >= 200) {
			transittime = 30;
		} else {
			transittime = 20;
		}
		disorder(linearry);
		mSleep = 10;
	}

	private void LineVerticalRandomData() {
		mode_w = 5;
		modenumb = image_w / mode_w;
		if (modenumb >= 200) {
			transittime = 30;
		} else {
			transittime = 20;
		}
		linearry = new int[modenumb];
		random = 0;
		disorder(linearry);
		mSleep = 10;
	}

	private void LineInit() {
		modenumb = 0;
		modenumb_x = 16;
		modenumb_y = 16;
		mode_w = image_w / modenumb_x;
		mode_h = image_h / modenumb_y;
		mSleep = 10;
	}

	private void zoomInData() {
		image_half_w = image_w / 2;
		image_half_h = image_h / 2;
		transittime = image_w / 40;
		transittime_y = image_h / 40;
		imagevleft_x = 0;
		imagevright_x = image_w - transittime - 1;
		imagehup_y = 0;
		imagehdown_y = image_h - transittime_y - 1;
		mSleep = 50;
	}

	private void zoomOutData() {
		image_half_w = image_w / 2;
		image_half_h = image_h / 2;
		transittime = image_w / 15;
		transittime_y = image_h / 15;
		image_x = image_half_w - transittime;
		image_y = image_half_h - transittime_y;
		mSleep = 10;
	}

	private void turnRightData() {
		image_x = 0;
		modenumb = 10;
		mode_w = image_w / modenumb;
		transittime = mode_w / 10;
		mSleep = 10;
	}

	private void turnDownData() {
		image_y = 0;
		modenumb = 10;
		mode_h = image_h / modenumb;
		transittime = mode_h / 10;
		mSleep = 10;
	}

	private void verticalCloseData() {
		image_half_w = image_w / 2;
		transittime = image_w / 40;
		imagevleft_x = 0;
		imagevright_x = image_w - transittime - 1;
		mSleep = 10;
	}

	private void verticalOpenData() {
		image_half_w = image_w / 2;
		transittime = image_w / 40;
		imagevleft_x = image_half_w  - transittime;
		imagevright_x = image_half_w;
		mSleep = 10;
	}

	private void horizontalCloseData() {
		image_half_h = image_h / 2;
		transittime = image_h / 40;
		imagehup_y = 0;
		imagehdown_y = image_h - transittime - 1;
		mSleep = 10;
	}

	private void horizontaleOpenData() {
		image_half_h = image_h / 2;
		transittime = image_h / 40;
		imagehup_y = image_half_h - transittime;
		imagehdown_y = image_half_h;
		mSleep = 10;
	}

	private void slideUpData() {
		transittime = image_h / 20;
		image_y = image_h - transittime - 1;
		mSleep = 10;
	}

	private void slideDownData() {
		image_y = 0;
		transittime = image_h / 20;
		mSleep = 10;
	}

	private void slideLeftData() {
		transittime = image_w / 20;
		mSleep = 10;
		image_x = image_w - transittime - 1;
	}

	private void slideRightData() {
		image_x = 0;
		transittime = image_w / 20;
		mSleep = 10;
	}
	
	private void disorder(int [] linearry) {
		if (linearry == null) {
			return;
		}
		int len = linearry.length;
		
		for (int i = 0; i < len; i++) {
			linearry[i] = i;
		}
		
		for (int i = 0; i < len; i++) {
			int index = (int) (Math.random() * len);
			int tmp = linearry[index];
			linearry[index] = linearry[i];
			linearry[i] = tmp;
		}
	}

	class Mode {
		public static final byte ZOOM_OUT               =  1; // 缩小
		public static final byte ZOOM_IN                =  0; // 放大
		public static final byte SLIDE_RIGHT            =  6; // 向右滑动
		public static final byte SLIDE_LEFT             =  7; // 向左滑动
		public static final byte SLIDE_UP               =  4; // 向上滑动
		public static final byte SLIDE_DOWN             =  5; // 向下滑动
		public static final byte TURN_RIGHT             =  8; // 向右翻转
		public static final byte TURN_DWON              =  9; // 向下翻转
		public static final byte RECT_RADOM             = 10; // 国际象棋棋盘
		public static final byte VERTICAL_CLOSE         = 13; // 竖屏关闭
		public static final byte VERTICAL_OPEN          = 14; // 竖屏打开
		public static final byte HORIZONTAL_CLOSE       = 15; // 横屏关闭
		public static final byte HORIZONTAL_OPEN        = 16; // 横屏打开
		public static final byte LINE_DOWN_RIGHT        = 17; // 柱状左下滑动
		public static final byte LINE_UP_RIGHT          = 20;
		public static final byte LINE_DOWN_LEFT         = 19;
		public static final byte LINE_UP_LEFT           = 18;
		public static final byte LINE_HORIZONTAL_RANDOM = 21; // 随机横线条
		public static final byte LINE_VERTICAL_RANDOM   = 22; // 随机竖线条
		public static final byte TRANSLATE_RANDOM       = 23; // 随即
		public static final byte FADE                   = 24; // 图片淡入
		public static final byte RANDOM_LINE			= 30; // 随机线条运动
		public static final byte TRANSLATE_RIGHT        = 31; // 向右平移
		public static final byte TRANSLATE_LEFT         = 32; // 向左平移
		public static final byte TRANSLATE_UP           = 33; // 向上平移
		public static final byte TRANSLATE_DOWN         = 34; // 向下平移
	}
}
