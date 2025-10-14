# Audio Converter Application - How It Works

## Overview
This is a **JavaFX-based audio converter application** that allows users to convert audio files between different formats (MP3, M4A, WAV, FLAC) with customizable quality settings. The application uses FFmpeg (via JAVE library) for audio conversion and provides a drag-and-drop interface with real-time playback capabilities.

---

## Architecture

### MVC Pattern
The application follows the **Model-View-Controller (MVC)** design pattern:

- **Model**: `Audio.java` - Stores audio configuration data
- **View**: `MainScreen.java`, `ConfigScreen.java` - UI controllers
- **Controller**: `Convert.java` - Handles audio conversion logic
- **Entry Point**: `Launcher.java` - Application startup

---

## Core Components

### 1. **Launcher.java** (Entry Point)
```
Purpose: Application entry point that initializes JavaFX
```

**How it works:**
1. Extends `javafx.application.Application`
2. Loads the main FXML file (`AudioConverterUI.fxml`)
3. Creates the primary stage (main window) with dimensions 600x400
4. Passes the primary stage reference to `MainScreen` controller
5. Displays the application window

**Key Methods:**
- `start(Stage primaryStage)` - Initializes and shows the main window
- `main(String[] args)` - Launches the JavaFX application

---

### 2. **Audio.java** (Model)
```
Purpose: Data model that stores audio conversion settings
```

**Properties:**
- `format` - Target audio format (mp3, m4a, wav, flac) - Default: "mp3"
- `channels` - Audio channels (1=mono, 2=stereo) - Default: 1
- `samplingRate` - Sample rate in Hz - Default: 44100 Hz
- `Bitrate` - Audio bitrate in bps - Default: 192000 bps (192 kbps)
- `path` - Source file path
- `target` - Target file path
- `isCBR` - Constant Bitrate mode flag - Default: true
- `isVBR` - Variable Bitrate mode flag - Default: false
- `VBR` - VBR quality level (0-9) - Default: "1"

**How it works:**
- Acts as a data container for audio conversion parameters
- Provides getters and setters for all properties
- Shared between UI components and conversion logic
- Maintains state throughout the conversion process

---

### 3. **MainScreen.java** (Main View Controller)
```
Purpose: Main UI controller handling file management, format selection, and conversion
```

#### Key Features:

##### **A. Drag-and-Drop File Management**
**How it works:**
1. User drags audio files onto the `dropBarList` ListView
2. `setupDragAndDrop()` method handles drag events:
   - `onDragOver` - Accepts files being dragged
   - `onDragDropped` - Validates and adds files
3. File validation:
   - Checks file extension against allowed formats: `mp3, m4a, wav, flac, ogg, aac, wma, aiff`
   - Prevents duplicate filenames
   - Shows warning alerts for invalid files
4. Files are stored in `droppedFiles` ArrayList
5. File names displayed in ListView (sorted alphabetically)
6. First file automatically set as the audio path

##### **B. Format Selection**
**How it works:**
1. Four buttons: `mp3Btn`, `m4aBtn`, `wavBtn`, `flacBtn`
2. Each button sets the target format in the `Audio` model
3. Triggers `updateQualityOptions(format)` to adjust quality presets
4. Quality options vary by format:
   - **MP3**: 64, 128, 192, 320 kbps (bitrate presets)
   - **M4A**: 64, 128, 160, 256 kbps (bitrate presets)
   - **WAV**: 22.05, 44.1, 48, 96 kHz (sample rate presets)
   - **FLAC**: No quality options (lossless)

##### **C. Quality Selection**
**How it works:**
1. Four radio buttons (`quality1`, `quality2`, `quality3`, `quality4`) in a ToggleGroup
2. Each quality option updates the `Audio` model:
   - For MP3/M4A: Sets bitrate, sampling rate (44.1 kHz), channels (1)
   - For WAV: Sets sampling rate only
   - For FLAC: Disabled (lossless format)
3. Default selections:
   - MP3: 192 kbps
   - M4A: 128 kbps
   - WAV: 44.1 kHz

##### **D. Audio Playback**
**How it works:**
1. **Play Button (`playSelectedFile()`)**:
   - Gets selected file from ListView
   - Creates JavaFX `MediaPlayer` with the file
   - Plays audio through system audio output
   - Handles playback errors
2. **Stop Button (`stopPlayback()`)**:
   - Stops current playback
   - Disposes MediaPlayer resources

##### **E. File Deletion**
**How it works:**
1. User selects file in ListView and presses DELETE or BACKSPACE
2. `setupDeleteFileOnKeyPress()` handles the event:
   - Stops MediaPlayer if file is currently playing
   - Removes file from `droppedFiles` list
   - Removes filename from ListView
   - Updates placeholder visibility
   - If first file deleted, sets next file as audio path

##### **F. Configuration Popup**
**How it works:**
1. User clicks config button (`configBtn`)
2. `showConfigPopup()` method:
   - Loads `ConfigUI.fxml`
   - Creates modal dialog window
   - Passes current `Audio` object to ConfigScreen
   - Waits for user to apply or cancel
   - Retrieves updated configuration
   - Prints configuration to console

##### **G. Conversion Process**
**How it works:**
1. User clicks convert button (`convertBtn`)
2. `showProgressPopup()` method executes:
   - **Validation**: Checks if files are selected
   - **Directory Selection**: Opens DirectoryChooser for output location
   - **Progress Window**: Creates modal popup with progress bar
   - **Background Task**: Creates JavaFX `Task<Void>` for conversion
   - **File Processing Loop**:
     ```
     For each file in droppedFiles:
       - Update UI with current file info
       - Set source file path in Audio model
       - Generate target filename (same name, new extension)
       - Call Convert.convertWithProgress()
       - Update progress bar with conversion progress
       - Handle success/error/skip scenarios
       - Sleep 500ms between files
     ```
   - **Completion**: Shows success message, auto-closes after 2 seconds

**Threading:**
- Conversion runs on separate thread to prevent UI freezing
- `Platform.runLater()` updates UI from background thread
- Progress callbacks update UI in real-time

---

### 4. **ConfigScreen.java** (Configuration View Controller)
```
Purpose: Advanced configuration dialog for fine-tuning audio settings
```

#### Key Features:

##### **A. Bitrate Mode Selection**
**How it works:**
1. Two radio buttons: `constant` (CBR) and `Variable` (VBR)
2. **CBR (Constant Bitrate)**:
   - Enables `bitrateChoice` dropdown
   - Disables `qualityChoice` dropdown
   - Fixed bitrate throughout the file
3. **VBR (Variable Bitrate)**:
   - Disables `bitrateChoice` dropdown
   - Enables `qualityChoice` dropdown (0-9 scale)
   - Bitrate varies based on audio complexity

##### **B. Format-Specific Constraints**
**How it works:**
1. `applyFormatConditions()` adjusts UI based on selected format:
   - **WAV/FLAC**: Disables both CBR and VBR (lossless formats)
   - **M4A**: Disables VBR (only CBR supported)
   - **MP3**: Both CBR and VBR available
2. `updateSampleRateOptions()` loads format-specific sample rates:
   - MP3: 32.0, 44.1, 48.0 kHz
   - M4A: 8.0 to 48.0 kHz (9 options)
   - WAV: 8.0 to 96.0 kHz (12 options)
   - FLAC: 8.0 to 48.0 kHz (9 options)

##### **C. Configuration Options**
**Available Settings:**
- **Bitrate**: 32-320 kbps (14 options)
- **VBR Quality**: 0-9 (0=highest quality, 9=lowest)
- **Sample Rate**: Format-dependent (see above)
- **Channels**: 1 (mono) or 2 (stereo)

##### **D. Apply/Cancel Actions**
**How it works:**
1. **Apply Button (`handleApply()`)**:
   - Parses values from dropdowns
   - Converts units (kbps → bps, kHz → Hz)
   - Updates `Audio` model with new settings
   - Closes configuration window
2. **Close Button (`handleCancel()`)**:
   - Closes window without saving changes

---

### 5. **Convert.java** (Conversion Controller)
```
Purpose: Handles actual audio file conversion using FFmpeg via JAVE library
```

#### How Conversion Works:

##### **A. Pre-Conversion Validation**
```java
convertWithProgress(Audio audio, Consumer<Double> progressCallback)
```

**Steps:**
1. **Same File Check**:
   - Compares source and target canonical paths
   - If same, creates temporary file to prevent data loss
   - Temporary file deleted on exit

2. **Same Format Check**:
   - Extracts file extensions from source and target
   - If extensions match:
     - Reads source bitrate (for MP3 files)
     - Compares with target bitrate
     - Throws `SameFileTypeException` if identical
     - Allows conversion if bitrates differ

##### **B. Audio Encoding Configuration**
**How it works:**
1. Creates `AudioAttributes` object:
   ```java
   audioAtt.setBitRate(audio.getBitrate());      // e.g., 192000 bps
   audioAtt.setChannels(audio.getChannels());    // e.g., 1 (mono)
   audioAtt.setSamplingRate(audio.getSamplingRate()); // e.g., 44100 Hz
   ```

2. Creates `EncodingAttributes` with audio settings

3. **VBR Handling**:
   - If VBR enabled, adds FFmpeg argument: `-qscale:a [quality]`
   - Quality range: 0-9 (0=best, 9=worst)

##### **C. Conversion Execution**
**How it works:**
1. Creates `MultimediaObject` from source file
2. Retrieves media info (duration, format, etc.)
3. Creates `EncoderProgressListener` for real-time progress:
   ```java
   progress(int permil) {
       double progress = permil / 1000.0;  // Convert to 0.0-1.0
       progressCallback.accept(progress);   // Update UI
   }
   ```
4. Calls `encoder.encode()` with:
   - Source file
   - Target file
   - Encoding attributes
   - Progress listener

##### **D. FFmpeg Under the Hood**
**What happens:**
1. JAVE library wraps FFmpeg command-line tool
2. FFmpeg decodes source audio format
3. Re-encodes to target format with specified settings
4. Writes output to target file
5. Reports progress via listener callbacks

##### **E. Error Handling**
**Exceptions thrown:**
- `SameFileTypeException` - Same format and bitrate
- `EncoderException` - FFmpeg encoding error
- `IOException` - File I/O error
- `RuntimeException` - Media info retrieval error

---

## Data Flow

### Complete Conversion Flow:
```
1. User Action: Drag files onto UI
   ↓
2. MainScreen: Validate and store files in droppedFiles list
   ↓
3. User Action: Select format (MP3/M4A/WAV/FLAC)
   ↓
4. MainScreen: Update Audio model with format
   ↓
5. User Action: Select quality preset
   ↓
6. MainScreen: Update Audio model with bitrate/sample rate
   ↓
7. User Action (Optional): Click config button
   ↓
8. ConfigScreen: Fine-tune bitrate, sample rate, channels, CBR/VBR
   ↓
9. User Action: Click convert button
   ↓
10. MainScreen: Open directory chooser for output location
   ↓
11. MainScreen: Create background Task for conversion
   ↓
12. For each file:
    a. Set source path in Audio model
    b. Generate target filename
    c. Call Convert.convertWithProgress()
    d. Convert: Validate source/target
    e. Convert: Configure FFmpeg encoding
    f. Convert: Execute conversion with progress callbacks
    g. MainScreen: Update progress bar via Platform.runLater()
    h. Handle success/error/skip
   ↓
13. MainScreen: Show completion message
   ↓
14. MainScreen: Auto-close progress window after 2 seconds
```

---

## Dependencies

### Maven Dependencies (from pom.xml):

1. **JavaFX** (v21.0.6)
   - `javafx-controls` - UI controls (buttons, lists, etc.)
   - `javafx-fxml` - FXML layout support
   - `javafx-media` (v17.0.2) - Audio playback

2. **JAVE** (v3.5.0)
   - `jave-all-deps` - FFmpeg wrapper for audio conversion
   - Includes native FFmpeg binaries for all platforms

3. **MP3agic** (v0.9.1)
   - `mp3agic` - MP3 metadata and bitrate reading

4. **SLF4J** (v2.0.16)
   - `slf4j-simple` - Logging framework (runtime)

5. **JUnit** (v5.12.1)
   - `junit-jupiter-api` - Unit testing (test scope)
   - `junit-jupiter-engine` - Test execution (test scope)

---

## Key Technologies

### 1. **JavaFX**
- **Purpose**: Modern UI framework for Java
- **Usage**: 
  - FXML for declarative UI layout
  - Controllers for UI logic
  - Task API for background processing
  - MediaPlayer for audio playback

### 2. **FFmpeg (via JAVE)**
- **Purpose**: Multimedia conversion engine
- **Usage**:
  - Audio format conversion
  - Bitrate/sample rate adjustment
  - Channel configuration
  - Progress reporting

### 3. **MP3agic**
- **Purpose**: MP3 file analysis
- **Usage**:
  - Read MP3 bitrate
  - Prevent unnecessary conversions
  - Validate MP3 files

---

## File Structure

```
prototype/
├── src/main/java/org/audioconvert/prototype/
│   ├── Launcher.java                    # Application entry point
│   ├── controller/
│   │   └── Convert.java                 # Conversion logic
│   ├── model/
│   │   └── Audio.java                   # Data model
│   ├── view/
│   │   ├── MainScreen.java              # Main UI controller
│   │   └── ConfigScreen.java            # Config UI controller
│   └── exception/
│       └── SameFileTypeException.java   # Custom exception
├── src/main/resources/
│   ├── AudioConverterUI.fxml            # Main UI layout
│   └── ConfigUI.fxml                    # Config UI layout
└── pom.xml                              # Maven configuration
```

---

## Special Features

### 1. **Smart File Validation**
- Prevents duplicate filenames in queue
- Validates audio file extensions
- Shows user-friendly error messages

### 2. **Same Format Detection**
- Detects when source and target formats match
- Compares bitrates to prevent unnecessary conversions
- Skips files with identical format and bitrate

### 3. **Progress Tracking**
- Real-time conversion progress (0-100%)
- File-by-file status updates
- Visual progress bar
- Color-coded status messages:
  - Green: Success
  - Orange: Skipped
  - Red: Error

### 4. **Thread Safety**
- Background conversion thread prevents UI freezing
- `Platform.runLater()` ensures thread-safe UI updates
- Atomic operations for progress tracking

### 5. **Audio Playback**
- Preview files before conversion
- Play/Stop controls
- Automatic resource cleanup

### 6. **Format-Specific Presets**
- Each format has optimized quality presets
- Automatic UI adjustment based on format
- Prevents invalid configurations

---

## Error Handling

### Exception Types:
1. **SameFileTypeException**
   - Thrown when source and target are identical
   - Prevents wasted processing
   - User sees "Skipped" message

2. **EncoderException**
   - FFmpeg encoding errors
   - Invalid format combinations
   - Corrupted source files

3. **IOException**
   - File access errors
   - Disk space issues
   - Permission problems

### Error Display:
- Modal alert dialogs for critical errors
- Status label updates for conversion errors
- Console logging for debugging
- Color-coded error messages in progress window

---

## Configuration Persistence

**Current Behavior:**
- Configuration stored in `Audio` model instance
- Settings persist during application session
- Reset when application closes

**How Settings are Maintained:**
1. User selects format → Stored in `audio.format`
2. User selects quality → Stored in `audio.bitrate/samplingRate`
3. User opens config → Current values loaded into UI
4. User applies config → Values updated in `audio` model
5. Conversion uses current `audio` model values

---

## Performance Considerations

### 1. **Batch Processing**
- Processes multiple files sequentially
- 500ms delay between files (for UI updates)
- Single thread to prevent resource contention

### 2. **Memory Management**
- MediaPlayer disposed after playback
- Temporary files deleted on exit
- File list stored as references (not file contents)

### 3. **UI Responsiveness**
- Conversion runs on background thread
- Progress updates via callbacks
- Non-blocking file selection dialogs

---

## Limitations

1. **Sequential Processing**
   - Files converted one at a time
   - No parallel conversion support

2. **Format Support**
   - Limited to formats supported by FFmpeg
   - VBR only available for MP3

3. **Bitrate Detection**
   - Only works for MP3 files
   - Other formats always converted

4. **No Undo**
   - Conversions cannot be reversed
   - Original files not modified (unless same file)

---

## Future Enhancements (Potential)

1. **Parallel Conversion**
   - Convert multiple files simultaneously
   - Utilize multi-core processors

2. **Batch Presets**
   - Save/load conversion presets
   - Quick format switching

3. **Audio Preview**
   - Waveform visualization
   - Duration display
   - Metadata viewing

4. **Advanced Options**
   - Custom FFmpeg arguments
   - Audio filters (normalize, fade, etc.)
   - Batch renaming

5. **Conversion History**
   - Track converted files
   - Re-convert with same settings
   - Export conversion logs

---

## Conclusion

This audio converter application provides a **user-friendly interface** for converting audio files between popular formats. It leverages **FFmpeg's powerful conversion capabilities** through the JAVE library while maintaining a **responsive JavaFX UI**. The **MVC architecture** ensures clean separation of concerns, making the codebase maintainable and extensible.

**Key Strengths:**
- ✅ Drag-and-drop simplicity
- ✅ Real-time progress tracking
- ✅ Format-specific optimizations
- ✅ Error handling and validation
- ✅ Audio preview capabilities
- ✅ Advanced configuration options

**Target Users:**
- Content creators needing format conversion
- Audio engineers requiring bitrate adjustments
- Anyone needing batch audio conversion with quality control
