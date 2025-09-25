package org.audioconvert.prototype.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import org.audioconvert.prototype.model.Audio;

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
        // Set default radio button
        constant.setSelected(true);
        
        // Initialize choice boxes with default values
        bitrateChoice.getItems().addAll("32 kbps","40 kbps","48 kbps","56 kbps","64 kbps","80 kbps","96 kbps","112 kbps","128 kbps","160 kbps","192 kbps","224 kbps", "256 kbps", "320 kbps");
        qualityChoice.getItems().addAll("0","1", "2", "3", "4", "5", "6", "7", "8", "9");
        sampleRateChoice.getItems().addAll("32 kHz","44.1 kHz", "48 kHz", "96 kHz");
        channelsChoice.getItems().addAll("1", "2");

        if (config == null) {
            bitrateChoice.setValue("192 kbps");
            qualityChoice.setValue("1");
            sampleRateChoice.setValue("44.1 kHz");
            channelsChoice.setValue("1");
        } else {
            updateUIFromConfig();
        }

        // Set toggle group for radio buttons
        //Set Apply and Close Button
        applyButton.setOnAction(e -> handleApply());
        CloseBtn.setOnAction(e -> handleCancel());

        //Set Bitrate
        Handle_Bitrate();
    }
    @FXML
    private void handleApply() {
        if (config != null) {
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

                // Close the window
                if (stage != null) {
                    stage.hide();
                }
            } catch (NumberFormatException e) {
                // Handle parsing errors
                System.err.println("Error parsing configuration values: " + e.getMessage());
            }
        }
    }

    public void setConfig(Audio config) {
        this.config = config;
        // Update UI to reflect the current config
        updateUIFromConfig();
    }

    private void updateUIFromConfig() {
        if (config == null) return;

        // Update radio buttons
        constant.setSelected(config.isCBR);
        Variable.setSelected(!config.isCBR);

        // Update choice boxes with null checks
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
        // Handle cancel button action
        CloseBtn.getScene().getWindow().hide();
    }

    private void Handle_Bitrate() {
        if (constant.isSelected()) {
            Variable.setSelected(false);
            if (config != null) {
                config.setCBR(true);
            }
        }
        if (Variable.isSelected()) {
            constant.setSelected(false);
            if (config != null) {
                config.setCBR(false);
            }
        }
    }
public void setStage(Stage stage) {
    this.stage = stage;
}
public Stage getStage() {
    return stage;
}

}
