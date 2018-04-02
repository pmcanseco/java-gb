/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManager {
    private Logger log = new Logger(this.getClass().getName());
    private final static String bootrom =
            "31FEFFAF21FF9F32CB7C20FB2126FF0E" +
            "113E8032E20C3EF3E2323E77773EFCE0" +
            "471104012110801ACD9500CD9600137B" +
            "FE3420F311D80006081A1322230520F9" +
            "3E19EA1099212F990E0C3D2808320D20" +
            "F92E0F18F3673E6457E0423E91E04004" +
            "1E020E0CF044FE9020FA0D20F71D20F2" +
            "0E13247C1E83FE6228061EC1FE642006" +
            "7BE20C3E87F2F04290E0421520D20520" +
            "4F162018CB4F0604C5CB1117C1CB1117" +
            "0520F522232223C9CEED6666CC0D000B" +
            "03730083000C000D0008111F8889000E" +
            "DCCC6EE6DDDDD999BBBB67636E0EECCC" +
            "DDDC999FBBB9333E3c42B9A5B9A5424C" +
            "21040111A8001A13BE20FE237DFE3420" +
            "F506197886230520FB8620FE3E01E050";
    public static int[] getBiosLogo() {
        return hexStringToByteArray(bootrom.toLowerCase().substring(336, 432));
    }
    private static int[] getBios() {
        return hexStringToByteArray(bootrom.toLowerCase());
    }

    private Cartridge cartridge;
    private Gpu gpu;
    public final int memorySize = 0xFFFF;

    private int[] ram = new int[memorySize];

    private int[] sram = new int[0x2000]; // 8192
    private int[] io   = new int[0x100];  // 256
    private int[] oam  = new int[0x100];  // 256
    private int[] wram = new int[0x2000]; // 8192
    private int[] hram = new int[0x80];   // 128
    private int interruptFlags;
    private int interruptEnable;

    private boolean inBootrom = true;

    MemoryManager(Cartridge cart, Gpu gpu) {
        this.cartridge = cart;
        this.gpu = gpu;
    }
    MemoryManager(Cartridge cart) {
        this(cart, new Gpu());
    }

    public void zeroize() {
        for (int i = 0; i < memorySize; i++) {
            ram[i] = 0;
        }
    }

    public int readByte(final int address) throws IndexOutOfBoundsException {
        if (isValidMemoryAddress(address)) {
            //return ram[address];

            // from CTurt/Cinoop
            if (address <= 0x7fff) {
                if (inBootrom)
                {
                    if (address < 0x0100) { // less than 256
                        return getBios()[address];
                    }
                    else if (address == 0x0100) { // pc is 256
                        inBootrom = false;
                        log.fatal("WE LEFT HE BIOS");
                        System.exit(0);
                    }
                }

                return cartridge.readFromAddress(address);
            }
            else if (address >= 0x8000 && address <= 0x9fff) {
                return gpu.vram[address - 0x8000];
            }
            else if (address >= 0xa000 && address <= 0xbfff) {
                return sram[address - 0xa000];
            }
            else if (address >= 0xc000 && address <= 0xdfff) {
                return wram[address - 0xc000];
            }
            else if (address >= 0xe000 && address <= 0xfdff) {
                return wram[address - 0xe000];
            }
            else if (address >= 0xfe00 && address <= 0xfeff) {
                return oam[address - 0xfe00];
            }
            // TODO Should return a div timer, but a random number works just as well for Tetris
            else if (address == 0xff04) {
                return new java.util.Random().nextInt(256);
            }
            else if (address == 0xff40) {
                return gpu.lcdControl;
            }
            else if (address == 0xff42) {
                if(gpu.scrollY <= 5) {
                    log.debug("SCROLLY = " + gpu.scrollY);
                }
                //dumpVram();
                return gpu.scrollY;
            }
            else if (address == 0xff43) {
                return gpu.scrollX;
            }
            else if (address == 0xff44) {
                log.debug("mmu read gpu.line = " + gpu.line);
                return gpu.line; // read only
            }
            else if (address == 0xff00) {
                if((io[0x00] & 0x20) == 0) {
                    //return (unsigned char)(0xc0 | keys.keys1 | 0x10);
                    log.debug("Unimplemented RAM address " + address + ". Deals with input.");
                }

                else if((io[0x00] & 0x10) == 0) {
                    //return (unsigned char)(0xc0 | keys.keys2 | 0x20);
                    log.debug("Unimplemented RAM address " + address + ". Deals with input.");
                }

                else if((io[0x00] & 0x30) == 0) {
                    return 0xff;
                }
                else {
                    return 0;
                }
            }
            else if (address == 0xff0f) {
                return interruptFlags;
            }
            else if (address == 0xffff) {
                return interruptEnable;
            }
            else if (address >= 0xff80 && address <= 0xfffe) {
                return hram[address - 0xff80];
            }
        }
        else {
            throw new IndexOutOfBoundsException(address + " isn't a valid memory address.");
        }

        return 0;
    }
    public void writeByte(final int address, final int value) throws IndexOutOfBoundsException, NumberFormatException {
        if (!isValidMemoryAddress(address)) {
            throw new IndexOutOfBoundsException();
        }

        if (value > 255 || value < 0) {
            throw new NumberFormatException(value + " isn't between 0 and 255 inclusive");
        }

        //ram[address] = value;

        // from CTurt/Cinoop
        if(address >= 0xa000 && address <= 0xbfff) {
            sram[address - 0xa000] = value;
        }
        else if(address >= 0x8000 && address <= 0x9fff) {
            gpu.vram[address - 0x8000] = value;
            if(address <= 0x97ff) {
                gpu.updateTile(address, value);
                log.debug(String.format("write %02x updateTile(address,%02x)", address, value));
            }
        }

        if(address >= 0xc000 && address <= 0xdfff) {
            wram[address - 0xc000] = value;
        }
        else if(address >= 0xe000 && address <= 0xfdff) {
            wram[address - 0xe000] = value;
        }
        else if(address >= 0xfe00 && address <= 0xfeff) {
            oam[address - 0xfe00] = value;
        }
        else if(address >= 0xff80 && address <= 0xfffe) {
            hram[address - 0xff80] = value;
        }
        else if(address == 0xff40) {
            gpu.lcdControl = value;
        }
        else if(address == 0xff42) {
            gpu.scrollY = value;
        }
        else if(address == 0xff43) {
            gpu.scrollX = value;
        }
        else if(address == 0xff46) {
            log.debug("write " + address + "copy(0xfe00, value << 8, 160); // OAM DMA");
        }
        else if(address == 0xff47) { // write only
            //for(int i = 0; i < 4; i++) backgroundPalette[i] = palette[(value >> (i * 2)) & 3];
            log.debug("write " + address + " gpu update background palette");
        }
        else if(address == 0xff48) { // write only
            //for(int i = 0; i < 4; i++) spritePalette[0][i] = palette[(value >> (i * 2)) & 3];
            log.debug("write " + address + " gpu update sprite palette 0");
        }
        else if(address == 0xff49) { // write only
            //for(int i = 0; i < 4; i++) spritePalette[1][i] = palette[(value >> (i * 2)) & 3];
            log.debug("write " + address + " gpu update sprite palette 1");
        }
        else if(address >= 0xff00 && address <= 0xff7f) {
            io[address - 0xff00] = value;
        }
        else if(address == 0xff0f) {
            interruptFlags = value;
            log.debug("write " + address + ": interrupt flags");
        }
        else if(address == 0xffff) {
            interruptEnable = value;
            log.debug("write " + address + ": interrupt enable");
        }
    }

    public int readWord(final int address) throws IndexOutOfBoundsException {
        if (isValidMemoryAddress(address)) {
            int value = readByte(address + 1);
            value <<= 8;
            value += readByte(address);
            return value;
        }
        else { throw new IndexOutOfBoundsException(); }
    }
    public void writeWord(final int address, int value) {
        if (isValidMemoryAddress(address)) {
            int lowerValue = value & 0b00000000_11111111;
            int upperValue = (value & 0b11111111_00000000) >> 8;
            writeByte(address, lowerValue);
            writeByte(address + 1, upperValue);
        }
        else { throw new IndexOutOfBoundsException(); }
    }

    // utility methods
    public static int[] hexStringToByteArray(final String s) {
        int len = s.length();
        int[] data = new int[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16);
        }
        return data;
    }
    private boolean isValidMemoryAddress(final int address) {
        if (address < 0 || address > memorySize) {
            log.error("Address " + address + " is not in memory range (" + memorySize + ").");
            return false;
        }
        return true;
    }

    // DO NOT USE EXCEPT BY TEST
    public int[] getRawRam() {
        return this.ram;
    }


    /*public void dumpVram() {
        for(int x=0, y=0, tile=0, tileRow=0; x < 8192; x++) {

            if (y % 8 == 0) {
                y = 0;
            }

            if (x != 0 && x % 2 == 0) {
                y++;
            }

            if (x % 128 == 0 && x != 0) {
                tileRow++;
                tile = 0;
                y = 0;
            }
            else if (x % 16 == 0 && x != 0) {
                tile++;
                y = 0;
            }

            int j = tileRow * 8 + y;
            int i = tile * 8 + ( (x%2) * 4);

            int color = 0;
            for (int pixel = 3, pOffset = 0; pixel >= 0; pixel--, pOffset++) {
                color = (vram[x] >> (pixel * 2)) & 0b11;
                Gpu.Colors c = Gpu.Colors.ON;
                switch (color) {
                    case 0: c = Gpu.Colors.OFF; break;
                    case 1: c = Gpu.Colors.DARK; break;
                    case 2: c = Gpu.Colors.LIGHT; break;
                    case 3: c = Gpu.Colors.ON; break;
                }

                gpu.canvas.setRGB(i+pOffset, j, c.getColor().getRGB());
                gpu.frame.repaint();
            }
        }

        List<Integer[][]> tileSet = new ArrayList<>();
        List<Integer[]> tile = new ArrayList<>();
        for (int i = 0; i < 8192; i++) {
            int vramByte = vram[i];
            int[] lowerHalfRow = new int[4];
            int[] upperHalfRow = new int[4];

            lowerHalfRow[0] = vramByte & 0b1100_0000;
            lowerHalfRow[1] = vramByte & 0b0011_0000;
            lowerHalfRow[2] = vramByte & 0b0000_1100;
            lowerHalfRow[3] = vramByte & 0b0000_0011;

            i++;
            vramByte = vram[i];

            upperHalfRow[0] = vramByte & 0b1100_0000;
            upperHalfRow[1] = vramByte & 0b0011_0000;
            upperHalfRow[2] = vramByte & 0b0000_1100;
            upperHalfRow[3] = vramByte & 0b0000_0011;

            Integer[] fullRow = new Integer[8];
            for(int j = 0; j < fullRow.length; j++) {
                if(j < 4) {
                    fullRow[j] = lowerHalfRow[j];
                }
                else {
                    fullRow[j] = upperHalfRow[j-4];
                }
            }
            tile.add(fullRow);

            Integer[][] fullTile = new Integer[8][8];
            if(tile.size() == 8) {
                tile.toArray(fullTile);
                tileSet.add(fullTile);

                tile = new ArrayList<>();
            }
        }

        int xOffset = 0;
        int yOffset = 0;
        for(int tileCounter = 0; tileCounter < tileSet.size(); tileCounter++) {
            Integer[][] theTile = tileSet.get(tileCounter);

            for(int tileY = 0; tileY < 8; tileY++) {
                for(int tileX = 0; tileX < 8; tileX++) {
                    int colorInt = theTile[tileX][tileY];
                    Gpu.Colors color = Gpu.Colors.ON;
                    switch (colorInt) {
                        case 0: color = Gpu.Colors.OFF; break;
                        case 1: color = Gpu.Colors.DARK; break;
                        case 2: color = Gpu.Colors.LIGHT; break;
                        case 3: color = Gpu.Colors.ON; break;
                    }

                    gpu.canvas.setRGB(tileX + xOffset, tileY + yOffset, color.getColor().getRGB());
                    gpu.frame.repaint();
                }
            }

            xOffset += 8;
            if(xOffset > 128) {
                yOffset += 8;
                xOffset = 0;
            }
        }
    }*/
}
