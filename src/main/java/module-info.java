module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    //opens com.example.demo to javafx.fxml;
    opens com.example.demo to javafx.base, javafx.fxml, javafx.controls;
    exports com.example.demo;
}