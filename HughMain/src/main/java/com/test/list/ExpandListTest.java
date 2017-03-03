package com.test.list;

import java.util.ArrayList;

import com.set.R;
import com.set.ui.list.UIExpandableListView;
import com.set.ui.list.adapter.UIExpandableListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class ExpandListTest extends Activity {
	UIExpandableListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listview = new UIExpandableListView(this, null);
		listview.initHeaderView(R.layout.item_expand_group, 80);
		setContentView(listview,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		ArrayList<String> list = new ArrayList<String>();
		list.add("section one");
		list.add("section two");
		list.add("section three");
		list.add("section four");
		list.add("section five");
		ArrayList<ArrayList<String>> alllist = new ArrayList<ArrayList<String>>();
		ArrayList<String> templist;
		for (int i = 0; i < list.size(); i++) {
			templist = new ArrayList<String>();
			templist.add(list.get(i) + " one");
			templist.add(list.get(i) + " two");
			templist.add(list.get(i) + " three");
			templist.add(list.get(i) + " four");
			alllist.add(templist);
		}
		UIExpandableListAdapter adapter = new UIExpandableListAdapter(this, listview, list, alllist);
		listview.setAdapter(adapter);
		listview.expandGroup(0);
	}

}
