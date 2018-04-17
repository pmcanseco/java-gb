import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by Pablo Canseco on 4/13/2018.
 */

public class Display extends JPanel implements KeyListener {

    private String name ="GUI";
    private Logger log =  new Logger(name, Logger.Level.WARN);
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
        canvasTestPattern();
    }
    private Display(boolean testMode) {
        this.isTestMode = testMode;
        name += "/T";
        log = new Logger(name, Logger.Level.WARN);
    }

    private BufferedImage canvas;
    private JFrame frame;

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
        canvas = new BufferedImage(Gpu.width, Gpu.height, BufferedImage.TYPE_INT_ARGB);
        frame.setSize(320, 288);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
        frame.addKeyListener(this);
    }
    private void canvasTestPattern() {
        for (int i = 0; i < 160; i++) {
            for (int j = 0; j < 144; j++) {
                canvas.setRGB(i, j, Colors.getRandomColor().getRGB());

                if (i < (160/2) && j < (144/2))
                    canvas.setRGB(i, j, Colors.DARK.getColor().getRGB());
                else if (i < (160/2) && j > (144/2))
                    canvas.setRGB(i, j, Colors.DARK.getColor().getRGB());
                else if (i > (160/2) && j < (144/2))
                    canvas.setRGB(i, j, Colors.LIGHT.getColor().getRGB());
                else
                    canvas.setRGB(i, j, Colors.getRandomColor().getRGB());
            }
        }
        frame.repaint();
    }

    public void renderFrame(int[] screen) {
        if (!isTestMode) {
            for (int y = 0; y < 144; y++) {
                for (int x = 0; x < 160; x++) {
                    Colors c = Colors.get(screen[((160 * y) + x)]);
                    canvas.setRGB(x, y, c.getColor().getRGB());
                }
            }
            frame.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        log.warning("keyTyped: " + e.getKeyChar() + " : " + e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.warning("keyPressed: " + e.getKeyChar() + " : " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.warning("keyReleased: " + e.getKeyChar() + " : " + e.getKeyCode());
    }
}
