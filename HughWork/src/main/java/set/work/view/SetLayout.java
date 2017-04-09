package set.work.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by Hugh on 2017/3/13.
 * 实例，不能继承 ViewGroup
 */

public class SetLayout extends ViewGroup implements ViewTreeObserver.OnGlobalLayoutListener {
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected Resources mResources;
    public SetLayout(Context context) {
        super(context);
        init(context);
    }

    public SetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = context.getResources();
    }
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }
    @Override
    public void onGlobalLayout() {

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

}
