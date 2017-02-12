package com.set.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ScrollTextView extends SurfaceView implements SurfaceHolder.Callback{
	private static final String TAG = "TextView";
	private TextViewListener listener;
	/**
	 * 方向
	 */
	private final int DIR_LEFT  = 1;
	private final int DIR_RIGHT = 2;
	private final int DIR_UP    = 3;
	private final int DIR_DOWN  = 4;

	/**
	 * 文本排列方向
	 */
	public final byte TEXT_HORIZONTAL = 0;
	public final byte TEXT_VERTICAL   = 1;

	/**
	 * 文本移动的速度
	 */
	private final int MOVE_SLOW   = 1;  // 1px per 40ms, 25p/s
	private final int MOVE_MIDD   = 2;  // 1px per 30ms, 33p/s
	private final int MOVE_FAST   = 3;  // 1px per 22ms, 45p/s
	private final int MOVE_SOFAST = 4;  // 2px per 35ms, 57p/s
	/**
	 * 文本字体类型
	 */
	public static final String TYPE_FACE_ARIAL = "Chinese-黑体";
	public static final String TYPE_FACE_BLACK = "English-Arial";

	private Paint paintText;      // 画笔属性
	private Paint paintBg;        // 画笔属性
	private String content;       // 文本內容
	private char[] textChars;     // 文本个数
	private float size;           // 字体大小
	private float length;         // 文本长度
	private float height;         // 文本的高
	private float areaHeight;     // 背景区域的高
	private float areaWidth;      // 背景区域的宽
	private float x;              // 文本x位置
	private float y;              // 文本y位置
	private int speed;            // 文本速度
	private int moveDirection;    // 文本移动的方向
	private byte compose = TEXT_HORIZONTAL;
	private boolean isDraw = false;
	private boolean mRun = true;
	Bitmap bitmap;
	Matrix matrix = new Matrix();
	private Thread thread;
	public ScrollTextView(Context context, float areawidth, float areaheight) {
		super(context);
		this.areaWidth = areawidth;
		this.areaHeight = areaheight;
		init();
	}

	private void init() {
		this.setZOrderOnTop(true);
		mHolder = this.getHolder();
		mHolder.setFormat(PixelFormat.TRANSPARENT);
		mHolder.addCallback(this);
		paintText = new Paint();
		paintText.setAntiAlias(true);
		paintBg = new Paint();
		thread = new Thread(runnable);
		thread.start();
	}

	private Runnable runnable = new Runnable() {
		public void run() {	
//			Log.d(TAG, "threadNAME === > "+Thread.currentThread().getName()+"   threadID === > "+Thread.currentThread().getId());
			while(mRun){
				if (!isDraw ||!mHasCreated) {
					continue;
				}
				mCanvas = mHolder.lockCanvas();
				if(mCanvas == null || mHolder == null){
					//TODO:: is needed unlockCanvasAndPost() ??
					return;
				}
				drawText(mCanvas);
				mHolder.unlockCanvasAndPost(mCanvas);
				moveText();
				try {
					Thread.sleep(mSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private boolean mHasCreated = false;
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	private int mSleep = 30;

	public void setTextView(String content, int size, int dir, int speedtype,
			String typeface, String textColor, String bgColor, String alphaPer, int time,TextViewListener _listener) {
		this.content = content;
		this.size = size;
		this.moveDirection = dir;
		if(size >= areaHeight){//自适应
			this.size = areaHeight - 3 ;
			paintText.setTextSize(areaHeight);
		}else{
			paintText.setTextSize(size);
		}
		if (textColor != null) {
			paintText.setColor(Color.parseColor(textColor));
		}
		if (bgColor != null) {
			int alphaP = Integer.parseInt(alphaPer.substring(0, alphaPer.length()-1));
			int alpha = alphaP*255/100;
			paintBg.setColor(Color.parseColor(bgColor));
			paintBg.setAlpha(alpha);
		}
		if (typeface != null) {
			setTypeface(typeface);
		}

		if (content != null) {
			if(content.contains("\n")&& (moveDirection == 1|| moveDirection == 2)){
				content.replace("\n", "  ");
			}
			textChars = content.toCharArray();
			length = paintText.measureText(content);
			if(content.length() != 0){
				height = paintText.measureText(textChars, 0, 1);
			}
		}
		speed = 1;
		initLocation(dir);
		frameCount = 0;
		isDraw = true;
		listener = _listener;
	}

	// 设置字体类型
	private void setTypeface(String typeface) {
		if (typeface.equals(TYPE_FACE_ARIAL)) {
			paintText.setTypeface(Typeface.MONOSPACE);
		} else if (typeface.equals(TYPE_FACE_BLACK)) {
			paintText.setTypeface(Typeface.SANS_SERIF);
		}
	}

	// 获取文本移动的速度
	private int getSpeed(int speedtype) {
		switch (speedtype) {
		case MOVE_SLOW:
			mSleep = 40;
			return 1;
		case MOVE_MIDD:
			mSleep = 30;
			return 1;
		case MOVE_FAST:
			mSleep = 22;
			return 1;
		case MOVE_SOFAST:
			mSleep = 35;
			return 2;
		}

		mSleep = 22;
		return 1;
	}

	// 初始化文本的位置
	private void initLocation(int dir) {
		switch (dir) {
		case DIR_UP:
			y = areaHeight;
			break;
		case DIR_DOWN:
			y = -height;
			break;
		case DIR_LEFT:
			x = areaWidth;
			break;
		case DIR_RIGHT:
			x = -length;
			break;
		}
	}

	int frameCount = 0;
	private void drawText(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
		if (isDraw && mHasCreated) {
			canvas.drawPaint(paintBg);
			if (compose == TEXT_HORIZONTAL) {
				drawHText(canvas);
			} else if (compose == TEXT_VERTICAL) {
				drawVText(canvas);
			}
		}
	}

	// 绘制横向文本
	private void drawHText(Canvas c) {
		c.save();
		c.clipRect(0, 0, areaWidth, areaHeight);
		c.drawText(content, x, areaHeight/2 + size/2 - size/5 , paintText);
		c.restore();
	}

	// 绘制纵向文本
	private void drawVText(Canvas c) {
		for (int i = 0; i < textChars.length; i++) {
			c.save();
			c.clipRect(0, 0, areaWidth, areaHeight);
			c.drawText("" + textChars[i], x, y + i * size, paintText);
			c.restore();
		}
	}
	
	void ClearDraw() {  
		Canvas canvas = mHolder.lockCanvas(null);  
		canvas.drawColor(Color.BLACK);// 清除画布  
		mHolder.unlockCanvasAndPost(canvas);  
	}  


	private void moveText() {
		speed = 3;
		switch (moveDirection) {
		case DIR_UP:
			y -= speed;
			if (y + size <= 0) {
				y = areaHeight;
				if(listener != null){
					listener.OnTextOverListener();
				}
			}
			break;
		case DIR_DOWN:
			y += speed;
			if (y >= areaHeight) {
				y = -size;
				if(listener != null){
					listener.OnTextOverListener();
				}
			}
			break;
		case DIR_LEFT:
			x -= speed;
			if (x + length <= 0) {
				x = areaWidth;
				if(listener != null){
					listener.OnTextOverListener();
				}
			}
			break;
		case DIR_RIGHT:
			x += speed;
			if (x >= areaWidth) {
				x = -length;
				if(listener != null){
					listener.OnTextOverListener();
				}
			}
			break;
		default:
			break;
		}
	}

	public void cancel(){
		mRun = false;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		Log.i(TAG, "onDetachedFromWindow");
		mRun = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDetachedFromWindow();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHasCreated = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		mHasCreated = false;
	}
}

interface TextViewListener {
	public void OnTextOverListener();
}

