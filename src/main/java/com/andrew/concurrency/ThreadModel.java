package com.andrew.concurrency;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Handle the list of all threads,
 * keeps list updated, and allows controllers
 * to access details of threads.
 */
public class ThreadModel {
    private ObservableList<Thread> observableThreadList;
    private ThreadGroup[] allGroups;
    private Thread[] allThreads;
    private int groupCount;
    private int threadCount;
    private ThreadGroup parent;

    /**
     * Thread model takes no parameters.
     */
    public ThreadModel() {
        observableThreadList = FXCollections.observableArrayList();
        allGroups = new ThreadGroup[0];
        groupCount = 0;
        threadCount = 0;
        parent = Thread.currentThread().getThreadGroup();
    }

    /**
     * Gets a list of all active threads in the JVM.
     * It uses the enumerate method to get a list
     * of all threads and thread groups.
     * Checks if a thread is already is its
     * thread list, and if not it adds it.
     * @return - thread list for controllers to use.
     */
    public ObservableList<Thread> getObservableThreadList() {
        // Ensure navigation to the highest thread group
        if (parent.getParent() != null) {
            parent = parent.getParent();
        }
        allGroups = new ThreadGroup[parent.activeGroupCount()];
        groupCount = parent.enumerate(allGroups);
        allThreads = new Thread[parent.activeCount()];
        threadCount = parent.enumerate(allThreads);
        for (Thread t: allThreads) {
            if (!observableThreadList.contains(t)) {
                observableThreadList.add(t);
            }
        }
        return observableThreadList;
    }

    /**
     * Get details of all thread groups.
     * @return Array containing details all thread groups.
     */
    public ThreadGroup[] getAllGroups() {
        return allGroups;
    }

    /**
     * Get details of all threads.
     * @return Array containing details of all threads.
     */
    public Thread[] getAllThreads() {
        return allThreads;
    }

    /**
     * Get number of active threads.
     * @return Integer - the number of currently active threads.
     */
    public int getActiveThreadCount() {
        return threadCount;
    }

    /**
     * Get number of total threads.
     * @return Integer - the number of total threads,
     * including terminated and new.
     */
    public int getTotalThreadCount() {
        return observableThreadList.size();
    }

    /**
     * Get number of threads groups.
     * @return Integer of number of thread groups.
     */
    public int getGroupCount() {
        return groupCount;
    }

    /**
     * Get number of active threads with state "NEW".
     * @return Integer of active threads with state "NEW".
     */
    public int getNewStateCount() {
        int count = 0;
        for (Thread t : observableThreadList) {
            if (t.getState().equals(Thread.State.NEW)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of threads with state "RUNNABLE".
     * @return Integer of threads with state "RUNNABLE".
     */
    public int getRunnableStateCount() {
        int count = 0;
        for (Thread t : observableThreadList) {
            if (t.getState().equals(Thread.State.RUNNABLE)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of threads with state "BLOCKED".
     * @return Integer of threads with state "BLOCKED".
     */
    public int getBlockedStateCount() {
        int count = 0;
        for (Thread t : observableThreadList) {
            if (t.getState().equals(Thread.State.BLOCKED)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of threads with state "WAITING".
     * @return Integer of threads with state "WAITING".
     */
    public int getWaitingStateCount() {
        int count = 0;
        for (Thread t : observableThreadList) {
            if (t.getState().equals(Thread.State.WAITING)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of threads with state "TIMED WAITING".
     * @return Integer of threads with state "TIMED WAITING".
     */
    public int getTimedWaitingStateCount() {
        int count = 0;
        for (Thread t : observableThreadList) {
            if (t.getState().equals(Thread.State.TIMED_WAITING)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of threads with state "TERMINATED".
     * @return Integer of threads with state "TERMINATED".
     */
    public int getTerminatedStateCount() {
        int count = 0;
        for (Thread t : observableThreadList) {
            if (t.getState().equals(Thread.State.TERMINATED)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of active daemon threads.
     * @return Integer of active daemon threads
     */
    public int getActiveDaemonCount() {
        int count = 0;
        for (Thread t: observableThreadList) {
            if (t.isDaemon()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get number of active non-daemon threads.
     * @return Integer of active non-daemon threads
     */
    public int getActiveNonDaemonCount() {
        int count = 0;
        for (Thread t: getAllThreads()) {
            if (!t.isDaemon()) {
                count++;
            }
        }
        return count;
    }
}
