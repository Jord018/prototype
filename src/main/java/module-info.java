module org.audioconvert.prototype {
    requires javafx.controls;
    requires javafx.fxml;
    requires jave.core;


    opens org.audioconvert.prototype to javafx.fxml;
    exports org.audioconvert.prototype;
}