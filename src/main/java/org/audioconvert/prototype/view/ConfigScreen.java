package org.audioconvert.prototype.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.audioconvert.prototype.model.Audio;

import java.util.Arrays;
import java.util.List;

public class ConfigScreen {
    @FXML private RadioButton constant;
    @FXML private RadioButton Variable;
    @FXML private ChoiceBox<String> bitrateChoice;
    @FXML private ChoiceBox<String> qualityChoice;
    @FXML private ChoiceBox<String> sampleRateChoice;
    @FXML private ChoiceBox<String> channelsChoice;
    @FXML private Button applyButton;
    @FXML private Button CloseBtn;
private Stage stage;
private Audio config;

    @FXML
    private void initialize() {
        constant.setSelected(true);

        ToggleGroup bitrateGroup = new ToggleGroup();
        constant.setToggleGroup(bitrateGroup);
        Variable.setToggleGroup(bitrateGroup);

        bitrateChoice.getItems().addAll("32 kbps","40 kbps","48 kbps","56 kbps","64 kbps","80 kbps","96 kbps","112 kbps","128 kbps","160 kbps","192 kbps","224 kbps", "256 kbps", "320 kbps");
        qualityChoice.getItems().addAll("0","1", "2", "3", "4", "5", "6", "7", "8", "9");
        channelsChoice.getItems().addAll("1", "2");

        updateSampleRateOptions("mp3");

        if (config == null) {
            bitrateChoice.setValue("192 kbps");
            qualityChoice.setValue("1");
            sampleRateChoice.setValue("44.1 kHz");
            channelsChoice.setValue("1");
        } else {
            updateUIFromConfig();
        }

        applyButton.setOnAction(e -> handleApply());
        CloseBtn.setOnAction(e -> handleCancel());

        bitrateGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            Handle_Bitrate();
        });

        Handle_Bitrate();
    }

    private void updateSampleRateOptions(String format) {
        if (format == null) format = "mp3";

        List<String> sampleRates;

        switch (format.toLowerCase()) {
            case "mp3":
                sampleRates = Arrays.asList("32.0 kHz", "44.1 kHz", "48.0 kHz");
                break;
            case "m4a":
                sampleRates = Arrays.asList("8.0 kHz", "11.025 kHz", "12.0 kHz", "16.0 kHz", "22.05 kHz", "24.0 kHz", "32.0 kHz", "44.1 kHz", "48.0 kHz");
                break;
            case "wav":
                sampleRates = Arrays.asList("8.0 kHz", "11.025 kHz", "12.0 kHz", "16.0 kHz", "22.05 kHz", "24.0 kHz", "32.0 kHz", "44.1 kHz", "48.0 kHz", "64.0 kHz", "88.2 kHz", "96.0 kHz");
                break;
            case "flac":
                sampleRates = Arrays.asList("8.0 kHz", "11.025 kHz", "12.0 kHz", "16.0 kHz", "22.05 kHz", "24.0 kHz", "32.0 kHz", "44.1 kHz", "48.0 kHz");
                break;
            default:
                sampleRates = Arrays.asList("32.0 kHz", "44.1 kHz", "48.0 kHz");
                break;
        }

        sampleRateChoice.getItems().clear();
        sampleRateChoice.getItems().addAll(sampleRates);

        if (sampleRates.contains("44.1 kHz")) {
            sampleRateChoice.setValue("44.1 kHz");
        } else if (!sampleRates.isEmpty()) {
            sampleRateChoice.setValue(sampleRates.get(0));
        }
    }

    private void applyFormatConditions() {
        if (config == null) return;

        String format = config.getFormat();

        updateSampleRateOptions(format);

        constant.setDisable(false);
        Variable.setDisable(false);

        if (format == null) return;

        switch (format.toLowerCase()) {
            case "wav":
            case "flac":

                constant.setDisable(true);
                Variable.setDisable(true);

                qualityChoice.setDisable(true);
                bitrateChoice.setDisable(true);
                break;

            case "m4a":

                constant.setDisable(false);
                Variable.setDisable(true);

                constant.setSelected(true);
                qualityChoice.setDisable(true);
                bitrateChoice.setDisable(false);
                break;

            case "mp3":

                constant.setDisable(false);
                Variable.setDisable(false);

                break;
        }

        Handle_Bitrate();
    }

    @FXML
    private void handleApply() {
        if (config != null) {
            String format = config.getFormat();


            if (format != null && (format.equalsIgnoreCase("wav") || format.equalsIgnoreCase("flac"))) {

                try {

                    int bitrateKbps = Integer.parseInt(bitrateChoice.getValue().replace(" kbps", ""));
                    int bitrate = bitrateKbps * 1000;
                    String vbr = qualityChoice.getValue();
                    double sampleRateKHz = Double.parseDouble(sampleRateChoice.getValue().replace(" kHz", ""));
                    int sampleRate = (int)(sampleRateKHz * 1000);
                    int channels = Integer.parseInt(channelsChoice.getValue());

                    config.setBitrate(bitrate);
                    config.setVBR(vbr);
                    config.setSamplingRate(sampleRate);
                    config.setChannels(channels);
                    config.setCBR(constant.isSelected());

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing configuration values: " + e.getMessage());
                    return;
                }
            } else {

                try {

                    int bitrateKbps = Integer.parseInt(bitrateChoice.getValue().replace(" kbps", ""));
                    int bitrate = bitrateKbps * 1000;
                    String vbr = qualityChoice.getValue();
                    double sampleRateKHz = Double.parseDouble(sampleRateChoice.getValue().replace(" kHz", ""));
                    int sampleRate = (int)(sampleRateKHz * 1000);
                    int channels = Integer.parseInt(channelsChoice.getValue());

                    config.setBitrate(bitrate);
                    config.setVBR(vbr);
                    config.setSamplingRate(sampleRate);
                    config.setChannels(channels);
                    config.setCBR(constant.isSelected());

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing configuration values: " + e.getMessage());
                    return;
                }
            }

            if (stage != null) {
                stage.hide();
            }
        }
    }

    public void setConfig(Audio config) {
        this.config = config;
        updateUIFromConfig();
        applyFormatConditions();
    }

    private void updateUIFromConfig() {
        if (config == null) return;

        constant.setSelected(config.isCBR);
        Variable.setSelected(!config.isCBR);

        if (bitrateChoice != null && config.getBitrate() > 0) {
            bitrateChoice.setValue((config.getBitrate() / 1000) + " kbps");
        }
        
        if (qualityChoice != null && config.getVBR() != null) {
            qualityChoice.setValue(config.getVBR());
        }
        
        if (sampleRateChoice != null && config.getSamplingRate() > 0) {
            String sampleRateText = String.format("%.1f kHz", config.getSamplingRate() / 1000.0);
            sampleRateChoice.setValue(sampleRateText);
        }
        
        if (channelsChoice != null) {
            channelsChoice.setValue(String.valueOf(config.getChannels()));
        }
    }
    public Audio getConfig() {
        return config;
    }
    
    @FXML
    private void handleCancel() {

        CloseBtn.getScene().getWindow().hide();
    }

    private void Handle_Bitrate() {

        if (!constant.isDisable() && constant.isSelected()) {
            bitrateChoice.setDisable(false);
            qualityChoice.setDisable(true);
            if (config != null) config.setCBR(true);
        } else if (!Variable.isDisable() && Variable.isSelected()) {
            bitrateChoice.setDisable(true);
            qualityChoice.setDisable(false);
            if (config != null) config.setCBR(false);
        }
    }


public void setStage(Stage stage) {
        this.stage = stage;
}

public Stage getStage() {
        return stage;
}

}

