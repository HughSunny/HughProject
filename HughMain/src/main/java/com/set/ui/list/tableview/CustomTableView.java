package com.set.ui.list.tableview;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import com.set.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import set.work.utils.android.LogUtil;

/**
 * Created by Hugh on 2016/7/4.
 * 自定义表格页面
 * 支持横向滑动listview
 */
public class CustomTableView extends LinearLayout {
    private static final String TAG = "CustomTableView";
    private int totalHeight;
    private int totalWidth;
    private Context context;

    private  AdapterView.OnItemClickListener listener;
    private String[] heads;
    private TableConfig config;
    private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
    private HorizontalScrollView horScrollView;
    public CustomTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public AdapterView.OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }
    /**
     * 设置宽高
     * @param width
     * @param height
     */
    public void setDisplay(int width, int height) {
        LogUtil.logW(TAG, "setDisplay width == " + width + "  height == " +  height);
        totalWidth = width;
        totalHeight = height;
    }

    /**
     * 画表
     * @param heads
     * @param list
     * @param bean
     */
    public void drawTable(String[] heads, List<Map<String, String>> list, TableConfig bean) {
        this.config = bean;
        if (this.getChildCount() > 0)
            reset();
        if (heads == null) {
            if(list == null || list.size() == 0){
                return;
            }
            heads = generateHeads(list);
            this.heads = heads;
        } else {
            this.heads = heads;
        }
        if (list == null) {
            dataList.clear();
        } else {
            dataList.addAll(list);
        }
        LinearLayout lin_all = new LinearLayout(context);
        lin_all.setOrientation(LinearLayout.VERTICAL);
        // 存放每个字段值最大长度的Map
        Map<String, Integer> lengthMap = new HashMap<String, Integer>();
        List<Integer> columnWeightList = bean.getColumnWeightList();
        if (columnWeightList != null && !columnWeightList.contains(0) && columnWeightList.size() == heads.length) {
            if(columnWeightList.size() == heads.length){
                int weightSum = 0;
                for (int i = 0; i < columnWeightList.size(); i++) {
                    weightSum = weightSum + columnWeightList.get(i);
                }
                for (int i = 0; i < heads.length; i++) {
                    lengthMap.put(heads[i],totalWidth * columnWeightList.get(i) / weightSum);
                }
            } else {
                LogUtil.Error(TAG, "THE 'columnWeightList' CONFIG IS NOT CORRECT");
            }
        } else {
            TextPaint tpaint = new TextPaint();
            tpaint.setTextSize(bean.getSIZE_ITME_TXT());
            for (int i = 0; i < list.size(); i++) {// 找出每一列最长的字段
                for (Map.Entry<String, String> entry : list.get(i).entrySet()) {
                    String key = (String) entry.getKey();
                    int textLen = (int) tpaint.measureText(entry.getValue());
                    if (!lengthMap.containsKey(key) || textLen > lengthMap.get(key)) {
                        lengthMap.put(key, textLen);
                    }
                }
            }
            int sumLength = 0;
            TextPaint tpaint_title = new TextPaint();
            tpaint_title.setTextSize(bean.getSIZE_TITLE_TXT());
            for (int i = 0; i < heads.length; i++) {// 与标题比较，找出每列最长的字段
                int[] padding = bean.getPadding_title();
                int[] padding_item = bean.getPadding_item();
                int maxLength = 0;
                if (lengthMap.containsKey(heads[i]))
                    maxLength = lengthMap.get(heads[i]) + padding_item[0] + padding_item[2];
                int headLength = (int) tpaint_title.measureText(heads[i]) + padding[0] + padding[2];
                if (headLength > maxLength) {// 标题长度大于内容长度时，按标题长度
                    lengthMap.put(heads[i], headLength);
                    sumLength = sumLength + headLength;
                } else {
                    lengthMap.put(heads[i], maxLength);
                    sumLength = sumLength + maxLength;
                }
            }
            if (sumLength < bean.getTable_width()) {// 表格总长度小于屏幕宽度时，铺满全屏
                int dis = (bean.getTable_width() - sumLength) / heads.length;
                for (int i = 0; i < heads.length; i++) {
                    lengthMap.put(heads[i], lengthMap.get(heads[i]) + dis);
                }
            }
        }

        if (!drawTableTitle(lin_all,lengthMap)) {
            return;
        }
        // 默认设置每行颜色

        List<Integer> bgResList = bean.getBgResList();
        if (bgResList.size() == 0 || bgResList.size() != list.size()) {
            bgResList.clear();
            for (int i = 0; i < list.size(); i++) {
                if (i % 2 == 0) {
                    bgResList.add(bean.RES_ROW_ONE);
                } else {
                    bgResList.add(bean.RES_ROW_ANOTHER);
                }
            }
        }
        List<Integer> txtColorResList = bean.getTxtColorResList();
        if (txtColorResList.size() == 0 || txtColorResList.size() != list.size() ) {
            txtColorResList.clear();
            for (int i = 0; i < list.size(); i++) {
                txtColorResList.add(R.color.black);
            }
        }

        if(bean.needRecycle) {
            TableListContent listView = new TableListContent(context,null);
            lin_all.addView(listView);
            addView(lin_all,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            listView.setOnItemClickListener(listener);
            listView.refreshData(list, heads, lengthMap, bean);
        } else {
            TableCanvasContent tbv = new TableCanvasContent(context, list, heads, lengthMap, bean);
            tbv.listener = this.listener;
            ScrollView scrollview = new ScrollView(context);
            scrollview.addView(tbv);
            lin_all.addView(scrollview);
            horScrollView = new HorizontalScrollView(context);
            horScrollView.setHorizontalScrollBarEnabled(false);
//            horScrollView.setFillViewport(true);
            horScrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            horScrollView.addView(lin_all);
            addView(horScrollView);
        }
    }

    /**
     * 根据数据生成头
     * @param list
     * @return
     */
    private String[] generateHeads(List<Map<String, String>> list) {
        Map<String, String> map = list.get(0);
        String[] heads = new String[map.size()];
        int i = 0;
        // 遍历获取显示的字段数 和字段名称
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            heads[i] = key;
            i++;
        }
        return heads;
    }

    /**
     * 画table的头
     * @param lin_all
     * @param lengthMap
     * @return
     */
    private boolean drawTableTitle(LinearLayout lin_all, Map<String, Integer> lengthMap) {
        if(config != null && config.needTitle){
            TableCanvasTitle tbt = new TableCanvasTitle(context, heads, lengthMap, config);// 标题View
            lin_all.addView(tbt);
        }
        if (dataList.size() == 0) {
            this.addView(lin_all);
            return false;
        }
        return true;
    }

    private void reset(){
        this.removeAllViews();
    }
    /**
     * 重新控件大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        LogUtil.logW(TAG, "onMeasure" );
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
//        LogUtil.logW(TAG, "width == " + width + "  height == " +  height);
        totalWidth = width;
        totalHeight = height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
//        Log.w(TAG, "measureWidth :" + result);
        return result;
    }

    protected int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
//        Log.w(TAG, "measureHeight :" + result);
        return result;
    }

}
