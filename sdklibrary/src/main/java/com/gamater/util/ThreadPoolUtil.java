package com.gamater.util;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工具类
 * 
 */
public class ThreadPoolUtil {

	// 正常CPU核心数
	private static final int NORMAL_CPU_SIZE = 4;
	// 最少CPU核心数
	private static final int MIN_CPU_SIZE = 2;

	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	// 线程池核心线程大小
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	// 线程池核心线程最大大小
	private static final int MAXIMUM_POOL_SIZE = 6;

	private static final int KEEP_ALIVE = 1;

	// 如果CPU核心数大于等于4个或者小于等于2个，则设置核心线程数为CPU核心数+1个(如果核心线程数超过6个，则设置核心线程数最多为6个)，否则设置核心线程数为3个
	private static int POOL_SIZE = (NORMAL_CPU_SIZE <= CPU_COUNT || MIN_CPU_SIZE >= CPU_COUNT) ? (CPU_COUNT + 1 > MAXIMUM_POOL_SIZE ? MAXIMUM_POOL_SIZE
			: CPU_COUNT + 1) : CORE_POOL_SIZE;
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "ThreadPoolExecutor #" + mCount.getAndIncrement());
		}
	};
	/**
	 * 用于处理零散任务的线程池
	 */
	// public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new
	// ThreadPoolExecutor(POOL_SIZE,
	// MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
	// sThreadFactory);

	public static final ExecutorService THREAD_POOL_EXECUTOR = Executors.newCachedThreadPool(sThreadFactory);

	/**
	 * 主要用于需要线性执行的任务队列
	 */
	public static class SerialExecutor implements Executor {
		final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
		Runnable mActive;

		private static SerialExecutor mInstance;
		public static SerialExecutor getInstance(){
			if(mInstance == null){
				mInstance = new SerialExecutor();
			}
			return mInstance;
		}
		public synchronized void execute(final Runnable r) {
			mTasks.offer(new Runnable() {
				public void run() {
					try {
						r.run();
					} finally {
						scheduleNext();
					}
				}
			});
			if (mActive == null) {
				scheduleNext();
			}
		}

		protected synchronized void scheduleNext() {
			if ((mActive = mTasks.poll()) != null) {
				THREAD_POOL_EXECUTOR.execute(mActive);
			}
		}
	}

}
