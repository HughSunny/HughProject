package com.set.ui.list.menu_list;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * 菜单项
 */
public final class ListMenuItem {
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = -1;

    public final int width;
    public final String text;
    public final int textSize;
    public final int textColor;
    public final Drawable icon;
    public final Drawable background;
    public final int direction;

    public int margin = 0;

    public ListMenuItem(int width, String text, int textSize, int textColor, Drawable icon, Drawable background, int direction) {
        this.width = width;
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.icon = icon;
        this.background = background;
        this.direction = direction;
    }

    public ListMenuItem(int width, String text, int textSize, int textColor, Drawable icon, Drawable background, int direction, int margin) {
        this.width = width;
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.icon = icon;
        this.background = background;
        this.direction = direction;
        this.margin = margin;
    }
    public static class Builder {
        private int width = 50;
        private String text = null;
        private int textSize = 14;
        private int textColor = 0xff000000;
        private Drawable icon = null;
        private Drawable background = new ColorDrawable(0xffffffff);
        private int direction = ListMenuItem.DIRECTION_LEFT;
        private int margin = 0;
        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setMargin(int margin) {
            this.margin = margin;
            return this;
        }
        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder setBackground(Drawable background) {
            this.background = background;
            return this;
        }

        public Builder setDirection(int direction) {
            this.direction = direction;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public String getText() {
            return text;
        }

        public int getTextSize() {
            return textSize;
        }

        public int getTextColor() {
            return textColor;
        }

        public Drawable getIcon() {
            return icon;
        }

        public Drawable getBackground() {
            return background;
        }

        public int getDirection() {
            return direction;
        }

        public ListMenuItem build() {
            return new ListMenuItem(width, text, textSize, textColor, icon, background, direction, margin);
        }
    }

}
