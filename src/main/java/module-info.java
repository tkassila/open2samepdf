module com.metait.open2samepdf {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.metait.open2samepdf to javafx.fxml;
    exports com.metait.open2samepdf;
}