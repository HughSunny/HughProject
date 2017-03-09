package com.set.ui.list.tableview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.set.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import set.work.utils.DensityUtil;

/**
 * Created by Hugh on 2016/7/4.
 */
public class TableListContent extends ListView {
    private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
    private String[] items;
    private TableConfig config;
    private Context mContext;
    private Map<String, Integer> itemLength;
    private List<Integer> textcolorList;
    private float lengthSum = 0, heightOne;
    private TextPaint tpaint;
    private Paint linePaint, rectPaint;
    private int[] padding_item;
    private Rect rect = new Rect();
    private List<Integer> background_items = new ArrayList<Integer>();
    private int paddingH;
    public TableListContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setSelector(R.color.transparent);
        setDivider(null);
    }

    public void refreshData(List<Map<String, String>> list, String[] items, Map<String, Integer> itemLength, TableConfig bean) {
        dataList = list;
        this.items = items;
        config = bean;
        this.itemLength = itemLength;
        background_items = bean.getBgResList();
        for (int i = 0; i < items.length; i++) {
            lengthSum = lengthSum + itemLength.get(items[i]);
        }
        textcolorList = bean.getTxtColorResList();
        padding_item = bean.getPadding_item();
        heightOne = bean.getSIZE_ITME_TXT() + padding_item[1] + padding_item[3];
        tpaint = new TextPaint();
        tpaint.setTextSize(bean.getSIZE_ITME_TXT());
        tpaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(bean.RES_SEPERATOR));
        tpaint.getTextBounds("输入法", 0, 2, rect);
        paddingH = padding_item[0] - rect.left;
        rectPaint = new Paint();
        if (!bean.needGrid) {
            setDivider(mContext.getResources().getDrawable(bean.RES_SEPERATOR));
            setDividerHeight(1);
        }
        CustomListAdapter adapter = new CustomListAdapter();
        setAdapter(adapter);
    }

    class CustomListAdapter extends BaseAdapter {
        public CustomListAdapter() {

        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

        int selected = -1;

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (!config.needGrid) {
                ViewHolder tag;
                if (convertView == null) {
                    tag = new ViewHolder();
                    convertView = new LinearLayout(mContext, null);
                    ((LinearLayout)convertView).setOrientation(LinearLayout.HORIZONTAL);
                    convertView.setLayoutParams(new LayoutParams((int)lengthSum, (int) heightOne));
                    tag.contentLayout = (LinearLayout) convertView;
                    for (int i = 0; i <items.length ; i++) {
                        TextView textView = new TextView(mContext,null);
                        textView.setSingleLine(false);
                        textView.setMaxLines(2);//config.rowLines
                        textView.setTextSize(DensityUtil.px2sp(config.getSIZE_ITME_TXT()));
                        textView.setTextColor(getResources().getColor(config.getRES_COLOR_ITEM_TXT()));
                        textView.setGravity(Gravity.CENTER_VERTICAL);
                        textView.setLayoutParams(new LayoutParams(itemLength.get(items[i]),(int)heightOne));
                        textView.setPadding(paddingH, paddingH, paddingH, paddingH);
                        ((LinearLayout)convertView).addView(textView);
                        tag.viewList.add(textView);
                    }
                    convertView.setTag(tag);
                } else {
                    tag = (ViewHolder)convertView.getTag();
                }
                Map<String, String> dataMap = dataList.get(position);

                for (int i = 0; i <items.length ; i++) {
                    TextView textview = tag.viewList.get(i);
                    textview.setText(dataMap.get(items[i]));
                    textview.setGravity(Gravity.CENTER_VERTICAL);
//                    textview.invalidate();
                }
                if (position == selected ) {
                    convertView.setBackgroundResource(config.RES_COLOR_SELECTED_BG);
                } else {
                    convertView.setBackgroundResource(background_items.get(position));
                }
            } else {
                if (convertView == null) {
                    convertView = new CustomListItem(mContext, null);
                    convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) heightOne));
                }
                ((CustomListItem) convertView).setItem(dataList.get(position), position);
            }
            return convertView;
        }
    }

    class ViewHolder {
        LinearLayout contentLayout;
        List<TextView> viewList = new ArrayList<TextView>();
    }

    class CustomListItem extends View {
        private Map<String, String> dataMap = new LinkedHashMap<>();
        private int position = 0;

        public CustomListItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void setItem(Map<String, String> map, int _position) {
            dataMap = map;
            position = _position;
            if (dataMap != null) {
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            rectPaint.setColor(getResources().getColor(background_items.get(position)));
            canvas.drawRect(0, 0, lengthSum, heightOne, rectPaint);
            canvas.drawLine(0, 0, lengthSum, 0, linePaint);
            tpaint.setColor(getResources().getColor(textcolorList.get(position)));
            int lastWidth = padding_item[0];
            for (int j = 0; j < items.length; j++) {
                String text = dataMap.get(items[j]);
                canvas.drawText(text, lastWidth - rect.left, padding_item[1] - rect.top,
                        tpaint);
                lastWidth = lastWidth + itemLength.get(items[j]);
            }
            if (config.needGrid) {
                lastWidth = 0;
                for (int i = 0; i < items.length; i++) {
                    canvas.drawLine(lastWidth, 0, lastWidth, heightOne, linePaint);
                    lastWidth = lastWidth + itemLength.get(items[i]);
                }
            }
            super.onDraw(canvas);
        }
    }

}
