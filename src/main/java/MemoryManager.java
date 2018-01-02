/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManager {

    private final static String bios =
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
        return hexStringToByteArray(bios.toLowerCase().substring(336, 432));
    }

    public final int memorySize = 0xFFFF;
    private int[] ram = new int[memorySize];
    private Cartridge cartridge;

    MemoryManager(Cartridge cart) {
        this.cartridge = cart;
    }

    public void zeroize() {
        for (int i = 0; i < memorySize; i++) {
            ram[i] = 0;
        }
    }

    public int rawRead(int address) throws IndexOutOfBoundsException {
        if (isValidMemoryAddress(address)) {
            return ram[address];
        }
        else { throw new IndexOutOfBoundsException(address + " isn't a valid memory address."); }
    }

    public void rawWrite(int address, int value) throws IndexOutOfBoundsException, NumberFormatException {
        if (!isValidMemoryAddress(address)) {
            throw new IndexOutOfBoundsException();
        }

        if (value > 255 || value < 0) {
            throw new NumberFormatException(value + " isn't between 0 and 255 inclusive");
        }

        ram[address] = value;
    }

    public int readWord(int address) throws IndexOutOfBoundsException {
        if (isValidMemoryAddress(address)) {
            int value = rawRead(address + 1);
            value <<= 8;
            value += rawRead(address);
            return value;
        }
        else { throw new IndexOutOfBoundsException(); }
    }

    public void writeWord(int address, int value) {
        if (isValidMemoryAddress(address)) {
            int lowerValue = value & 0b00000000_11111111;
            int upperValue = (value & 0b11111111_00000000) >> 8;
            rawWrite(address, lowerValue);
            rawWrite(address + 1, upperValue);
        }
        else { throw new IndexOutOfBoundsException(); }
    }

    // utility methods
    public static int[] hexStringToByteArray(String s) {
        int len = s.length();
        int[] data = new int[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16);
        }
        return data;
    }

    private boolean isValidMemoryAddress(int address) {
        if (address < 0 || address > memorySize) {
            System.out.println("Address " + address + " is not in memory range (" + memorySize + ").");
            return false;
        }
        return true;
    }

    // DO NOT USE EXCEPT BY TEST
    public int[] getRawRam() {
        return this.ram;
    }
}
