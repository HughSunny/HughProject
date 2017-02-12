package com.test.fragment;

import com.set.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BottomFragment extends Fragment {

	private static final String TEXT_CHAT = "CHAT";

	private static final String TAG = "BottomFragment";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	/**
     * Factory method to generate a new instance of the fragment given a string .
     *
     * @param char 主页面要传过来的信息
     * @return A new instance of MyFragment with chat extras
     */
    public static BottomFragment newInstance(String chat) {
        final BottomFragment f = new BottomFragment();
        final Bundle args = new Bundle();
        args.putString(TEXT_CHAT, chat);
        f.setArguments(args);
        return f;
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bottom_fragment_layout, container, false);
		TextView tv = (TextView) view.findViewById(R.id.bottom_text);
		String str = getArguments() != null ? getArguments().getString(
				TEXT_CHAT) : null;
		if (str != null) {
			tv.setText(str);
		} else {
			tv.setText("获取字段出错了，求指导");
		}
		return view;
	}
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
    }
}
