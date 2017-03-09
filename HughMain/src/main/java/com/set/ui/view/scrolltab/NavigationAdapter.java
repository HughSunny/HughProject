package com.set.ui.view.scrolltab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class NavigationAdapter extends CacheFragmentStatePagerAdapter {
	List<String> titleList = new ArrayList<String>();
	protected LinkedHashMap<String, Fragment> fragmentmap;
	public List<Fragment> fragmentlist = new ArrayList<Fragment>();
	public NavigationAdapter(FragmentManager fm, LinkedHashMap<String, Fragment> fragmentmap) {
		super(fm);
		if (fragmentmap != null) {
			Iterator iter = fragmentmap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				titleList.add(key);
				fragmentlist.add(fragmentmap.get(key));
			}
		}
	}

	@Override
	protected Fragment createItem(int position) {
		Fragment f = fragmentlist.get(position);
		return f;
	}

	@Override
	public int getCount() {
		return fragmentlist.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titleList.get(position);
	}
}
