package org.audioconvert.prototype.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import org.audioconvert.prototype.controller.Input_Handle;
import org.audioconvert.prototype.model.Audio;

import java.io.File;
import java.util.List;

public class MainScreen {
    @FXML
    private Button closeBtn;
    @FXML private Button mp3Btn;
    @FXML private Button m4aBtn;
    @FXML private Button wavBtn;
    @FXML private Button flacBtn;
    @FXML private Button configBtn;
    @FXML private Button convertBtn;
    @FXML private Label dropBarLabel;
    @FXML private Button Target;
    @FXML private Slider Quality;
    @FXML private Label NumQuality;
    private List<File> droppedFiles;
    @FXML
    private void initialize() {
        Input_Handle inputHandler = new Input_Handle();

        // Initialize buttons
        closeBtn.setOnAction(e -> System.exit(0));
        mp3Btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                inputHandler.setFormat("mp3");
            }
        });
        m4aBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                inputHandler.setFormat("m4a");
            }
        }));
        wavBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                inputHandler.setFormat("wav");
            }
        }));
        flacBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                inputHandler.setFormat("flac");
            }
        }));
        configBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                inputHandler.setFormat("mp3");
            }
        }));
        convertBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                inputHandler.combine();
            }
        }));
        Target.setOnAction(e -> {
            DirectoryChooser outputDir = new DirectoryChooser();
            outputDir.setTitle("Select Output Directory");
            File selectedDir = outputDir.showDialog(null);
            System.out.println(selectedDir.getPath()+"/output."+inputHandler.getFormat());
            inputHandler.setTarget(selectedDir.getAbsolutePath()+"/output."+inputHandler.getFormat());
});
        // Set up drag and drop handlers
        setupDragAndDrop(inputHandler);
        setupSlider(inputHandler);
    }
    private void setupDragAndDrop(Input_Handle inputHandler) {
        // Set up drag over event
        dropBarLabel.setOnDragOver(event -> {
            if (event.getGestureSource() != dropBarLabel &&
                    event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        // Set up drag entered event (visual feedback)
        dropBarLabel.setOnDragEntered(event -> {
            if (event.getGestureSource() != dropBarLabel &&
                    event.getDragboard().hasFiles()) {

            }
            event.consume();
        });

        // Set up drag exited event (reset visual feedback)
        dropBarLabel.setOnDragExited(event -> {
            event.consume();
        });

        // Handle dropped files
        dropBarLabel.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                droppedFiles = db.getFiles();
                if (!droppedFiles.isEmpty()) {
                    // Update the label to show the first file name
                    String fileName = droppedFiles.get(0).getName();
                    if (droppedFiles.size() > 1) {
                        fileName += " and " + (droppedFiles.size() - 1) + " more files";
                    }
                    dropBarLabel.setText(fileName);
                    File file = droppedFiles.get(0);
                    inputHandler.setFile(file);
                    success = true;
                }
                System.out.println("Dropped files: " + droppedFiles.get(0).getName());
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }
    private void setupSlider(Input_Handle inputHandler) {
        Quality.setMin(0);
        Quality.setMax(320);
        Quality.setValue(64); // ค่าเริ่มต้น

        Quality.setShowTickMarks(false);   // แสดงขีดเล็ก
        Quality.setShowTickLabels(true);  // แสดงตัวเลข
        Quality.setMajorTickUnit(25);     // ระยะห่างระหว่างขีดใหญ่
        Quality.setMinorTickCount(3);     // ขีดย่อย
        Quality.setBlockIncrement(64);     // เลื่อนทีละกี่หน่วย

        // ฟังการเปลี่ยนแปลงค่า
        Quality.valueProperty().addListener((observable, oldValue, newValue) -> {
            inputHandler.setQuality(newValue.intValue());
        });

    }

}
