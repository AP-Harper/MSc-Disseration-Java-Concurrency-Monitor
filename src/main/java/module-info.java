module com.andrew.concurrency {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.andrew.concurrency to javafx.fxml;
    exports com.andrew.concurrency;


}