package com.andrew.concurrency;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * Controller for Help Screen.
 * Allows the user to access information
 * about the program, and also
 * load the User Guide (opens a text file
 * outside the program).
 */
public class HelpController {
    @FXML
    private Button btnReturn;


    /**
     * Closes Help pop-up to return to main window.
     */
    public void clickCloseWindow() {
        Stage thisStage = (Stage) btnReturn.getScene().getWindow();
        thisStage.close();
    }

    /**
     * Opens the User Guide as a text file.
     * This opens outside the program.
     * @throws IOException - Opens text file.
     */
    @FXML
    public void clickOpenHelp() throws IOException {
        File userGuide = new File(
                "./src/main/java/com/andrew/Help/User_Guide.txt");
        Desktop desktop = java.awt.Desktop.getDesktop();
        desktop.open(userGuide);
    }
}

