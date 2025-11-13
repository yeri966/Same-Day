module co.edu.uniquindio.sameday {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;



    opens co.edu.uniquindio.sameday to javafx.fxml;
    exports co.edu.uniquindio.sameday.app;
    opens co.edu.uniquindio.sameday.app to javafx.fxml;

    opens co.edu.uniquindio.sameday.controllers to javafx.fxml;
    opens co.edu.uniquindio.sameday.models to javafx.fxml, javafx.base;
    opens co.edu.uniquindio.sameday.models.structural.decorator to javafx.fxml;
    opens co.edu.uniquindio.sameday.models.structural.facade to javafx.fxml;  // NUEVA LÍNEA
    opens co.edu.uniquindio.sameday.models.creational.singleton to javafx.fxml;


}
