import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import edu.macalester.graphics.*;


public class KeyboardManager {
    private final int H_SPACING = 50;
    private final int LOWEST_NOTE = 48;
    private final int N_OCTAVES = 3;
    GraphicsGroup keyboard;
    Queue<PlayableKey> whiteKeys;
    Queue<PlayableKey> blackKeys;
    List<PlayableKey> keys;
    CanvasWindow canvas;
    MidiManager midiMan;

    /**
     * Create a keyboard manager object 
     * @param canvas the canvas that the keyboard is to be drawn on
     * @param midiMan the MidiManager that will be used to play sounds
     */
    public KeyboardManager(CanvasWindow canvas, MidiManager midiMan) {
        this.canvas = canvas;
        this.midiMan = midiMan;
        keyboard = new GraphicsGroup();
        whiteKeys = new ArrayDeque<>();
        blackKeys = new ArrayDeque<>();
        keys = new ArrayList<PlayableKey>();
        createKeyboard(N_OCTAVES);

    }

    // ----------- Setting Up Keyboard -----------------------------------------
    /**
     * Creates the white keys and adds them to the canvas
     * 
     * @param numOctaves number of octaves on the keyboard
     */
    public void createWhiteKeys(int numOctaves) {
        double posX = 0;
        for (int j = 0; j < numOctaves; j++) {
            for (int i = 0; i < 7; i++) {
                WhiteKey whiteKey = new WhiteKey(posX, 100, midiMan);
                whiteKeys.add(whiteKey);
                canvas.add(whiteKey);
                posX += H_SPACING;
            }
        }
    }

    /**
     * Creates the black keys and adds them to the canvas
     * 
     * @param numOctaves number of octaves on the keyboard
     */
    public void createBlackKeys(int numOctaves) {
        double posX = H_SPACING - BlackKey.WIDTH / 2;
        for (int j = 0; j < numOctaves; j++) {
            for (int i = 0; i < 2; i++) {
                BlackKey blackKey = new BlackKey(posX, 100, midiMan);
                blackKeys.add(blackKey);
                canvas.add(blackKey);
                posX += H_SPACING;
            }
            posX += H_SPACING;
            for (int i = 0; i < 3; i++) {
                BlackKey blackKey = new BlackKey(posX, 100, midiMan);
                blackKeys.add(blackKey);
                canvas.add(blackKey);
                posX += H_SPACING;
            }
            posX += H_SPACING;
        }
    }

    /**
     * Add the black and white keys to a list in order of ascending pitch
     * 
     * @param numOctaves number of octaves on the keyboard
     */
    private void addKeysToList(int numOctaves) {
        for (int i = 0; i < N_OCTAVES * 12; i++) {
            if (i % 12 == 1 || i % 12 == 3 || i % 12 == 6 || i % 12 == 8 || i % 12 == 10) {
                keys.add(blackKeys.remove());
            } else {
                keys.add(whiteKeys.remove());
            }
        }
    }

    /**
     * Assigns a pitch to all of the keys
     * 
     * @param keys          list containing all of the keys
     * @param startingPitch the pitch of the lowest note
     */
    private void addPitch(List<PlayableKey> keys, int startingPitch) {
        for (PlayableKey key : keys) {
            key.setPitch(startingPitch);
            startingPitch++;
        }
    }

    /**
     * Creates the keyboard
     * 
     * @param numOctaves the number of octaves on the keyboard
     */
    private void createKeyboard(int numOctaves) {
        createWhiteKeys(numOctaves);
        createBlackKeys(numOctaves);
        addKeysToList(numOctaves);
        addPitch(keys, LOWEST_NOTE);
    }

    /**
     * Clears the text on all keys
     */
    public void clearKeyText() {
        for (PlayableKey key : keys) {
            key.setText(null);
        }
    }

    /**
     * Gets the list of keys on the keyboard
     * 
     * @return a list of the keyboard's keys
     */
    public List<PlayableKey> getKeys() {
        return keys;
    }

}

