import helpers.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Random;

/**
 * Created by Pablo Canseco on 4/13/2018.
 */

public class Display extends JPanel {

    private String name ="GUI";
    private Logger log =  new Logger(name, Logger.Level.INFO);
    private boolean isTestMode = false;

    // Singleton
    private static Display instance;
    public static Display getInstance() {
        if (instance == null) {
            instance = new Display();
        }
        return instance;
    }
    public static Display getTestInstace() {
        if (instance == null) {
            instance = new Display(true);
            return instance;
        }
        else {
            instance.log.fatal("Could not create test instance becanse an instance already exists.");
            return null;
        }
    }
    public static void reset() {
        instance = null;
    }
    private Display() {
        initAppWindow();
    }
    private Display(boolean testMode) {
        this.isTestMode = testMode;
        name += "/T";
        log = new Logger(name, Logger.Level.WARN);
    }

    private GraphicsEnvironment ge;
    private GraphicsConfiguration gc;
    private BufferedImage canvasBuffer;
    private VolatileImage canvas;
    private JFrame frame;

    private final int scaleFactor = 2;
    private final int frameXoffset = 6;
    private final int frameYoffset = 34;

    public enum Colors {

        OFF(255, 255, 255),
        LIGHT(192, 192, 192),
        DARK(96, 96, 96),
        ON(40, 40, 40);

        //<editor-fold desc=" IMPLEMENTATION " defaultstate="collapsed">
        private final int r;
        private final int g;
        private final int b;
        private final String rgb;
        Colors(final int r,final int g,final int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.rgb = r + ", " + g + ", " + b;
        }
        public Color getColor() {
            return new Color(r, g, b);
        }
        public static Color getRandomColor() {
            Random random = new Random();
            Colors c = values()[random.nextInt(values().length)];
            return new Color(c.r, c.g, c.b);
        }
        public static Colors get(int index) {
            return Colors.values()[index];
        }
        //</editor-fold>
    }

    private void initAppWindow() {
        frame = new JFrame("java-gb");

        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

        canvasBuffer = gc.createCompatibleImage(Gpu.width, Gpu.height, Transparency.TRANSLUCENT);
        canvas = gc.createCompatibleVolatileImage(Gpu.width * scaleFactor, Gpu.height  * scaleFactor, Transparency.TRANSLUCENT);
        canvas.setAccelerationPriority( (float) 1.0 );

        frame.setSize((Gpu.width * scaleFactor) + frameXoffset, (Gpu.height * scaleFactor) + frameYoffset);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        frame.getContentPane().add(new JLabel(new ImageIcon(canvas)), FlowLayout.LEFT);
        frame.addKeyListener(Joypad.getInstance());

        log.info("HW Acceleration:       " + canvas.getCapabilities(gc).isAccelerated());
        log.info("Acceleration Priority: " + canvas.getAccelerationPriority());
    }

    public void renderFrame(int[] screen) {
        if (!isTestMode) {
            for (int y = 0; y < 144; y++) {
                for (int x = 0; x < 160; x++) {
                    Colors c = Colors.get(screen[((160 * (y)) + (x))]);
                    canvasBuffer.setRGB(x, y, c.getColor().getRGB());
                }
            }

            Graphics2D g2 = canvas.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(canvasBuffer, 0, 0, Gpu.width * scaleFactor, Gpu.height * scaleFactor, null);

            frame.repaint();
        }
    }

}
