package com.imbos.chat.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadPool {
	private static ThreadPool threadPool = new ThreadPool();
	private ExecutorService  executor ;
	private ThreadPool(){
		executor = Executors.newFixedThreadPool(5);
	}
	public static void submit(Runnable runnable){
		threadPool.executor.submit(runnable);
	}

}
