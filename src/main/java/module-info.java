module org.audioconvert.prototype {
    requires javafx.controls;
    requires javafx.fxml;
    requires jave.core;
    requires javafx.graphics;
    requires java.desktop;

    opens org.audioconvert.prototype to javafx.fxml;
    opens org.audioconvert.prototype.controller to javafx.fxml;
    opens org.audioconvert.prototype.view to javafx.fxml;
    exports org.audioconvert.prototype;
    exports org.audioconvert.prototype.controller;
    exports org.audioconvert.prototype.view;
}