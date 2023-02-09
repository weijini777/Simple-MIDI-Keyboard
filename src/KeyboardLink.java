import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.macalester.graphics.events.Key;

public class KeyboardLink {
    public static final List<Key> KEY_LIST = List.of(
        Key.A, Key.W, Key.S, Key.E, Key.D, Key.F, Key.T, Key.G, Key.Y, Key.H,
        Key.U, Key.J, Key.K, Key.O, Key.L, Key.P, Key.SEMICOLON);

    private Map<Key, PlayableKey> keyMap;

    /**
     * Creates a keyboardLink object that initializes and handles the KeyMap
     * 
     * @param keys list of PlayableKeys from the keyboard
     */
    public KeyboardLink(List<PlayableKey> keys) {
        makeKeyMap(keys, 0);
    }

    /**
     * Creates a Map that links the typing keys with the music keys
     * 
     * @param keys   list of PlayableKeys from the keyboard
     * @param octave the octave of the music keyboard we are currently in
     */
    public void makeKeyMap(List<PlayableKey> keys, int octave) {
        Map<Key, PlayableKey> keyMap = new HashMap<>();
        for (int i = 0; i < KEY_LIST.size(); i++) {
            if (i + octave * 12 < 36 && i + octave * 12 >= 0) {
                PlayableKey currKey = keys.get(i + octave * 12);
                Key mappedKey = KEY_LIST.get(i);
                currKey.setText(mappedKey.name());
                keyMap.put(mappedKey, currKey);
            }
        }
        this.keyMap = keyMap;
    }

    /**
     * @return Returns the KeyMap
     */
    public Map<Key, PlayableKey> getKeyMap() {
        return keyMap;
    }

}
