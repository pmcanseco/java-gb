import helpers.Logger;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Pablo Canseco on 4/22/2018.
 */
public class Joypad implements KeyListener {
    private Logger log = new Logger("PAD", Logger.Level.DEBUG);

    private static Joypad instance;
    public static Joypad getInstance() {
        if (instance == null) {
            instance = new Joypad();
        }
        return instance;
    }
    public static void reset() {
        instance = null;
    }

    private boolean aPressed;
    private boolean bPressed;
    private boolean startPressed;
    private boolean selectPressed;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private enum Keys {
        A,
        B,
        START,
        SELECT,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }


    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.debug("pressed " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
