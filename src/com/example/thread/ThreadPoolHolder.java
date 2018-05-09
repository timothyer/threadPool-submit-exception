package com.example.thread;
import java.util.concurrent.*;

/**
 * @author timothyer
 * @date 2018-5-9
 */
public class ThreadPoolHolder {

    private static final int THREAD_POOL_SIZE = 40;

    private static final long KEEP_ALIVE_TIME = 60L;

    public static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>()) {

        /**
         * 覆盖这个方法是为了解决ThreadPoolExecutor#submit方法吞掉了线程中的异常的问题
         * 如果调用ThreadPoolExecutor#execute则不会出现这个问题
         *
         * see https://imxylz.com/blog/2013/08/02/handling-the-uncaught-exception-of-java-thread-pool/
         * see https://stackoverflow.com/questions/4016091/what-is-the-difference-between-submit-and-execute-method-with-threadpoolexecutor
         */
        @Override
        protected void afterExecute(Runnable r, Throwable t) {

            super.afterExecute(r, t);

            //submit提交的任务异常在这里处理
            if (t == null && r instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) r;
                    if (future.isDone())
                        future.get();
                } catch (CancellationException ce) {
                    t = ce;
                } catch (ExecutionException ee) {
                    t = ee.getCause();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // ignore/reset
                }
                if (t != null) {
                    t.printStackTrace();
                }
            }

            //execute提交的任务异常在这里，因为excute的执行异常自行打印堆栈，下面可以不执行，否者异常堆栈打印2次
           /*if (t != null) {
                t.printStackTrace();
            }*/
        }
    };


    /**
     * 提交一个任务
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        THREAD_POOL.execute(runnable);
    }

    /**
     * 使用线程池执行任务，并使CountDownLatch计数减一
     * @param task
     * @param ctl
     */
    public static void executeAndCountDown(final Runnable task, final CountDownLatch ctl) {
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    ctl.countDown();
                }
            }
        });
    }
}
