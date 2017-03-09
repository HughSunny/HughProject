package com.set.ui.list.tableview;

import android.content.Context;
import android.graphics.Rect;


import com.set.R;

import java.util.ArrayList;
import java.util.List;

import set.work.utils.EquipmentInfo;

/**
 * Created by Hugh on 2016/7/4.
 */
public class TableConfig {
    private static final int DEFAULT_SCREEN_HEIGHT = 1600;
    private static final int DEFAULT_SCREEN_WIDTH = 2560;
    private static float DEFAULT_TITLE_TEXT_SIZE = 28 ;
    private static float DEFAULT_ITEM_TEXT_SIZE = 24;
    private float SIZE_TITLE_TXT = DEFAULT_TITLE_TEXT_SIZE;
    private float SIZE_ITME_TXT = DEFAULT_ITEM_TEXT_SIZE;
    public int RES_ROW_ONE = R.color.list_bg_one;
    public int RES_ROW_ANOTHER = R.color.list_bg_another;
    public int RES_ROW_SELECTED = R.color.table_item_focus;
    public int RES_TITLE_BG = R.color.table_title_color;
    public int RES_COLOR_TITLE_TXT = R.color.table_title_text;
    public int RES_COLOR_ITEM_TXT = R.color.black;
    public int RES_COLOR_SELECTED_BG =  R.color.table_title_color;
    public int RES_SEPERATOR = R.color.table_seperator;
    /** 每一行背景颜色list */
    private List<Integer> bgResList = new ArrayList<Integer>();
    /* 每一行txt的颜色*/
    private ArrayList<Integer> txtColorResList = new ArrayList<Integer>();
    private int tableWidth = 0;
    /**
     * 标题栏  四边的padding
     */
    private int[] padding_title = {10, 16, 10, 16};
    /** item  四边的padding */
    private int[] padding_item = {10, 32, 10, 32};
    private Context mContext;
    private List<Integer> columnWeightList = new ArrayList<Integer>();
    /** 是否需要标题栏 */
    public boolean needTitle = true;
    /**是否用listview*/
    public boolean needRecycle = true;
    /**是否用网格*/
    public boolean needGrid = false;

    /* 当是画图的情况下， 一行数据最多几行 linenum //TODO*/
    public int rowLines = 1;

    public TableConfig(Context context) {// 设置默认大小，适应平板端
        mContext = context;
        DEFAULT_TITLE_TEXT_SIZE = mContext.getResources().getDimension(R.dimen.text_nomal_size) + 2;
        DEFAULT_ITEM_TEXT_SIZE = mContext.getResources().getDimension(R.dimen.text_nomal_size);
        float scale = getSizeScale();
        SIZE_TITLE_TXT = (int) (DEFAULT_TITLE_TEXT_SIZE);
        SIZE_ITME_TXT = (int) (DEFAULT_ITEM_TEXT_SIZE);
        padding_title[0] = (int) (padding_title[0] * scale);
        padding_title[1] = (int) (padding_title[1] * scale);
        padding_title[2] = (int) SIZE_TITLE_TXT;
        padding_title[3] = (int) (padding_title[3] * scale);

        padding_item[0] = (int) (padding_item[0] * scale);
        padding_item[1] = (int) (padding_item[1] * scale);
        padding_item[2] = (int) SIZE_TITLE_TXT;
        padding_item[3] = (int) (padding_item[3] * scale);
    }

    /**
     * 为适应多种分辨率，以768*1280为基础进行放大缩小
     */
    private int setSize(float size) {
        Rect screen = new Rect(0, 0, EquipmentInfo.getScreenWidth(), EquipmentInfo.getScreenHeight());// 1280*768
        int screenWidth = screen.width() > screen.height() ? screen.height() : screen.width();
        int screenHeight = screen.width() > screen.height() ? screen.width() : screen.height();
        float ratioWidth = (float) screenWidth / DEFAULT_SCREEN_HEIGHT;
        float ratioHeight = (float) screenHeight / DEFAULT_SCREEN_WIDTH;
        float RATIO = Math.min(ratioWidth, ratioHeight);
        return Math.round(size * RATIO);
    }

    /**
     * 为适应多种分辨率，以768*1280为基础进行放大缩小
     */
    private float getSizeScale() {
        Rect screen = new Rect(0, 0, EquipmentInfo.getScreenWidth(), EquipmentInfo.getScreenHeight());// 1280*768
        int screenWidth = screen.width() > screen.height() ? screen.height() : screen.width();
        int screenHeight = screen.width() > screen.height() ? screen.width() : screen.height();
        float ratioWidth = (float) screenWidth / DEFAULT_SCREEN_HEIGHT;
        float ratioHeight = (float) screenHeight / DEFAULT_SCREEN_WIDTH;
        float RATIO = Math.min(ratioWidth, ratioHeight);
        return RATIO;
    }

    public List<Integer> getColumnWeightList() {
        return columnWeightList;
    }

    public void setColumnWeightList(int[] columnWeightArray) {
        columnWeightList.clear();
        for (int i = 0; i < columnWeightArray.length; i++) {
            columnWeightList.add(columnWeightArray[i]);
        }
    }

    public int getRES_TITLE_BG() {
        return RES_TITLE_BG;
    }

    public void setRES_TITLE_BG(int bg) {
        this.RES_TITLE_BG = bg;
    }

    public float getSIZE_TITLE_TXT() {
        return SIZE_TITLE_TXT;
    }

    public void setSIZE_TITLE_TXT(float SIZE_TITLE_TXT) {
        this.SIZE_TITLE_TXT = setSize(SIZE_TITLE_TXT);
    }

    public float getSIZE_ITME_TXT() {
        return SIZE_ITME_TXT;
    }

    public void setSIZE_ITME_TXT(float SIZE_ITME_TXT) {
        this.SIZE_ITME_TXT = setSize(SIZE_ITME_TXT);
    }

    public int getRES_COLOR_TITLE_TXT() {
        return RES_COLOR_TITLE_TXT;
    }

    public void setRES_COLOR_TITLE_TXT(int RES_COLOR_TITLE_TXT) {
        this.RES_COLOR_TITLE_TXT = RES_COLOR_TITLE_TXT;
    }

    public int getRES_COLOR_ITEM_TXT() {
        return RES_COLOR_ITEM_TXT;
    }

    public void setRES_COLOR_ITEM_TXT(int RES_COLOR_ITEM_TXT) {
        this.RES_COLOR_ITEM_TXT = RES_COLOR_ITEM_TXT;
    }

    public List<Integer> getBgResList() {
        return bgResList;
    }

    public void setBgResList(List<Integer> rowBgList) {
        bgResList = rowBgList;
    }

    public List<Integer> getTxtColorResList() {
        return txtColorResList;
    }

    public void setTxtColorResList(ArrayList<Integer> TextColor_itemList) {
        this.txtColorResList = TextColor_itemList;
    }

    public int getTable_width() {
        return tableWidth;
    }

    public void setTable_width(int table_width) {
        tableWidth = table_width;
    }

    public int[] getPadding_title() {
        return padding_title;
    }

    public void setPadding_title(int[] title_padding) {
        this.padding_title[0] = setSize(title_padding[0]);
        this.padding_title[1] = (int) (this.padding_title[0] * (float) title_padding[1] / title_padding[0]);
        this.padding_title[2] = (int) (this.padding_title[0] * (float) title_padding[2] / title_padding[0]);
        this.padding_title[3] = (int) (this.padding_title[0] * (float) title_padding[3] / title_padding[0]);
    }

    public int[] getPadding_item() {
        return padding_item;
    }

    public void setPadding_item(int[] item_padding) {
        this.padding_item[0] = setSize(item_padding[0]);
        this.padding_item[1] = (int) (this.padding_item[0] * (float) item_padding[1] / item_padding[0]);
        this.padding_item[2] = (int) (this.padding_item[0] * (float) item_padding[2] / item_padding[0]);
        this.padding_item[3] = (int) (this.padding_item[0] * (float) item_padding[3] / item_padding[0]);
    }

}
