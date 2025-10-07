package com.expensetracker.background;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.expensetracker.util.LoggerUtil;

/**
 * Manages background tasks for the expense tracker application
 */
public class BackgroundTaskManager {

    private static BackgroundTaskManager INSTANCE;
    private static final Object lock = new Object();

    private final ScheduledExecutorService scheduler;
    private final ExecutorService taskExecutor;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;

    private BackgroundTaskManager() {
        // Create a scheduled executor for periodic tasks
        this.scheduler = Executors.newScheduledThreadPool(CORE_POOL_SIZE,
                new ThreadFactory() {
            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ExpenseTracker-Scheduler-" + (++counter));
                t.setDaemon(true);
                return t;
            }
        });

        // Create a general task executor
        this.taskExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ExpenseTracker-Task-" + (++counter));
                t.setDaemon(true);
                return t;
            }
        });

        LoggerUtil.info("Background task manager initialized");
    }

    public static BackgroundTaskManager getInstance() {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = new BackgroundTaskManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Schedule a task to run periodically
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        if (isShutdown.get()) {
            throw new IllegalStateException("TaskManager has been shut down");
        }

        LoggerUtil.debug("Scheduling periodic task with initial delay: " + initialDelay + " " + unit);
        return scheduler.scheduleAtFixedRate(new SafeRunnable(task), initialDelay, period, unit);
    }

    /**
     * Schedule a task to run once after a delay
     */
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        if (isShutdown.get()) {
            throw new IllegalStateException("TaskManager has been shut down");
        }

        LoggerUtil.debug("Scheduling one-time task with delay: " + delay + " " + unit);
        return scheduler.schedule(new SafeRunnable(task), delay, unit);
    }

    /**
     * Submit a task for execution
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("TaskManager has been shut down");
        }

        LoggerUtil.debug("Submitting callable task for execution");
        return taskExecutor.submit(new SafeCallable<>(task));
    }

    /**
     * Submit a runnable task for execution
     */
    public Future<?> submit(Runnable task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("TaskManager has been shut down");
        }

        LoggerUtil.debug("Submitting runnable task for execution");
        return taskExecutor.submit(new SafeRunnable(task));
    }

    /**
     * Execute a task immediately on a background thread
     */
    public void execute(Runnable task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("TaskManager has been shut down");
        }

        LoggerUtil.debug("Executing task immediately");
        taskExecutor.execute(new SafeRunnable(task));
    }

    /**
     * Shutdown the task manager gracefully
     */
    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            return; // Already shut down
        }

        LoggerUtil.info("Shutting down background task manager");

        scheduler.shutdown();
        taskExecutor.shutdown();

        try {
            // Wait for termination
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                LoggerUtil.warn("Scheduler did not terminate gracefully, forcing shutdown");
                scheduler.shutdownNow();
            }

            if (!taskExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                LoggerUtil.warn("Task executor did not terminate gracefully, forcing shutdown");
                taskExecutor.shutdownNow();
            }

            LoggerUtil.info("Background task manager shut down successfully");
        } catch (InterruptedException e) {
            LoggerUtil.warn("Interrupted while shutting down task manager", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get the number of active tasks
     */
    public int getActiveTaskCount() {
        if (taskExecutor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) taskExecutor).getActiveCount();
        }
        return 0;
    }

    /**
     * Check if the task manager is shut down
     */
    public boolean isShutdown() {
        return isShutdown.get();
    }

    /**
     * Wrapper for safe execution of runnables
     */
    private static class SafeRunnable implements Runnable {

        private final Runnable delegate;

        public SafeRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                delegate.run();
            } catch (Exception e) {
                LoggerUtil.error("Uncaught exception in background task", e);
            }
        }
    }

    /**
     * Wrapper for safe execution of callables
     */
    private static class SafeCallable<T> implements Callable<T> {

        private final Callable<T> delegate;

        public SafeCallable(Callable<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T call() throws Exception {
            try {
                return delegate.call();
            } catch (Exception e) {
                LoggerUtil.error("Uncaught exception in background callable", e);
                throw e;
            }
        }
    }
}
