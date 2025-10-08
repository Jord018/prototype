package org.audioconvert.prototype.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.audioconvert.prototype.controller.Convert;
import org.audioconvert.prototype.model.Audio;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class MainScreen {
    @FXML private RadioButton quality1, quality2, quality3, quality4;
    @FXML private Button mp3Btn, m4aBtn, wavBtn, flacBtn;
    @FXML private Button PlayButton;
    @FXML private Button StopButton;
    @FXML private Button configBtn;
    @FXML private Button convertBtn;
    @FXML private Label dropBarLabel;
    @FXML private ListView<String> dropBarList;
    @FXML private ImageView DropFilesPic;

    private List<File> droppedFiles = new ArrayList<>();
    private Stage currentPopup;
    private Stage primaryStage;
    private Audio audio = new Audio();
    private MediaPlayer mediaPlayer;

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
    private void playSelectedFile() {
        String selectedFileName = dropBarList.getSelectionModel().getSelectedItem();
        if (selectedFileName == null) {
            System.out.println("No file selected in the list.");
            return;
        }

        File fileToPlay = null;
        for (File file : droppedFiles) {
            if (file.getName().equals(selectedFileName)) {
                fileToPlay = file;
                break;
            }
        }

        if (fileToPlay == null) {
            System.out.println("Selected file object not found.");
            return;
        }

        // 1. หยุดเพลงเก่า (ถ้ามี)
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            // ไม่ต้อง dispose ถ้าต้องการให้ MediaPlayer พร้อมเล่นต่อทันที
        }

        // 2. หากไฟล์ที่เลือกเป็นไฟล์เดียวกับที่กำลังเล่น/หยุดชั่วคราว ให้เล่นต่อ
        if (mediaPlayer != null && mediaPlayer.getMedia().getSource().equals(fileToPlay.toURI().toString())) {
            mediaPlayer.play();
            System.out.println("Resuming: " + selectedFileName);
            return;
        }


        try {
            // 3. สร้าง Media และ MediaPlayer ตัวใหม่
            if (mediaPlayer != null) {
                mediaPlayer.dispose(); // ทิ้ง MediaPlayer เก่าก่อนสร้างใหม่สำหรับไฟล์ใหม่
            }
            Media media = new Media(fileToPlay.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);

            // 4. เริ่มเล่น
            mediaPlayer.play();
            System.out.println("Playing: " + selectedFileName);

            // 5. ตั้งค่า End of Media และ Error Handling
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Playback finished.");
                // สามารถเพิ่มโค้ดเปลี่ยนไอคอน Play/Stop ที่นี่ได้
            });
            mediaPlayer.setOnError(() -> {
                System.err.println("Media Player Error: " + mediaPlayer.getError());
            });

        } catch (Exception e) {
            System.err.println("Failed to play file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            System.out.println("Playback stopped.");
        }
    }

    private void updatePlaceholderVisibility() {
        if (DropFilesPic != null) {
            // ตรวจสอบว่ามีไฟล์หรือไม่
            boolean hasFiles = droppedFiles != null && !droppedFiles.isEmpty();

            // ซ่อน/แสดง Placeholder
            DropFilesPic.setVisible(!hasFiles);
            DropFilesPic.setManaged(!hasFiles);
        }
    }

    private void setupDeleteFileOnKeyPress() {
        dropBarList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                final int selectedIndex = dropBarList.getSelectionModel().getSelectedIndex();

                if (selectedIndex != -1) {
                    // ตรวจสอบว่าไฟล์ที่ถูกลบเป็นไฟล์แรกหรือไม่
                    boolean wasFirstFile = selectedIndex == 0;

                    // 1. ตรวจสอบและหยุดเล่นเพลงหากไฟล์ที่กำลังเล่นถูกลบ
                    if (mediaPlayer != null && droppedFiles.get(selectedIndex).toURI().toString().equals(mediaPlayer.getMedia().getSource())) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                        mediaPlayer = null;
                    }

                    // 2. ลบไฟล์ออกจากรายการ
                    if (selectedIndex < droppedFiles.size()) {
                        droppedFiles.remove(selectedIndex);
                    }
                    dropBarList.getItems().remove(selectedIndex);

                    // 3. จัดการการแสดงผล
                    updatePlaceholderVisibility();

                    // 4. อัปเดต audio.setPath
                    if (wasFirstFile) {
                        if (!droppedFiles.isEmpty()) {
                            audio.setPath(droppedFiles.get(0));
                        } else {
                            audio.setPath(null);
                        }
                    }
                    event.consume();
                }
            }
        });
    }

    @FXML
    private void initialize() {

        // *** เชื่อมต่อปุ่มเล่นและปุ่มหยุด ***
        PlayButton.setOnAction(event -> playSelectedFile());
        StopButton.setOnAction(event -> stopPlayback());

        // Set up drag and drop functionality
        setupDragAndDrop(audio);

        setupDeleteFileOnKeyPress();
        updatePlaceholderVisibility();

        ToggleGroup qualityGroup = new ToggleGroup();
        quality1.setToggleGroup(qualityGroup);
        quality2.setToggleGroup(qualityGroup);
        quality3.setToggleGroup(qualityGroup);
        quality4.setToggleGroup(qualityGroup);

        updateQualityOptions("mp3");

        configBtn.setOnAction(event -> showConfigPopup());

        convertBtn.setOnAction(event -> {
            Show_ProgresPopup();
        });
        mp3Btn.setOnAction(e -> {
            audio.setFormat("mp3");
            System.out.println("Format: " + audio.getFormat());
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
        dropBarList.setOnDragOver(event -> {
            if (event.getGestureSource() != dropBarList &&
                    event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        // Set up drag entered event (visual feedback)
        dropBarList.setOnDragEntered(event -> {
            if (event.getGestureSource() != dropBarList &&
                    event.getDragboard().hasFiles()) {

            }
            event.consume();
        });

        // Set up drag exited event (reset visual feedback)
        dropBarList.setOnDragExited(event -> {
            event.consume();
        });

        // Handle dropped files
        dropBarList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            // กำหนดส่วนขยายไฟล์เสียงที่ยอมรับ
            final java.util.Set<String> AUDIO_EXTENSIONS = java.util.Set.of(
                    "mp3", "m4a", "wav", "flac", "ogg", "aac", "wma", "aiff"
            );

            if (db.hasFiles()) {
                List<File> newlyDroppedFiles = db.getFiles();

                if (!newlyDroppedFiles.isEmpty()) {

                    List<File> filesToAdd = new java.util.ArrayList<>();
                    // ดึงรายการชื่อไฟล์ปัจจุบันจาก ListView
                    javafx.collections.ObservableList<String> dropBarItems = dropBarList.getItems();

                    for (File file : newlyDroppedFiles) {
                        String fileName = file.getName();

                        // 1. ตรวจสอบส่วนขยายไฟล์ว่าเป็นไฟล์เสียงหรือไม่
                        String extension = "";
                        int i = fileName.lastIndexOf('.');
                        if (i > 0) {
                            extension = fileName.substring(i + 1).toLowerCase();
                        }

                        if (!AUDIO_EXTENSIONS.contains(extension)) {
                            System.out.println("File skipped (Not an audio file): " + fileName);
                            continue;
                        }

                        // 2. ตรวจสอบความซ้ำซ้อนของชื่อไฟล์
                        if (!dropBarItems.contains(fileName)) {
                            filesToAdd.add(file);
                        } else {
                            System.out.println("File skipped (Duplicate name): " + fileName);
                        }
                    }

                    // 3. อัปเดตรายการไฟล์ทั้งหมดและ ListView
                    if (!filesToAdd.isEmpty()) {
                        if (droppedFiles == null) {
                            droppedFiles = new java.util.ArrayList<>();
                        }

                        // เพิ่มไฟล์ใหม่เข้าในรายการเก็บไฟล์จริง
                        droppedFiles.addAll(filesToAdd);

                        // เพิ่มชื่อไฟล์ใหม่ลงใน ListView
                        for (File file : filesToAdd) {
                            dropBarItems.add(file.getName());
                            System.out.println("Added file: " + file.getAbsolutePath());
                        }

                        // === 4. จัดเรียงชื่อไฟล์ใน ListView ตามลำดับตัวอักษร ===
                        dropBarItems.sort(String::compareTo);
                        // หรือ dropBarItems.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
                        // ถ้าต้องการจัดเรียงโดยไม่คำนึงถึงตัวพิมพ์เล็ก-ใหญ่
                        // =======================================================

                        if (audio.getPath() == null) {
                            audio.setPath(droppedFiles.get(0));
                        }
                        success = true;
                    } else {
                        System.out.println("No valid, non-duplicate audio files were dropped.");
                    }
                }
            }

            if (DropFilesPic != null) {
                // ซ่อนรูปภาพถ้ามีไฟล์ใดๆ อยู่ในรายการ (droppedFiles ไม่เป็น null และไม่ว่างเปล่า)
                boolean hasFiles = droppedFiles != null && !droppedFiles.isEmpty();
                DropFilesPic.setVisible(!hasFiles);
                DropFilesPic.setManaged(!hasFiles);

                // ทำให้ ListView แสดงผลเต็มพื้นที่เมื่อ Placeholder ถูกซ่อน
                dropBarList.setVisible(true);
                dropBarList.setManaged(true);
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
        // Check if a file is selected
        if (audio.getPath() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No File Selected");
            alert.setContentText("Please select or drop a file to convert first.");
            alert.showAndWait();
            return;
        }
        
        // Set the target file path
        String sourcePath = audio.getPath().getAbsolutePath();
        String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf('.')) + "." + audio.getFormat();
        audio.setTarget(new File(targetPath));

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
