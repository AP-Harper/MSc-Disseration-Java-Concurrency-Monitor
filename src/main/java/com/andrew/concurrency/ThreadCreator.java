package com.andrew.concurrency;

/**
 * Used for testing - allows creation of threads to test table
 * and graphs display information correctly.
 */
public class ThreadCreator implements Runnable {
    private final String threadName;
    private final int threadRuntime;

    /**
     * Allows for naming of thread and to set how long before thread terminates.
     * @param tName - Thread Name
     * @param tRuntime - Time for thread to sleep,
     *                 after which it will terminate.
     */
    public ThreadCreator(String tName, int tRuntime) {
        threadName = tName;
        threadRuntime = tRuntime;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(threadName);
            System.out.println(
                    Thread.currentThread().getName() + " is running");
            Thread.sleep(threadRuntime);
            System.out.println(
                    Thread.currentThread().getName() + " is finished");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
