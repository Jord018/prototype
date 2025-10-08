package org.audioconvert.prototype.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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

        ToggleGroup bitrateGroup = new ToggleGroup();
        constant.setToggleGroup(bitrateGroup);
        Variable.setToggleGroup(bitrateGroup);

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

        //Set Apply and Close Button
        applyButton.setOnAction(e -> handleApply());
        CloseBtn.setOnAction(e -> handleCancel());

        // *** 1. เพิ่ม Listener ให้กับ ToggleGroup เพื่อให้ Handle_Bitrate() ทำงานทุกครั้งที่เลือก ***
        bitrateGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            Handle_Bitrate();
        });

        //Set Bitrate - เรียกใช้ครั้งแรกเพื่อตั้งค่าเริ่มต้น
        Handle_Bitrate();
    }

    private void applyFormatConditions() {
        if (config == null) return;

        String format = config.getFormat();

        // Default: อนุญาตให้เลือกได้ทั้งหมด (สำหรับ MP3)
        constant.setDisable(false);
        Variable.setDisable(false);

        if (format == null) return;

        switch (format.toLowerCase()) {
            case "wav":
            case "flac":
                // เมื่อเลือก WAV หรือ FLAC: ไม่สามารถเลือก Constant หรือ Variable ได้ (Disable ทั้งคู่)
                constant.setDisable(true);
                Variable.setDisable(true);

                // CBR/VBR ChoiceBoxes ควรถูก Disable ด้วย
                qualityChoice.setDisable(true);
                bitrateChoice.setDisable(true);
                break;

            case "m4a":
                // เมื่อเลือก M4A: เลือกได้แค่ Constant (Variable ถูก Disable)
                constant.setDisable(false);
                Variable.setDisable(true);

                // บังคับเลือก Constant และตั้งค่า ChoiceBox
                constant.setSelected(true);
                qualityChoice.setDisable(true);
                bitrateChoice.setDisable(false);
                break;

            case "mp3":
                // เมื่อเลือก MP3: เลือกได้ทั้ง Constant และ Variable
                constant.setDisable(false);
                Variable.setDisable(false);
                // สถานะ ChoiceBox จะถูกจัดการโดย Handle_Bitrate() ซึ่งจะถูกเรียกต่อจากนี้
                break;
        }

        // ต้องเรียก Handle_Bitrate อีกครั้ง เพื่อปรับ ChoiceBox ตาม RadioButton ที่อาจถูกเปลี่ยนโดย switch (เช่น M4A)
        Handle_Bitrate();
    }

    @FXML
    private void handleApply() {
        if (config != null) {
            String format = config.getFormat();

            // *** 1. ตรวจสอบ Format ก่อนพยายามดึงค่า Bitrate/VBR ***
            if (format != null && (format.equalsIgnoreCase("wav") || format.equalsIgnoreCase("flac"))) {
                // สำหรับ WAV/FLAC: ไม่ต้องตั้งค่า BitRate/VBR
            } else {
                // สำหรับ MP3/M4A: ต้องตั้งค่า BitRate/VBR
                try {
                    // ใช้ logic เดิมในการดึงค่า
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
                    // บันทึกสถานะ CBR/VBR
                    config.setCBR(constant.isSelected());

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing configuration values: " + e.getMessage());
                    return; // ไม่ปิดหน้าต่างหากมี error
                }
            }

            // Close the window
            if (stage != null) {
                stage.hide();
            }
        }
    }

    public void setConfig(Audio config) {
        this.config = config;
        updateUIFromConfig();
        applyFormatConditions(); // *** เรียกใช้เงื่อนไข Format ทันทีที่ตั้งค่า config ***
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
        // เงื่อนไขนี้จะทำงานเฉพาะเมื่อ Radio Buttons ไม่ได้ถูก Disable (เช่น MP3)
        if (!constant.isDisable() && constant.isSelected()) {
            bitrateChoice.setDisable(false); // เปิด Bitrate ChoiceBox
            qualityChoice.setDisable(true);  // ปิด Quality ChoiceBox
            if (config != null) config.setCBR(true);
        } else if (!Variable.isDisable() && Variable.isSelected()) {
            bitrateChoice.setDisable(true); // ปิด Bitrate ChoiceBox
            qualityChoice.setDisable(false); // เปิด Quality ChoiceBox
            if (config != null) config.setCBR(false);
        }
        // ถ้าเป็น WAV/FLAC, constant.isDisable() จะเป็น true, Logic นี้จะไม่ทำงาน
    }


public void setStage(Stage stage) {
        this.stage = stage;
}

public Stage getStage() {
        return stage;
}

}

