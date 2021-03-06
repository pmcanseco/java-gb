import helpers.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;

public class Cartridge {
    private static final int ADDRESS_RAM_SIZE = 0x0149;
    private static final int ADDRESS_TITLE_START = 0x134;
    private static final int ADDRESS_TITLE_END = 0x143;
    private static final int ADDRESS_LOCALE = 0x14A;
    private static final int ADDRESS_ROM_SIZE = 0x148;
    private static final int ADDRESS_CART_TYPE = 0x0147;
    private static final int ADDRESS_LOGO_START = 0x104;
    private static final int ADDRESS_LOGO_END = 0x0133;
    private static final int ADDRESS_HEADER_CHECKSUM_EXPECTED = 0x014D;
    private static final int ADDRESS_HEADER_CHECKSUM_CALCULATED_START = 0x0134;
    private static final int ADDRESS_HEADER_CHECKSUM_CALCULATED_END = 0x014C;

    private Logger log = new Logger("CART", Logger.Level.DEBUG);

    public enum Locale {
        Japanese,
        World,
        Unknown
    }
    private enum RamSize {
        None    ("None",  0),
        Ram2KB  ("2KB",   1),
        Ram8KB  ("8KB",   2),
        Ram32KB ("32KB",  3),
        Ram128KB("128KB", 4),
        Ram64KB ("64KB",  5);

        //<editor-fold desc=" Implementation " default-state="collapsed">
        public final String displayName;
        public final int id;
        RamSize(String displayName, int id) {
            this.displayName = displayName;
            this.id = id;
        }
        static RamSize getById(int id) {
            for (RamSize r : RamSize.values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
        //</editor-fold>
    }
    private enum RomSize {
        Rom32KB (0x00, 2),
        Rom64KB (0x01, 4),
        Rom128KB(0x02, 8),
        Rom256KB(0x03, 16),
        Rom512KB(0x04, 32),
        Rom1MB  (0x05, 64),
        Rom2MB  (0x06, 128),
        Rom4MB  (0x07, 256),
        Rom8MB  (0x08, 512);

        //<editor-fold desc=" Implementation " default-state="collapsed">
        public final int id;
        public final int numBanks;
        RomSize(int id, int numBanks) {
            this.id = id;
            this.numBanks = numBanks;
        }
        static RomSize getById(int id) {
            for (RomSize r : RomSize.values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
        //</editor-fold>
    }

    private int[] rom; // the entire contents
    private String title; // the game title
    private Locale locale; // japanese or non-japanese?
    private RamSize ramSize; // size of ram
    private RomSize romSize; // size of rom
    private MbcManager.CartridgeType cartridgeType; // presence of additional hardware like MBC, RAM, BAT, etc.
    private int headerChecksum; // see verifyRom()
    private int expectedHeaderChecksum; // see verifyRom()
    private int cartridgeLogoChecksum; // see verifyLogoChecksums()
    private int bootromLogoChecksum; // see verifyLogoChecksums()

    public Cartridge(String path) {
        loadRomFile(path);
    }

    Cartridge(String path, boolean printCartInfo) {
        this(path);
        if (printCartInfo) {
            log.info(log.noColor + "\nLoaded ROM: " + path + "\n" + this.toString());
        }
    }

    private void loadRomFile(String romPath) {
        try {
            byte[] tempRom = Files.readAllBytes(new File(romPath).toPath());
            rom = new int[tempRom.length];
            for (int i = 0; i < tempRom.length; i++) {
                rom[i] = tempRom[i] & 0xFF;
            }
            setTitle();
            setLocale();
            setRamSize();
            setRomSize();
            setCartridgeType();
            verifyHeaderChecksum();
            verifyLogoChecksums();
        } catch(Exception ex) {
            System.out.println("Exception: " + ex);
            System.out.println("Please enter valid Rom file path: ");
            Scanner sc = new Scanner(System.in);
            loadRomFile(sc.nextLine());
        }
    }

    private void setTitle() {
        // bytes at 0x0134 through 0x0143 contain the title
        StringBuilder sb = new StringBuilder();
        for (int i = ADDRESS_TITLE_START; i <= ADDRESS_TITLE_END; i++) {
            if (this.rom[i] == 0) { // don't process NUL game chars
                continue;
            }

            sb.append((char) this.rom[i]);
        }
        this.title = sb.toString();
    }

    public String getTitle() {
        return this.title;
    }

    private void setLocale() {
        // 0x014A contains the destination code. 0 = Japan, 1 = anywhere else
        int localeValue = rom[ADDRESS_LOCALE];
        if (localeValue == 0x00) {
            locale = Locale.Japanese;
        }
        else if (localeValue == 0x01) {
            locale = Locale.World;
        }
        else {
            locale = Locale.Unknown;
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    private void setRamSize() {
        // 0x0149 is the size of the cart's ram.
        switch (rom[ADDRESS_RAM_SIZE]) {
            case 0: this.ramSize = RamSize.None; break;
            case 1: this.ramSize = RamSize.Ram2KB; break;
            case 2: this.ramSize = RamSize.Ram8KB; break;
            case 3: this.ramSize = RamSize.Ram32KB; break;
            case 4: this.ramSize = RamSize.Ram128KB; break;
            case 5: this.ramSize = RamSize.Ram64KB; break;
            default: this.ramSize = RamSize.None;
        }
    }

    private void setRomSize() {
        this.romSize = RomSize.getById(rom[ADDRESS_ROM_SIZE]);
    }

    private void setCartridgeType() {
        this.cartridgeType = MbcManager.cartridgeTypes.get(rom[ADDRESS_CART_TYPE]);
    }

    private void verifyHeaderChecksum() {
        // 0x014D is the header checksum.
        // Contains an 8 bit headerChecksum across the cartridge header bytes 0134-014C.
        // Formula: x=0:FOR i=0134h TO 014Ch:x=x-MEM[i]-1:NEXT
        // The lower 8 bits of the result must be the same than the value in this entry.
        // The GAME WON'T WORK if this headerChecksum is incorrect.
        expectedHeaderChecksum = rom[ADDRESS_HEADER_CHECKSUM_EXPECTED];
        headerChecksum = 0;
        for (int i = ADDRESS_HEADER_CHECKSUM_CALCULATED_START; i <= ADDRESS_HEADER_CHECKSUM_CALCULATED_END; i++) {
            headerChecksum = headerChecksum - rom[i] - 1;
        }
        headerChecksum &= 255; // mask to lower 8 bits
    }

    private void verifyLogoChecksums() {
        //0x0104 through 0x0133 are the Nintendo Logo bitmap bytes.
        //They are verified here by adding up the ones in the bios
        //and comparing them to the ones in the rom.
        bootromLogoChecksum = 0;
        cartridgeLogoChecksum = 0;
        for (int i : MemoryManager.getBiosLogo())
            bootromLogoChecksum += i;

        for (int i = ADDRESS_LOGO_START; i <= ADDRESS_LOGO_END; i++)
            cartridgeLogoChecksum += rom[i];
    }

    public boolean validChecksum() {
        return bootromLogoChecksum == cartridgeLogoChecksum;
    }

    public String toString() {
        return "Title:\t\t" + title + "\n" +
               "Locale:\t\t" + locale.name() + "\n" +
               "ROM Size:\t" + romSize.name().replace("Rom", "") + "\n" +
               "RAM Size:\t" + ramSize.displayName + "\n" +
               "Cart Type:\t" + cartridgeType.name + "\n" +
               "Header Checksums:\t( " + headerChecksum + " == " + expectedHeaderChecksum + " ) is " +
               (headerChecksum == expectedHeaderChecksum) + "\n" +
               "Logo Checksums:\t\t( " + cartridgeLogoChecksum + " == " + bootromLogoChecksum + " ) is " +
               validChecksum();
    }

    public int readFromAddress(int address) {
        return rom[address];
    }

    public final MbcManager.CartridgeType getCartridgeType() {
        return this.cartridgeType;
    }

    public final int getRamSize() {
        switch (ramSize) {
            case None:
                return 0;
            case Ram2KB:
                return 2048;
            case Ram8KB:
                return 8192;
            case Ram32KB:
                return 8192 * 4; // 8KB * 4
            case Ram64KB:
                return 8192 * 8; // 8KB * 8
            case Ram128KB:
                return 8192 * 16; // 8KB * 16
            default:
                return 0;
        }
    }

    public final int getRomSize() {
        switch (romSize) {
            case Rom32KB:
                return 8192 * 4; // 8KB * 4
            case Rom64KB:
                return 8192 * 8; // 8KB * 8
            case Rom128KB:
                return 8192 * 16; // 8KB * 16
            case Rom256KB:
                return 8192 * 32; // 8KB * 32
            case Rom512KB:
                return 8192 * 64; // 8KB * 64
            case Rom1MB:
                return 8192 * 128; // 8KB * 128
            case Rom2MB:
                return 8192 * 256; // 8KB * 256
            case Rom4MB:
                return 8192 * 512; // 8KB * 512
            case Rom8MB:
                return 8192 * 1024; // 8KB * 1024
            default:
                return 0;
        }
    }
}
