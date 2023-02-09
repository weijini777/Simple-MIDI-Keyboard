import java.awt.Color;

public class WhiteKey extends PlayableKey {
    public final static Color UP = Color.WHITE;
    public final static Color DOWN = Color.decode("#ADEAFF");
    public final static Color TEXT = Color.decode("#555555");

    public final static double HEIGHT = 250;
    public final static double WIDTH = 50;
    
    double posX;
    double posY;

    /**
     * Creates a WhiteKey that extends PlayableKey with its position and size
     * 
     * @param posX    x position of the WhiteKey on the canvas
     * @param posY    y position of the WhiteKey on the canvas
     * @param manager MidiManager that is used in its methods
     */
    public WhiteKey(double posX, double posY, MidiManager manager) {
        super(manager, posX, posY, WIDTH, HEIGHT);
        this.posX = posX;
        this.posY = posY;

        setUpColor(UP);
        setDownColor(DOWN);
        setFillColor(UP);
        setTextColor(TEXT);
    }

}
