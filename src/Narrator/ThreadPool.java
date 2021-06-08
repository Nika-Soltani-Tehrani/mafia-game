package Narrator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public ThreadPool() {
    }

    public static void execute(Runnable task) {
        pool.execute(task);
    }

    public static void shutDown() {
        pool.shutdown();
    }
}
