package com.andrew.concurrency;

/**
 * Used for testing -  intentionally creates a deadlock
 * to demonstrate blocked threads display in table and on graphs.
 */
public class BlockedThreadCreator {

    /**
     * BlockedThreadCreator takes no parameters.
     */
    public BlockedThreadCreator() {
}

    /**
     * Creates two threads and creates an intentional deadlock.
     */
    public void blockedThreads() {
        String block1 = "Test";
         String block2 = "Test 2";

        Thread firstThread = new Thread(() -> {
            synchronized (block1) {

                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
                synchronized (block2) {
                    System.out.println("Unreachable");
                }
            }
        });
        Thread secondThread = new Thread(() -> {
            synchronized (block2) {

                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
                synchronized (block1) {
                    System.out.println("Unreachable");
                }
            }
        });
    firstThread.setName("BLOCKED 1");
    secondThread.setName("BLOCKED 2");
    firstThread.start();
    secondThread.start();
    }
}
