
package com.andrew.concurrency;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class which handles displaying thread information in
 * form of graphs.
 * It provides a graphical representation
 * of various thread metrics to allow the user
 * to visualise their data in different ways.
 */
public class ThreadGraphController {
    @FXML
    private Label timer;
    @FXML
    private Label labelActiveCount;
    @FXML
    private Label labelTotalCount;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnMain;
    @FXML
    private PieChart activeStatePie;
    @FXML
    private PieChart totalStatePieChart;
    @FXML
    private CategoryAxis xAxisTotalCount = new CategoryAxis();
    @FXML
    private NumberAxis yAxisTotalCount = new NumberAxis();
    @FXML
    private LineChart<String, Number> totalThreadCountLine
            = new LineChart<>(xAxisTotalCount, yAxisTotalCount);
    @FXML
    private NumberAxis yAxisDaemon = new NumberAxis();
    @FXML
    private CategoryAxis xAxisDaemon = new CategoryAxis();
    @FXML
    private LineChart<String, Number> lineChartDaemon
            = new LineChart<>(xAxisDaemon, yAxisDaemon);
    @FXML
    private CategoryAxis xAxisActiveState = new CategoryAxis();
    @FXML
    private NumberAxis yAxisActiveState = new NumberAxis();
    @FXML
    private LineChart<String, Number> lineChartActiveState
            = new LineChart<>(xAxisActiveState, yAxisActiveState);
    private final int WINDOW_SIZE = 10;
    private final SimpleDateFormat simpleDateFormat
            = new SimpleDateFormat("HH:mm:ss");
    private ThreadModel model;
    private Main main;
    private Scene preScene;

    /**
     * ThreadGraphController takes no parameters.
     */
    public ThreadGraphController() {
        model = new ThreadModel();
        main = new Main();
    }

    /**
     * Allows user to return to thread table screen.
     * Passes current state of ThreadGraphController screen
     * to ThreadTableController to maintain state of graphs
     * when not on screen.
     * @throws IOException - opens thread table screen.
     */
    @FXML
    public void openThreadTable() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                getResource("/Fxml/ThreadTable.fxml"));
        fxmlLoader.load();
        ThreadTableController controller = fxmlLoader.getController();
        controller.setPreScene(btnMain.getScene());
        Stage stage = (Stage) btnMain.getScene().getWindow();
        stage.setScene(preScene);
        stage.show();
    }

    /**
     * Preserves state of thread table screen when navigated
     * away from.
     * @param tablePreScene - Preserve thread table screen
     */
    public void setPreScene(Scene tablePreScene) {
        this.preScene = tablePreScene;
    }

    /**
     * Opens a pop-up Help window. Main screen stays open,
     * but user must close Help window before interacting
     * with it again.
     * @throws IOException - Opens Help stage.
     */
    @FXML
    public void setBtnHelp() throws IOException {
        main.openHelpScreen();
    }

    /**
     * Exits the program; clicking closes program.
     */
    public void setBtnExit() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    /**
     * Removes terminated threads from thread list permanently.
     */
    @FXML
    public void clearTerminated() {
            model.getObservableThreadList().removeIf(
                    t -> t.getState().equals(Thread.State.TERMINATED));
    }

    /**
     * Initialises the time on the GUI.
     * Uses updateTimer method to make it run.
     */
    public void initTime() {
        Date startTime = new Date();
        timer.setText(simpleDateFormat.format(startTime));
    }

    /**
     * Gets Active and Total thread count
     * from Thread Model and displays them on GUI.
     * Uses updateTimer method up keep them up to date.
     */
    public void showThreadCount() {
        int initialTotalCount = model.getTotalThreadCount();
        int initialActiveCount = model.getActiveThreadCount();
        labelTotalCount.setText(String.valueOf(initialTotalCount));
        labelActiveCount.setText(String.valueOf(initialActiveCount));
    }

    /**
     * Create a pie chart to show the thread state of active threads
     * - therefore excluding terminated and new threads.
     * Uses updateTimer method to keep it up to date.
     */
    public void activeStatePieChart() {
        ObservableList<PieChart.Data> activeStatePieChartData
                = FXCollections.observableArrayList(
                new PieChart.Data(
                        "Runnable", model.getRunnableStateCount()),
                new PieChart.Data(
                        "Blocked", model.getBlockedStateCount()),
                new PieChart.Data(
                        "Waiting", model.getWaitingStateCount()),
                new PieChart.Data("Timed Waiting",
                        model.getTimedWaitingStateCount()));
        // Label each section of pie correctly.
        activeStatePieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(data.getName(),
                                " Threads - ", data.pieValueProperty())
                ));
        activeStatePie.setData(activeStatePieChartData);
        activeStatePie.setTitle("Active Thread State Summary");
        activeStatePie.setAnimated(true);
        activeStatePie.setLegendSide(Side.RIGHT);
    }

    /**
     * Create a pie chart to show the thread state of all threads
     * - includes terminated and new threads.
     * Uses updateTimer method to keep it up to date.
     */
    public void totalStatePieChart() {
        ObservableList<PieChart.Data> totalStatePieChartData
                = FXCollections.observableArrayList(
                new PieChart.Data("New", model.getNewStateCount()),
                new PieChart.Data("Runnable",
                        model.getRunnableStateCount()),
                new PieChart.Data("Blocked", model.getBlockedStateCount()),
                new PieChart.Data("Waiting", model.getWaitingStateCount()),
                new PieChart.Data("Timed Waiting",
                        model.getTimedWaitingStateCount()),
                new PieChart.Data("Terminated",
                        model.getTerminatedStateCount()));
        // Label each section of pie correctly.
        totalStatePieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(data.getName(),
                                " Threads - ", data.pieValueProperty())
                ));
        totalStatePieChart.setData(totalStatePieChartData);
        totalStatePieChart.setTitle("Total Thread State Summary");
        totalStatePieChart.setAnimated(true);
        totalStatePieChart.setLegendSide(Side.RIGHT);
    }

    /**
     * Create a line graph to display total thread count
     * and active thread count.
     * Uses TimeLine to update graph every second.
     */
    public void threadCountLineGraph() {
        yAxisTotalCount.setLabel("Thread Count");
        xAxisTotalCount.setLabel("Time");
        yAxisTotalCount.setAutoRanging(true);
        yAxisTotalCount.setTickLength(10);
        XYChart.Series<String, Number> totalCount = new XYChart.Series<>();
        XYChart.Series<String, Number> activeCount = new XYChart.Series<>();
        totalThreadCountLine.getData().add(totalCount);
        totalThreadCountLine.getData().add(activeCount);
        totalThreadCountLine.setLegendSide(Side.RIGHT);
        totalThreadCountLine.setAnimated(false);
        totalThreadCountLine.setTitle("Total Thread Count");
        totalCount.setName("Total Thread Count");
        activeCount.setName("Active Thread Count");
        // Update graph
        Timeline updateThreadCountGraph = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Date now = new Date();
                    totalCount.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getTotalThreadCount()));
                    activeCount.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getActiveThreadCount()));
                    //Limit number of data points that show on graph.
                    if (totalCount.getData().size() > WINDOW_SIZE) {
                        totalCount.getData().remove(0);
                    }
                    if (activeCount.getData().size() > WINDOW_SIZE) {
                        activeCount.getData().remove(0);
                    }
                }));
        updateThreadCountGraph.setCycleCount(Animation.INDEFINITE);
        updateThreadCountGraph.play();
    }

    /**
     * Create a line graph to display the active thread count
     * and daemon thread count.
     * Uses TimeLine to update graph every second.
     */
    public void threadDaemonLineGraph() {
        XYChart.Series<String, Number> daemonThreads = new XYChart.Series<>();
        XYChart.Series<String, Number> activeThreads = new XYChart.Series<>();
        daemonThreads.setName("Daemon Threads");
        activeThreads.setName("Active Threads");
        lineChartDaemon.getData().add(daemonThreads);
        lineChartDaemon.getData().add(activeThreads);
        lineChartDaemon.setAnimated(false);
        lineChartDaemon.setLegendSide(Side.RIGHT);
        lineChartDaemon.setTitle("Active vs Daemon Thread Count");
        yAxisDaemon.setLabel("Thread Count");
        xAxisDaemon.setLabel("Time");
        //Update graph
        Timeline updateDaemonGraph = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Date now = new Date();
                    daemonThreads.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getActiveDaemonCount()));
                    activeThreads.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getActiveThreadCount()));
                    //Limit number of data points that show on graph.
                    if (daemonThreads.getData().size() > WINDOW_SIZE) {
                        daemonThreads.getData().remove(0);
                    }
                    if (activeThreads.getData().size() > WINDOW_SIZE) {
                        activeThreads.getData().remove(0);
                }
                }));
        updateDaemonGraph.setCycleCount(Animation.INDEFINITE);
        updateDaemonGraph.play();
    }

    /**
     * Create a line graph to display the number of
     * active threads in each state.
     * Uses TimeLine to update graph every second.
     */
    public void threadStateLineGraph() {
        XYChart.Series<String, Number> stateRunnable = new XYChart.Series<>();
        XYChart.Series<String, Number> stateBlocked = new XYChart.Series<>();
        XYChart.Series<String, Number> stateWaiting = new XYChart.Series<>();
        XYChart.Series<String, Number> stateTimedWaiting
                = new XYChart.Series<>();
        stateRunnable.setName("Runnable");
        stateBlocked.setName("Blocked");
        stateWaiting.setName("Waiting");
        stateTimedWaiting.setName("Timed Waiting");
        lineChartActiveState.getData().add(stateRunnable);
        lineChartActiveState.getData().add(stateBlocked);
        lineChartActiveState.getData().add(stateWaiting);
        lineChartActiveState.getData().add(stateTimedWaiting);
        lineChartActiveState.setTitle("Active Thread State Count");
        lineChartActiveState.setLegendSide(Side.RIGHT);
        lineChartActiveState.setAnimated(false);
        xAxisActiveState.setLabel("Time");
        yAxisActiveState.setLabel("Thread Count");
        // Update graph
        Timeline updateDaemonGraph = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Date now = new Date();
                    stateRunnable.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getRunnableStateCount()));
                    stateBlocked.getData().add(
                            new XYChart.Data<>(
                                    simpleDateFormat.format(now),
                                    model.getBlockedStateCount()));
                    stateWaiting.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getWaitingStateCount()));
                    stateTimedWaiting.getData().add(
                            new XYChart.Data<>(simpleDateFormat.format(now),
                                    model.getTimedWaitingStateCount()));
                    //Limit number of data points that show on graph.
                    if (stateRunnable.getData().size() > WINDOW_SIZE) {
                        stateRunnable.getData().remove(0);
                    }
                    if (stateBlocked.getData().size() > WINDOW_SIZE) {
                        stateBlocked.getData().remove(0);
                    }
                    if (stateWaiting.getData().size() > WINDOW_SIZE) {
                        stateWaiting.getData().remove(0);
                    }
                    if (stateTimedWaiting.getData().size() > WINDOW_SIZE) {
                        stateTimedWaiting.getData().remove(0);
                    }
                }));
        updateDaemonGraph.setCycleCount(Animation.INDEFINITE);
        updateDaemonGraph.play();
    }

    /**
     * Timer to update all data every second (other than line graphs,
     * which have their own timer).
     */
    public void updateTimer() {
        Timeline updateInfo = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // Update thread table
                    model.getObservableThreadList();
                    // Update Total and Active Thread Count display on GUI.
                    int totalCount = model.getTotalThreadCount();
                    int activeCount = model.getActiveThreadCount();
                    labelTotalCount.setText(String.valueOf(totalCount));
                    labelActiveCount.setText(String.valueOf(activeCount));
                    // Update time on GUI
                    Date now = new Date();
                    timer.setText(simpleDateFormat.format(now));
                    // Update pie chart of states of active threads.
                    // Excludes terminated and new threads.
                    activeStatePie.getData().get(0).
                            setPieValue(model.getRunnableStateCount());
                    activeStatePie.getData().get(1).
                            setPieValue(model.getBlockedStateCount());
                    activeStatePie.getData().get(2).
                            setPieValue(model.getWaitingStateCount());
                    activeStatePie.getData().get(3).
                            setPieValue(model.
                                    getTimedWaitingStateCount());
                    // Update pie chart of states all threads -
                    // Includes terminated and new threads.
                    totalStatePieChart.getData().get(0).
                            setPieValue(model.getNewStateCount());
                    totalStatePieChart.getData().get(1).
                            setPieValue(model.getRunnableStateCount());
                    totalStatePieChart.getData().get(2).
                            setPieValue(model.getBlockedStateCount());
                    totalStatePieChart.getData().get(3).
                            setPieValue(model.getWaitingStateCount());
                    totalStatePieChart.getData().get(4).
                            setPieValue(model.getTimedWaitingStateCount());
                    totalStatePieChart.getData().get(5).
                            setPieValue(model.getTerminatedStateCount());
                }));
        updateInfo.setCycleCount(Animation.INDEFINITE);
        updateInfo.play();
    }

    @FXML
    private void initialize() {
        initTime();
        showThreadCount();
        updateTimer();
        activeStatePieChart();
        totalStatePieChart();
        threadDaemonLineGraph();
        threadCountLineGraph();
        threadStateLineGraph();
        }
}
