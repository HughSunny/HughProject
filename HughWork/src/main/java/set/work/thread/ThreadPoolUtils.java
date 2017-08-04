package set.work.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hugh on 2017/6/5.
 * 线程池控制线程
 */

public class ThreadPoolUtils {

    private ThreadPoolUtils() {}
    //核心线程数
    private static int CORE_POOL_SIZE = 8;
    //最大线程数
    private static int MAX_POOL_SIZE = 32;
    //线程池中超过corePoolSize数目的空闲线程最大存活时间；可以allowCoreThreadTimeOut(true)使得核心线程有效时间
    private static int KEEP_ALIVE_TIME = 5;
    //任务队列
    private static BlockingQueue<Runnable> mWorkQueue = new ArrayBlockingQueue<>(64);

    private static ThreadPoolExecutor mThreadpool;

    static {
        mThreadpool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, mWorkQueue);
    }

    public static void push(BaseRunnable runnable) {
        mThreadpool.execute(runnable);
    }
}
