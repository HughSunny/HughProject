package com.test;

import com.set.R;
import com.set.R.id;
import com.set.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * ClassName:DirectShowActivity
 * Function: TODO ADD FUNCTION
 *
 * @author   wl
 * @version  
 * @Date	 2015-3-12		下午3:53:35
 */
public class DirectShowActivity extends Activity implements OnClickListener{
	ProgressDialog dialog ;
	Button btn ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(new CustomDatePickerView(this, null));
		setContentView(R.layout.activity_test_show);
		btn = (Button)findViewById(R.id.show_btn);
		btn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		dialog = new ProgressDialog(this);
		dialog.setTitle("oncreate 显示");
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		new Thread(ticker).run();
//		new Thread(ticker).start();
//		Toast.makeText(this, "SSDFDFDFDFDFD====", Toast.LENGTH_LONG).show();
		handler.sendEmptyMessage(0);
		Toast.makeText(this, "end====", Toast.LENGTH_LONG).show();
	}
	
	
	
	Handler handler;
	private Runnable ticker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			for (int i = 0; i < 3; i++) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 0) {
						System.out.println("j = " + msg.arg1);
						// txt.setText(msg.arg1 + "");
						Toast.makeText(DirectShowActivity.this, "SSDFDFDFDFDFD====", Toast.LENGTH_LONG).show();
					}
				}
			};
			// 启动Looper
			Looper.loop();
			
//			Looper.prepare();
//			Looper.getMainLooper();
//			Looper.loop();
//			for (int i = 0; i < 3; i++) {
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
	};


	
}

