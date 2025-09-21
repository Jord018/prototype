package org.audioconvert.prototype.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.audioconvert.prototype.controller.Convert;
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
    @FXML private Slider Quality;
    @FXML private Label NumQuality;
    private List<File> droppedFiles;
    private Stage currentPopup;
    @FXML
    private void initialize() {
        Audio audio = new Audio();
        
        // Config button action
        configBtn.setOnAction(event -> showConfigPopup());

        // Initialize buttons
        closeBtn.setOnAction(e -> System.exit(0));
        mp3Btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                audio.setFormat("mp3");
                System.out.println("Format: " + audio.getFormat());
            }
        });
        m4aBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                audio.setFormat("m4a");
            }
        }));
        wavBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                audio.setFormat("wav");
            }
        }));
        flacBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                audio.setFormat("flac");
            }
        }));
        convertBtn.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DirectoryChooser outputDir = new DirectoryChooser();
                outputDir.setTitle("Select Output Directory");
                File selectedDir = outputDir.showDialog(null);
                System.out.println(selectedDir.getPath()+"/output."+audio.getFormat());
                audio.setTarget(new File(selectedDir.getAbsolutePath() + "/output." + audio.getFormat()));
                Convert.Convert(audio);
            }
        }));

        // Set up drag and drop handlers
        setupDragAndDrop(audio);
        setupSlider(audio);
    }
    private void setupDragAndDrop(Audio audio) {
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
                    // สร้างข้อความชื่อไฟล์ทั้งหมด
                    StringBuilder fileNames = new StringBuilder();
                    for (int i = 0; i < droppedFiles.size(); i++) {
                        File file = droppedFiles.get(i);
                        fileNames.append(file.getName());
                        if (i < droppedFiles.size() - 1) {
                            fileNames.append(", ");
                        }

                        System.out.println("Dropped file: " + file.getAbsolutePath());
                    }


                    dropBarLabel.setText(fileNames.toString());


                    audio.setPath(droppedFiles.get(0));

                    success = true;
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

    }

    private void setupSlider(Audio audio) {
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
            audio.setQuality(newValue.intValue());
        });

    }
    @FXML
    private void showConfigPopup() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ConfigUI.fxml"));
            Parent root = loader.load();
            ConfigScreen controller = loader.getController();
            // Create a new stage for the config window
            Stage configStage = new Stage();
            configStage.initModality(Modality.APPLICATION_MODAL);
            configStage.initStyle(StageStyle.UTILITY);
            configStage.setTitle("Configuration");

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            configStage.setScene(scene);
            configStage.setResizable(false);
            this.currentPopup = configStage;

            // Show the config window
            controller.setStage(configStage);
            configStage.showAndWait();
            
            // After window is closed, get the config
            Audio config = controller.getConfig();
            System.out.println("=== Configuration Saved ===");
            System.out.println("Bitrate: " + config.getBitrate() + " kbps");
            System.out.println("VBR: " + config.getVBR());
            System.out.println("Sample Rate: " + config.getSamplingRate() + " kHz");
            System.out.println("Channels: " + config.getChannels());
            System.out.println("==========================");

        } catch (Exception e) {
            e.printStackTrace();
            // Show error message to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load configuration");
            alert.setContentText("Could not open the configuration window. Please try again.\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}
