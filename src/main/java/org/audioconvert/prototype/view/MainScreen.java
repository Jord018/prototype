package org.audioconvert.prototype.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.audioconvert.prototype.controller.Convert;
import org.audioconvert.prototype.model.Audio;
import ws.schild.jave.EncoderException;

import java.io.File;
import java.util.List;

public class MainScreen {
    @FXML private RadioButton quality1, quality2, quality3, quality4;
    @FXML private Button mp3Btn, m4aBtn, wavBtn, flacBtn;
    @FXML private Button PlayBtn;
    @FXML private Button configBtn;
    @FXML private Button convertBtn;
    @FXML private Label dropBarLabel;
    private List<File> droppedFiles;
    private Stage currentPopup;
    private Stage primaryStage;
    private Audio audio = new Audio();

    private void updateQualityOptions(String format) {
        switch(format) {
            case "mp3":
                enableQualities(true);
                quality1.setText("64 kbps");
                quality2.setText("128 kbps");
                quality3.setText("192 kbps");
                quality4.setText("320 kbps");
                quality1.setSelected(true);
                audio.setQuality(64);
                break;
            case "m4a":
                enableQualities(true);
                quality1.setText("64 kbps");
                quality2.setText("128 kbps");
                quality3.setText("160 kbps");
                quality4.setText("256 kbps");
                quality1.setSelected(true);
                audio.setQuality(64);
                break;
            case "wav":
                enableQualities(true);
                quality1.setText("20 kHz");
                quality2.setText("44.1 kHz");
                quality3.setText("48 kHz");
                quality4.setText("96 kHz");
                quality1.setSelected(true);
                audio.setQuality(20); // หรือ 44.1 แล้วแต่ต้องการ
                break;
            case "flac":
                enableQualities(false);
                quality1.setText("N/A");
                quality2.setText("N/A");
                quality3.setText("N/A");
                quality4.setText("N/A");
                break;
        }
    }



    private void enableQualities(boolean enable) {
        quality1.setDisable(!enable);
        quality2.setDisable(!enable);
        quality3.setDisable(!enable);
        quality4.setDisable(!enable);
    }
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {

        ToggleGroup qualityGroup = new ToggleGroup();
        quality1.setToggleGroup(qualityGroup);
        quality2.setToggleGroup(qualityGroup);
        quality3.setToggleGroup(qualityGroup);
        quality4.setToggleGroup(qualityGroup);

        // set format default
        updateQualityOptions("mp3");

        configBtn.setOnAction(event -> showConfigPopup());

        convertBtn.setOnAction(event -> {
            System.out.println("Convert button clicked!");
        });

            mp3Btn.setOnAction(e -> {
                audio.setFormat("mp3");
                updateQualityOptions("mp3");
            });
            m4aBtn.setOnAction(e -> {
                audio.setFormat("m4a");
                updateQualityOptions("m4a");
            });
            wavBtn.setOnAction(e -> {
                audio.setFormat("wav");
                updateQualityOptions("wav");
            });
            flacBtn.setOnAction(e -> {
                audio.setFormat("flac");
                updateQualityOptions("flac");
            });

            qualityGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (flacBtn.isFocused() || quality1.isDisabled()) return;
                if (quality1.isSelected()) audio.setQuality(parseQuality(quality1.getText()));
                else if (quality2.isSelected()) audio.setQuality(parseQuality(quality2.getText()));
                else if (quality3.isSelected()) audio.setQuality(parseQuality(quality3.getText()));
                else if (quality4.isSelected()) audio.setQuality(parseQuality(quality4.getText()));
            });
        }

    private int parseQuality(String txt) {
        // "128 kbps" or "44.1 kHz"
        String num = txt.split(" ")[0].replace("kHz", "").replace("kbps", "");
        return (int) Double.parseDouble(num);
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

    @FXML
    private void showConfigPopup() {
        try {

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ConfigUI.fxml"));
            Parent root = loader.load();
            ConfigScreen controller = loader.getController();
            controller.setConfig(audio);

            // Create a new stage for the config window
            Stage configStage = new Stage();
            if (primaryStage != null) {
                configStage.initOwner(primaryStage);  // Set the owner to the main application stage
            }
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

    @FXML
    private void Show_ProgresPopup() {
        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Converting...");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        Label statusLabel = new Label("Preparing conversion...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        Label fileNameLabel = new Label("");
        fileNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        VBox vbox = new VBox(15, statusLabel, progressBar, fileNameLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");

        Scene scene = new Scene(vbox, 400, 150);
        popupStage.setScene(scene);
        
        // Show the popup immediately
        popupStage.show();

        // Create a task for the conversion
        Task<Void> conversionTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Update UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        statusLabel.setText("Starting conversion...");
                        if (audio.getPath() != null) {
                            fileNameLabel.setText("File: " + audio.getPath().getName());
                        }
                    });

                    // Start the conversion with progress updates
                    Convert.convertWithProgress(audio, progress -> {
                        // This runs on the encoder's thread, so we need to update UI on the JavaFX thread
                        double percentage = progress * 100;
                        Platform.runLater(() -> {
                            progressBar.setProgress(progress);
                            statusLabel.setText(String.format("Converting... %.1f%%", percentage));
                        });
                    });
                    
                    // Conversion completed successfully
                    Platform.runLater(() -> {
                        statusLabel.setText("Conversion completed successfully!");
                        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    });
                    
                } catch (Exception e) {
                    // Handle any errors
                    Platform.runLater(() -> {
                        statusLabel.setText("Error during conversion: " + e.getMessage());
                        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        e.printStackTrace();
                    });
                } finally {
                    // Close the popup after a short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500); // Show success/error message for 1.5 seconds
                            Platform.runLater(popupStage::close);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                }
                return null;
            }
        };

        // Start the conversion in a background thread
        new Thread(conversionTask, "Conversion-Thread").start();
    }
}
