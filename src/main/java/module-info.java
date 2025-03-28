module com.kavi.dotsandboxes {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.kavi.dotsandboxes to javafx.fxml;
    exports com.kavi.dotsandboxes;
}
