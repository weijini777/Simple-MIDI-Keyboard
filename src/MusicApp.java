import java.awt.Color;
import java.util.HashMap;
import edu.macalester.graphics.*;
import edu.macalester.graphics.events.Key;
import edu.macalester.graphics.ui.Button;
import edu.macalester.graphics.ui.TextField;
import edu.macalester.graphics.Line;


public class MusicApp {
    private final Color GREEN = Color.decode("#CBDD5A");
    private final Color RED = Color.decode("#F95D69");
    private final Color BLUE = Color.decode("#ADECFF");

    CanvasWindow canvas;
    MidiManager midiMan;
    KeyboardManager keyboard;
    GraphicsGroup instruments;
    GraphicsGroup fileOptions;
    KeyboardLink keyboardLink;
    HashMap<Rectangle, Integer> trackRectangles;
    boolean pauseClicked = false;
    int recordingTick;
    Line progressBar;
    int octave;

    /**
     * Creates Music App
     */
    public MusicApp() {
        recordingTick = 0;
        octave = 0;
        canvas = new CanvasWindow("musicApp", 1050, 550);
        midiMan = new MidiManager();
        keyboard = new KeyboardManager(canvas, midiMan);
        instruments = new GraphicsGroup();
        fileOptions = new GraphicsGroup();
        trackRectangles = new HashMap<>();
        keyboardLink = new KeyboardLink(keyboard.getKeys());
        // canvas.setBackground(Color.decode("#5DC6E9"));
        setup();
    }

    /**
     * Set up of entire GUI interface
     */
    public void setup() {
        instrumentMenu();
        playbackSystem();
        setUpTrackRectangles();
        addingTracks();
    }

    // ---------- Instrument Selection ----------------------------------------
    /**
     * Sets up Instrument Selection
     */
    private void instrumentMenu() {
        Button instrument = new Button("Instruments");
        instrument.setPosition(950, 0);
        canvas.add(instrument);

        Button piano = new Button("Piano");
        piano.setPosition(970, 0);
        instruments.add(piano);

        Button guitar = new Button("Guitar");
        guitar.setPosition(968, piano.getSize().getY());
        instruments.add(guitar);

        Button violin = new Button("Violin");
        violin.setPosition(970, guitar.getY() + guitar.getSize().getY());
        instruments.add(violin);

        Button synth = new Button("Synth");
        synth.setPosition(970, violin.getY() + violin.getSize().getY());
        instruments.add(synth);

        violin.onClick(() -> {
            midiMan.selectInstrument(MidiManager.VIOLIN, recordingTick);
            resetMenu(instrument);
        });
        piano.onClick(() -> {
            midiMan.selectInstrument(MidiManager.PIANO, recordingTick);
            resetMenu(instrument);
        });
        guitar.onClick(() -> {
            midiMan.selectInstrument(MidiManager.GUITAR, recordingTick);
            resetMenu(instrument);
        });
        synth.onClick(() -> {
            midiMan.selectInstrument(MidiManager.SYNTH, recordingTick);
            resetMenu(instrument);
        });
        instrument.onClick(() -> {
            canvas.remove(instrument);
            canvas.add(instruments);
        });
    }

    /**
     * Returns to the Instrument Button
     * 
     * @param instrumentMenu the button that calls the list of instruments
     */
    private void resetMenu(Button instrumentMenu) {
        canvas.remove(instruments);
        canvas.add(instrumentMenu);
    }


    // ---------- Playback and Recording --------------------------------------
    /**
     * Overall Playback System that includes Play/Pause, Recording, Save, Skip to Beginning
     */
    private void playbackSystem() {
        GraphicsGroup playBack = new GraphicsGroup();
        playPauseSystem(playBack);
        recordingSystem(playBack);
        skipping(playBack);
        saveSong(playBack);
        playBack.setPosition(300, 0);
        canvas.add(playBack);
    }

    /**
     * Controls Play/Pause Functions
     * 
     * @param group the GraphicsGroup the buttons will be placed in
     */
    private void playPauseSystem(GraphicsGroup group) {
        Button play = new Button("Play");
        Button pause = new Button("Pause");
        play.setPosition(0, 0);
        pause.setPosition(play.getSize().getX(), 1);
        group.add(play);
        group.add(pause);

        play.onClick(() -> {
            pauseClicked = false;
            midiMan.playSequence();
        });
        pause.onClick(() -> {
            pauseClicked = true;
            midiMan.pauseSequence();
        });
    }

    /**
     * Contains the recording system
     * 
     * @param group the GraphicsGroup the buttons will be placed in
     */
    private void recordingSystem(GraphicsGroup group) {
        Button recordButton = new Button("Record");
        Button pauseButton = new Button("Stop");
        recordButton.setPosition(175, 1);
        pauseButton.setPosition(recordButton.getX() + recordButton.getSize().getX(), 0);
        group.add(recordButton);
        group.add(pauseButton);

        recordButton.onClick(() -> {
            recordingTick = 0;
            midiMan.clearTrack(midiMan.getActiveTrack());
            midiMan.startRecording();
            for (HashMap.Entry<Rectangle, Integer> pair : trackRectangles.entrySet()) {
                if (pair.getValue() == midiMan.getActiveTrack())
                    pair.getKey().setFillColor(RED);
            }
        });
        pauseButton.onClick(() -> {
            midiMan.stopRecording();
            for (HashMap.Entry<Rectangle, Integer> pair : trackRectangles.entrySet()) {
                if (pair.getValue() == midiMan.getActiveTrack())
                    pair.getKey().setFillColor(GREEN);
            }
        });
    }

    /**
     * Saves the Music Project into a MIDI file that has a custom name
     * 
     * @param group GraphicsGroup that the Save Button will be placed in
     */
    private void saveSong(GraphicsGroup group) {
        TextField input = new TextField();
        Button save = new Button("Save");
        input.setPosition(0, 4);
        save.setPosition(99, 0);
        canvas.add(input);
        canvas.add(save);

        input.onChange(t -> {
            input.setText(t);
        });
        save.onClick(() -> {
            String songName = input.getText();
            midiMan.saveRecording(songName);
        });
    }

    /**
     * Skip to Beginning of Music Playback
     * 
     * @param group the GraphicsGroup the buttons will be placed in
     */
    private void skipping(GraphicsGroup group) {
        Button skipToBeginning = new Button("Beginning");
        group.add(skipToBeginning);
        skipToBeginning.setPosition(357, 0);

        skipToBeginning.onClick(() -> {
            midiMan.setToStart();
        });
    }


    // ---------- Tracks at the bottom ----------------------------------------
    /**
     * Adding all 4 Tracks
     */
    private void addingTracks() {
        addTrack(100, 400, 0);
        addTrack(325, 400, 1);
        addTrack(550, 400, 2);
        addTrack(775, 400, 3);
    }

    /**
     * Sets Up a Track and adds it to canvas
     * 
     * @param posX        X position of the Track
     * @param posY        Y position of the Track
     * @param trackNumber Number that corresponds to the Track in the Sequence
     */
    private void addTrack(double posX, double posY, int trackNumber) {
        GraphicsGroup singularTrack = new GraphicsGroup();
        double width = 180;
        double height = 100;
        int newTrackNumber = trackNumber + 1;
        GraphicsText trackNum = new GraphicsText("Track " + newTrackNumber);
        Button select = new Button("select");
        trackNum.setCenter(width / 2, height * 0.3);
        select.setCenter(width / 2, height * 0.65);
        singularTrack.add(select);
        singularTrack.add(trackNum);
        singularTrack.setPosition(posX, posY);
        canvas.add(singularTrack);

        select.onClick(() -> {
            midiMan.setActiveTrack(trackNumber);
            updateTrackRectangles();
        });
    }

    /**
     * Sets up the Tracks Backgrounds
     */
    private void setUpTrackRectangles() {
        double width = 180;
        double height = 100;
        double posX = 100;
        double xIncr = 225;
        for (int i = 0; i < 4; i++) {
            Rectangle rect = new Rectangle(posX, 400, width, height);
            rect.setStrokeWidth(1);
            rect.setStrokeColor(Color.decode("#DADADA"));
            trackRectangles.put(rect, i);
            posX += xIncr;
            canvas.add(rect);
        }
    }

    /**
     * Updates the Tracks Backrounds color
     */
    private void updateTrackRectangles() {
        for (Rectangle rect : trackRectangles.keySet()) {
            if (midiMan.isEmpty(trackRectangles.get(rect))) {
                rect.setFillColor(Color.white);
            } else {
                rect.setFillColor(BLUE);
            }

            if (trackRectangles.get(rect) == midiMan.getActiveTrack()) {
                rect.setFillColor(GREEN);
            }


        }
    }

    // ------------------------------------------------------------------------

    /**
     * Runs the Music App and handles mouse and keyboard interactions
     */
    private void run() {
        canvas.onMouseDown(event -> {
            for (PlayableKey key : keyboard.getKeys()) {
                if (key.testHit(event.getPosition(), canvas)) {
                    key.play(recordingTick);
                }
            }
        });

        canvas.onMouseUp(event -> {
            for (PlayableKey key : keyboard.getKeys()) {
                if (key.testHit(event.getPosition(), canvas)) {
                    key.stop(recordingTick);
                }
            }
        });

        canvas.onKeyDown(event -> {
            Key pressedKey = event.getKey();
            if (pressedKey.equals(Key.UP_ARROW)) {
                octave++;
                keyboard.clearKeyText();
                keyboardLink.makeKeyMap(keyboard.getKeys(), octave);
            }
            if (pressedKey.equals(Key.DOWN_ARROW)) {
                octave--;
                keyboard.clearKeyText();
                keyboardLink.makeKeyMap(keyboard.getKeys(), octave);
            }
            if (KeyboardLink.KEY_LIST.contains(pressedKey)) {
                PlayableKey key = keyboardLink.getKeyMap().get(pressedKey);
                if (key != null)
                    key.play(recordingTick);
            }
        });

        canvas.onKeyUp(event -> {
            Key pressedKey = event.getKey();
            if (KeyboardLink.KEY_LIST.contains(pressedKey)) {
                PlayableKey key = keyboardLink.getKeyMap().get(pressedKey);
                if (key != null)
                    key.stop(recordingTick);
            }
        });

        canvas.animate(() -> {
            if (midiMan.isRecording())
                recordingTick += 1;
        });

    }


    public static void main(String[] args) {
        MusicApp musicApp = new MusicApp();
        musicApp.run();
    }
}

