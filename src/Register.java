import java.util.BitSet;

/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class Register {

    // members
    private final String name;
    private final int size;
    private int value;

    // constructors
    Register(String regName, int regSize, int regVal) {
        this.name = regName;
        this.value = regVal;
        this.size = regSize;
    }

    // methods
    public void write(int value) {
        if( value >= 0 && (
            ( this.size == 16 && value <= 65535 ) ||
            ( this.size == 8  && value <= 255) )
          )
            this.value = value;
        else {
            System.out.println("Value " + value + " is out of range for " + this.size + "-bit register " + this.name);
        }
    }
    public void and(int value) {
        this.value &= value;
    }
    public void or(int value) {
        this.value |= value;
    }
    public void inc() {
        this.value += 1;
    }
    public int read() {
        return this.value;
    }
    public boolean readBit(int index) {
        if (index > this.size - 1 || index < 0) {
            System.out.println("Bit index " + index + " is out of bounds for " + this.size + "-bit register " + this.name);
            return false;
        }
        int tmp = this.value;
        tmp >>= index;
        tmp &= 1;
        return tmp == 1;
    }
    public void writeBit(int index, boolean value) {
        if (index > this.size - 1 || index < 0) {
            System.out.println("Bit index " + index + " is out of bounds for " + this.size + "-bit register " + this.name);
            return;
        }
        BitSet bs = new BitSet(this.read());
        if (value) {
            bs.set(index);
        }
        else {
            bs.clear(index);
        }
        this.write((int) bs.toLongArray()[0]);
    }
    public final String getName() {
        return this.name;
    }
    public final int getSize() {
        return this.size;
    }
    public String toString() {
        return "Register " + this.name + " (" + this.size + "-bit): " + this.value;
    }
}

