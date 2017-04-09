package set.work.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Hugh on 2017/3/13.
 */

public class TextViewUtil {

    /**
     * 设置 textsize
     * @param textView
     * @param resources
     * @param dimenId
     */
    public static void setTextSize(TextView textView, Resources resources, int dimenId) {
        if (textView == null) {
            return;
        }
        int textSize = resources.getDimensionPixelSize(dimenId);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }
}
