package set.work.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Hugh on 2016/3/17.
 * 自定义View
 */
public class SetView extends View {
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected Resources mResources;
    public SetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = context.getResources();
    }



}
