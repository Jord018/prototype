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
        // Initialize config
        this.config = new Audio();
        
        // Set default radio button
        constant.setSelected(true);
        
        // Initialize choice boxes with default values
        bitrateChoice.getItems().addAll("32 kbps","40 kbps","48 kbps","56 kbps","64 kbps","80 kbps","96 kbps","112 kbps","128 kbps","160 kbps","192 kbps","224 kbps", "256 kbps", "320 kbps");
        qualityChoice.getItems().addAll("0","1", "2", "3", "4", "5", "6", "7", "8", "9");
        sampleRateChoice.getItems().addAll("32 kHz","44.1 kHz", "48 kHz", "96 kHz");
        channelsChoice.getItems().addAll("1", "2");

        // Set default selections
        bitrateChoice.setValue("192 kbps");
        qualityChoice.setValue("1");
        sampleRateChoice.setValue("44.1 kHz");
        channelsChoice.setValue(String.valueOf(config.getChannels()));

        // Set toggle group for radio buttons
        //Set Apply and Close Button
        applyButton.setOnAction(e -> handleApply());
        CloseBtn.setOnAction(e -> handleCancel());

        //Set Bitrate
        Handle_Bitrate();
    }
    
    @FXML
    private void handleApply() {
        int bitrateKbps = Integer.parseInt(bitrateChoice.getValue().replace(" kbps", ""));
        int bitrate = bitrateKbps * 1000;
        String vbr = qualityChoice.getValue();
        // Parse as double first to handle decimal values, then convert to integer (kHz to Hz)
        double sampleRateKHz = Double.parseDouble(sampleRateChoice.getValue().replace(" kHz", ""));
        int sampleRate = (int)(sampleRateKHz * 1000); // Convert kHz to Hz
        int channels = Integer.parseInt(channelsChoice.getValue());
        config.setBitrate(bitrate);
        config.setVBR(vbr);
        config.setSamplingRate(sampleRate);
        config.setChannels(channels);
        
        // Close the window
        if (stage != null) {
            stage.close();
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


    
   private void Handle_Bitrate(){
        if(constant.isSelected()){
            Variable.setSelected(false);
            config.setCBR(true);
        }
        if(Variable.isSelected()){
            constant.setSelected(false);
            config.setVBR(true);
        }
   }
public void setStage(Stage stage) {
    this.stage = stage;
}
public Stage getStage() {
    return stage;
}

}
