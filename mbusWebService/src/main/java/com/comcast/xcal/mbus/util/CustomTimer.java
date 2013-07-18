package com.comcast.xcal.mbus.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of scheduler for the reaper.
 * Is used for own implementation of message reaper.
 *
 */
public class CustomTimer {
	
	// To have only one instace.
	private static class SingletonHolder {
		private final static CustomTimer instance = new CustomTimer();
	}
	
	// Default constructor
	private CustomTimer(){
		
	}
	
	/**
	 * Static method, to return singleton instance of timer.
	 * 
	 * @return CustomTimer
	 */
	public static CustomTimer getInstance(){
		return SingletonHolder.instance;
	}
	
	
	private final ScheduledExecutorService scheduler = 
	       Executors.newScheduledThreadPool(50);
	
	/**
	 * Wrapper on ScheduledExecutorService to create task and process
	 * it periodically.
	 * 
	 * @param r - runnable task to be executed
	 * @param timeout - the time from now to delay execution 
	 * @param timeUnit - the time unit of the delay parameter
	 */
	public void createTimer(Runnable r, long timeout, TimeUnit timeUnit){
		scheduler.schedule(r, timeout, timeUnit);
	}

	/**
	 * Creates and executes a periodic action that becomes enabled 
	 * first after the given initial delay, and subsequently with 
	 * the given period; that is executions will commence after initialDelay 
	 * then initialDelay+period, then initialDelay + 2 * period, and so on. 
	 * If any execution of the task encounters an exception, subsequent executions 
	 * are suppressed. Otherwise, the task will only terminate via cancellation or 
	 * termination of the executor. If any execution of this task takes longer than 
	 * its period, then subsequent executions may start late, but will not 
	 * concurrently execute.
	 * 
	 * @param - the task to execute
	 * @param - the time to delay first execution
	 * @param - the period between successive executions
	 * @param - the time unit of the initialDelay and period parameters
	 */
    public void createRoutineTask(Runnable r, long timeout, long timeInterval, TimeUnit timeUnit){
		scheduler.scheduleAtFixedRate(r, timeout, timeInterval, timeUnit);
	}
}
