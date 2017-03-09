package com.set.ui.list.tableview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用画布画出listview
 */
public class TableCanvasContent extends View {
    private List<Map<String, String>> list;
    private String[] items;
    private Map<String, Integer> itemLength;
    private List<Integer> textcolorList;
    private float lengthSum = 0, heightSum, heightOne;
    private TextPaint tpaint;
    private Paint linePaint, rectPaint;
    private int[] padding_item;
    private Rect rect = new Rect();
    private float mDownX;
    private float mDownY;
    private int touchSlop;
    public AdapterView.OnItemClickListener listener;
    private List<Integer> background_items = new ArrayList<Integer>();
    private TableConfig tableConfig;
    private int lastIndex = -1, lastColor;
    public TableCanvasContent(Context context, List<Map<String, String>> list,
                              String[] items, Map<String, Integer> itemLength,
                              TableConfig bean) {
        super(context);
        this.list = list;
        this.items = items;
        tableConfig = bean;
        this.itemLength = itemLength;
        background_items = bean.getBgResList();
        for (int i = 0; i < items.length; i++) {
            lengthSum = lengthSum + itemLength.get(items[i]);
        }
        textcolorList = bean.getTxtColorResList();
        padding_item = bean.getPadding_item();
        heightOne = bean.getSIZE_ITME_TXT() + padding_item[1] + padding_item[3];
        heightSum = list.size() * heightOne;
        tpaint = new TextPaint();
        tpaint.setTextSize(bean.getSIZE_ITME_TXT());
        tpaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(bean.RES_SEPERATOR));
        tpaint.getTextBounds("输入法", 0, 2, rect);
        rectPaint = new Paint();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)lengthSum, (int)heightSum);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < list.size(); i++) {
            rectPaint.setColor(getResources().getColor(background_items.get(i)));
            canvas.drawRect(0, i * heightOne, lengthSum, (i + 1) * heightOne, rectPaint);
//            if (tableConfig.needGrid)
            canvas.drawLine(0, i * heightOne, lengthSum, i * heightOne, linePaint);
            int lastWidth = padding_item[0];
            float curHeight = i * heightOne + padding_item[1];
            tpaint.setColor(getResources().getColor(textcolorList.get(i)));
            for (int j = 0; j < items.length; j++) {
                String text = list.get(i).get(items[j]);
                canvas.drawText(text, lastWidth - rect.left, curHeight - rect.top,
                        tpaint);
                lastWidth = lastWidth + itemLength.get(items[j]);
            }
        }
        if (tableConfig.needGrid) {
            int lastWidth = 0;
            for (int i = 0; i < items.length; i++) {
                canvas.drawLine(lastWidth, 0, lastWidth, heightSum, linePaint);
                lastWidth = lastWidth + itemLength.get(items[i]);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int index = (int) (mDownY / heightOne);
                    if (listener != null) {
                        listener.onItemClick(null,null,index,0);
                    }
                    if (lastIndex != -1)
                        background_items.set(lastIndex, lastColor);
                    lastColor = background_items.get(index);
                    background_items.set(index,tableConfig.RES_ROW_SELECTED);
                    lastIndex = index;
                    invalidate();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnTableRowClickListener {
        public void OnRowClick(int position);
    }
}
