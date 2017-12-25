/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManager {

    public final int memorySize = 0xFFFF;

    private int[] ram = new int[memorySize];

    private Cartridge cartridge;

    public MemoryManager(Cartridge cart) {
        this.cartridge = cart;
    }

    public void zeroize() {
        for (int i = 0; i < memorySize; i++) {
            ram[i] = 0;
        }
    }

    public int rawRead(int address) throws IndexOutOfBoundsException {
        if (address < 0 || address > memorySize) {
            throw new IndexOutOfBoundsException("Address " + address + "is greater than memory size (" + memorySize + ").");
        }

        return ram[address];
    }

    public void rawWrite(int address, int value) throws IndexOutOfBoundsException {
        if (address < 0 || address > memorySize) {
            throw new IndexOutOfBoundsException("Address " + address + "is greater than memory size (" + memorySize + ").");
        }

        ram[address] = value;
    }

    // DO NOT USE EXCEPT BY TEST
    public int[] getRawRam() {
        return this.ram;
    }
}
