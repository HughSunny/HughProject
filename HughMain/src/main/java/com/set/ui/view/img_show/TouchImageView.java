package com.set.ui.view.img_show;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;

import set.work.utils.LogUtil;


/**
 * ImageView
 */
@SuppressLint("NewApi")
public class TouchImageView extends ImageView {
    private static final String TAG = "TouchImageView";
    // These matrices will be used to move and calcScale image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    static final long DOUBLE_PRESS_INTERVAL = 600;
    static final float FRICTION = 0.9f;

    // We can be in one of these 4 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 10;
    int mode = NONE;

    //图片的宽高
    private float bmWidth, bmHeight;
    //页面的宽高
    private float width, height;

    //图片自适应填充之后 的宽和高
    float origWidth, origHeight;

    //图片自适应填充之后 宽高的边距
    float redundantXSpace, redundantYSpace;

    //计算后的右侧和底部
    float right, bottom ;

    PointF last = new PointF();
    PointF mid = new PointF();
    PointF start = new PointF();

    PointF focusP = new PointF();
    float[] matrixArray;
    float matrixX, matrixY;

    float saveScale = 1f;
    float minScale = 1f;
    float maxScale = 3f;
    float oldDist = 1f;

    PointF lastDelta = new PointF(0, 0);
    float velocity = 0;

    long lastPressTime = 0, lastDragTime = 0;
    //在操作的时候不能绘画
    boolean allowInert = false;

    private Context mContext;
    private Timer mClickTimer;
    private OnClickListener mOnClickListener;
    private Object mScaleDetector;
    private Handler mTimerHandler = null;
    VelocityTracker mVelocityTracker;
    private boolean isWidthMainScale = false;
    // Scale mode on DoubleTap
    private boolean zoomToOriginalSize = false;


    public boolean onLeftSide = false, onTopSide = false, onRightSide = false,
            onBottomSide = false;

    public TouchImageView(Context context) {
        super(context);
        super.setClickable(true);
        this.mContext = context;

        init();
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setClickable(true);
        this.mContext = context;

        init();
    }

    protected void init() {
        mTimerHandler = new TimeHandler(this);
        matrix.setTranslate(1f, 1f);
        matrixArray = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        if (Build.VERSION.SDK_INT >= 8) {
            mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        }
        setOnTouchListener(touchListener);
    }


    private OnTouchListener touchListener =  new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent rawEvent) {
            WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
            if (mScaleDetector != null) {
                ((ScaleGestureDetector) mScaleDetector).onTouchEvent(rawEvent);
            }
            fillMatrixXY();
            PointF curr = new PointF(event.getX(), event.getY());
            int actionid = event.getAction() & MotionEvent.ACTION_MASK;
//            LogUtil.logW(TAG, "onTouch  actionid = " + actionid + "; mode = " + mode);
            if (mVelocityTracker == null)
                mVelocityTracker = VelocityTracker.obtain();
            mVelocityTracker.addMovement(rawEvent);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    allowInert = false;
                    savedMatrix.set(matrix);
                    last.set(event.getX(), event.getY());
                    start.set(last);
                    mode = DRAG;

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    // Log.d(TAG, "oldDist=" + oldDist);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        // Log.d(TAG, "mode=ZOOM");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    allowInert = true;
                    mode = NONE;
                    int xDiff = (int) Math.abs(event.getX() - start.x);
                    int yDiff = (int) Math.abs(event.getY() - start.y);
                    if (xDiff < CLICK && yDiff < CLICK) {
                        long pressTime = System.currentTimeMillis();
                        if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {//双击事件
                            if (mClickTimer != null)
                                mClickTimer.cancel();
                            if (saveScale == 1) {
                                final float targetScale = maxScale / saveScale;
                                matrix.postScale(targetScale, targetScale,
                                        start.x, start.y);
                                saveScale = maxScale;
                            } else {
                                matrix.postScale(minScale / saveScale, minScale
                                        / saveScale, width / 2, height / 2);
                                saveScale = minScale;
                            }
                            calcPadding();
                            checkAndSetTranslate(0, 0);
                            lastPressTime = 0;
                        } else {//不是双击事件
                            lastPressTime = pressTime;
                            mClickTimer = new Timer();
                            mClickTimer.schedule(new Task(), 300);
                        }
                        if (saveScale == minScale) {
                            scaleMatrixToBounds();
                        }
                    }

                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (mode == ZOOM) {
                        mVelocityTracker.computeCurrentVelocity(1000);
                        int velocityX = (int) mVelocityTracker.getXVelocity();
                        int velocityY = (int) mVelocityTracker.getYVelocity();
                        int velocity = velocityY > velocityX ? velocityY:velocityX;
                        float destScale = -1;
                        if (saveScale > maxScale) {
                            destScale = maxScale;
                        } else if (saveScale < minScale) {
                            destScale = minScale;
                        }
                        if (destScale != -1) {
                            computeReset(saveScale,destScale);
                        }
                    }
                    mode = NONE;
                    velocity = 0;
                    savedMatrix.set(matrix);
                    oldDist = spacing(event);
                    // Log.d(TAG, "mode=NONE");
                    break;

                case MotionEvent.ACTION_MOVE:
                    allowInert = false;
                    if (mode == DRAG) {
                        float deltaX = curr.x - last.x;
                        float deltaY = curr.y - last.y;

                        long dragTime = System.currentTimeMillis();

                        velocity = (float) distanceBetween(curr, last)
                                / (dragTime - lastDragTime) * FRICTION;
                        lastDragTime = dragTime;

                        checkAndSetTranslate(deltaX, deltaY);
                        lastDelta.set(deltaX, deltaY);
                        last.set(curr.x, curr.y);
                    } else if (mScaleDetector == null && mode == ZOOM) {

                        float newDist = spacing(event);
                        if (rawEvent.getPointerCount() < 2)
                            break;
                        if (10 > Math.abs(oldDist - newDist)
                                || Math.abs(oldDist - newDist) > 100)
                            break;
                        float mScaleFactor = newDist / oldDist;
                        oldDist = newDist;
                        //LogUtil.logW(TAG, "onTouch  ZOOM  mScaleFactor = " + mScaleFactor + "; saveScale = " + saveScale);

                        focusP = midPointF(event);
                        calcScale(mScaleFactor, midPointF(event));
                    }
                    break;
            }

            setImageMatrix(matrix);
            invalidate();
            return false;
        }
    };



    @SuppressLint("NewApi")
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = (float) Math.min(Math.max(.95f, detector.getScaleFactor()), 1.05);
//            LogUtil.logW(TAG, "onScale  mScaleFactor = " + mScaleFactor);
            focusP = new PointF(detector.getFocusX(), detector.getFocusY());
            calcScale(mScaleFactor, focusP);
            return true;

        }
    }

    /**
     * 根据 放大缩小因素 进行放大缩小计算
     * @param mScaleFactor
     * @param mid
     */
    private void calcScale(float mScaleFactor, PointF mid) {
        float origScale = saveScale;
        saveScale *= mScaleFactor;
//        if (saveScale > maxScale) {
//            saveScale = maxScale;
//            mScaleFactor = maxScale / origScale;
//        } else if (saveScale < minScale) {
//            saveScale = minScale;
//            mScaleFactor = minScale / origScale;
//        }

        calcPadding();
        if (origWidth * saveScale <= width || origHeight * saveScale <= height) {//图片在显示范围之内
            LogUtil.logW(TAG, "calcScale  ZOOM  <SMALL> mScaleFactor = " + mScaleFactor );
            matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
            fillMatrixXY();
//            if (mScaleFactor < 1) {
//                float translateX = 0;
//                if (Math.round(origWidth * saveScale) < width) {
//                    if (matrixX < -right)
//                        translateX = -(matrixX + right);
////                        matrix.postTranslate(-(matrixX + right), 0);
//                    else if (matrixX > 0)
//                        translateX = -matrixX;
////                        matrix.postTranslate(-matrixX, 0);
//                }
//
//
//                float translateY = 0;
//                if (Math.round(origHeight * saveScale) < height) {
//                    if (matrixY < -bottom)
//                        translateY = -(matrixY + bottom);
////                        matrix.postTranslate(0, -(matrixY + bottom));
//                    else if (matrixY > 0)
//                        translateY = -matrixY;
////                        matrix.postTranslate(0, -matrixY);
//                }
//                matrix.postTranslate(translateX/2, translateY/2);
//            }
        } else {
            LogUtil.logW(TAG, "calcScale  ZOOM <LARGE> mScaleFactor = " + mScaleFactor );
            matrix.postScale(mScaleFactor, mScaleFactor, mid.x,
                    mid.y);
            fillMatrixXY();
            if (mScaleFactor < 1) {
                if (matrixX < -right)
                    matrix.postTranslate(-(matrixX + right), 0);
                else if (matrixX > 0)
                    matrix.postTranslate(-matrixX, 0);
                if (matrixY < -bottom)
                    matrix.postTranslate(0, -(matrixY + bottom));
                else if (matrixY > 0)
                    matrix.postTranslate(0, -matrixY);
            }
        }
        //TODO 新添加
        checkSiding();
    }

    float computeScale = -1;

    /**
     * 阻尼效果
     * @param nowScale
     * @param destScale
     */
    public void computeReset(float nowScale, final float destScale) {
        computeScale = -1;
        ValueAnimator animator;// 动画器
        animator = ValueAnimator.ofFloat(nowScale, destScale);// 动画更新的监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float nowScale = (Float) valueAnimator.getAnimatedValue();
                LogUtil.logW(TAG, "ValueAnimator onAnimationUpdate nowScale ===> " + nowScale);

                if (computeScale != -1) {
                    float mScaleFactor = nowScale/computeScale;
                    if (destScale == maxScale) {
                        calcScale(mScaleFactor, focusP);
                    } else {
                        calcScale(mScaleFactor, new PointF(width/2,height/2));
                    }
                }

                computeScale = nowScale;
                if (destScale == nowScale  && nowScale == minScale) {
                    checkAndSetTranslate(0, 0);
                }
                setImageMatrix(matrix);
                invalidate();
            }
        });
        animator.setDuration(50);// 动画时间
        animator.start();// 开启动画
    }


    public void resetScale() {
        fillMatrixXY();
        matrix.postScale(minScale / saveScale, minScale / saveScale, width / 2,
                height / 2);
        saveScale = minScale;

        calcPadding();
        checkAndSetTranslate(0, 0);

        scaleMatrixToBounds();

        setImageMatrix(matrix);
        invalidate();
    }

    public boolean pagerCanScroll() {
        if (mode != NONE)
            return false;
        return saveScale == minScale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!allowInert)
            return;

        final float deltaX = lastDelta.x * velocity, deltaY = lastDelta.y
                * velocity;
        LogUtil.logW(TAG, "onDraw --- deltaX = " + deltaX + " ； deltaY =  " + deltaY);

        if (deltaX > width || deltaY > height) {
            return;
        }
        velocity *= FRICTION;
        if (Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1)
            return;
        checkAndSetTranslate(deltaX, deltaY);
        setImageMatrix(matrix);
    }

    /**
     * 将图片拖拽
     * @param deltaX
     * @param deltaY
     */
    private void checkAndSetTranslate(float deltaX, float deltaY) {
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        fillMatrixXY();
        if (scaleWidth < width) {
            deltaX = 0;
            if (matrixY + deltaY > 0)
                deltaY = -matrixY;
            else if (matrixY + deltaY < -bottom)
                deltaY = -(matrixY + bottom);
        } else if (scaleHeight < height) {
            deltaY = 0;
            if (matrixX + deltaX > 0)
                deltaX = -matrixX;
            else if (matrixX + deltaX < -right)
                deltaX = -(matrixX + right);
        } else {
            if (matrixX + deltaX > 0)
                deltaX = -matrixX;
            else if (matrixX + deltaX < -right)
                deltaX = -(matrixX + right);

            if (matrixY + deltaY > 0)
                deltaY = -matrixY;
            else if (matrixY + deltaY < -bottom)
                deltaY = -(matrixY + bottom);
        }
        matrix.postTranslate(deltaX, deltaY);
        checkSiding();
    }

    private void checkSiding() {
        fillMatrixXY();
        Log.d(TAG, "matrixX: " + matrixX + " matrixY: " + matrixY + " left: " + right / 2 + " top:" + bottom / 2);
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        onLeftSide = onRightSide = onTopSide = onBottomSide = false;
        if (-matrixX < 10.0f)
            onLeftSide = true;
        // Log.d("GalleryViewPager",
        // String.format("ScaleW: %f; W: %f, MatrixX: %f", scaleWidth, width,
        // matrixX));
        if ((scaleWidth >= width && (matrixX + scaleWidth - width) < 10)
                || (scaleWidth <= width && -matrixX + scaleWidth <= width))
            onRightSide = true;
        if (-matrixY < 10.0f)
            onTopSide = true;
        if (Math.abs(-matrixY + height - scaleHeight) < 10.0f)
            onBottomSide = true;
    }

    /**
     * 计算边距
     */
    private void calcPadding() {
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
    }

    /**
     * 计算出MatrixXY
     */
    private void fillMatrixXY() {
        matrix.getValues(matrixArray);
        matrixX = matrixArray[Matrix.MTRANS_X];
        matrixY = matrixArray[Matrix.MTRANS_Y];
    }


    private void scaleMatrixToBounds() {
        if (Math.abs(matrixX + right / 2) > 0.5f)
            matrix.postTranslate(-(matrixX + right / 2), 0);
        if (Math.abs(matrixY + bottom / 2) > 0.5f)
            matrix.postTranslate(0, -(matrixY + bottom / 2));

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if(bm == null) {
            return;
        }
        bmWidth = bm.getWidth();
        bmHeight = bm.getHeight();
    }

//	public void setImageDrawable(android.graphics.drawable.Drawable drawable) {
//		super.setImageDrawable(drawable);
//		if (drawable == null) {
//			return;
//		}
//		Bitmap bitmap = drawable2Bitmap(drawable);
//		bmWidth = bitmap.getWidth();
//		bmHeight = bitmap.getHeight();
//		Log.w(TAG,"bmWidth = " +  bmWidth + "; bmHeight;" + bmHeight);
//	};
//

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        Log.w(TAG, "onMeasure :" +  " ; width: " + width +" ; height:"+ height + "; bmWidth :" + bmWidth );
        // Fit to screen.
        float scale;
        float scaleX = width / bmWidth;
        float scaleY = height / bmHeight;

        if (scaleX > scaleY) {
            isWidthMainScale = true;
        } else {
            isWidthMainScale = false;
        }
        scale = Math.min(scaleX, scaleY);
        matrix.setScale(scale, scale);
        saveScale = 1f;

        // Center the image
        redundantYSpace = height - (scale * bmHeight);
        redundantXSpace = width - (scale * bmWidth);
        redundantYSpace /= (float) 2;
        redundantXSpace /= (float) 2;
        matrix.postTranslate(redundantXSpace, redundantYSpace);

        origWidth = width - 2 * redundantXSpace;
        origHeight = height - 2 * redundantYSpace;
        calcPadding();
        setImageMatrix(matrix);
    }

    private double distanceBetween(PointF left, PointF right) {
        return Math.sqrt(Math.pow(left.x - right.x, 2)
                + Math.pow(left.y - right.y, 2));
    }

    /** Determine the space between the first two fingers */
    private float spacing(WrapMotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, WrapMotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private PointF midPointF(WrapMotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    private class Task extends TimerTask {
        public void run() {
            mTimerHandler.sendEmptyMessage(0);
        }
    }



    static class TimeHandler extends Handler {
        private final WeakReference<TouchImageView> mService;

        TimeHandler(TouchImageView view) {
            mService = new WeakReference<TouchImageView>(view);

        }

        @Override
        public void handleMessage(Message msg) {
            mService.get().performClick();
            if (mService.get().mOnClickListener != null)
                mService.get().mOnClickListener.onClick(mService.get());
        }
    }

    public boolean isZoomToOriginalSize() {
        return this.zoomToOriginalSize;
    }

    public void setZoomToOriginalSize(boolean zoomToOriginalSize) {
        this.zoomToOriginalSize = zoomToOriginalSize;
    }

}

