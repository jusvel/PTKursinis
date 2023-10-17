module com.kursinis.ptkursinis {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires lombok;


    opens com.kursinis.ptkursinis to javafx.fxml;
    exports com.kursinis.ptkursinis;
    exports com.kursinis.ptkursinis.fxControllers;
    opens com.kursinis.ptkursinis.fxControllers to javafx.fxml;
}