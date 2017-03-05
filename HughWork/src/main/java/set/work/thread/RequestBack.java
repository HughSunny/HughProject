package set.work.thread;

import set.work.bean.ResultBean;

public interface RequestBack {
	/**
	 * 预处理 request result
	 * @param result
	 * @return false就是不需要进行下一步了
	 */
	public boolean onPreRequestBack(ResultBean result);
	/**
	 * 处理 request result
	 * @param result
	 */
	public void onRequestBack(ResultBean result);


	// public void onProgress(int progress);

}
