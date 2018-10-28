package set.work.view.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Hugh on 2017/4/10.
 * RecyclerView 工具类
 */

public class RecyclerViewUtil {

    public static void moveToPosition(RecyclerView mRecyclerView, int n) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager)layoutManager;
            //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
            int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
            //然后区分情况
            if (n <= firstItem ){
                //当要置顶的项在当前显示的第一个项的前面时
                mRecyclerView.scrollToPosition(n);
            }else if ( n <= lastItem ){
                //当要置顶的项已经在屏幕上显示时
                int top = mRecyclerView.getChildAt(n - firstItem).getTop();
                mRecyclerView.scrollBy(0, top);
            }else{
                //当要置顶的项在当前显示的最后一项的后面时
                mRecyclerView.scrollToPosition(n);
                //这里这个变量是用在RecyclerView滚动监听里面的
            }
        }
    }

    public interface RecyclerItemClickListener {
        void onItemClick(RecyclerView.Adapter adapter, View view, int position);
    }
}
