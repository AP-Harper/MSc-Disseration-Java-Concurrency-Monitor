package com.andrew.concurrency.Tests;

import com.andrew.concurrency.ThreadCreator;
import com.andrew.concurrency.ThreadModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for Thread Model.
 */
class ThreadModelTest {
    ThreadModel threadModel = new ThreadModel();
    private final ObservableList<Thread> observableThreadListTest = FXCollections.observableArrayList();
    private ThreadGroup[] allGroupsTest = new ThreadGroup[0];
    private Thread[] allThreadsTest = new Thread[0];
    private int groupCountTest = 0;
    private int threadCountTest = 0;
    private ThreadGroup parentTest;

    ThreadModelTest() {
    }

    @BeforeEach
    void setUp() {
        threadModel.getObservableThreadList();
        parentTest = Thread.currentThread().getThreadGroup();
        if (parentTest.getParent() != null) {
            parentTest = parentTest.getParent();
        }
        allGroupsTest = new ThreadGroup[parentTest.activeGroupCount()];
        groupCountTest = parentTest.enumerate(allGroupsTest);
        allThreadsTest = new Thread[parentTest.activeCount()];
        threadCountTest = parentTest.enumerate(allThreadsTest);


    }

    /**
     * Asserts that .getParent method is correctly reaching the most senior
     * thread group -  "system".
     */
    @Test
    void testGetParent() {
        assertEquals(parentTest.getName(), "system");
    }

    /**
     * Test uses same logic to create thread list as the thread model.
     * It navigates to the most senior thread group
     * and then uses enumerate method to create an array
     * containing all threads.
     * This test asserts that the getObservableThreadList
     * functions correctly.
     */
    @Test
    void getObservableThreadList() {
        observableThreadListTest.addAll(allThreadsTest);
        assertEquals(observableThreadListTest, threadModel.getObservableThreadList());
    }

    /**
     * Assert that the getAllGroups method correctly returns all active thread groups.
     * Assertion uses toString in order to show the actual and test are identical.
     */
    @Test
    void getAllGroups() {
        String allGroupsTestString = Arrays.toString(allGroupsTest);
        String getAllGroupsString = Arrays.toString(threadModel.getAllGroups());
        assertEquals(allGroupsTestString, getAllGroupsString);
    }

    /**
     * Assert that the getAllThreads method correctly returns all active threads.
     * Assertion uses toString in order to show the actual and test are identical.
     */
    @Test
    void getAllThreads() {
        String allThreadsTestString = Arrays.toString(allThreadsTest);
        String getAllThreadsString = Arrays.toString(threadModel.getAllThreads());
        assertEquals(allThreadsTestString, getAllThreadsString);
    }

    /**
     * Calculates the number of active threads in the test thread list
     * and compares it with the number returned by getActiveThreadCount method.
     * Test assumes that active threads are those in any of the following states:
     * Runnable, Blocked, Waiting, Timed Waiting.
     */
    @Test
    void getActiveThreadCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int activeCount = 0;
            for (Thread t : observableThreadListTest) {
                if (!(t.getState().equals(Thread.State.TERMINATED)) && (!(t.getState().equals(Thread.State.NEW)))) {
                    activeCount++;
                }
            }
            assertEquals(activeCount, threadModel.getActiveThreadCount());
    }

    /**
     * Test launches a thread and then sleeps until it terminates
     * to ensure terminated threads are counted in totalThreadCount.
     * @throws InterruptedException - new thread launched.
     */
    @Test
    void getTotalThreadCount() throws InterruptedException {
        observableThreadListTest.addAll(allThreadsTest);
        Thread testThread = new Thread(new ThreadCreator("getTotalThreadCount Test Thread", 1000));
        testThread.start();
        threadModel.getObservableThreadList();
        observableThreadListTest.add(testThread);
        Thread.sleep(2000);
        int totalThreadsTest =  observableThreadListTest.size();
        assertEquals(totalThreadsTest, threadModel.getTotalThreadCount());
    }

    /**
     * Asserts that the getGroupCount works accurately by comparing
     * with test group count.
     */
    @Test
    void getGroupCount() {
        assertEquals(groupCountTest, threadModel.getGroupCount());
    }

    /**
     * Creates a thread and adds it to both the test list and Thread Model list
     * to assert that getNewStateCount returns correct count.
     */
    @Test
    void getNewStateCount() {
        observableThreadListTest.addAll(allThreadsTest);
        Thread testThread = new Thread(new ThreadCreator("getActiveNewStateCount Test Thread", 5000));
        threadModel.getObservableThreadList().add(testThread);
        observableThreadListTest.add(testThread);
        int newCount = 0;
        for (Thread t : observableThreadListTest) {
            if (t.getState().equals(Thread.State.NEW)) {
                newCount++;
            }
        }
        assertEquals(newCount, threadModel.getNewStateCount());
    }

    /**
     * Calculates the number of Runnable threads in the test thread list
     * and compares it with the number returned by the
     * getRunnableStateCount method.
     */
    @Test
    void getRunnableStateCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int runnableCount = 0;
        for (Thread t : observableThreadListTest) {
            if (t.getState().equals(Thread.State.RUNNABLE)) {
                runnableCount++;
            }
        }
        assertEquals(runnableCount, threadModel.getRunnableStateCount());
    }


    /**
     * Calculates the number of Blocked threads in the test thread list
     * and compares it with the number returned by the
     * getBlockedStateCount method.
     */
    @Test
    void getBlockedStateCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int blockedCount = 0;
        for (Thread t : observableThreadListTest) {
            if (t.getState().equals(Thread.State.BLOCKED)) {
                blockedCount++;
            }
        }
        assertEquals(blockedCount, threadModel.getBlockedStateCount());
    }


    /**
     * Calculates the number of Waiting threads in the test thread list
     * and compares it with the number returned by the
     * getWaitingStateCount method.
     */
    @Test
    void getWaitingStateCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int waitingCount = 0;
        for (Thread t : threadModel.getObservableThreadList()) {
            if (t.getState().equals(Thread.State.WAITING)) {
                waitingCount++;
            }
        }
        assertEquals(waitingCount, threadModel.getWaitingStateCount());
    }

    /**
     * Calculates the number of Timed Waiting threads in the test thread list
     * and compares it with the number returned by the
     * getTimedWaitingStateCount method.
     */
    @Test
    void getTimedWaitingStateCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int timedWaitingCount = 0;
        for (Thread t : threadModel.getObservableThreadList()) {
            if (t.getState().equals(Thread.State.TIMED_WAITING)) {
                timedWaitingCount++;
            }
        }
        assertEquals(timedWaitingCount, threadModel.getTimedWaitingStateCount());
    }

    /**
     * Launches thread and then waits from them to terminate
     * before calculating the number of terminated threads
     * in test thread list and compares it with the number returned
     * by the getTerminatedStateCount method.
     */
    @Test
    void getTerminatedStateCount() throws InterruptedException {
        observableThreadListTest.addAll(allThreadsTest);
        Thread testThread = new Thread(new ThreadCreator("getTerminatedStateCount Test Thread", 1000));
        Thread testThreadTwo = new Thread(new ThreadCreator("getTerminatedStateCount Test Thread Two", 1000));
        testThread.start();
        testThreadTwo.start();
        observableThreadListTest.add(testThread);
        observableThreadListTest.add(testThreadTwo);
        threadModel.getObservableThreadList();
        Thread.sleep(3000);
        int terminatedCount = 0;
        for (Thread t : observableThreadListTest) {
            if (t.getState().equals(Thread.State.TERMINATED)) {
                terminatedCount++;
            }
        }
        assertEquals(terminatedCount, threadModel.getTerminatedStateCount());
    }

    /**
     * Calculates the number of Daemon threads in the test thread list
     * and compares it with the number returned by the
     * getActiveDaemonCount method.
     */
    @Test
    void getActiveDaemonCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int daemonCount = 0;
        for (Thread t : observableThreadListTest) {
            if (t.isDaemon()) {
                daemonCount++;
            }
        }
        assertEquals(daemonCount, threadModel.getActiveDaemonCount());
    }

    /**
     * Calculates the number of Non-Daemon threads in the test thread list
     * and compares it with the number returned by the
     * getActiveNonDaemonCount method.
     */
    @Test
    void getActiveNonDaemonCount() {
        observableThreadListTest.addAll(allThreadsTest);
        int nonDaemonCount = 0;
        for (Thread t : observableThreadListTest) {
            if (!(t.isDaemon())) {
                nonDaemonCount++;
            }
        }
        assertEquals(nonDaemonCount, threadModel.getActiveNonDaemonCount());
    }
}
