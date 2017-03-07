package com.set.ui.list.menu_list;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单
 */
public final class ListMenu {
    public static final int ITEM_NOTHING = 0;
    public static final int ITEM_SCROLL_BACK = 1;
    public static final int ITEM_DELETE_FROM_BOTTOM_TO_TOP = 2;

    private List<ListMenuItem> mLeftMenuItems;
    private List<ListMenuItem> mRightMenuItems;

    private boolean mWannaOver = true;
    private boolean mWannaTransparentWhileDragging = true;

    private int mMenuViewType = 0;

    public ListMenu(boolean wannaTransparentWhileDragging) {
        this(wannaTransparentWhileDragging, true);
    }

    public ListMenu(boolean wannaTransparentWhileDragging, boolean wannaOver) {
        this(wannaTransparentWhileDragging, wannaOver, 0);
    }

    public ListMenu(boolean wannaTransparentWhileDragging, boolean wannaOver, int menuViewType) {
        mWannaTransparentWhileDragging = wannaTransparentWhileDragging;
        mWannaOver = wannaOver;
        mLeftMenuItems = new ArrayList<>();
        mRightMenuItems = new ArrayList<>();
        mMenuViewType = menuViewType;
    }

    protected boolean isWannaOver() {
        return mWannaOver;
    }

    protected boolean isWannaTransparentWhileDragging() {
        return mWannaTransparentWhileDragging;
    }

    public void addItem(ListMenuItem menuItem) {
        if (menuItem.direction == ListMenuItem.DIRECTION_LEFT) {
            mLeftMenuItems.add(menuItem);
        } else {
            mRightMenuItems.add(menuItem);
        }
    }

    public void addItem(ListMenuItem menuItem, int position) {
        if (menuItem.direction == ListMenuItem.DIRECTION_LEFT) {
            mLeftMenuItems.add(position, menuItem);
        } else {
            mRightMenuItems.add(position, menuItem);
        }
    }

    public boolean removeItem(ListMenuItem menuItem) {
        if (menuItem.direction == ListMenuItem.DIRECTION_LEFT) {
            return mLeftMenuItems.remove(menuItem);
        } else {
            return mRightMenuItems.remove(menuItem);
        }
    }

    protected int getTotalBtnLength(int direction) {
        int total = 0;
        if (direction == ListMenuItem.DIRECTION_LEFT) {
            for (ListMenuItem menuItem : mLeftMenuItems) {
                total += menuItem.width;
            }
            return total;
        } else {
            for (ListMenuItem menuItem : mRightMenuItems) {
                total += menuItem.width;
            }
            return total;
        }
    }

    /**
     * 这个函数并不是十分安全，因为获取到List之后自己操作add或者remove的话btn总长度不会有操作变化
     *
     * @param direction
     * @return
     */
    protected List<ListMenuItem> getMenuItems(int direction) {
        if (direction == ListMenuItem.DIRECTION_LEFT) {
            return mLeftMenuItems;
        } else {
            return mRightMenuItems;
        }
    }

    public int getMenuViewType() {
        return mMenuViewType;
    }
}
