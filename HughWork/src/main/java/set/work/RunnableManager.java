package set.work;

import java.util.Vector;

import set.work.bean.RunnableBean;
import set.work.thread.BaseRequestRunnable;
import set.work.thread.BaseRunnable;
import set.work.thread.RequestBack;
import set.work.utils.LogUtil;


/**
 * 线程通过这个管理器管理
 * @author Hugh
 *f
 */
public class RunnableManager {
	private static final String TAG = "RunnableManager";
	public static final int MAX_CONNECTIONS = 5;
	private Vector<BaseRequestRunnable> active = new Vector<BaseRequestRunnable>();
	private Vector<BaseRequestRunnable> queue = new Vector<BaseRequestRunnable>();
	private Vector<RunnableBean> queueList = new Vector<RunnableBean>();
	//队列管理
	private static RunnableManager instance;

	public static RunnableManager getInstance() {
		if (instance == null)
			instance = new RunnableManager();
		return instance;
	}

	public void push(BaseRequestRunnable runnable) {
		queue.add(runnable);
		if (active.size() < MAX_CONNECTIONS)
			startNext();
	}

	private void startNext() {
		if (!queue.isEmpty()) {
			BaseRequestRunnable next = queue.get(0);
			queue.remove(0);
			if (next != null) {
				active.add(next);
				startThread(next);
			}
		}
	}

	public void clearAll() {
		LogUtil.logW(TAG,"clearAll");
		for (int i = 0; i < active.size(); i++) {
			active.get(i).stopRunnable();
		}
		active.clear();
		for (int i = 0; i < queue.size(); i++) {
			queue.get(i).stopRunnable();
		}
		queue.clear();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * 结束上一个，进行下一个请求
	 *
	 * @param runnable
	 * @return 如果已经被销毁了
	 */
	public boolean didComplete(Runnable runnable) {
		boolean ds = active.remove(runnable);
		runnable = null;
		startNext();
		return ds;
	}

	//-----------------------------------------
	private void startThread(BaseRunnable next) {
		Thread thread = new Thread(next);
		next.setActive(true);
		thread.start();
	}
	//-----------------新版管理器------------------------

	/**
	 * 立马执行runnable
	 * @param back
	 * @param runnable
     */
	public void start(RequestBack back, BaseRunnable runnable){
		RunnableBean item = add2Queue(back,runnable);
		startThread(runnable);
	}

	/**
	 * 加入队列，并按照顺序执行
	 * @param back
	 * @param runnable
     */
	public void push(RequestBack back, BaseRunnable runnable) {
		push(back, runnable, true);
	}

	/**
	 * 加入队列，并按照顺序执行
	 * @param back
	 * @param runnable
	 * @param isSync 是否同步或者异步
     */
	public void push(RequestBack back, BaseRunnable runnable,boolean isSync) {
		LogUtil.logW(TAG,"push");
		RunnableBean item = add2Queue(back,runnable);
		if (active.size() < MAX_CONNECTIONS)
			startNextRunnable(item);
	}

	/**
	 * 加入队列
	 * @param back
	 * @param runnable
	 */
	private RunnableBean add2Queue(RequestBack back, BaseRunnable runnable){
		int i = 0;
		RunnableBean item = null;
		for( i = 0; i< queueList.size() ;i++){
			if(queueList.get(i).getBack() == back ){
				item = queueList.get(i);
				item.addRunnable(runnable);
				break;
			}
		}
		if (item == null) {
			item = new RunnableBean(back,runnable);
			queueList.add(item);
		}
//		LogUtil.logW(TAG,"queueList.size == " + queueList.size());
		return item;
	}

	/**
	 * 开始下一个请求
	 * @param item 当前页面的网络请求数组对象
     */
	private void startNextRunnable(RunnableBean item) {
//		LogUtil.logW(TAG,"startNextRunnable item is " + (item == null?"null":"not null" ));
		if (queueList.isEmpty()) {
			LogUtil.logW(TAG,"startNextRunnable ---------------- > queueList == null" );
			return;
		}
		if (item == null) { //自动下一个
			//如果没有item，那么当前页面的请求已经结束了
//			for (int i = 0; i < queueList.size(); i++) {
//				RunnableBean temp = queueList.get(i);
//				BaseRunnable tempRun = temp.getNextRunable();
//				if (tempRun != null) {
//					startThread(tempRun);
//					break;
//				}
//			}
		} else { //指定一组下一个
			BaseRunnable runnable = item.getNextRunable();
			if (runnable != null && !runnable.isActive()) {
				runnable.setActive(true);
				startThread(runnable);
			}
		}
	}

	/**
	 * 结束上一个，进行下一个请求
	 * @param back
	 * @param runnable
	 * @return 如果已经被销毁了
	 */
	public boolean runnableDone(RequestBack back ,BaseRunnable runnable) {
		LogUtil.logW(TAG,"runnableDone");
		boolean ds = false;
		RunnableBean item = null;
		for( int i = 0; i< queueList.size() ;i++){
			if(queueList.get(i).getBack() == back ){
				item = queueList.get(i);
				ds = item.removeRunnable(runnable);
				if (item.isEmpty()) {
					queueList.remove(item);
					item = null;
				}
				break;
			}
		}
		runnable = null;
		startNextRunnable(item);
		return ds;
	}

	/**
	 * 清理当前的页面的请求
	 * @param back 当前页面的回调
     */
	public void clearCallBack(RequestBack back) {
		LogUtil.logW(TAG,"clearCallBack back is" + (back==null?"null":"not null"));
		if (back == null) {
			for (int i = 0; i < queueList.size(); i++) {
				queueList.get(i).stopAll();
			}
			queueList.clear();
			return;
		}
		for (int i = 0; i < queueList.size(); i++) {
			if (queueList.get(i).getBack() == back) {
				queueList.get(i).stopAll();
			}
		}
	}
}
