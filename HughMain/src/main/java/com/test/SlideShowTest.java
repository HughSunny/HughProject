package com.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.set.R;
import com.set.bean.SlideItemBean;
import com.set.ui.view.SlideShowView;

public class SlideShowTest extends Activity {
	SlideShowView  slideView;
	private List<SlideItemBean>  slideDatas = new ArrayList<SlideItemBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_test_slide_show);
		slideView = (SlideShowView) findViewById(R.id.slideshowView);
		SlideItemBean bean = new SlideItemBean();
		bean.img = "http://image.zcool.com.cn/56/35/1303967876491.jpg";
		bean.title = "";
		bean.url = "";
		slideDatas.add(bean);
		bean = new SlideItemBean();
		bean.img = "http://image.zcool.com.cn/59/54/m_1303967870670.jpg";
		bean.title = "";
		bean.url = "";
		slideDatas.add(bean);
		bean = new SlideItemBean();
		bean.img = "http://image.zcool.com.cn/47/19/1280115949992.jpg";
		bean.title = "";
		bean.url = "";
		slideDatas.add(bean);
		bean = new SlideItemBean();
		bean.img = "http://image.zcool.com.cn/59/11/m_1303967844788.jpg";
		bean.title = "";
		bean.url = "";
		slideDatas.add(bean);
		slideView.setSlideData(slideDatas);
	}
}
