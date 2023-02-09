import java.awt.Color;

public class BlackKey extends PlayableKey {
    public final static Color UP = Color.decode("#555555");
    public final static Color DOWN = Color.decode("#ADECFF");
    public final static Color TEXT = Color.WHITE;

    public final static double HEIGHT = 150;
    public final static double WIDTH = 30;

    double posX;
    double posY;

    /**
     * Creates a BlackKey that extends PlayableKey with its position and size
     * 
     * @param posX    x position of the black key on the canvas
     * @param posY    y position of the black key on the canvas
     * @param manager MidiManager that is used in its methods
     */
    public BlackKey(double posX, double posY, MidiManager manager) {
        super(manager, posX, posY, WIDTH, HEIGHT);
        this.posX = posX;
        this.posY = posY;

        setUpColor(UP);
        setDownColor(DOWN);
        setFillColor(UP);
        setTextColor(TEXT);
    }

}
