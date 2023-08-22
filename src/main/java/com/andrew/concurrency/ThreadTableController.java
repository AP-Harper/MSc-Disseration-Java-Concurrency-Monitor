package com.andrew.concurrency;

import java.io.IOException;
import java.text.SimpleDateFormat;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Date;
import java.util.Objects;

/** Controls the ThreadTable screen,
 * which is the main screen of the GUI.
 * Populates the thread table, which is the main view of the program
 * and allows searching and filtering of table.
 */
public class ThreadTableController {
    @FXML
    private Button btnExit;
    @FXML
    private Button btnOpenGraphs;
    @FXML
    private CheckBox checkTerminatedThreads;
    @FXML
    private CheckBox checkSystemThreads;
    @FXML
    private CheckBox checkInnocuousThreads;
    @FXML
    private TextField searchBox;
    @FXML
    private ChoiceBox<String> searchFilter;
    @FXML
    private ChoiceBox<String> priorityFilter;
    @FXML
    private ChoiceBox<String> statusFilter;
    @FXML
    private ChoiceBox<String> daemonFilter;
    @FXML
    private ChoiceBox<String> groupFilter;
    @FXML
    private  Label timer;
    @FXML
    private Label labelActiveCount;
    @FXML
    private Label labelTotalCount;
    @FXML
    private TableView<Thread> tableView;
    @FXML
    private TableColumn<Thread, String> threadNameCol;
    @FXML
    private TableColumn<Thread, Long> threadID;
    @FXML
    private TableColumn<Thread, String> threadGroup;
    @FXML
    private TableColumn<Thread, Integer> threadPriority;
    @FXML
    private TableColumn<Thread, Thread.State> threadState;
    @FXML
    private TableColumn<Thread, Boolean> threadDaemon;
    private final SimpleDateFormat simpleDateFormat;
    private ThreadModel model = new ThreadModel();
    private final FXMLLoader fxmlLoaderGraphScreen;
    private Scene sceneGraphScreen;
    private Main main;
    private Scene preScene;
    // Create a series of filtered lists in order
    // to apply multiple filters at the same time.
    private FilteredList<Thread> filteredList;
    private FilteredList<Thread> filteredListState;
    private FilteredList<Thread> filteredListPriority;
    private FilteredList<Thread> filteredListGroup;
    private FilteredList<Thread> filteredListDaemon;
    private FilteredList<Thread> filteredListSystemThreads;
    private FilteredList<Thread> filteredListTerminatedThreads;
    private FilteredList<Thread> filteredListInnocuousThreads;
    private SortedList<Thread> sortedData;

    /**
     * ThreadTable take no parameters.
     * Initialises filtered lists and sorted list
     * for filtering and search thread table.
     * @throws IOException - Loads ThreadGraph screen.
     */
    public ThreadTableController() throws IOException {
        main = new Main();
        fxmlLoaderGraphScreen = new FXMLLoader(
                getClass().getResource("/Fxml/ThreadGraph.fxml"));
        sceneGraphScreen = new Scene(fxmlLoaderGraphScreen.load());
        simpleDateFormat  = new SimpleDateFormat("HH:mm:ss");
        filteredList = new FilteredList<>(
                model.getObservableThreadList(), t -> true);
        filteredListState = new FilteredList<>(
                filteredList, t -> true);
        filteredListPriority = new FilteredList<>(
                filteredListState, t -> true);
        filteredListGroup = new FilteredList<>(
                filteredListPriority, t -> true);
        filteredListDaemon = new FilteredList<>(
                filteredListGroup, t -> true);
        filteredListSystemThreads = new FilteredList<>(
                filteredListDaemon, t -> true);
        filteredListTerminatedThreads = new FilteredList<>(
                filteredListSystemThreads, t -> true);
        filteredListInnocuousThreads = new FilteredList<>(
                filteredListTerminatedThreads, t -> true);
        sortedData = new SortedList<>(
                filteredListInnocuousThreads);
    }

    /**
     * Preserves state of thread graph screen when navigated
     * away from.
     * @param tablePreScene - Preserve thread table screen
     */
    public void setPreScene(Scene tablePreScene) {
        this.preScene = tablePreScene;
    }

    /**
     * Opens the screen containing the thread graphs.
     * Passes current state of ThreadTableController screen
     * to ThreadGraphController to maintain state screen.
     */
    public void openGraphs() {
        ThreadGraphController controller =
                fxmlLoaderGraphScreen.getController();
        controller.setPreScene(btnOpenGraphs.getScene());
        Stage stage = (Stage) btnOpenGraphs.getScene().getWindow();
        stage.setScene(Objects.requireNonNullElse(preScene, sceneGraphScreen));
        stage.show();
    }

    /**
     * Opens a pop-up Help window. Main screen stays open,
     * but user must close Help window before interacting
     * with it again.
     * @throws IOException - Opens Help stage.
     */
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
     * Resets the search filters to their default values.
     * Note that this does not include the checkboxes,
     * they are controlled separately.
     */
    public void setBtnClear() {
        filteredListDaemon.setPredicate(t -> true);
        searchBox.clear();
        groupFilter.setValue("Thread Groups");
        priorityFilter.setValue("Priority");
        statusFilter.setValue("State");
        daemonFilter.setValue("Is Daemon?");
        searchFilter.setValue("Name");
    }

    /**
     * Deletes all threads where the thread state is "TERMINATED"
     * at the moment the button is pressed.
     * Unlike the method setCheckTerminatedThreads,
     * this permanently deletes them.
     */
    public void setBtnDeleteTerminated() {
        model.getObservableThreadList().removeIf(
                t -> t.getState().equals(Thread.State.TERMINATED));
        tableView.refresh();
    }

    /**
     * Allows threads that belong to thread group "system"
     * to be hidden from the thread table.
     * Unchecking will display them again.
     */
    public  void setCheckSystemThreads() {
        if (checkSystemThreads.isSelected()) {
            filteredListSystemThreads.setPredicate(
                    Thread -> (Thread.getThreadGroup() == null)
                            || (!Thread.getThreadGroup()
                            .getName().equals("system")));
        }
        if (!(checkSystemThreads.isSelected())) {
            filteredListSystemThreads.setPredicate(Thread -> true);
        }
    }

    /**
     * Allows threads that belong to thread group
     * "InnocuousThreadGroup" to be hidden from the thread table.
     * Unchecking will display them again.
     */
    public  void setCheckInnocuousThreads() {
        if (checkInnocuousThreads.isSelected()) {
            filteredListInnocuousThreads.setPredicate(
                    Thread -> (Thread.getThreadGroup() == null)
                            || (!Thread.getThreadGroup()
                            .getName().equals("InnocuousThreadGroup")));
        }
        if (!(checkInnocuousThreads.isSelected())) {
            filteredListInnocuousThreads.setPredicate(Thread -> true);
        }
    }

    /**
     * Allows threads whose thread group shows "TERMINATED"
     * to be hidden from the thread table.
     * Unchecking will display them again.
     * Threads that are terminated after box is checked
     * will still appear on the thread table.
     */
    public  void setCheckTerminatedThreads() {
        if (checkTerminatedThreads.isSelected()) {
            filteredListTerminatedThreads.setPredicate(
                    Thread -> Thread.getThreadGroup() != null);
        }
        if (!(checkTerminatedThreads.isSelected())) {
            filteredListTerminatedThreads.setPredicate(Thread -> true);
        }
    }

    /**
     * Allows thread table to be filtered based on the priority of threads.
     * Threads are ranked from 1-10, with 1 being the lowest priority
     * and 10 being the highest.
     * Can be combined with other filters.
     * @param event - Mouse click on priorityFilter
     */
    public void getPriorityFilter(final ActionEvent event) {
        String selected = priorityFilter.getValue();
        filteredListPriority.setPredicate(Thread -> {
            if (selected.equals("All") && (!(Thread.getThreadGroup() == null))
                || selected.equals("Priority")) {
                return true;
            } else if (selected.equals("1") && (Thread.getPriority() == 1)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("2") && (Thread.getPriority() == 2)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("3") && (Thread.getPriority() == 3)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("4") && (Thread.getPriority() == 4)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("5") && (Thread.getPriority() == 5)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("6") && (Thread.getPriority() == 6)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("7") && (Thread.getPriority() == 7)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("8") && (Thread.getPriority() == 8)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("9") && (Thread.getPriority() == 9)
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else return selected.equals("10") && (Thread.getPriority() == 10)
                    && (!(Thread.getThreadGroup() == null));
        });
        // Bind SortedList (which contains filteredPriorityList)
        // to tableView in order to display.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Allows thread table to be filtered based on the state of each thread.
     * Possible states are -
     * New, Runnable, Blocked, Waiting, Timed Waiting and Terminated.
     * Can be combined with other filters.
     * @param event - Mouse click on statusFilter.
     */
    public void getStatusFilter(final ActionEvent event) {
        String selected = statusFilter.getValue();
        filteredListState.setPredicate(Thread -> {
            if (selected.equals("All") || selected.equals("State")
                    || selected.isEmpty() || selected.isBlank()) {
                return true;
            } else if (selected.equals("Active")
                    && (!(Thread.getThreadGroup() == null))) {
                return true;
            } else if (selected.equals("New")
                    && Thread.getState().equals(java.lang.Thread.State.NEW)) {
                return true;
            } else if (selected.equals("Runnable") && Thread.getState().
                    equals(java.lang.Thread.State.RUNNABLE)) {
                return true;
            } else if (selected.equals("Blocked") && Thread.getState().
                    equals(java.lang.Thread.State.BLOCKED)) {
                return true;
            } else if (selected.equals("Waiting") && Thread.getState().
                    equals(java.lang.Thread.State.WAITING)) {
                return true;
            } else if (selected.equals("Timed Waiting") && Thread.getState().
                    equals(java.lang.Thread.State.TIMED_WAITING)) {
                return true;
            } else return selected.equals("Terminated")
                    && Thread.getThreadGroup() == null;
        });
        // Bind SortedList (which contains filteredListState)
        // to tableView in order to display.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Allows thread table to be filtered
     * based on the thread group of each thread.
     * The default groups are "system", "InnocuousThreadGroup" and "main".
     * For the purposes of displaying thread information,
     * "Terminated" is counted as a thread group,
     * although technically a terminated thread has a null value for its group.
     * Any new groups will be added upon creation.
     * Can be combined with other filters.
     * @param event - Mouse click on groupFilter drop-down.
     */
    public void getThreadGroupFilter(final ActionEvent event) {
        String selected = groupFilter.getValue();
        filteredListGroup.setPredicate(Thread -> {
            if (selected.equals("All") || selected.equals("Thread Groups")
                    || selected.isEmpty() || selected.isBlank()) {
                return true;
            } else if (selected.equals("Active") && !Thread.getState()
                    .equals(java.lang.Thread.State.TERMINATED)) {
                return true;
            }
            if (Thread.getThreadGroup() == null
                    && selected.equals("Terminated")) {
                return true;
            }
            if (Thread.getState().equals(java.lang.Thread.State.TERMINATED)) {
                return false;
            }
            return selected.equals(Thread.getThreadGroup().getName());
        });
        // Bind SortedList (which contains filteredListGroup)
        // to tableView in order to display.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Allows thread table to be filtered
     * based on whether thread is a daemon or not.
     * Daemon status is either true (Yes) or false (No).
     * Can be combined with other filters.
     * @param event - Mouse click on daemonFilter
     */
    public void getDaemonFilter(final ActionEvent event) {
        String selected = daemonFilter.getValue();
        filteredListDaemon.setPredicate(Thread -> {
            if (selected.equals("Is Daemon?")
                    || selected.isEmpty() || selected.isBlank()) {
                return true;
            } else if (selected.equals("True") && Thread.isDaemon()) {
                return true;
            } else if (selected.equals("All")) {
                return true;
            }
                return selected.equals("False") && !Thread.isDaemon();
        });
        // Bind SortedList (which contains filteredListDaemon)
        // to tableView in order to display.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Initialises the time on the GUI and use TimeLine instance to make it run.
     */
    public void initTime() {
        Date startTime = new Date();
        timer.setText(simpleDateFormat.format(startTime));
    }

    /**
     * Gets Active and Total thread count
     * from Thread Model and display them on GUI.
     * Uses timeline to check value and update.
     */
    public void showThreadCount() {
        int initialTotalCount = model.getTotalThreadCount();
        int initialActiveCount = model.getActiveThreadCount();
        labelTotalCount.setText(String.valueOf(initialTotalCount));
        labelActiveCount.setText(String.valueOf(initialActiveCount));
    }

    /**
     * Create table columns and populate it with details from Thread Model.
     */
    public void initialiseTable() {
        threadNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        threadID.setCellValueFactory(new PropertyValueFactory<>("Id"));
        // Lambda handles the null value returned
        // for the thread group of a terminated thread.
        threadGroup.setCellValueFactory(cellData -> {
            ThreadGroup checkThreadGroup;
            checkThreadGroup = cellData.getValue().getThreadGroup();
            if (checkThreadGroup == null) {
                return new SimpleStringProperty("TERMINATED");
            }
            return new SimpleStringProperty(
                    cellData.getValue().getThreadGroup().getName());
        });
        threadPriority.setCellValueFactory(
                new PropertyValueFactory<>("Priority"));
        threadState.setCellValueFactory(
                new PropertyValueFactory<>("State"));
        threadDaemon.setCellValueFactory(
                new PropertyValueFactory<>("Daemon"));
        // Populate table using Thread Model class.
        tableView.setItems(model.getObservableThreadList());
        tableView.setPlaceholder(new Label("No threads to display..."));
    }

    /**
     * Updates all displayed information every one second.
     */
    public void updateTimer() {
        Timeline updateInfo = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
            // Update thread table
            model.getObservableThreadList();
            tableView.refresh();
            // Update Total and Active Thread Count display on GUI.
            int totalCount = model.getTotalThreadCount();
            int activeCount = model.getActiveThreadCount();
            labelTotalCount.setText(String.valueOf(totalCount));
            labelActiveCount.setText(String.valueOf(activeCount));
            // Update time on GUI
            Date now = new Date();
            timer.setText(simpleDateFormat.format(now));
            // Update thread group filter.
            for (ThreadGroup t : model.getAllGroups()) {
                if (!groupFilter.getItems().contains(t.getName())) {
                    groupFilter.getItems().add(t.getName());
                }
            }
        }));
        updateInfo.setCycleCount(Animation.INDEFINITE);
        updateInfo.play();
    }

    /**
     * Allows for searching by either thread name or thread ID.
     * User can select which to search
     * for from the dropdown menu - defaults to name.
     */
    public void threadSearch() {
        // Populate down-down menu
        searchFilter.getItems().add("Name");
        searchFilter.getItems().add("ID");
        searchFilter.setValue("Name");
        // Configure search box
        searchBox.setPromptText("Search...");
        searchBox.textProperty().addListener((observable, oldValue, newValue)
                -> filteredList.setPredicate(Thread -> {
            String searchFilterValue = searchFilter.getValue();
            String id = String.valueOf(Thread.threadId());
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (searchFilterValue.equals("Name")
                    && Thread.getName().
                    toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return searchFilterValue.equals("ID")
                    && id.contains(lowerCaseFilter);
        }));

        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Create filter that allows filtering of thread table by thread group.
     * For this purpose, "Terminated" is considered a thread group.
     */
    public void groupFilter() {
        groupFilter.getItems().add("All");
        groupFilter.getItems().add("Active");
        groupFilter.getItems().add("system");
        for (ThreadGroup t : model.getAllGroups()) {
            groupFilter.getItems().add(t.getName());
        }
        groupFilter.getItems().add("Terminated");
        groupFilter.setOnAction(this::getThreadGroupFilter);
        groupFilter.setValue("Thread Groups");
    }

    /**
     * Create a filter that allows filtering
     * of the thread table by thread state.
     */
    public void statusFilter() {
        statusFilter.getItems().add("All");
        statusFilter.getItems().add("Active");
        statusFilter.getItems().add("New");
        statusFilter.getItems().add("Runnable");
        statusFilter.getItems().add("Blocked");
        statusFilter.getItems().add("Waiting");
        statusFilter.getItems().add("Timed Waiting");
        statusFilter.getItems().add("Terminated");
        statusFilter.setValue("State");
        statusFilter.setOnAction(this::getStatusFilter);
    }

    /**
     * Create a filter that allows filtering
     * of the thread table by thread priority (1-10).
     */
    public void priorityFilter() {
        priorityFilter.setValue("Priority");
        priorityFilter.getItems().add("All");
        for (int i = 1; i <= 10; i++) {
            priorityFilter.getItems().add(String.valueOf(i));
        }
        priorityFilter.setOnAction(this::getPriorityFilter);
    }

    /**
     * Create a filter that allows filtering
     * of the thread table by whether a thread is a daemon or not.
     */
    public void daemonFilter() {
        daemonFilter.getItems().add("All");
        daemonFilter.getItems().add("True");
        daemonFilter.getItems().add("False");
        daemonFilter.setValue("Is Daemon?");
        daemonFilter.setOnAction(this::getDaemonFilter);
    }

    @FXML
    private void initialize() {
        initTime();
        showThreadCount();
        initialiseTable();
        updateTimer();
        groupFilter();
        threadSearch();
        statusFilter();
        priorityFilter();
        daemonFilter();
        }
}
