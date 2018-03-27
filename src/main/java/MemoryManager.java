/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManager extends Logger {

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
    public final int memorySize = 0xFFFF;

    private int[] ram = new int[memorySize];

    private int[] sram = new int[0x2000]; // 8192
    private int[] io   = new int[0x100];  // 256
    private int[] vram = new int[0x2000]; // 8192
    private int[] oam  = new int[0x100];  // 256
    private int[] wram = new int[0x2000]; // 8192
    private int[] hram = new int[0x80];   // 128

    private boolean inBootrom = true;

    MemoryManager(Cartridge cart) {
        this.cartridge = cart;
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
                    }
                }

                return cartridge.readFromAddress(address);
            }
            else if (address >= 0x8000 && address <= 0x9fff) {
                return vram[address - 0x8000];
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
                //return gpu.control;
                logDebug("read " + address + ": Read gpu.control");
            }
            else if (address == 0xff42) {
                //return gpu.scrollY;
                logDebug("read " + address + ": Read gpu.scrollY");
            }
            else if (address == 0xff43) {
                //return gpu.scrollX;
                logDebug("read " + address + ": Read gpu.scrollX");
            }
            else if (address == 0xff44) {
                //return gpu.scanline; // read only
                logDebug("read " + address + ": Read gpu.scanline");
            }
            else if (address == 0xff00) {
                if((io[0x00] & 0x20) == 0) {
                    //return (unsigned char)(0xc0 | keys.keys1 | 0x10);
                    logDebug("Unimplemented RAM address " + address + ". Deals with input.");
                }

                else if((io[0x00] & 0x10) == 0) {
                    //return (unsigned char)(0xc0 | keys.keys2 | 0x20);
                    logDebug("Unimplemented RAM address " + address + ". Deals with input.");
                }

                else if((io[0x00] & 0x30) == 0) {
                    return 0xff;
                }
                else {
                    return 0;
                }
            }
            else if (address == 0xff0f) {
                //return interrupt.flags;
                logDebug("Read interrupt.flags");
            }
            else if (address == 0xffff) {
                //return interrupt.enable;
                logDebug("Read interrupt.enable");
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
            vram[address - 0x8000] = value;
            if(address <= 0x97ff) {
                //updateTile(address, value);
                logDebug("write " + address + " updateTile(address,value");
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
            logDebug("write " + address + "gpu.control = value;");
        }
        else if(address == 0xff42) {
            logDebug("write " + address + "gpu.scrollY = value;");
        }
        else if(address == 0xff43) {
            logDebug("write " + address + "gpu.scrollX = value;");
        }
        else if(address == 0xff46) {
            logDebug("write " + address + "copy(0xfe00, value << 8, 160); // OAM DMA");
        }
        else if(address == 0xff47) { // write only
            //for(int i = 0; i < 4; i++) backgroundPalette[i] = palette[(value >> (i * 2)) & 3];
            logDebug("write " + address + "gpu update background palette");
        }
        else if(address == 0xff48) { // write only
            //for(int i = 0; i < 4; i++) spritePalette[0][i] = palette[(value >> (i * 2)) & 3];
            logDebug("write " + address + "gpu update sprite palette 0");
        }
        else if(address == 0xff49) { // write only
            //for(int i = 0; i < 4; i++) spritePalette[1][i] = palette[(value >> (i * 2)) & 3];
            logDebug("write " + address + "gpu update sprite palette 1");
        }
        else if(address >= 0xff00 && address <= 0xff7f) {
            io[address - 0xff00] = value;
        }
        else if(address == 0xff0f) {
            //interrupt.flags = value;
            logDebug("write " + address + ": interrupt flags");
        }
        else if(address == 0xffff) {
            //interrupt.enable = value;
            logDebug("write " + address + ": interrupt enable");
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
            logError("Address " + address + " is not in memory range (" + memorySize + ").");
            return false;
        }
        return true;
    }

    // DO NOT USE EXCEPT BY TEST
    public int[] getRawRam() {
        return this.ram;
    }
}
