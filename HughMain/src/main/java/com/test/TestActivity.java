package com.test;

import android.app.Activity;
import android.os.Bundle;

import com.set.R;
import com.set.ui.view.Lock9View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends Activity{

    @BindView(R.id.lock_9_view)
    protected Lock9View lock9View;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockView();
    }

    /**
     * 测试 九点解锁
     */
    private void lockView() {
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }
}
