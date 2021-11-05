module dz.webinar2.lesson4_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens dz.webinar2.lesson4_javafx to javafx.fxml;
    exports dz.webinar2.lesson4_javafx;
}