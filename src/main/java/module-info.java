module co.edu.uniquindio.sameday {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.sameday to javafx.fxml;
    exports co.edu.uniquindio.sameday;
}