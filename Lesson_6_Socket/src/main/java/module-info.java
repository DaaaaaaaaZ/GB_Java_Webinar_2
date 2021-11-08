module dz.webinar2.lesson_6_socket {
    requires javafx.controls;
    requires javafx.fxml;


    opens dz.webinar2.lesson6 to javafx.fxml;
    exports dz.webinar2.lesson6;
}