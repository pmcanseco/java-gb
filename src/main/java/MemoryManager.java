import java.util.Arrays;
import java.util.Timer;

/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManager {
    private final String name = "MMU";
    private Logger log = new Logger(name, Logger.Level.INFO);
    private final static int[] bootrom = {
            0x31, 0xFE, 0xFF, 0xAF, 0x21, 0xFF, 0x9F, 0x32, 0xCB, 0x7C, 0x20, 0xFB, 0x21, 0x26, 0xFF, 0x0E,
            0x11, 0x3E, 0x80, 0x32, 0xE2, 0x0C, 0x3E, 0xF3, 0xE2, 0x32, 0x3E, 0x77, 0x77, 0x3E, 0xFC, 0xE0,
            0x47, 0x11, 0x04, 0x01, 0x21, 0x10, 0x80, 0x1A, 0xCD, 0x95, 0x00, 0xCD, 0x96, 0x00, 0x13, 0x7B,
            0xFE, 0x34, 0x20, 0xF3, 0x11, 0xD8, 0x00, 0x06, 0x08, 0x1A, 0x13, 0x22, 0x23, 0x05, 0x20, 0xF9,
            0x3E, 0x19, 0xEA, 0x10, 0x99, 0x21, 0x2F, 0x99, 0x0E, 0x0C, 0x3D, 0x28, 0x08, 0x32, 0x0D, 0x20,
            0xF9, 0x2E, 0x0F, 0x18, 0xF3, 0x67, 0x3E, 0x64, 0x57, 0xE0, 0x42, 0x3E, 0x91, 0xE0, 0x40, 0x04,
            0x1E, 0x02, 0x0E, 0x0C, 0xF0, 0x44, 0xFE, 0x90, 0x20, 0xFA, 0x0D, 0x20, 0xF7, 0x1D, 0x20, 0xF2,
            0x0E, 0x13, 0x24, 0x7C, 0x1E, 0x83, 0xFE, 0x62, 0x28, 0x06, 0x1E, 0xC1, 0xFE, 0x64, 0x20, 0x06,
            0x7B, 0xE2, 0x0C, 0x3E, 0x87, 0xF2, 0xF0, 0x42, 0x90, 0xE0, 0x42, 0x15, 0x20, 0xD2, 0x05, 0x20,
            0x4F, 0x16, 0x20, 0x18, 0xCB, 0x4F, 0x06, 0x04, 0xC5, 0xCB, 0x11, 0x17, 0xC1, 0xCB, 0x11, 0x17,
            0x05, 0x20, 0xF5, 0x22, 0x23, 0x22, 0x23, 0xC9, 0xCE, 0xED, 0x66, 0x66, 0xCC, 0x0D, 0x00, 0x0B,
            0x03, 0x73, 0x00, 0x83, 0x00, 0x0C, 0x00, 0x0D, 0x00, 0x08, 0x11, 0x1F, 0x88, 0x89, 0x00, 0x0E,
            0xDC, 0xCC, 0x6E, 0xE6, 0xDD, 0xDD, 0xD9, 0x99, 0xBB, 0xBB, 0x67, 0x63, 0x6E, 0x0E, 0xEC, 0xCC,
            0xDD, 0xDC, 0x99, 0x9F, 0xBB, 0xB9, 0x33, 0x3E, 0x3c, 0x42, 0xB9, 0xA5, 0xB9, 0xA5, 0x42, 0x3C,
            0x21, 0x04, 0x01, 0x11, 0xA8, 0x00, 0x1A, 0x13, 0xBE, 0x20, 0xFE, 0x23, 0x7D, 0xFE, 0x34, 0x20,
            0xF5, 0x06, 0x19, 0x78, 0x86, 0x23, 0x05, 0x20, 0xFB, 0x86, 0x20, 0xFE, 0x3E, 0x01, 0xE0, 0x50 };
    public static int[] getBiosLogo() {
        return Arrays.copyOfRange(bootrom, 104, 104+48);
    }
    private static int[] getBios() {
        return bootrom;
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

    private boolean inBootrom = true;

    MemoryManager(Cartridge cart, Gpu gpu) {
        this.cartridge = cart;
        this.gpu = gpu;
    }
    MemoryManager(Cartridge cart) {
        this(cart, new Gpu(Logger.Level.FATAL));
        this.log = new Logger(this.getClass().getName(), Logger.Level.FATAL);
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
            else if (address == 0xff40) {
                return gpu.lcdControl;
            }
            else if(address == 0xff41) {
                return gpu.lcdStatus;
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

            // TIMER ADDRESSES
            else if (address == 0xff04) {
                return TimerService.getInstance().getDivider();
            }
            else if (address == 0xff05) {
                return TimerService.getInstance().getCounter();
            }
            else if (address == 0xff06) {
                return TimerService.getInstance().getModulo();
            }
            else if(address == 0xff07) { // timer control register
                return TimerService.getInstance().getControl();
            }
            // END TIMER ADDRESSES

            else if (address == 0xff0f) { // interrupt flags
                int iflags = InterruptManager.getInstance().getInterruptsRaised();
                log.info("read the interrupt flags address, value = " + iflags);
                return iflags;
            }
            else if (address == 0xffff) { // interrupt enable
                int ie =  InterruptManager.getInstance().getInterruptsEnabled();
                log.info("read the interrupt enable address, value = " + ie);
                return ie;
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
        else if(address >= 0xff00 && address <= 0xff7f) {
            if(address == 0xff40) {
                gpu.lcdControl = value;
            }
            else if(address == 0xff41) {
                // write to gpu status
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
                for(int i = 0; i < 4; i++) gpu.backgroundPalette[i] = gpu.palette[(value >> (i * 2)) & 3];
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
            else if(address == 0xff0f) { // interrupt flags register
                InterruptManager.getInstance().raiseInterrupt(value);
                //log.info("write " + address + "=" + value + ": interrupt flags");
            }

            // TIMER ADDRESSES
            else if(address == 0xff04) { // timer divider register
                TimerService.getInstance().clearDivider();
            }
            else if (address == 0xff05) { // timer counter register
                TimerService.getInstance().setCounter(value);
            }
            else if (address == 0xff06) { // timer modulo register
                TimerService.getInstance().setModulo(value);
            }
            else if(address == 0xff07) { // timer control register
                TimerService.getInstance().setControl(value);
            }
            // END TIMER ADDRESSES

            else {
                io[address - 0xff00] = value;
            }
        }
        else if(address == 0xffff) { // interrupt enable register
            InterruptManager.getInstance().enableInterrupt(value);
            //log.info("write " + address + "=" + value + " : interrupt enable");
        }


        // hooks/intercepts
        if (address == 0xFF01) { // SERIAL
            System.out.print((char) value);
            //if((char) value == '#') System.exit(1);
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
}
