package set.work.utils;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

/**
 * 作用&目的：组件管�?作�?：王晓峰 编写日期�?013-08-15
 */
public class ActivityCollector {

	private static final String TAG = "ActivityCollector";
	// 组件集合
	public static ArrayList<Activity> activitys = new ArrayList<Activity>();

	/**
	 * 注册
	 * 
	 * @param activity
	 *            组件
	 */
	public static void registry(Activity activity) {
		activitys.add(activity);
	}

	/**
	 * 说明：释放
	 */
	public static void release() {
		for (int i = 0, size = activitys.size(); i < size; i++)
			activitys.get(i).finish();
		activitys.clear();
	}

	/**
	 * 说明：释放指定类别的Activity
	 */
	public static void releaseActivity(Class<?> cls) {
		Activity activity;
		for (int i = activitys.size() - 1; i >= 0; i--) {
			activity = activitys.get(i);
			if (activity == null) {
				continue;
			}
			if (activity.getClass() == cls) {
				if (!activity.isFinishing()) {
					activity.finish();
				}
			}
		}
	}

	/**
	 * 说明：检查是否有类别的Activity存在内存
	 */
	public static boolean hasActivity(Class<?> cls) {
		Activity activity;
		for (int i = activitys.size() - 1; i >= 0; i--) {
			activity = activitys.get(i);
			if (activity == null) {
				continue;
			}
			if (activity.getClass() == cls) {
				Log.i(TAG, "");
				return true;
			}
		}
		return false;
	}

	/**
	 * 说明：释放至主界�?
	 */
	public static void releaseToMain() {
		for (int i = 3, size = activitys.size(); i < size; i++) {
			activitys.get(i).finish();
		}
		for (int j = 3; j < activitys.size(); j++) {
			activitys.remove(j);
		}
		// activitys.clear();
	}

	/**
	 * 说明：释�?
	 */
	public static void releaseWelcome() {
		for (int i = 0; i < 1; i++) {
			activitys.get(i).finish();
			break;
		}
	}

	/**
	 * 说明：注�?
	 */
	public static void deRegistry(Activity activity) {
		if (activity != null) {
			activitys.remove(activity);
		}
	}

	/**
	 * 说明：取得组�?
	 */
	public static Activity getActivity(Integer id) {
		if (id == null)
			id = activitys.size() - 2;
		if (id > activitys.size() - 1 || id < 0)
			return null;
		return activitys.get(id);
	}

}
