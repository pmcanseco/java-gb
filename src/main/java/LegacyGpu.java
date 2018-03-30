import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LegacyGpu extends JPanel {

    public static final int CONST_VRAM_SIZE = 8192;


    private BufferedImage canvas;

    static int[] screen = new int[160 * 144 * 4];
    static int[] vram = new int[CONST_VRAM_SIZE];

    private int[][][] tilemap = new int[512][8][8];
    private static int[][] palette = new int[4][4];
    private int mode = 0;
    private int modeclock = 0;
    private static int line = 0;
    private static int bgmap = 0;
    private static int bgtile = 0;
    private static int switchbg = 0;
    private static int switchlcd = 0;
    static int[][][] tileset = new int[384][8][8];


    // new implementation variables
    public enum lcdmode {
        HBlank(0), VBlank(1), SearchingOam(2), TransferringData(3);
        private final int value;

        lcdmode(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    static int[] oam = new int[256];
    private static boolean lcdcControlOperationEnabled;
    private static boolean lcdcLycLyCoincidenceInterruptEnabled;
    private static boolean lcdcOamInterruptEnabled;
    private static boolean lcdcVblankInterruptEnabled;
    private static boolean lcdcHblankInterruptEnabled;
    private boolean lcdcInterruptRequested;
    private boolean vBlankInterruptRequested;
    private static boolean largeSprites;
    private static boolean spritesDisplayed;
    private static boolean backgroundDisplayed;
    private static boolean lcdcInterruptEnabled;
    private static boolean vBlankInterruptEnabled;
    private static boolean lcdControlOperationEnabled;
    private static boolean windowTileMapDisplaySelect;
    private static boolean windowDisplayed;
    private static boolean backgroundAndWindowTileDataSelect;
    private static boolean backgroundTileMapDisplaySelect;
    private static boolean invalidateAllBackgroundTilesRequests;
    private static boolean invalidateAllSpriteTilesRequests;
    private static int scrollY, windowY;
    private static int scrollX, windowX;
    private static int lcdcMode;
    private static int ly;
    private static int lyCompare;
    static Color[][] windowBuffer = new Color[144][168];
    private Color[][] backgroundBuffer = new Color[256][256];
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color LIGHT_GRAY = new Color(180, 180, 180);
    public static final Color DARK_GRAY = new Color(96, 96, 96);
    public static final Color BLACK = new Color(0, 0, 0);
    private static Color[] backgroundPalette = {WHITE, LIGHT_GRAY, DARK_GRAY, BLACK};
    private static Color[] objectPalette0 = {WHITE, LIGHT_GRAY, DARK_GRAY, BLACK};
    private static Color[] objectPalette1 = {WHITE, LIGHT_GRAY, DARK_GRAY, BLACK};
    private Color[][][][] spriteTile = new Color[256][8][8][2];
    private boolean[] spriteTileInvalidated = new boolean[256];
    private boolean[][] backgroundTileInvalidated = new boolean[32][32];
    private Color[] pixels = new Color[160 * 144];

    // end new implementation variables

    public static Random rng = new Random();

    public LegacyGpu(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        JFrame frame = new JFrame("Gameboy");
        frame.setSize(width, height);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fillCanvas(Color.BLUE);
        for (int i = 0; i < 160 * 144 * 4; i++) {
            screen[i] = rng.nextInt(255);
        }
        //drawScreen();
    }


    public void step() {
        for (int y = 0, pixelIndex = 0; y < 144; y++) {
            ly = y;
            lcdcMode = lcdmode.SearchingOam.getValue();
            if (lcdcInterruptEnabled
                    && (lcdcOamInterruptEnabled
                    || (lcdcLycLyCoincidenceInterruptEnabled && lyCompare == y))) {
                lcdcInterruptRequested = true;
            }

            updateWindow();
            updateBackground();
            updateSpriteTiles();

            int currwindowX = windowX - 7;
            int windowPointY = windowY + y;

            for (int x = 0; x < 160; x++, pixelIndex++) {

                Color intensity = BLACK;

                if (backgroundDisplayed) {
                    intensity = backgroundBuffer[0xFF & (scrollY + y)][0xFF & (scrollX + x)];
                }

                if (windowDisplayed && y >= windowY && y < windowY + 144 && x >= windowX && x < windowX + 160
                        && windowX >= -7 && windowX <= 159 && windowY >= 0 && windowY <= 143) {
                    intensity = windowBuffer[y - windowY][x - windowX];
                }

                pixels[pixelIndex] = intensity;
            }

            if (spritesDisplayed) {
                if (largeSprites) {
                    for (int address = 0; address < 160; address += 4) {
                        int spriteY = oam[address];
                        int spriteX = oam[address + 1];
                        if (spriteY == 0 || spriteX == 0 || spriteY >= 160 || spriteX >= 168) {
                            continue;
                        }
                        spriteY -= 16;
                        if (spriteY > y || spriteY + 15 < y) {
                            continue;
                        }
                        spriteX -= 8;

                        int spriteTileIndex0 = 0xFE & oam[address + 2];
                        int spriteTileIndex1 = spriteTileIndex0 | 0x01;
                        int spriteFlags = oam[address + 3];
                        boolean spritePriority = (0x80 & spriteFlags) == 0x80;
                        boolean spriteYFlipped = (0x40 & spriteFlags) == 0x40;
                        boolean spriteXFlipped = (0x20 & spriteFlags) == 0x20;
                        int spritePalette = (0x10 & spriteFlags) == 0x10 ? 1 : 0;

                        if (spriteYFlipped) {
                            int temp = spriteTileIndex0;
                            spriteTileIndex0 = spriteTileIndex1;
                            spriteTileIndex1 = temp;
                        }

                        int spriteRow = y - spriteY;
                        if (spriteRow >= 0 && spriteRow < 8) {
                            int screenAddress = (y << 7) + (y << 5) + spriteX;
                            for (int x = 0; x < 8; x++, screenAddress++) {
                                int screenX = spriteX + x;
                                if (screenX >= 0 && screenX < 160) {
                                    Color color = spriteTile[spriteTileIndex0][spriteYFlipped ? 7 - spriteRow : spriteRow][spriteXFlipped ? 7 - x : x][spritePalette];
                                    if (!color.equals(BLACK)) {
                                        if (spritePriority) {
                                            if (pixels[screenAddress].equals(WHITE)) {
                                                pixels[screenAddress] = color;
                                            }
                                        } else {
                                            pixels[screenAddress] = color;
                                        }
                                    }
                                }
                            }
                            continue;
                        }

                        spriteY += 8;

                        spriteRow = y - spriteY;
                        if (spriteRow >= 0 && spriteRow < 8) {
                            int screenAddress = (y << 7) + (y << 5) + spriteX;
                            for (int x = 0; x < 8; x++, screenAddress++) {
                                int screenX = spriteX + x;
                                if (screenX >= 0 && screenX < 160) {
                                    Color color = spriteTile[spriteTileIndex1][spriteYFlipped ? 7 - spriteRow : spriteRow][spriteXFlipped ? 7 - x : x][spritePalette];
                                    if (!color.equals(BLACK)) {
                                        if (spritePriority) {
                                            if (pixels[screenAddress].equals(WHITE)) {
                                                pixels[screenAddress] = color;
                                            }
                                        } else {
                                            pixels[screenAddress] = color;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (int address = 0; address < 160; address += 4) {
                        int spriteY = oam[address];
                        int spriteX = oam[address + 1];
                        if (spriteY == 0 || spriteX == 0 || spriteY >= 160 || spriteX >= 168) {
                            continue;
                        }
                        spriteY -= 16;
                        if (spriteY > y || spriteY + 7 < y) {
                            continue;
                        }
                        spriteX -= 8;

                        int spriteTileIndex = oam[address + 2];
                        int spriteFlags = oam[address + 3];
                        boolean spritePriority = (0x80 & spriteFlags) == 0x80;
                        boolean spriteYFlipped = (0x40 & spriteFlags) == 0x40;
                        boolean spriteXFlipped = (0x20 & spriteFlags) == 0x20;
                        int spritePalette = (0x10 & spriteFlags) == 0x10 ? 1 : 0;

                        int spriteRow = y - spriteY;
                        int screenAddress = (y << 7) + (y << 5) + spriteX;
                        for (int x = 0; x < 8; x++, screenAddress++) {
                            int screenX = spriteX + x;
                            if (screenX >= 0 && screenX < 160) {
                                Color color = spriteTile[spriteTileIndex][spriteYFlipped ? 7 - spriteRow : spriteRow][spriteXFlipped ? 7 - x : x][spritePalette];
                                if (!color.equals(BLACK)) {
                                    if (spritePriority) {
                                        if (pixels[screenAddress].equals(WHITE)) {
                                            pixels[screenAddress] = color;
                                        }
                                    } else {
                                        pixels[screenAddress] = color;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            lcdcMode = lcdmode.HBlank.getValue();
            if (lcdcInterruptEnabled && lcdcHblankInterruptEnabled) {
                lcdcInterruptRequested = true;
            }
        }


        lcdcMode = lcdmode.VBlank.getValue();
        if (vBlankInterruptEnabled) {
            vBlankInterruptRequested = true;
        }
        if (lcdcInterruptEnabled && lcdcVblankInterruptEnabled) {
            lcdcInterruptRequested = true;
        }
        for (int y = 144; y <= 153; y++) {
            ly = y;
            if (lcdcInterruptEnabled && lcdcLycLyCoincidenceInterruptEnabled
                    && lyCompare == y) {
                lcdcInterruptRequested = true;
            }
        }
        drawScreen();
    }

    public void reset() {
        for (int i = 0; i < 8192; i++) {
            vram[i] = 0;
        }
        for (int i = 0; i < 160; i++) {
            oam[i] = 0;
        }
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    tilemap[i][j][k] = 0;
                }
            }
        }

        mode = 2;
        modeclock = 0;

        System.out.println("Gpu Reset");
    }

    public static void updatetile(int addr, int val) {
        // get the base address for thie tile row
        addr &= 0xFF;
        addr &= 0x1FFE;

        //evaluate which tile and row was updated
        int tile = (addr >> 4) & 511;
        int y = (addr >> 1) & 7;

        int sx;
        for (int x = 0; x < 8; x++) {
            sx = 1 << (7 - x); // find bit index for this pixel
            tileset[tile][y][x] = ((((vram[addr] & sx) != 0) ? 1 : 0) + (((vram[addr + 1] & sx) != 0) ? 2 : 0));
        }
    }

    public static void renderscan() {
        // VRAM offset for the tile map
        int mapOffsets = (bgmap != 0 ? 0x1C00 : 0x1800);

        // which line of tiles to use in the map
        mapOffsets += ((line + scrollX) & 255) >> 3;

        // which tile to start with in the map line
        int lineOffsets = (scrollX >> 3);

        // which line of pixels to use inthe tiles
        int y = ((line + scrollY) & 7);

        // where in the tile line to start
        int x = (scrollX & 7);

        // where to render on the canvas
        int canvasOffsets = (line * 160 * 4);

        // read tile index from the background map
        int[] color;
        int tile = vram[mapOffsets + lineOffsets];

        // if tile data set is #1, use signed indices, calc real tile offset
        if (bgtile == 1 && tile < 128) tile += 256;

        for (int i = 0; i < 160; i++) {
            //re-map the tile pixel through the palette
            color = palette[tileset[tile][y][x]];

            //plot pixel to canvas
            screen[canvasOffsets] = color[0];
            screen[canvasOffsets + 1] = color[1];
            screen[canvasOffsets + 2] = color[2];
            screen[canvasOffsets + 3] = color[3];
            canvasOffsets += 4;

            //when this tile ends, read another
            x++;
            if (x == 8) {
                x = 0;
                lineOffsets = ((lineOffsets + 1) & 31);
                tile = vram[mapOffsets + lineOffsets];
                if (bgtile == 1 && tile < 128) tile += 256;
            }
        }
        System.out.println("renderscan()");
    }

    public static int rb(int addr) {
        switch (addr) {
            case 0xFF40: { // LCD Control
                int value = 0;
                if (lcdcControlOperationEnabled) value |= 0x80;
                if (windowTileMapDisplaySelect) value |= 0x40;
                if (windowDisplayed) value |= 0x20;
                if (backgroundAndWindowTileDataSelect) value |= 0x10;
                if (backgroundTileMapDisplaySelect) value |= 0x08;
                if (largeSprites) value |= 0x04;
                if (spritesDisplayed) value |= 0x02;
                if (backgroundDisplayed) value |= 0x02;
                return value;
            }
            case 0xFF41: { // LCD Status
                int value = 0;
                if (lcdcLycLyCoincidenceInterruptEnabled) value |= 0x40;
                if (lcdcOamInterruptEnabled) value |= 0x20;
                if (lcdcVblankInterruptEnabled) value |= 0x10;
                if (lcdcHblankInterruptEnabled) value |= 0x08;
                if (ly == lyCompare) value |= 0x04;
                value |= lcdcMode;
                return value;
            }
            case 0xFF42:
                return scrollY; // Scroll Y
            case 0xFF43:
                return scrollX; // Scroll X
            case 0xFF44:
                return ly; // LY (current line)
            case 0xFF45:
                return lyCompare; // LY Compare
            case 0xFF47: { // Background Palette
                invalidateAllBackgroundTilesRequests = true;
                int value = 0;
                for (int i = 3; i >= 0; i--) {
                    value <<= 2;
                    switch (backgroundPalette[i].getRGB()) {
                        case -16777216:
                            value |= 3;
                            break;
                        case -10461088:
                            value |= 2;
                            break;
                        case -4934476:
                            value |= 1;
                            break;
                        case -1:
                            break;
                    }
                }
                return value;
            }
            case 0xFF48: { // Object palette 0
                invalidateAllSpriteTilesRequests = true;
                int value = 0;
                for (int i = 3; i >= 0; i--) {
                    value <<= 2;
                    switch (objectPalette0[i].getRGB()) {
                        case -16777216:
                            value |= 3;
                            break;
                        case -10461088:
                            value |= 2;
                            break;
                        case -4934476:
                            value |= 1;
                            break;
                        case -1:
                            break;
                    }
                }
                return value;
            }
            case 0xFF49: { // Object palette 1
                invalidateAllSpriteTilesRequests = true;
                int value = 0;
                for (int i = 3; i >= 0; i--) {
                    value <<= 2;
                    switch (objectPalette1[i].getRGB()) {
                        case -16777216:
                            value |= 3;
                            break;
                        case -10461088:
                            value |= 2;
                            break;
                        case -4934476:
                            value |= 1;
                            break;
                        case -1:
                            break;
                    }
                }
                return value;
            }
            case 0xFF4A:
                return windowY; // Window Y
            case 0xFF4B:
                return windowX; // Window X
            case 0xFFFF: // TODO : Interrupts
                return 0;
        }
        return 0;
    }

    public static void wb(int addr, int value) {
        switch (addr) {
            // LCD Control
            case 0xFF40:
                boolean bAWTDS = backgroundAndWindowTileDataSelect;
                boolean bTMDS = backgroundTileMapDisplaySelect;
                boolean wTMDS = windowTileMapDisplaySelect;

                lcdControlOperationEnabled = (value & 0x80) == 0x80;
                windowTileMapDisplaySelect = (value & 0x40) == 0x40;
                windowDisplayed = (value & 0x20) == 0x20;
                backgroundAndWindowTileDataSelect = (value & 0x10) == 0x10;
                backgroundTileMapDisplaySelect = (value & 0x08) == 0x08;
                largeSprites = (value & 0x04) == 0x04;
                spritesDisplayed = (value & 0x02) == 0x02;
                backgroundDisplayed = (value & 0x01) == 0x01;

                if (bAWTDS != backgroundAndWindowTileDataSelect
                        || bTMDS != backgroundTileMapDisplaySelect
                        || wTMDS != windowTileMapDisplaySelect) {
                    invalidateAllBackgroundTilesRequests = true;
                }
                break;
            case 0xFF41: // LCDC Status
                lcdcLycLyCoincidenceInterruptEnabled = (value & 0x40) == 0x40;
                lcdcOamInterruptEnabled = (value & 0x20) == 0x20;
                lcdcVblankInterruptEnabled = (value & 0x10) == 0x10;
                lcdcHblankInterruptEnabled = (value & 0x08) == 0x08;
                lcdcMode = (value & 0x03);
                break;
            case 0xFF42:
                scrollY = value;
                break; // Scroll Y
            case 0xFF43:
                scrollX = value;
                break; // Scroll X
            case 0xFF44:
                ly = value;
                break; // LY
            case 0xFF45:
                lyCompare = value;
                break; // LY Compare
            case 0xFF47: // Background palette
                for (int i = 0; i < 4; i++) {
                    switch (value & 0x03) {
                        case 0:
                            backgroundPalette[i] = WHITE;
                            break;
                        case 1:
                            backgroundPalette[i] = LIGHT_GRAY;
                            break;
                        case 2:
                            backgroundPalette[i] = DARK_GRAY;
                            break;
                        case 3:
                            backgroundPalette[i] = BLACK;
                            break;
                    }
                    value >>= 2;
                }
                invalidateAllBackgroundTilesRequests = true;
                break;
            case 0xFF48: // Object palette 0
                for (int i = 0; i < 4; i++) {
                    switch (value & 0x03) {
                        case 0:
                            objectPalette0[i] = WHITE;
                            break;
                        case 1:
                            objectPalette0[i] = LIGHT_GRAY;
                            break;
                        case 2:
                            objectPalette0[i] = DARK_GRAY;
                            break;
                        case 3:
                            objectPalette0[i] = BLACK;
                            break;
                    }
                    value >>= 2;
                }
                invalidateAllBackgroundTilesRequests = true;
                break;
            case 0xFF49: // Object palette 1
                for (int i = 0; i < 4; i++) {
                    switch (value & 0x03) {
                        case 0:
                            objectPalette1[i] = WHITE;
                            break;
                        case 1:
                            objectPalette1[i] = LIGHT_GRAY;
                            break;
                        case 2:
                            objectPalette1[i] = DARK_GRAY;
                            break;
                        case 3:
                            objectPalette1[i] = BLACK;
                            break;
                    }
                    value >>= 2;
                }
                invalidateAllBackgroundTilesRequests = true;
                break;
            case 0xFF4A:
                windowY = value;
                break; // Window Y
            case 0xFF4B:
                windowX = value;
                break; // Window X
            case 0xFFFF: // TODO Interrupts
                break;
        }
    }


    public void updateSpriteTiles() {
        for (int i = 0; i < 256; i++) {
            if (spriteTileInvalidated[i] || invalidateAllSpriteTilesRequests) {
                spriteTileInvalidated[i] = false;
                int address = i << 4;
                for (int y = 0; y < 8; y++) {
                    int lowByte = vram[address++];
                    int highByte = vram[address++] << 1;
                    for (int x = 7; x >= 0; x--) {
                        int paletteIndex = (0x02 & highByte) | (0x01 & lowByte);
                        lowByte >>= 1;
                        highByte >>= 1;
                        if (paletteIndex > 0) {
                            spriteTile[i][y][x][0] = objectPalette0[paletteIndex];
                            spriteTile[i][y][x][1] = objectPalette1[paletteIndex];
                        } else {
                            spriteTile[i][y][x][0] = WHITE;
                            spriteTile[i][y][x][1] = WHITE;
                        }
                    }
                }
            }
        }
        invalidateAllSpriteTilesRequests = false;
    }

    public void updateWindow() {
        int tileMapAddress = windowTileMapDisplaySelect ? 0x1C00 : 0x1800;
        if (backgroundAndWindowTileDataSelect) {
            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 21; j++) {
                    if (backgroundTileInvalidated[i][j] || invalidateAllBackgroundTilesRequests) {
                        int tileDataAddress = vram[tileMapAddress + ((i << 5) | j)] << 4;
                        int y = i << 3;
                        int x = j << 3;
                        for (int k = 0; k < 8; k++) {
                            int lowByte = vram[tileDataAddress++];
                            int highByte = vram[tileDataAddress++] << 1;
                            for (int b = 7; b >= 0; b--) {
                                windowBuffer[y + k][x + b] = backgroundPalette[(0x02 & highByte) | (0x01 & lowByte)];
                                lowByte >>= 1;
                                highByte >>= 1;
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 21; j++) {
                    if (backgroundTileInvalidated[i][j] || invalidateAllBackgroundTilesRequests) {
                        int tileDataAddress = vram[tileMapAddress + ((i << 5) | j)];
                        if (tileDataAddress > 127) {
                            tileDataAddress -= 256;
                        }
                        tileDataAddress = 0x1000 + (tileDataAddress << 4);
                        int y = i << 3;
                        int x = j << 3;
                        for (int k = 0; k < 8; k++) {
                            int lowByte = vram[tileDataAddress++];
                            int highByte = vram[tileDataAddress++] << 1;
                            for (int b = 7; b >= 0; b--) {
                                windowBuffer[y + k][x + b] = backgroundPalette[(0x02 & highByte) | (0x01 & lowByte)];
                                lowByte >>= 1;
                                highByte >>= 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateBackground() {
        int tileMapAddress = backgroundTileMapDisplaySelect ? 0x1C00 : 0x1800;
        if (backgroundAndWindowTileDataSelect) {
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++, tileMapAddress++) {
                    if (backgroundTileInvalidated[i][j] || invalidateAllBackgroundTilesRequests) {
                        backgroundTileInvalidated[i][j] = false;
                        int tileDataAddress = vram[tileMapAddress] << 4;
                        int y = i << 3;
                        int x = j << 3;
                        for (int k = 0; k < 8; k++) {
                            int lowByte = vram[tileDataAddress++];
                            int highByte = vram[tileDataAddress++] << 1;
                            for (int b = 7; b >= 0; b--) {
                                backgroundBuffer[y + k][x + b] = backgroundPalette[(0x02 & highByte) | (0x01 & lowByte)];
                                lowByte >>= 1;
                                highByte >>= 1;
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++, tileMapAddress++) {
                    if (backgroundTileInvalidated[i][j] || invalidateAllBackgroundTilesRequests) {
                        backgroundTileInvalidated[i][j] = false;
                        int tileDataAddress = vram[tileMapAddress];
                        if (tileDataAddress > 127) {
                            tileDataAddress -= 256;
                        }
                        tileDataAddress = 0x1000 + (tileDataAddress << 4);
                        int y = i << 3;
                        int x = j << 3;
                        for (int k = 0; k < 8; k++) {
                            int lowByte = vram[tileDataAddress++];
                            int highByte = vram[tileDataAddress++] << 1;
                            for (int b = 7; b >= 0; b--) {
                                backgroundBuffer[y + k][x + b] = backgroundPalette[(0x02 & highByte) | (0x01 & lowByte)];
                                lowByte >>= 1;
                                highByte >>= 1;
                            }
                        }
                    }
                }
            }
        }
        invalidateAllBackgroundTilesRequests = false;
    }


    ///// canvas helper functions /////
    public void fillCanvas(Color c) {
        int color = c.getRGB();
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }

    public void drawScreen() {
        for (int i = 0; i < 160; i++) {
            for (int j = 0; j < 144; j++) {
                /*int r = screen[i*j];
				int g = screen[i*j+1];
				int b = screen[i*j+2];
				if(r>255) r = 255;
				if(g>255) g = 255;
				if(b>255) b = 255;*/
                Color c = pixels[i * j];
                //System.out.println(c);
                canvas.setRGB(i, j, c.getRGB());
                if (!c.equals(Color.WHITE)) System.out.print("Color wasn't white!");
                if (100 < i && i < 120) canvas.setRGB(i, j, Color.CYAN.getRGB());
            }
        }
        repaint();
        //System.out.println("drawScreen()");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

}
