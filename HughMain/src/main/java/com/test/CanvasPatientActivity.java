package com.test;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class CanvasPatientActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// CanvasPatientLevelView view = new CanvasPatientLevelView(this, null);
		CanvasRefreshView view = new CanvasRefreshView(this, null);
		view.setImageResource(R.drawable.ic_dialog_info);
		LayoutParams layouParm = new LayoutParams(290, 400);
		view.setLayoutParams(layouParm);
		setContentView(view, layouParm);
	}

	class CanvasRefreshView extends ImageView {
		private Paint paint;
		private Paint textPaint;
		private Paint circlePaint;
		private int progress = -1;
		/**
		 * 默认100
		 */
		private int max = 100;
		private boolean isLoading = true;
		LinearGradient mShader;
		private int COLOR_START = Color.parseColor("#30C977");
		private int COLOR_MIDDLE = Color.parseColor("#31ACB3");
		private int COLOR_END = Color.parseColor("#0A8B90");
		private int textColor = Color.parseColor("#1FB5AC");
		public CanvasRefreshView(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint = new Paint();
			paint.setColor(Color.LTGRAY); // 设置圆环的颜色
			paint.setStyle(Paint.Style.STROKE); // 设置空心
			paint.setAntiAlias(true); // 消除锯齿
			circlePaint = new Paint();
			circlePaint.setColor(Color.GREEN); // 设置圆环的颜色
			circlePaint.setStyle(Paint.Style.STROKE); // 设置空心
			circlePaint.setAntiAlias(true); // 消除锯齿

			textPaint = new Paint();
			textPaint.setStrokeWidth(0);
			textPaint.setColor(textColor);
			textPaint.setTypeface(Typeface.DEFAULT_BOLD);
			textPaint.setAntiAlias(true);
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			if (!isLoading) {
				return;
			}
			canvas.drawARGB(255, 255, 255, 255);
			// 画最外层的大圆环
			int totalWidth = getWidth();
			int totalHeight = getHeight();

			int centreX = totalWidth / 2;// 获取圆心的x坐标
			int centreY = totalHeight / 2;// 获取圆心的Y坐标
			float radius = 0;
			// 画最外层的大圆环宽度
			float roundWidth;
			if (totalWidth > totalHeight) {
				roundWidth = centreY / 6;
				radius = centreY - roundWidth / 2;
			} else {
				roundWidth = centreX / 6;
				radius = centreX - roundWidth / 2;
			}

			RectF oval = new RectF(centreX - radius, centreY - radius, centreX + radius, centreY + radius);

			paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
			canvas.drawCircle(centreX, centreY, radius, paint); // 画出圆环
			mShader = new LinearGradient(totalWidth, 0, totalWidth, totalWidth, new int[] { COLOR_START, COLOR_MIDDLE },
					null, Shader.TileMode.CLAMP);
			circlePaint.setShader(mShader);
			circlePaint.setStrokeWidth(roundWidth);
			float circlePrg = 360 * progress / max;
			if (circlePrg > 360) {
				circlePrg = 360;
			}
			if (circlePrg > 180) {
				canvas.drawArc(oval, -90, 180, false, circlePaint); // 根据进度画圆弧
				mShader = new LinearGradient(totalWidth, 0, totalWidth, totalWidth,
						new int[] { COLOR_END, COLOR_MIDDLE }, null, Shader.TileMode.CLAMP);
				circlePaint.setShader(mShader);
				canvas.drawArc(oval, 89, circlePrg - 180, false, circlePaint);
			} else {
				canvas.drawArc(oval, -90, circlePrg, false, circlePaint);
			}
			/**
			 * 画进度百分比
			 */
			String percent = (int) (((float) progress / (float) max) * 100) + "%";
			float textSize = radius * 4 / 7;
			textPaint.setTextSize(textSize);
			float textWidth = textPaint.measureText(percent);
			canvas.drawText(percent, centreX - textWidth / 2, centreY + textSize / 3, textPaint); // 画出进度百分比
		}

		public void setProgress(int progress) {
			this.progress = progress;
			if (progress > 0) {
				isLoading = true;
			} else if (progress >= max) {
				isLoading = false;
			} else {
				isLoading = false;
			}
			invalidate();
		}
		
		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}

	}

	class CanvasPatientLevelView extends View {
		private int rectColor = Color.RED;
		private int boardColor = Color.parseColor("#C52724");
		Paint rectPaint;
		Paint boardPaint;
		private int totalH;
		String text = "一级";

		public CanvasPatientLevelView(Context context, AttributeSet attrs) {
			super(context, attrs);
			rectPaint = new Paint();
			rectPaint.setColor(rectColor);
			rectPaint.setStyle(Style.FILL);
			boardPaint = new Paint();
			boardPaint.setAntiAlias(true);
			boardPaint.setColor(boardColor);
			boardPaint.setStyle(Style.FILL);
		}

		public void setInfo(int rectColor, int boardColor, String text) {
			this.rectColor = rectColor;
			this.boardColor = boardColor;
			this.text = text;
			invalidate();
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			int totalH = getMeasuredHeight();
			int totalW = getMeasuredWidth();
			float rectLength = (float) (totalW * 0.46);
			float start = (float) (totalW * 0.46);
			double rectH = rectLength / Math.sqrt(2);
			double rectW = start * Math.sqrt(2);
			double boardH = totalH / 15;
			double boardS = totalH / 20;
			canvas.save();
			canvas.translate(0, start);
			canvas.rotate(-45);
			Path path = new Path();
			path.moveTo(0, 0);
			path.lineTo((int) rectW, 0);
			path.lineTo((int) (rectW + rectH), (int) (rectH));
			path.lineTo((int) (-rectH), (int) (rectH));
			path.close();
			canvas.drawPath(path, rectPaint);

			Path path1 = new Path();
			path1.moveTo((int) (rectW + rectH), (int) (rectH));
			path1.quadTo((int) (rectW + rectH + boardH / 3), (int) (rectH + boardH / 3), (int) (rectW + rectH),
					(int) (rectH + boardH));
			// path1.lineTo((int) (rectW + rectH), (int) (rectH + boardH));
			path1.lineTo((int) (rectW + rectH - boardH), (int) (rectH));
			path1.close();
			canvas.drawPath(path1, boardPaint);

			Path path2 = new Path();
			path2.moveTo((int) (-rectH), (int) (rectH));
			path2.quadTo((int) (-rectH - boardS / 3), (int) (rectH + boardS / 3), (int) (-rectH),
					(int) (rectH + boardS));
			// path2.lineTo((int) (-rectH), (int) (rectH + boardS));
			path2.lineTo((int) (-rectH + boardS), (int) (rectH));
			path2.close();
			canvas.drawPath(path2, boardPaint);
			drawHText(canvas, 0, 0, (int) rectW, (int) rectH, text, (int) (rectH * 4 / 5));
			canvas.restore();
		}
	}

	// 绘制单元格中的文字
	protected void drawHText(Canvas canvas, float cellX, float cellY, float cellBX, float cellBY, String text,
			int fontsize) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setShadowLayer(3, 2, 2, Color.parseColor("#666666"));
		paint.setTextSize(fontsize);
		float textWidth = paint.measureText(text) + 1;
		float textX = cellX + (cellBX - cellX - textWidth) / 2;// ;
		float textY = cellBY - (cellBY - cellY - fontsize) / 2 - fontsize / 10;
		canvas.drawText(text, textX, textY, paint);
	}
}
