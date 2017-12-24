import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

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
        public int read() {
            return this.value;
        }
        public boolean readBit(int index) {
            if (index > this.size - 1) {
                System.out.println("Bit index " + index + " is out of bounds for " + this.size + "-bit register " + this.name);
                return false;
            }
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

    private Map<String, Register> eightBitRegisters = new HashMap<>();
    private Map<String, Register> sixteenBitRegisters = new HashMap<>();

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
        eightBitRegisters.put("A", registerA);
        eightBitRegisters.put("B", registerB);
        eightBitRegisters.put("C", registerC);
        eightBitRegisters.put("D", registerD);
        eightBitRegisters.put("E", registerE);
        eightBitRegisters.put("H", registerH);
        eightBitRegisters.put("L", registerL);
        eightBitRegisters.put("Flags", registerFlags);

        // initialize 16-bit registers
        registerPC  = new Register("PC",  16, 0);
        registerSP  = new Register("SP",  16, 0);
        registerI   = new Register("I",   16, 0);
        registerR   = new Register("R",   16, 0);
        registerM   = new Register("M",   16, 0);
        registerT   = new Register("T",   16, 0);
        registerIME = new Register("IME", 16, 0);
        sixteenBitRegisters.put("PC", registerPC);
        sixteenBitRegisters.put("SP", registerSP);
        sixteenBitRegisters.put("I",  registerI);
        sixteenBitRegisters.put("R",  registerR);
        sixteenBitRegisters.put("M",  registerM);
        sixteenBitRegisters.put("T",  registerT);
        sixteenBitRegisters.put("IME", registerIME);
    }

    // utility functions
    private Register search(String name) {
        if (eightBitRegisters.get(name) != null) {
            return eightBitRegisters.get(name);
        }
        else if (sixteenBitRegisters.get(name) != null) {
            return sixteenBitRegisters.get(name);
        }
        else {
            // unknown register?
            System.out.println("Unknown register");
            return null;
        }
    }
    public int getRegisterValue(String name) {
        if (eightBitRegisters.get(name) != null) {
            return eightBitRegisters.get(name).read();
        }
        else if (sixteenBitRegisters.get(name) != null) {
            return sixteenBitRegisters.get(name).read();
        }
        else {
            // unknown register?
            System.out.println("Unknown register");
            return -1;
        }
    }
    public void setRegisterValue(String name, int value) {
        if (eightBitRegisters.get(name) != null) {
            eightBitRegisters.get(name).write(value);
        }
        else if (sixteenBitRegisters.get(name) != null) {
            sixteenBitRegisters.get(name).write(value);
        }
        else {
            // unknown register?
            System.out.println("Unknown register");
        }
    }
    public boolean getRegisterBit(String name, int index) {
        Register r = search(name);
        return r != null && r.readBit(index);
    }
    public int readCombined8bitRegisters(String upper, String lower) throws InvalidPropertiesFormatException {
        Register u = search(upper);
        Register l = search(lower);
        if(u != null && l != null) {
            return readCombined8bitRegisters(u, l);
        }
        else {
            System.out.println("Did not find one of registers " + upper + ", " + lower);
            return -1;
        }
    }
    public void writeCombined8bitRegisters(String upper, String lower, int value) throws InvalidPropertiesFormatException {
        Register u = search(upper);
        Register l = search(lower);
        if(u != null && l != null) {
            writeCombined8bitRegisters(u, l, value);
        }
        else {
            System.out.println("Did not find one of registers " + upper + ", " + lower);
        }
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
    private void writeCombined8bitRegisters(Register upper, Register lower, final int value) throws InvalidPropertiesFormatException {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            throw new InvalidPropertiesFormatException("one of the registers to combine wasn't an 8-bit register");
        }
        int upper8bits = value / 256; // shift right  by 8 bits;
        int lower8bits = value & 0b0000000011111111; // mask out the upper 8 bits.
        upper.write(upper8bits);
        lower.write(lower8bits);
    }
    private int readRegister(Register r) {
        return r.read();
    }
    private void load(Register sourceRegister, Register destinationRegister) {
        destinationRegister.write(sourceRegister.read());
    }
    private void load(short number, Register destinationRegister) {
        destinationRegister.write(number);
    }

}
