package set.work.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by Hugh on 2017/3/6.
 * UI 工具
 */

public class UIUtil {
    private static final String TAG = "UIUtil";
    private static ProgressDialog progress;

    public static void showDialog(Context context, String message) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoginterface, int i) {
                                return;
                            }
                        }).show();
    }

    /**
     * 显示不能点掉的progress
     * @param context
     */
    public static void showProgress(Context context,String title, boolean cancelable){
        progress = new ProgressDialog(context);
        if(title != null){
            progress.setTitle(title);
        }
        progress.setCanceledOnTouchOutside(cancelable);
        progress.setCancelable(cancelable);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                Log.i(TAG, "User cancel the progress");
            }
        });
        progress.show();
    }

    public static void dismissProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        progress = null;
    }
    /**
     * 设置背景图片
     * @param view
     * @param drawable
     */
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        Log.w(TAG, "==================> listData local height  =" + listView.getHeight() );
        // 获取ListView对应的Adapter
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
        Log.w(TAG, "================== > listData height  =" + totalHeight );
    }
}
