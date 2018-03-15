import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;

public class Cartridge {

    private enum Locale {
        Japanese,
        World,
        Unknown
    }

    private enum RamSize {
        None("None"),
        TwoKb("2KB"),
        EightKb("8KB"),
        ThirtyTwoKb("32KB"),
        OneTwentyEightKb("128KB"),
        SixtyFourKb("64KB");

        //<editor-fold desc=" Implementation " default-state="collapsed">
        private String displayName;
        RamSize(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
        //</editor-fold>
    }

    private int[] rom; // the entire contents
    private String title; // the game title
    private Locale locale; // japanese or non-japanese?
    private RamSize ramSize; // size of ram
    private int headerChecksum; // see verifyRom()
    private int expectedHeaderChecksum; // see verifyRom()
    private int logoChecksum; // see verifyRom()
    private int expectedLogoChecksum; // see verifyRom()

    public Cartridge(String path) {
        loadRomFile(path);
    }

    Cartridge(String path, boolean print) {
        this(path);
        if (print) {
            System.out.println("Loaded ROM: " + path);
            System.out.println(this.toString());
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
            verifyHeaderChecksum();
            verifyCartAndMemoryLogosMatch();
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
        for (int i=0x134; i<0x143; i++) sb.append((char) this.rom[i]);
        this.title = sb.toString();
    }

    private void setLocale() {
        // 0x014A contains the destination code. 0 = Japan, 1 = not Japan
        if (rom[0x14A] == 0x00)  locale = Locale.Japanese;
        else if (rom[0x14A] == 0x01) locale = Locale.World;
        else locale = Locale.Unknown;
    }

    private void setRamSize() {
        // 0x0149 is the size of the cart's ram.
        switch (rom[0x0149]) {
            case 0: this.ramSize = RamSize.None; break;
            case 1: this.ramSize = RamSize.TwoKb; break;
            case 2: this.ramSize = RamSize.EightKb; break;
            case 3: this.ramSize = RamSize.ThirtyTwoKb; break;
            case 4: this.ramSize = RamSize.OneTwentyEightKb; break;
            case 5: this.ramSize = RamSize.SixtyFourKb; break;
            default: this.ramSize = RamSize.None;
        }
    }

    private void verifyHeaderChecksum() {
        // 0x014D is the header checksum.
        // Contains an 8 bit headerChecksum across the cartridge header bytes 0134-014C.
        // Formula: x=0:FOR i=0134h TO 014Ch:x=x-MEM[i]-1:NEXT
        // The lower 8 bits of the result must be the same than the value in this entry.
        // The GAME WON'T WORK if this headerChecksum is incorrect.
        headerChecksum = rom[0x014D];
        expectedHeaderChecksum = 0;
        for (int i = 0x0134; i <= 0x014C; i++) {
            expectedHeaderChecksum = expectedHeaderChecksum - rom[i] - 1;
        }
        expectedHeaderChecksum &= 255; // mask to lower 8 bits
    }

    private void verifyCartAndMemoryLogosMatch() {
        //0x0104 through 0x0133 are the Nintendo Logo bitmap bytes.
        //They are verified here by adding up the ones in the bios
        //and comparing them to the ones in the rom.
        expectedLogoChecksum = 0;
        logoChecksum = 0;
        for (int i : MemoryManager.getBiosLogo()) expectedLogoChecksum += i;
        for (int i=0x104; i <= 0x0133; i++) logoChecksum += rom[i];
    }

    public String toString() {
        return "Title:\t\t" + title + "\n" +
               "Locale:\t\t" + locale.name() + "\n" +
               "RAM Size:\t" + ramSize.getDisplayName() + "\n" +
               "Header Checksum:\t( " + headerChecksum + " == " + expectedHeaderChecksum + " ) is " +
               (headerChecksum == expectedHeaderChecksum) + "\n" +
               "Logo Match Check:\t( " + logoChecksum + " == " + expectedLogoChecksum + " ) is " +
               (logoChecksum == expectedLogoChecksum);
    }

    public int readFromAddress(int address) {
        return rom[address];
    }
}
