package com.set.model;

public class ImageViewModel {
	public int iconFocus;
	public int iconNormal;
	public float iconWidth;
	public float iconheight;

	public ImageViewModel(int iconNormal)
	{
		this.iconNormal = iconNormal;
	}

	public ImageViewModel(int iconNormal, int iconFocus)
	{
		this.iconNormal = iconNormal;
		this.iconFocus = iconFocus;
	}

	public ImageViewModel(int iconNormal, int paramInt2, int paramInt3)
	{
		this.iconNormal = iconNormal;
		this.iconWidth = paramInt2;
		this.iconheight = paramInt3;
	}

	public ImageViewModel(int iconNormal, int iconFocus, float paramInt3, float paramInt4)
	{
		this.iconNormal = iconNormal;
		this.iconFocus = iconFocus;
		this.iconWidth = paramInt3;
		this.iconheight = paramInt4;
	}

	public void setImage(int iconNormal, int iconFocus)
	{
		this.iconNormal = iconNormal;
		this.iconFocus = iconFocus;
	}

	public void setImage(int iconNormal, int iconFocus, float paramInt3, float paramInt4)
	{
		this.iconNormal = iconNormal;
		this.iconFocus = iconFocus;
		this.iconWidth = paramInt3;
		this.iconheight = paramInt4;
	}
}
