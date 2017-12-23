import java.util.InvalidPropertiesFormatException;

/**
 * Created by Pablo Canseco on 12/22/2017.
 * An Object-Oriented Approach to a Gameboy Z80 Processor Emulator
 */
public class Z80 {

    private class Register {

        // members
        private final String name;
        private final int size;
        private int value;

        // constructors
        private Register(String regName, int regSize, int regVal) {
            this.name = regName;
            this.value = regVal;
            this.size = regSize;
        }

        // methods
        public void write(int value) {
            this.value = value;
        }
        public void and(int value) {
            this.value &= value;
        }
        public void or(int value) {
            this.value |= value;
        }
        public int read() {
            return this.value;
        }
        public boolean readBit(int index) {
            int tmp = this.value;
            tmp >>= index;
            tmp &= 1;
            return tmp == 1;
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

    // 8-bit registers, constructed in the LegacyZ80 constructor.
    private Register registerA;
    private Register registerB;
    private Register registerC;
    private Register registerD;
    private Register registerE;
    private Register registerH;
    private Register registerL;

    // _________________________________
    // | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
    // | Z | N | H | C |   |   |   |   |
    // |___|___|___|___|___|___|___|___|
    // Z = Zero: set if last operation resulted in zero
    // N = Operation: set if last operation was subtraction
    // H = Half-carry: set if last operation's result's lower half overflowed past 15
    // C = Carry: set if last operation produced result greater than 255 for adds or less than 0 for subtracts
    // * NOTE * The lower 4 bits always read zero even if 1 is written to them.
    private Register registerFlags;

    // 16-bit registers, also constructed in the LegacyZ80 constructor.
    private Register registerPC; // program counter
    private Register registerSP; // stack pointer
    private Register registerI;
    private Register registerR;
    private Register registerM;  // m-time for last instruction
    private Register registerT;  // t-time for last instruction
    private Register registerIME;

    public Z80() {
        // initialize 8-bit registers
        registerA = new Register("A", 8, 0);
        registerB = new Register("B", 8, 0);
        registerC = new Register("C", 8, 0);
        registerD = new Register("D", 8, 0);
        registerE = new Register("E", 8, 0);
        registerH = new Register("H", 8, 0);
        registerL = new Register("L", 8, 0);
        registerFlags = new Register("Flags", 8, 0);

        // initialize 16-bit registers
        registerPC  = new Register("PC",  16, 0);
        registerSP  = new Register("SP",  16, 0);
        registerI   = new Register("I",   16, 0);
        registerR   = new Register("R",   16, 0);
        registerM   = new Register("M",   16, 0);
        registerT   = new Register("T",   16, 0);
        registerIME = new Register("IME", 16, 0);
    }

    private int readCombined8bitRegisters(Register upper, Register lower) throws InvalidPropertiesFormatException {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            throw new InvalidPropertiesFormatException("one of the registers to combine wasn't an 8-bit register");
        }
        int result = upper.read();
        result <<= 8;
        result |= lower.read();
        return result;
    }

    private void writeCombined8bitRegisters(Register upper, Register lower, int value) throws InvalidPropertiesFormatException {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            throw new InvalidPropertiesFormatException("one of the registers to combine wasn't an 8-bit register");
        }
        int upper8bits = value >>= 8; // shift right  by 8 bits;
        int lower8bits = value & 0b0000000011111111; // mask out the upper 8 bits.
        upper.write(upper8bits);
        lower.write(lower8bits);
    }

    private void load(Register sourceRegister, Register destinationRegister) {
        destinationRegister.write(sourceRegister.read());
    }
    private void load(short number, Register destinationRegister) {
        destinationRegister.write(number);
    }

}
