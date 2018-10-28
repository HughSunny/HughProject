package com.test;

import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.Button;
import com.test.list.SwapeRecyclerTest;
import com.set.R;

/**
 * ClassName:AndroidFirstTest
 * Function: TODO ADD FUNCTION
 *
 * @author   wl
 * @version  
 * @Date	 2015-4-13		下午4:11:41
 */
public class AndroidFirstTest extends InstrumentationTestCase {
    SwapeRecyclerTest sample = null;
	View button;
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        try {
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClassName("com.set", SwapeRecyclerTest.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sample = (SwapeRecyclerTest) getInstrumentation().startActivitySync(intent);
        button = (Button) sample.findViewById(R.id.test_button);;
	}
	
	
	 /*
     * 模拟按钮点击的接口
     */
    private class PerformClick implements Runnable {
        Button btn;
        public PerformClick(Button button) {
            btn = button;
        }
        
        public void run() {
            btn.performClick();
        }
    }
    
    @Override
	protected void runTest() throws Throwable {
		super.runTest();
		
		
		
	}

	@Override
	protected void tearDown() throws Exception {
		sample.finish();
		super.tearDown();
		
	}
}

