module com.kavi.dotsandboxes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.google.firebase.admin;
    requires google.auth.library.oauth2.http;
    requires com.google.code.gson;
    requires org.slf4j.simple;
    requires org.nanohttpd;

    opens com.kavi.dotsandboxes to javafx.graphics, javafx.fxml; // Open your main application package
    exports com.kavi.dotsandboxes;
}
