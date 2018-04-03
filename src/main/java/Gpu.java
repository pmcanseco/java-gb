import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by Pablo Canseco on 3/28/2018.
 */
class Gpu extends JPanel {
    private final String name ="GPU";
    private Logger log =  new Logger(name);
    private int width = 160;
    private int height = 144;
    public BufferedImage canvas;
    public JFrame frame;
    private boolean isTestMode = true;

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
        frame.setSize(300, 200);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
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
    }

    enum Mode {
        HBLANK,
        VBLANK,
        OAM_ACCESS,
        VRAM_ACCESS
    }
    private Mode currentMode;
    private int modeClock;
    public int line;
    public int lcdControl = 0x91;
    public int scrollX;
    public int scrollY;
    public int[] vram = new int[0x2000]; // 8192
    public int[][][] tileset = new int[384][8][8];
    private int[] screen = new int[160*144];

    Gpu() {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        modeClock = 0;
        currentMode = Mode.VRAM_ACCESS;
    }
    Gpu(Logger.Level level) {
        this();
        this.log = new Logger(name, level);
    }
    Gpu(boolean testMode) {
        this();
        this.isTestMode = testMode;

        if (!isTestMode) {
            frame = new JFrame("java-gb");
            initAppWindow();
            canvasTestPattern();
            frame.repaint();
        }
    }

    public boolean step(int cycles) {
        // this function returns true if VBLANK interrupt else false.
        boolean retval = false;

        modeClock += cycles;

        // GPU Mode Manager
        // Flow:
        // OAM_ACCESS -> VRAM_ACCESS -> HBLANK -> ... repeat thru line 143 -> VBLANK -> OAM_ACCESS
        switch(currentMode) {
            case OAM_ACCESS:
                if (modeClock >= 80) {
                    currentMode = Mode.VRAM_ACCESS;
                    modeClock = 0;
                    //log.debug("Entering VRAM_ACCESS line" + line);
                }
                break;
            case VRAM_ACCESS:
                if (modeClock >= 172) {
                    currentMode = Mode.HBLANK;
                    modeClock = 0;
                    //log.debug("Entering HBLANK line" + line);
                    // write line to framebuffer (canvas)
                    renderScanLine();
                }
                break;
            case HBLANK:
                if (modeClock >= 204) {
                    modeClock = 0;
                    line++;

                    if (line == 143) {
                        currentMode = Mode.VBLANK;
                        retval = true;
                        //log.debug("Triggered VBLANK interrupt.");
                        renderFrame();
                        renderTileData();
                    }
                    else {
                        currentMode = Mode.OAM_ACCESS;
                        //log.debug("Entering OAM_ACCESS line" + line);
                    }
                }
                break;
            case VBLANK:
                if (modeClock >= 456) {
                    modeClock = 0;
                    line++;

                    if (line > 153) {
                        currentMode = Mode.OAM_ACCESS;
                        line = 0;
                        //log.debug("Entering OAM_ACCESS line" + line);
                    }
                }
                break;
        }
        return retval;
    }

    public void updateTile(int address, int value) {

        // get base address for this tile row
        address &= 0x1FFE;

        // work out which tile and row was updated
        int tile = (address >> 4) & 511;
        int y = (address >> 1) & 7;

        int sx;
        for(int i = 0; i < 8; i++) {

            // find bit index for this pixel
            sx = 1 << (7-i);

            int val = ((vram[address] & sx)  != 0  ? 1 : 0) |
                      ((vram[address+1] & sx) != 0 ? 2 : 0);

            log.debug("updating tile " + tile + " row " + y + " value " + val);

            //update tileset
            tileset[tile][y][i] = val;
        }
    }
    public void renderScanLine() {
        boolean bgmap =  ((lcdControl & 0b0000_1000) >> 3) != 0;
        boolean bgtile = ((lcdControl & 0b0001_0000) >> 4) != 0;

        if (line == 3 && scrollY == 0x4A) {
            log.warning("WE ARE HERE");
        }

        int mapoffset = 0x1800; /*bgmap ? 0x1C00 : 0x1800;*/
        mapoffset += (((line + scrollY) & 0b1111_1111) >> 3) << 5;

        int lineoffset = (scrollX >> 3);

        int y = (line /*+ scrollY*/) & 7;
        int x = (scrollX & 7);

        int canvasoffset = line * 160;

        int colorint;
        int tile = vram[mapoffset + lineoffset];

        /*if (bgtile && (tile < 128)) {
            tile += 256;
        }*/

        int[] scanlineRow = new int[160];

        for (int i=0; i < 160; i++) {
            colorint = tileset[tile][y][x];

            //log.warning("color " + colorint);
            if (tile != 0) {
                log.fatal("NONZERO TILE");
            }
            if (colorint != 0) {
                log.fatal("NONZERO COLOR");
            }

            screen[canvasoffset] = colorint;
            canvasoffset++;

            scanlineRow[i] = colorint;

            x++;
            if (x == 8) {
                x = 0;
                lineoffset = (lineoffset + 1) & 31;
                tile = vram[mapoffset + lineoffset];
                /*if (bgtile && (tile < 128)) {
                    tile += 256;
                }(*/
            }
        }
        log.info("Rendered scanline " + this.line);
    }
    public void renderFrame() {
        for(int y=0; y<144; y++) {
            for(int x=0; x<160; x++) {
                Colors c = Colors.get(screen[((160*y) + x)]);
                canvas.setRGB(x, y, c.getColor().getRGB());
            }
        }
        if (!isTestMode){
            frame.repaint();
        }
        log.debug("Rendered frame.");
    }

    public void renderTileData() {
        for(int tile = 0; tile < 0x20; tile++) {
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    System.out.print(String.format("%02x ", tileset[tile][y][x]).replace("00", "  "));
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }
}
