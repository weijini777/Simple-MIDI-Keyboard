import java.awt.Color;
import edu.macalester.graphics.*;

public abstract class PlayableKey extends GraphicsGroup {
    private static final double PADDING = 3;
    private MidiManager midiManager;
    protected Rectangle key;
    private GraphicsText keyText;

    private boolean pressed;
    private int pitch;

    private Color upColor;
    private Color downColor;

    /**
     * Interactive Music Key that is displayed in canvas
     * 
     * @param midiManager MidiManager that is used
     * @param posX        X position of the Music key
     * @param posY        Y position of the Music key
     * @param width       Width of the Music Key
     * @param height      Height of the Music Key
     */
    public PlayableKey(MidiManager midiManager, double posX, double posY, double width, double height) {
        pressed = false;
        pitch = -1;
        this.midiManager = midiManager;
        key = new Rectangle(posX, posY, width, height);
        keyText = new GraphicsText(null, posX + PADDING, posY + height - PADDING);
        key.setStrokeWidth(1);
        key.setStrokeColor(Color.decode("#DADADA"));
        this.add(key);
        this.add(keyText);
    }

    /**
     * Checks for overlap between GraphicsObject and location on canvas
     * 
     * @param location position of the event
     * @param canvas   canvas that the event is happening in
     * @return
     */
    public boolean testHit(Point location, CanvasWindow canvas) {
        if(canvas.getElementAt(location)== null){
            return false;
        }
        return canvas.getElementAt(location).equals(key);
    }

    /**
     * Assigns a pitch to a Music Key
     * 
     * @param pitch Midi integer that represents pitch value
     */
    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    /**
     * Sets the color of the Music Key
     * 
     * @param color
     */
    protected void setFillColor(Color color) {
        key.setFillColor(color);
    }

    /**
     * Color of the key when not pressed
     * 
     * @param color
     */
    public void setUpColor(Color color) {
        upColor = color;
    }

    /**
     * Color of the key when pressed down
     * 
     * @param color
     */
    public void setDownColor(Color color) {
        downColor = color;
    }

    /**
     * sets the color of the label for the key
     * 
     * @param color key label color
     */
    public void setTextColor(Color color) {
        keyText.setFillColor(color);
    }

    /**
     * sets the text label of the key
     * 
     * @param text new label text
     */
    public void setText(String text) {
        keyText.setText(text);
    }

    /**
     * Using the midi manager, plays the key's pitch, if the key isn't already depressed
     * 
     * @param tick the time that the key is pressed, for the recording
     */
    public void play(int tick) {
        if (!pressed) {
            midiManager.playPitch(pitch, tick);
            pressed = true;
            key.setFillColor(downColor);
        }
    }

    /**
     * Using the midi manager, stops the key's pitch, if the key isn't already up
     * 
     * @param tick the time that the key is lifted, for the recording
     */
    public void stop(int tick) {
        if (pressed) {
            midiManager.stopPitch(pitch, tick);
            pressed = false;
            key.setFillColor(upColor);
        }
    }

}
