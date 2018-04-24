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

    private boolean isDpadMode;

    private enum Keys {
        A(90),
        B(88),
        START(10),
        SELECT(16),
        UP(38),
        DOWN(40),
        LEFT(37),
        RIGHT(39);

        //<editor-fold desc="IMPLEMENTATION" default-state="collapsed">
        private int keyCode;
        Keys(int code) {
            this.keyCode = code;
        }
        public int getKeyCode() {
            return keyCode;
        }
        public static Keys getKeyFromInt(int n) {
            for (Keys k : Keys.values()) {
                if (k.getKeyCode() == n) {
                    return k;
                }
            }
            return null;
        }
        //</editor-fold>
    }

    public int getKeysPressed() {
        int retval = 0b1100_1111;

        if (isDpadMode) {
            retval |= 0b0010_0000;
            retval &= downPressed  ? 0b1111_0111 : 0b1111_1111; // Bit 3 - P13 Input Down  (0=Pressed)
            retval &= upPressed    ? 0b1111_1011 : 0b1111_1111; // Bit 2 - P12 Input Up    (0=Pressed)
            retval &= leftPressed  ? 0b1111_1101 : 0b1111_1111; // Bit 1 - P11 Input Left  (0=Pressed)
            retval &= rightPressed ? 0b1111_1110 : 0b1111_1111; // Bit 0 - P10 Input Right (0=Pressed)
            return retval;
        }
        else {
            retval |= 0b0001_0000;
            retval &= startPressed  ? 0b1111_0111 : 0b1111_1111; // Bit 3 - P13 Input Start    (0=Pressed)
            retval &= selectPressed ? 0b1111_1011 : 0b1111_1111; // Bit 2 - P12 Input Select   (0=Pressed)
            retval &= bPressed      ? 0b1111_1101 : 0b1111_1111; // Bit 1 - P11 Input Button B (0=Pressed)
            retval &= aPressed      ? 0b1111_1110 : 0b1111_1111; // Bit 0 - P10 Input Button A (0=Pressed)
            return retval;
        }
    }

    public void setJoypadMode(int value) {
        value &= 0b0011_0000;

        if (value == 0b0010_0000) {
            //log.debug("set to dpad mode");
            isDpadMode = true;
        }
        else if(value == 0b0001_0000) {
            //log.debug("set to button mode");
            isDpadMode = false;
        }
        else {
            //log.fatal("joypad set to dpad and button modes at the same time.");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keys keyPressed = Keys.getKeyFromInt(e.getKeyCode());
        if (keyPressed == null) {
            //log.error("key " + e.getKeyChar() + "doesn't do anything.");
        }
        else {
            switch (keyPressed) {
                case A:
                    if (!isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    aPressed = true;
                    break;
                case B:
                    if (!isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    bPressed = true;
                    break;
                case START:
                    if (!isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    startPressed = true;
                    break;
                case SELECT:
                    if (!isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    selectPressed = true;
                    break;
                case UP:
                    if (isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    upPressed = true;
                    break;
                case DOWN:
                    if (isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    downPressed = true;
                    break;
                case LEFT:
                    if (isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    leftPressed = true;
                    break;
                case RIGHT:
                    if (isDpadMode) {
                        InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
                    }
                    rightPressed = true;
                    break;
            }
            log.debug("pressed " + e.getKeyChar());
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keys keyReleased = Keys.getKeyFromInt(e.getKeyCode());
        if (keyReleased == null) {
            log.error("key " + e.getKeyChar() + "doesn't do anything.");
        }
        else {
            switch (keyReleased) {
                case A:
                    aPressed = false;
                    break;
                case B:
                    bPressed = false;
                    break;
                case START:
                    startPressed = false;
                    break;
                case SELECT:
                    selectPressed = false;
                    break;
                case UP:
                    upPressed = false;
                    break;
                case DOWN:
                    downPressed = false;
                    break;
                case LEFT:
                    leftPressed = false;
                    break;
                case RIGHT:
                    rightPressed = false;
                    break;
            }
            log.debug("released " + e.getKeyChar());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing.
    }
}
