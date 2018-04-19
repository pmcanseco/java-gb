import javax.swing.*;

/**
 * Created by Pablo Canseco on 3/28/2018.
 */
class Gpu extends JPanel {
    private final String name ="GPU";
    private Logger log =  new Logger(name, Logger.Level.FATAL);
    public static final int width = 160;
    public static final int height = 144;

    enum Mode {
        HBLANK,
        VBLANK,
        OAM_ACCESS,
        VRAM_ACCESS
    }
    private Mode currentMode;
    private int modeClock;
    public int line;
    public int lyc;
    public int lcdControl = 0x91;
    public int lcdStatus = 0;
    public int scrollX;
    public int scrollY;
    public int[] vram = new int[0x2000]; // 8192
    private int[][][] tileset = new int[384][8][8];
    private int[] screen = new int[160*144];
    public int[] backgroundPalette = { 0, 3, 3, 3};
    public int[][] spritePalette = {  { 0, 3, 3, 3}  ,  { 0, 3, 3, 3}  };
    public int[] palette = { 0, 1, 2, 3};

    Gpu() {
        modeClock = 0;
        currentMode = Mode.VRAM_ACCESS;
    }
    Gpu(Logger.Level level) {
        this();
        this.log = new Logger(name, level);
    }

    public void step(int cycles) {
        modeClock += cycles;

        // GPU Mode Manager
        // Flow:
        // OAM_ACCESS -> VRAM_ACCESS -> HBLANK -> ... repeat thru line 143 -> VBLANK -> OAM_ACCESS
        switch(currentMode) {
            case OAM_ACCESS:
                if (modeClock >= 80) {
                    currentMode = Mode.VRAM_ACCESS;
                    modeClock = 0;
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
                        InterruptManager.getInstance()
                                .raiseInterrupt(InterruptManager.InterruptTypes.VBLANK);
                        Display.getInstance().renderFrame(screen);
                        //dumpTileData();
                    }
                    else {
                        currentMode = Mode.OAM_ACCESS;
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

        lcdStatus = getLcdStatus();
        processLcdStatusInterrupts();
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
    private void renderScanLine() {
        boolean bgmap =  ((lcdControl & 0b0000_1000) >> 3) != 0;
        boolean bgtile = ((lcdControl & 0b0001_0000) >> 4) == 0;

        int mapoffset = 0x1800; /*bgmap ? 0x1C00 : 0x1800;*/
        mapoffset += (((line + scrollY) & 0b1111_1111) >> 3) << 5;

        int lineoffset = (scrollX >> 3);

        int y = (line + scrollY) & 7;
        int x = (scrollX & 7);

        int canvasoffset = line * 160;

        int colorint;
        int tile = vram[mapoffset + lineoffset];

        if (bgtile && (tile < 128)) {
            tile += 256;
        }

        int[] scanlineRow = new int[160];

        for (int i=0; i < 160; i++) {
            colorint = tileset[tile][y][x];

            screen[canvasoffset] = backgroundPalette[colorint];
            canvasoffset++;

            scanlineRow[i] = colorint;

            x++;
            if (x == 8) {
                x = 0;
                lineoffset = (lineoffset + 1) & 31;
                tile = vram[mapoffset + lineoffset];
                if (bgtile && (tile < 128)) {
                    tile += 256;
                }
            }
        }
        log.info("Rendered scanline " + this.line);
    }

    public int getLcdStatus() {
        int value = lcdStatus;

        if (line == lyc) {
            value |= 0b0000_0100;
        }
        else {
            value &= 0b1111_1011;
        }

        value &= 0b1111_1100;
        value |= currentMode.ordinal();

        return value;
        // other bits will be as written by the rom.
    }
    private void processLcdStatusInterrupts() {

        boolean lycIntEnable = false;
        boolean oamCheckInt = false;
        boolean vblankCheckInt = false;
        boolean hblankCheckInt = false;
        boolean lycCheckInt = false;

        if ((lcdStatus & 0b0100_0000) != 0) lycIntEnable = true;
        if ((lcdStatus & 0b0010_0000) != 0) oamCheckInt = true;
        if ((lcdStatus & 0b0001_0000) != 0) vblankCheckInt = true;
        if ((lcdStatus & 0b0000_1000) != 0) hblankCheckInt = true;
        if ((lcdStatus & 0b0000_0100) != 0) lycCheckInt = true;

        if (  (lycIntEnable && lycCheckInt) ||
              oamCheckInt                   ||
              /*vblankCheckInt                ||*/
              hblankCheckInt  ) {

            InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.LCDC_STATUS);
        }
    }
}
