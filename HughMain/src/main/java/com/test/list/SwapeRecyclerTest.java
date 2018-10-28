package com.test.list;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.set.R;

import java.util.ArrayList;
import java.util.List;

import set.work.view.recyclerview_swipe.Closeable;
import set.work.view.recyclerview_swipe.OnSwipeMenuItemClickListener;
import set.work.view.recyclerview_swipe.SwipeMenu;
import set.work.view.recyclerview_swipe.SwipeMenuAdapter;
import set.work.view.recyclerview_swipe.SwipeMenuCreator;
import set.work.view.recyclerview_swipe.SwipeMenuItem;
import set.work.view.recyclerview_swipe.SwipeMenuRecyclerView;

/**
 * Created by Hugh on 2017/6/29.
 */

public class SwapeRecyclerTest extends Activity {
    private SwipeMenuRecyclerView mMenuRecyclerView;
    private Activity mContext;
    private MenuAdapter mMenuAdapter;
    private List<String> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_swipe_recycler);
        mContext = this;
        mDataList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mDataList.add("我是第" + i + "个。");
        }
        mMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.swipe_recycler_view);

        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。

        // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
        // 设置菜单创建器。
        mMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        mMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(mDataList);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        mMenuRecyclerView.setAdapter(mMenuAdapter);
    }


    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
//            int width = getResources().getDimensionPixelSize(R.dimen.item_height);
//
//            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//
//            // 添加左侧的，如果不添加，则左侧不会出现菜单。
//            {
//                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
//                        .setImage(R.drawable.ic_action_add) // 图标。
//                        .setWidth(width) // 宽度。
//                        .setHeight(height); // 高度。
//                swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_red)
//                        .setImage(R.drawable.ic_action_close)
//                        .setWidth(width)
//                        .setHeight(height);
//
//                swipeLeftMenu.addMenuItem(closeItem); // 添加一个按钮到左侧菜单。
//            }
//
//            // 添加右侧的，如果不添加，则右侧不会出现菜单。
//            {
//                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_red)
//                        .setImage(R.drawable.ic_action_delete)
//                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_purple)
//                        .setImage(R.drawable.ic_action_close)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
//
//                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_green)
//                        .setText("添加")
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到右侧菜单。
//            }

            int width = 100;
            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                    .setBackgroundDrawable(new ColorDrawable(Color.RED))
                    .setImage(R.drawable.ic_action_close)
                    .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                    .setTextSize(10)
                    .setWidth(width) // 宽度。
                    .setHeight(height)// 高度。
                    .setTextColor(Color.WHITE);
            swipeLeftMenu.addMenuItem(deleteItem);
        }
    };

    /**
     * Item点击监听。
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(mContext, "我是第" + position + "条。", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView
         *                        #RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(mContext, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(mContext, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {// 删除按钮被点击。
                mDataList.remove(adapterPosition);
                mMenuAdapter.notifyItemRemoved(adapterPosition);
            }
        }
    };

    class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

        private List<String> titles;

        private AdapterView.OnItemClickListener mOnItemClickListener;

        public MenuAdapter(List<String> titles) {
            this.titles = titles;
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return titles == null ? 0 : titles.size();
        }

        @Override
        public View onCreateContentView(ViewGroup parent, int viewType) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        }

        @Override
        public MenuAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
            DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
            viewHolder.mOnItemClickListener = mOnItemClickListener;
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MenuAdapter.DefaultViewHolder holder, int position) {
            holder.setData(titles.get(position));
        }

        class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvTitle;
            AdapterView.OnItemClickListener mOnItemClickListener;

            public DefaultViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            }

            public void setData(String title) {
                this.tvTitle.setText(title);
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(null, v, getAdapterPosition(), 0l);
                }
            }
        }

    }


}



