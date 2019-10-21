package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.ExecutionInfo;
import com.example.providerserver.proxy.QueryInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * Slow query detection listener.
 *
 * When query takes more than specified threshold, {@link #onSlowQuery(ExecutionInfo, java.util.List, long)} callback method
 * is called. The callback is called only once for the target query if it exceeds the threshold time.
 *
 * NOTE:
 * {@link ExecutionInfo#elapsedTime} contains the time when callback is triggered which usually is the specified threshold time.
 *
 * If you want to log or do something with AFTER execution that has exceeded specified threshold time, use normal
 * logging listener like following:
 * <pre>
 * {@code}
 * long thresholdInMills = ...
 * SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener(){
 *      {@literal @}Override
 *      public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
 *          if (execInfo.getElapsedTime() >= thresholdInMills) {
 *              super.afterQuery(execInfo, queryInfoList);
 *          }
 *      }
 * };
 * {@code}
 * </pre>
 *
 * @author complone

 * @since 1.4.1
 */
public class SlowQueryListener implements QueryExecutionListener {

    /**
     * Data holder for currently running query.
     *
     * This structure is used to avoid hard reference from scheduled {@link Runnable} to {@link ExecutionInfo} and etc.
     */
    protected static class RunningQueryContext {
        protected ExecutionInfo executionInfo;
        protected List<QueryInfo> queryInfoList;
        protected long startTimeInMills;

        public RunningQueryContext(ExecutionInfo executionInfo, List<QueryInfo> queryInfoList, long nowInMills) {
            this.executionInfo = executionInfo;
            this.queryInfoList = queryInfoList;
            this.startTimeInMills = nowInMills;
        }
    }

    protected boolean useDaemonThread = true;

    protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(SlowQueryListener.this.useDaemonThread);
            return thread;
        }
    });
    protected long threshold;
    protected TimeUnit thresholdTimeUnit;
    protected Map<String, RunningQueryContext> inExecution = new ConcurrentHashMap<String, RunningQueryContext>();

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {

        final String execInfoKey = getExecutionInfoKey(execInfo);

        // only pass the key to prevent hard reference from Runnable to ExecutionInfo. (Issue-53)
        Runnable check = new Runnable() {
            @Override
            public void run() {
                // if it's still in map, that means it's still running
                RunningQueryContext context = SlowQueryListener.this.inExecution.get(execInfoKey);

                onSlowQuery(context.executionInfo, context.queryInfoList, context.startTimeInMills);
            }
        };
        this.executor.schedule(check, this.threshold, this.thresholdTimeUnit);

        long now = System.currentTimeMillis();

        RunningQueryContext context = new RunningQueryContext(execInfo, queryInfoList, now);
        this.inExecution.put(execInfoKey, context);

    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String executionInfoKey = getExecutionInfoKey(execInfo);
        this.inExecution.remove(executionInfoKey);
    }


    /**
     * Calculate a key for given {@link ExecutionInfo}.
     *
     * <p>This key is passed to the slow query check {@link Runnable} as well as for removal in {@link #afterQuery(ExecutionInfo, java.util.List)}.
     *
     * <p>Default implementation uses {@link System#identityHashCode(Object)}. This does NOT guarantee 100% of uniqueness; however, since
     * the current usage of the key is short lived and good enough for this use case.
     * <p>Subclass can override this method to provide different implementation to uniquely represent {@link ExecutionInfo}.
     *
     * @param executionInfo execution info
     * @return key
     */
    protected String getExecutionInfoKey(ExecutionInfo executionInfo) {
        int exeInfoKey = System.identityHashCode(executionInfo);
        return String.valueOf(exeInfoKey);
    }

    /**
     * Callback when query execution time exceeds the threshold.
     *
     * This callback is called only once per query if it exceeds the threshold time.
     *
     * @param execInfo         query execution info
     * @param queryInfoList    query parameter info
     * @param startTimeInMills time in mills when the query started
     */
    protected void onSlowQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, long startTimeInMills) {
    }

    public void setThreshold(long threshHold) {
        this.threshold = threshHold;
    }

    public void setThresholdTimeUnit(TimeUnit thresholdTimeUnit) {
        this.thresholdTimeUnit = thresholdTimeUnit;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public long getThreshold() {
        return threshold;
    }

    public TimeUnit getThresholdTimeUnit() {
        return thresholdTimeUnit;
    }

    /**
     * When set to {@code true}(default), the executor creates daemon threads to check slow queries.
     *
     * @param useDaemonThread use daemon thread or not. (default is true)
     * @since 1.4.2
     */
    public void setUseDaemonThread(boolean useDaemonThread) {
        this.useDaemonThread = useDaemonThread;
    }


}
