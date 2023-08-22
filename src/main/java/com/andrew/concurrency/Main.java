package com.andrew.concurrency;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Launches program.
 */
public class Main extends Application {
    @FXML
    private Stage stage;

    /**
     * Main method for starting program.
     * @param stage - Main stage of program.
     * @throws Exception - Starts program.
     */
    @Override
    public void start(Stage stage) throws Exception {
        launchTestThread();
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/Fxml/ThreadTable.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Concurrency Monitor");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * For opening Help pop-up window. This window must be closed
     * before user can return to main screen.
     * @throws IOException - Opens Help pop-up
     */
    public void openHelpScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Fxml/Help.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent, 600, 400);
        Stage helpStage = new Stage();
        helpStage.setTitle("Help");
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setScene(scene);
        helpStage.show();
    }

    /**
     * Used for testing - Creates a series of threads with different attributes
     * to ensure thread table and graph display correct information.
     */
    public void launchTestThread() {
        // Launch some initial test threads when program starts.
        BlockedThreadCreator blockedThreadCreator = new BlockedThreadCreator();
        ThreadGroup newGroup = new ThreadGroup("New Group");
        blockedThreadCreator.blockedThreads();
        Thread threadInNewGroup = new Thread(
                newGroup, new ThreadCreator("Group Test Thread", 5000000));
        threadInNewGroup.setPriority(3);
        Thread threadToTerminate = new Thread(
                new ThreadCreator("An ex-thread", 2000));
        threadToTerminate.start();
        // Launches more test threads every 10 seconds
        // to simulate real use case.
        Timeline blockedLauncher = new Timeline(
        new KeyFrame(Duration.seconds(10), event -> {
            blockedThreadCreator.blockedThreads();
            Thread thread1 = new Thread(
                    new ThreadCreator("Timed Thread", 5000000));
            thread1.start();
            Thread threadInNewGroup2 = new Thread(newGroup, new ThreadCreator(
                    "Group Test Thread 2", 5000000));
            threadInNewGroup2.setPriority(1);
            Thread threadInNewGroup3 = new Thread(newGroup, new ThreadCreator(
                    "Group Test Thread 3", 5000000));
            threadInNewGroup3.setDaemon(true);
            Thread newThread = new Thread(
                    new ThreadCreator("A further thread", 5000000));
            newThread.setDaemon(true);
            threadInNewGroup2.start();
            threadInNewGroup3.start();
            newThread.start();
            // Create terminated thread
            Thread threadToTerminate2 = new Thread(
                    new ThreadCreator("DEAD THREAD", 2000));
            threadToTerminate2.start();
        }));
        blockedLauncher.setCycleCount(Animation.INDEFINITE);
        blockedLauncher.play();
    }

    /**
     * Main method of program.
     * @param args - Main
     */
    public static void main(String[] args) {
        launch();
    }
}
