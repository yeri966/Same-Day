module co.edu.uniquindio.sameday {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jdk.sctp;


    opens co.edu.uniquindio.sameday to javafx.fxml;
    exports co.edu.uniquindio.sameday.app;
    opens co.edu.uniquindio.sameday.app to javafx.fxml;
    opens co.edu.uniquindio.sameday.controllers to javafx.fxml;
}