package com.set.ui.list.tableview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import java.util.Map;

public class TableCanvasTitle extends View {
    private float lengthSum = 0, heightSum;
    private String[] items;
    private Map<String, Integer> itemLength;
    private int background_title;
    private int[] padding_title;
    private Paint linePaint;
    private TextPaint tpaint;
    private Rect r = new Rect();
    private TableConfig config;
    public TableCanvasTitle(Context context, String[] items,
                            Map<String, Integer> itemLength, TableConfig bean) {
        super(context);
        this.items = items;
        config = bean;
        this.itemLength = itemLength;
        background_title = bean.getRES_TITLE_BG();
        padding_title = bean.getPadding_title();
        for (int i = 0; i < items.length; i++) {
            lengthSum = lengthSum + itemLength.get(items[i]);
        }
        heightSum = bean.getSIZE_TITLE_TXT() + padding_title[1] + padding_title[3];

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(bean.RES_SEPERATOR));

        tpaint = new TextPaint();
        tpaint.setColor(getResources().getColor(bean.getRES_COLOR_TITLE_TXT()));
        tpaint.setTextSize(bean.getSIZE_TITLE_TXT());
        tpaint.setAntiAlias(true);
        tpaint.getTextBounds("治疗项目", 0, 3, r);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) lengthSum, (int) heightSum);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lastWidth = 0;
        for (int i = 0; i < items.length; i++) {
            if(config.needGrid) {
                canvas.drawLine(lastWidth, 0, lastWidth, heightSum, linePaint);
            }
            canvas.drawText(items[i], lastWidth + padding_title[0] - r.left, padding_title[1] - r.top,
                    tpaint);
            lastWidth = lastWidth + itemLength.get(items[i]);
        }
        this.setBackgroundResource(background_title);
    }
}
