package org.audioconvert.prototype.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.audioconvert.prototype.controller.Convert;
import org.audioconvert.prototype.exception.SameFileTypeException;
import org.audioconvert.prototype.model.Audio;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class MainScreen {
    @FXML private RadioButton quality1, quality2, quality3, quality4;
    @FXML private Button mp3Btn, m4aBtn, wavBtn, flacBtn;
    @FXML private Button PlayButton;
    @FXML private Button StopButton;
    @FXML private Button configBtn;
    @FXML private Button convertBtn;
    @FXML private ListView<String> dropBarList;
    @FXML private ImageView DropFilesPic;

    private List<File> droppedFiles = new ArrayList<>();
    private Stage currentPopup;
    private Stage primaryStage;
    private Audio audio = new Audio();
    private MediaPlayer mediaPlayer;

    private void updateQualityOptions(String format) {
        final int DEFAULT_SAMPLING_RATE = 44100;
        final int DEFAULT_CHANNELS = 1;

        switch(format) {
            case "mp3":
                enableQualities(true);
                quality1.setText("64 kbps");
                quality1.setOnAction(event -> {
                    audio.setBitrate(64000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 64kbps, 44.1kHz, 1ch");
                });
                quality2.setText("128 kbps");
                quality2.setOnAction(event -> {
                    audio.setBitrate(128000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 128kbps, 44.1kHz, 1ch");
                });
                quality3.setText("192 kbps");
                quality3.setOnAction(event -> {
                    audio.setBitrate(192000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 192kbps, 44.1kHz, 1ch");
                });
                quality4.setText("320 kbps");
                quality4.setOnAction(event -> {
                    audio.setBitrate(320000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 320kbps, 44.1kHz, 1ch");
                });
                quality3.setSelected(true);
                break;
            case "m4a":
                enableQualities(true);
                quality1.setText("64 kbps");
                quality1.setOnAction(event -> {
                    audio.setBitrate(64000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 64kbps, 44.1kHz, 1ch");
                });
                quality2.setText("128 kbps");
                quality2.setOnAction(event -> {
                    audio.setBitrate(128000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 128kbps, 44.1kHz, 1ch");
                });
                quality3.setText("160 kbps");
                quality3.setOnAction(event -> {
                    audio.setBitrate(160000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 160kbps, 44.1kHz, 1ch");
                });
                quality4.setText("256 kbps");
                quality4.setOnAction(event -> {
                    audio.setBitrate(256000);
                    audio.setSamplingRate(DEFAULT_SAMPLING_RATE);
                    audio.setChannels(DEFAULT_CHANNELS);
                    System.out.println("Main Preset Set: 256kbps, 44.1kHz, 1ch");
                });
                quality2.setSelected(true);
                break;
            case "wav":
                enableQualities(true);
                quality1.setText("22.05 kHz");
                quality1.setOnAction(event -> {
                    audio.setSamplingRate(22050);
                    System.out.println("Main Preset Set (WAV): 22.05kHz");
                });
                quality2.setText("44.1 kHz");
                quality2.setOnAction(event -> {
                    audio.setSamplingRate(44100);
                    System.out.println("Main Preset Set (WAV): 44.1kHz");
                });
                quality3.setText("48 kHz");
                quality3.setOnAction(event -> {
                    audio.setSamplingRate(48000);
                    System.out.println("Main Preset Set (WAV): 48kHz");
                });
                quality4.setText("96 kHz");
                quality4.setOnAction(event -> {
                    audio.setSamplingRate(96000);
                    System.out.println("Main Preset Set (WAV): 96kHz");
                });
                quality2.setSelected(true);
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

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        if (mediaPlayer != null && mediaPlayer.getMedia().getSource().equals(fileToPlay.toURI().toString())) {
            mediaPlayer.play();
            System.out.println("Resuming: " + selectedFileName);
            return;
        }


        try {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            Media media = new Media(fileToPlay.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.play();
            System.out.println("Playing: " + selectedFileName);

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Playback finished.");
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
            boolean hasFiles = droppedFiles != null && !droppedFiles.isEmpty();

            DropFilesPic.setVisible(!hasFiles);
            DropFilesPic.setManaged(!hasFiles);
        }
    }

    private void setupDeleteFileOnKeyPress() {
        dropBarList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                final int selectedIndex = dropBarList.getSelectionModel().getSelectedIndex();

                if (selectedIndex != -1) {
                    boolean wasFirstFile = selectedIndex == 0;

                    if (mediaPlayer != null && droppedFiles.get(selectedIndex).toURI().toString().equals(mediaPlayer.getMedia().getSource())) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                        mediaPlayer = null;
                    }

                    if (selectedIndex < droppedFiles.size()) {
                        droppedFiles.remove(selectedIndex);
                    }
                    dropBarList.getItems().remove(selectedIndex);

                    updatePlaceholderVisibility();

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

        PlayButton.setOnAction(event -> playSelectedFile());
        StopButton.setOnAction(event -> stopPlayback());

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

            showProgressPopup();
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
        String num = txt.split(" ")[0].replace("kHz", "").replace("kbps", "");
        return (int) Double.parseDouble(num);
    }


    private void setupDragAndDrop(Audio audio) {
        dropBarList.setOnDragOver(event -> {
            if (event.getGestureSource() != dropBarList &&
                    event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dropBarList.setOnDragEntered(event -> {
            if (event.getGestureSource() != dropBarList &&
                    event.getDragboard().hasFiles()) {

            }
            event.consume();
        });

        dropBarList.setOnDragExited(event -> {
            event.consume();
        });

        dropBarList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            final java.util.Set<String> AUDIO_EXTENSIONS = java.util.Set.of(
                    "mp3", "m4a", "wav", "flac", "ogg", "aac", "wma", "aiff"
            );

            if (db.hasFiles()) {
                List<File> newlyDroppedFiles = db.getFiles();

                if (!newlyDroppedFiles.isEmpty()) {

                    List<File> filesToAdd = new java.util.ArrayList<>();

                    javafx.collections.ObservableList<String> dropBarItems = dropBarList.getItems();

                    for (File file : newlyDroppedFiles) {
                        String fileName = file.getName();

                        String extension = "";
                        int i = fileName.lastIndexOf('.');
                        if (i > 0) {
                            extension = fileName.substring(i + 1).toLowerCase();
                        }

                        if (!AUDIO_EXTENSIONS.contains(extension)) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error");
                            alert.setHeaderText("Failed to add files");
                            alert.setContentText("(Not an audio file): " + fileName);
                            alert.show();
                            System.out.println("File skipped (Not an audio file): " + fileName);
                            continue;
                        }

                        if (!dropBarItems.contains(fileName)) {
                            filesToAdd.add(file);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error");
                            alert.setHeaderText("Failed to add files");
                            alert.setContentText("(Duplicate name): " + fileName);
                            alert.show();
                            System.out.println("File skipped (Duplicate name): " + fileName);
                            System.out.println("File skipped (Duplicate name): " + fileName);
                        }
                    }

                    if (!filesToAdd.isEmpty()) {
                        if (droppedFiles == null) {
                            droppedFiles = new java.util.ArrayList<>();
                        }

                        droppedFiles.addAll(filesToAdd);

                        for (File file : filesToAdd) {
                            dropBarItems.add(file.getName());
                            System.out.println("Added file: " + file.getAbsolutePath());
                        }

                        dropBarItems.sort(String::compareTo);

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

                boolean hasFiles = droppedFiles != null && !droppedFiles.isEmpty();
                DropFilesPic.setVisible(!hasFiles);
                DropFilesPic.setManaged(!hasFiles);

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

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ConfigUI.fxml"));
            Parent root = loader.load();
            ConfigScreen controller = loader.getController();
            controller.setConfig(audio);

            Stage configStage = new Stage();
            if (primaryStage != null) {
                configStage.initOwner(primaryStage);
            }
            configStage.initModality(Modality.APPLICATION_MODAL);
            configStage.initStyle(StageStyle.UTILITY);
            configStage.setTitle("Configuration");

            Scene scene = new Scene(root);
            configStage.setScene(scene);
            configStage.setResizable(false);
            this.currentPopup = configStage;

            controller.setStage(configStage);
            configStage.showAndWait();

            Audio config = controller.getConfig();
            System.out.println("=== Configuration Saved ===");
            System.out.println("Bitrate: " + config.getBitrate() + " kbps");
            System.out.println("Sample Rate: " + config.getSamplingRate() + " kHz");
            System.out.println("Channels: " + config.getChannels());
            System.out.println("==========================");

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load configuration");
            alert.setContentText("Could not open the configuration window. Please try again.\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void showProgressPopup() {
        if (droppedFiles == null || droppedFiles.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Files Selected");
            alert.setContentText("Please select or drop files to convert first.");
            alert.showAndWait();
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Target Directory");

        if (!droppedFiles.isEmpty()) {
            File firstFile = droppedFiles.get(0);
            if (firstFile != null && firstFile.getParentFile() != null) {
                directoryChooser.setInitialDirectory(firstFile.getParentFile());
            }
        }
        
        File targetDir = directoryChooser.showDialog(primaryStage);

        if (targetDir == null) {
            return;
        }

        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Converting Files...");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        Label statusLabel = new Label("Preparing to convert " + droppedFiles.size() + " files...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        Label fileNameLabel = new Label("");
        fileNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        Label progressLabel = new Label("0/" + droppedFiles.size() + " files processed");
        progressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");
        
        VBox vbox = new VBox(10, statusLabel, progressBar, progressLabel, fileNameLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");

        Scene scene = new Scene(vbox, 450, 180);
        popupStage.setScene(scene);

        popupStage.show();

        Task<Void> conversionTask = new Task<>() {
            @Override
            protected Void call() {
                int totalFiles = droppedFiles.size();
                int processedFiles = 0;
                
                for (File inputFile : droppedFiles) {
                    final int currentFile = processedFiles + 1;
                    final String currentFileName = inputFile.getName();

                    Platform.runLater(() -> {
                        statusLabel.setText("Converting file " + currentFile + " of " + totalFiles);
                        fileNameLabel.setText("File: " + currentFileName);
                        progressLabel.setText(currentFile + "/" + totalFiles + " files processed");
                        progressBar.setProgress((double) (currentFile - 1) / totalFiles);
                    });

                    try {
                        audio.setPath(inputFile);

                        String sourceFileName = inputFile.getName();
                        String targetFileName = sourceFileName;

                        int lastDot = targetFileName.lastIndexOf('.');
                        if (lastDot != -1) {
                            targetFileName = targetFileName.substring(0, lastDot);
                        }

                        targetFileName += "." + audio.getFormat();

                        File targetFile = new File(targetDir, targetFileName);
                        audio.setTarget(targetFile);

                        if (!targetDir.exists()) {
                            targetDir.mkdirs();
                        }

                        Convert.convertWithProgress(audio, progress -> {
                            Platform.runLater(() -> {
                                double percentage = progress * 100;
                                progressBar.setProgress(progress);
                                statusLabel.setText(String.format("Converting... %.1f%%", percentage));
                            });
                        });

                        processedFiles++;
                        updateProgress(processedFiles, totalFiles);

                        Platform.runLater(() -> {
                            statusLabel.setText("Successfully converted: " + currentFileName);
                            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        });

                    }catch (SameFileTypeException e) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Skipped " + currentFileName + " (Same format)");
                            statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            System.out.println("Skipped: " + e.getMessage());
                        });
                    }
                    catch (Exception e) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Error converting " + currentFileName + ": " + e.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            e.printStackTrace();
                        });
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                final int finalProcessedFiles = processedFiles;
                final int finalTotalFiles = totalFiles;

                Platform.runLater(() -> {
                    statusLabel.setText("Conversion complete! " + finalProcessedFiles + "/" + finalTotalFiles + " files processed");
                    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    progressBar.setProgress(1.0);

                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(popupStage::close);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            Platform.runLater(popupStage::close);
                        }
                    }).start();
                });
                return null;
            }
        };

        new Thread(conversionTask, "Conversion-Thread").start();
    }
}
