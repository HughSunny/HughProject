package com.set.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class MyVideoView extends VideoView {
    public static int WIDTH;
    public static int HEIGHT;
    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(WIDTH, widthMeasureSpec);
        int height = getDefaultSize(HEIGHT, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}