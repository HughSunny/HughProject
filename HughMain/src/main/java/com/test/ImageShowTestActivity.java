package com.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.set.R;
import com.set.ui.view.img_show.ImageShowViewPager;
import com.set.ui.view.img_show.TouchImageView;
import com.set.util.SyncImageLoader;

import set.work.utils.BitmapUtil;

/**
 * 历次病历 检查 心电、病理、超声 图片查看
 * @author Hugh
 *
 * 本次病历 废弃废弃废弃废弃废弃
 */
public class ImageShowTestActivity extends Activity implements OnClickListener {
	/**ViewPager*/
	private ViewPager viewPager;
	/** 装点点的ImageView数组*/
	private ImageView[] tips;
	private ImageView back;
	private List<View> viewlist = new ArrayList<View>();
	List<EmrImageBean> imageList = new ArrayList<EmrImageBean>();
	private static final String TEST_URL = "http://img1.3lian.com/img2011/w1/106/85/";
	ViewGroup group;
	private TextView imgName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_show_image);
		findView();
		if (imageList == null || imageList.size() == 0) {
//			imageList.add(new EmrImageBean("http://img1.cache.netease.com/catchpic/7/74/7488E820F21B335E8AF84B018E3D072C.jpg","网络图片实例1"));
//			imageList.add(new EmrImageBean("http://pic.52che.com/beauty/10/12/02/152934187726.jpg","网络图片实例2"));
			imageList.add(new EmrImageBean("http://img.tuku.cn/file_big/201503/31a4399e290f4b24907b0f04bfb68eb6.jpg","网络图片实例3"));
//			imageList.add(new EmrImageBean("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=804998278,3860262771&fm=21&gp=0.jpg","网络图片实例1"));
		}
		// 将点点加入到ViewGroup中
		tips = new ImageView[imageList.size()];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(20, 20);
			params_linear.setMargins(7, 20, 7, 20);
			imageView.setLayoutParams(params_linear);
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			group.addView(imageView);
		}
		if (imageList.size() > 0) {
			imgName.setText(imageList.get(0).getName());
		}
		// 设置Adapter
		viewPager.setAdapter(new ImageAdapter(this, imageList));
		// 设置监听，主要是设置点点的背景
		viewPager.setOnPageChangeListener(pageListener);
		// 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
		// viewPager.setCurrentItem((imageList.size()) * 100);
	}


	private void findView() {
		back = (ImageView) findViewById(R.id.img_back);
		back.setOnClickListener(this);
		imgName = (TextView) findViewById(R.id.text_img_name);
		group = (ViewGroup) findViewById(R.id.viewGroup);
		viewPager = (ImageShowViewPager) findViewById(R.id.viewPager);
	}
	
	
	

	@Override
	public void onClick(View v) {
		if (v == back) {
			this.finish();
		}
		
	}

	private OnPageChangeListener pageListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			int selectItem = arg0 % imageList.size();
			for (int i = 0; i < tips.length; i++) {
				if (i == selectItem) {
					tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
				} else {
					tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
				}
			}
			imgName.setText(imageList.get(selectItem).getName());
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};

	public class ImageAdapter extends PagerAdapter {
		List<EmrImageBean> imageList = new ArrayList<EmrImageBean>();
		private SyncImageLoader loader;
		Context cxt;

		public ImageAdapter(Context context, List<EmrImageBean> _imageList) {
			cxt = context;
			imageList = _imageList;
			loader = new SyncImageLoader();
		}

		@Override
		public int getCount() {
			return imageList.size();
		}
		/** 动态加载数据 */
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			((ImageShowViewPager) container).mCurrentView = ((TouchImageView) ((View)object).findViewById(R.id.full_image));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}
		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(final ViewGroup container, final int position) {
			final View view = LayoutInflater.from(cxt).inflate(R.layout.item_image_detail, null);
			final TouchImageView full_image = (TouchImageView)view.findViewById(R.id.full_image);
			final ProgressBar progress= (ProgressBar)view.findViewById(R.id.progress);
			final TextView retry= (TextView)view.findViewById(R.id.retry);//加载失败

			final String url = imageList.get(position).getUrl();
			final SyncImageLoader.ImageCallback callback =  new SyncImageLoader.ImageCallback() {
				@Override
				public void onLoadComplete(Bitmap imageBitmap, String iconUrl) {
					full_image.setImageBitmap(imageBitmap);
					progress.setVisibility(View.GONE);
					full_image.setVisibility(View.VISIBLE);
//					Log.w(TAG, EquipmentInfo.getScreenHeight() + " ;" +EquipmentInfo.getScreenWidth());
//					Log.w(TAG, container.getHeight() + " ;" +container.getWidth());
//					Log.w(TAG, full_image.getHeight() + " ;" +full_image.getWidth());
				}

				@Override
				public void onLoadingStarted(String iconUrl) {
					progress.setVisibility(View.VISIBLE);
					full_image.setVisibility(View.GONE);
					retry.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingFailed(String iconUrl) {
					progress.setVisibility(View.GONE);
					full_image.setVisibility(View.GONE);
					retry.setVisibility(View.VISIBLE);

				}
			};
//			Bitmap cachedImage = loader.loadDrawable(url,callback);
			Drawable draw1 = cxt.getResources().getDrawable(R.drawable.test);
			Bitmap cachedImage  = BitmapUtil.drawableToBitmap(draw1);
			if(cachedImage != null) {
				progress.setVisibility(View.GONE);
				full_image.setVisibility(View.VISIBLE);
			}
			full_image.setImageBitmap(cachedImage);
			retry.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					loader.loadDrawable(url,callback);
				}
			});
			((ViewPager) container).addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}



	class EmrImageBean implements Serializable {
		public EmrImageBean(String url,String name) {
			setUrl(url);
			setName(name);
		}

		private String url;
		private String name;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
