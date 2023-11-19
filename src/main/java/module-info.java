module com.kursinis.ptkursinis {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    opens com.kursinis.ptkursinis to javafx.fxml;
    exports com.kursinis.ptkursinis;
    opens com.kursinis.ptkursinis.model to javafx.fxml, org.hibernate.orm.core;
    exports com.kursinis.ptkursinis.model;
    opens com.kursinis.ptkursinis.fxControllers to javafx.fxml;
    exports com.kursinis.ptkursinis.fxControllers to javafx.fxml;
}