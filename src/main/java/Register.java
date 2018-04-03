/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class Register {
    private Logger log = new Logger(this.getClass().getName());

    // members
    private final String name;
    private final int size;
    private int value;

    // constructors
    public Register(String regName, int regSize, int regVal) {
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
            log.error("Value " + value + " is out of range for " + this.size + "-bit register " + this.name);
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
        if ((this.value == 256 && this.size == 8) || (this.value == 65536 && this.size == 16)) {
            this.value = 0;
        }
    }
    public void dec() {
        this.value -= 1;
        if (this.value < 0) {
            this.value = 0b1111_1111;
        }
    }
    public int read() {
        return this.value;
    }
    public boolean readBit(int index) {
        if (index > this.size - 1 || index < 0) {
            log.error("Bit index " + index + " is out of bounds for " + this.size + "-bit register " + this.name);
            return false;
        }
        int tmp = this.value;
        tmp >>= index;
        tmp &= 1;
        return tmp == 1;
    }
    public void writeBit(int index, boolean value) {
        if (index > this.size - 1 || index < 0) {
            log.error("Bit index " + index + " is out of bounds for " + this.size + "-bit register " + this.name);
            return;
        }

        if (value) {
            this.value |= (1 << index);
        }
        else {
            this.value &= ~(1 << index);
        }
    }

    // 16-bit reads
    public int readHigh() {
        if (this.size == 16) {
            return this.value & 0b11111111_00000000;
        }
        else {
            return this.read();
        }
    }
    public int readLow() {
        if (this.size == 16) {
            return this.value & 0b00000000_11111111;
        }
        else {
            return this.read();
        }
    }

    // 16-bit writes
    public void writeHigh(int value) {
        if (this.size == 16) {
            this.write(value & 0b11111111_00000000);
        }
        else {
            this.write(value);
        }
    }
    public void writeLow(int value) {
        if (this.size == 16) {
            this.write(value & 0b00000000_11111111);
        }
        else {
            this.write(value);
        }
    }

    private String getName() {
        return this.name;
    }
    public final int getSize() {
        return this.size;
    }
    public String toString() {
        return "Register " + getName() + " (" + getSize() + "-bit): " + read();
    }
}

