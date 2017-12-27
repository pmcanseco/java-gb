import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

/**
 * Created by Pablo Canseco on 12/22/2017.
 * An Object-Oriented Approach to a Gameboy Z80 Processor Emulator
 */
public class Z80 {

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

    private MemoryManager mmu;

    Z80(MemoryManager memMgr) {
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

        this.mmu = memMgr;
    }

    // utility functions
    private Register search(final String name) {
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
    public int getRegisterValue(final String name) {
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
    public void setRegisterValue(final String name, int value) {
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
    public boolean getRegisterBit(final String name, int index) {
        Register r = search(name);
        return r != null && r.readBit(index);
    }
    public int readCombinedRegisters(final String upper, final String lower) throws InvalidPropertiesFormatException {
        Register u = search(upper);
        Register l = search(lower);
        if(u != null && l != null) {
            return readCombinedRegisters(u, l);
        }
        else {
            System.out.println("Did not find one of registers " + upper + ", " + lower);
            return -1;
        }
    }
    public void writeCombinedRegisters(final String upper, final String lower, int value) throws InvalidPropertiesFormatException {
        Register u = search(upper);
        Register l = search(lower);
        if(u != null && l != null) {
            writeCombinedRegisters(u, l, value);
        }
        else {
            System.out.println("Did not find one of registers " + upper + ", " + lower);
        }
    }

    private int readCombinedRegisters(Register upper, Register lower) throws InvalidPropertiesFormatException {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            throw new InvalidPropertiesFormatException("one of the registers to combine wasn't an 8-bit register");
        }
        int result = upper.read();
        result <<= 8;
        result |= lower.read();
        return result;
    }
    private void writeCombinedRegisters(Register upper, Register lower, final int value) throws InvalidPropertiesFormatException {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            throw new InvalidPropertiesFormatException("one of the registers to combine wasn't an 8-bit register");
        }
        int upper8bits = value / 256; // shift right  by 8 bits;
        int lower8bits = value & 0b0000000011111111; // mask out the upper 8 bits.
        upper.write(upper8bits);
        lower.write(lower8bits);
    }

    private void load(Register destinationRegister, Register sourceRegister) {
        destinationRegister.write(sourceRegister.read());
    }
    private void load(Register destinationRegister, int number) {
        destinationRegister.write(number);
    }
    private void load(int opcode) throws InvalidPropertiesFormatException {
        // temp variables to temporarily hold stuff
        int address;
        int temp;
        int lowerValue;
        int upperValue;
        switch(opcode) {
            //<editor-fold desc="3.3.1.1 8-Bit Loads - LD nn, n" defaultstate="collapsed">
            /*
             * 3.3.1. 8-Bit Loads
             *   1. LD nn,n
             *   Description:
             *      Put value nn into n.
             *   Use with:
             *      nn = B,C,D,E,H,L,BC,DE,HL,SP
             *      n = 8 bit immediate value
             *   Opcodes:
             *      Instruction Parameters Opcode Cycles
             *      LD             B,n     06      8
             *      LD             C,n     0E      8
             *      LD             D,n     16      8
             *      LD             E,n     1E      8
             *      LD             H,n     26      8
             *      LD             L,n     2E      8
             */
            case 0x06: load(registerB, mmu.rawRead(registerPC.read())); registerPC.inc(); break;
            case 0x0E: load(registerC, mmu.rawRead(registerPC.read())); registerPC.inc(); break;
            case 0x16: load(registerD, mmu.rawRead(registerPC.read())); registerPC.inc(); break;
            case 0x1E: load(registerE, mmu.rawRead(registerPC.read())); registerPC.inc(); break;
            case 0x26: load(registerH, mmu.rawRead(registerPC.read())); registerPC.inc(); break;
            case 0x2E: load(registerL, mmu.rawRead(registerPC.read())); registerPC.inc(); break;
            //</editor-fold>
            //<editor-fold desc="3.3.1.2 8-Bit Loads - LD r1,r2" defaultstate="collapsed">
            /*
             *   2. LD r1,r2
             *   Description:
             *      Put value r2 into r1.
             *   Use with:
             *      r1,r2 = A,B,C,D,E,H,L,(HL)
             */
            case 0x78: load(registerA, registerB); break;
            case 0x7F: load(registerA, registerA); break;
            case 0x79: load(registerA, registerC); break;
            case 0x7A: load(registerA, registerD); break;
            case 0x7B: load(registerA, registerE); break;
            case 0x7C: load(registerA, registerH); break;
            case 0x7D: load(registerA, registerL); break;
            case 0x7E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.rawRead(address));
                break;
            case 0x40: load(registerB, registerB); break;
            case 0x41: load(registerB, registerC); break;
            case 0x42: load(registerB, registerD); break;
            case 0x43: load(registerB, registerE); break;
            case 0x44: load(registerB, registerH); break;
            case 0x45: load(registerB, registerL); break;
            case 0x46:
                address = readCombinedRegisters(registerH, registerL);
                load(registerB, mmu.rawRead(address));
                break;
            case 0x48: load(registerC, registerB); break;
            case 0x49: load(registerC, registerC); break;
            case 0x4A: load(registerC, registerD); break;
            case 0x4B: load(registerC, registerE); break;
            case 0x4C: load(registerC, registerH); break;
            case 0x4D: load(registerC, registerL); break;
            case 0x4E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerC, mmu.rawRead(address));
                break;
            case 0x50: load(registerD, registerB); break;
            case 0x51: load(registerD, registerC); break;
            case 0x52: load(registerD, registerD); break;
            case 0x53: load(registerD, registerE); break;
            case 0x54: load(registerD, registerH); break;
            case 0x55: load(registerD, registerL); break;
            case 0x56:
                address = readCombinedRegisters(registerH, registerL);
                load(registerD, mmu.rawRead(address));
                break;
            case 0x58: load(registerE, registerB); break;
            case 0x59: load(registerE, registerC); break;
            case 0x5A: load(registerE, registerD); break;
            case 0x5B: load(registerE, registerE); break;
            case 0x5C: load(registerE, registerH); break;
            case 0x5D: load(registerE, registerL); break;
            case 0x5E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerE, mmu.rawRead(address));
                break;
            case 0x60: load(registerH, registerB); break;
            case 0x61: load(registerH, registerC); break;
            case 0x62: load(registerH, registerD); break;
            case 0x63: load(registerH, registerE); break;
            case 0x64: load(registerH, registerH); break;
            case 0x65: load(registerH, registerL); break;
            case 0x66:
                address = readCombinedRegisters(registerH, registerL);
                load(registerH, mmu.rawRead(address));
                break;
            case 0x68: load(registerL, registerB); break;
            case 0x69: load(registerL, registerC); break;
            case 0x6A: load(registerL, registerD); break;
            case 0x6B: load(registerL, registerE); break;
            case 0x6C: load(registerL, registerH); break;
            case 0x6D: load(registerL, registerL); break;
            case 0x6E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerL, mmu.rawRead(address));
                break;
            case 0x70:
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, registerB.read());
                break;
            case 0x71:
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, registerC.read());
                break;
            case 0x72:
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, registerD.read());
                break;
            case 0x73:
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, registerE.read());
                break;
            case 0x74:
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, registerH.read());
                break;
            case 0x75:
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, registerL.read());
                break;
            case 0x36: // 12 cycles
                address = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(address, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            //</editor-fold>
            //<editor-fold desc="3.3.1.3 8-bit Loads - LD A, n" defaultstate="collapsed">
            /*
             *  3. LD A,n
             *  Description:
             *     Put value n into A.
             *  Use with:
             *     n = A,B,C,D,E,H,L,(BC),(DE),(HL),(nn),#
             *     nn = two byte immediate value. (LS byte first.)
             */
            case 0x0A: load(registerA, mmu.rawRead(readCombinedRegisters(registerB, registerC))); break;  // LD A,(BC) 0A 8
            case 0x1A: load(registerA, mmu.rawRead(readCombinedRegisters(registerD, registerE))); break;  // LD A,(DE) 1A 8
            case 0xFA: // 16 cycles
                load(registerA, mmu.rawRead(mmu.readWord(registerPC.read())));
                registerPC.inc(); registerPC.inc(); break;
            case 0x3E: load(registerA, mmu.rawRead(registerPC.read())); registerPC.inc(); break;  // LD A,# 3E 8
            //</editor-fold>
            //<editor-fold desc="3.3.1.4 8-bit Loads - LD n, A" defaultstate="collapsed">
            /*
             *  4. LD n,A
             *  Description:
             *     Put value A into n.
             *  Use with:
             *     n = A,B,C,D,E,H,L,(BC),(DE),(HL),(nn)
             *     nn = two byte immediate value. (LS byte first.)
             */
            case 0x47: load(registerB, registerA); break; // LD B,A 47 4
            case 0x4F: load(registerC, registerA); break; // LD C,A 4F 4
            case 0x57: load(registerD, registerA); break; // LD D,A 57 4
            case 0x5F: load(registerE, registerA); break; // LD E,A 5F 4
            case 0x67: load(registerH, registerA); break; // LD H,A 67 4
            case 0x6F: load(registerL, registerA); break; // LD L,A 6F 4
            case 0x02: mmu.rawWrite(mmu.rawRead(readCombinedRegisters(registerB, registerC)), registerA.read()); break; // LD (BC),A 02 8
            case 0x12: mmu.rawWrite(mmu.rawRead(readCombinedRegisters(registerD, registerE)), registerA.read()); break; // LD (DE),A 12 8
            case 0x77: mmu.rawWrite(mmu.rawRead(readCombinedRegisters(registerH, registerL)), registerA.read()); break; // LD (HL),A 77 8
            case 0xEA: // 16 cycles
                mmu.rawWrite(mmu.readWord(registerPC.read()), registerA.read());
                registerPC.inc(); registerPC.inc(); break; // LD (nn),A EA 16
            //</editor-fold>
            case 0xF2:
                // Put value at address $FF00 + register C into A , takes 8 cycles
                load(registerA, mmu.rawRead(registerC.read() + 0xFF00));
                break;
            case 0xE2:
                // Put A into address $FF00 + register C , takes 8 cycles
                mmu.rawWrite(0xFF00 + registerC.read(), registerA.read());
                break;
            case 0x3A:
                // Put value at address HL into A, Decrement HL. Takes 8 cycles
                temp = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.rawRead(temp));
                writeCombinedRegisters(registerH, registerL, temp - 1);
                break;
            case 0x32:
                // put A into memory address HL. Decrement HL. Takes 8 cycles.
                temp = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(temp, registerA.read());
                writeCombinedRegisters(registerH, registerL, temp - 1);
                break;
            case 0x2A:
                // Put value at address HL into A, Increment HL. Takes 8 cycles
                temp = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.rawRead(temp));
                writeCombinedRegisters(registerH, registerL, temp + 1);
                break;
            case 0x22:
                // put A into memory address HL. Increment HL. Takes 8 cycles.
                temp = readCombinedRegisters(registerH, registerL);
                mmu.rawWrite(temp, registerA.read());
                writeCombinedRegisters(registerH, registerL, temp + 1);
                break;
            case 0xE0:
                // Put A into memory address $FF00+n . 12 cycles
                mmu.rawWrite(0xFF00 + registerPC.read(), registerA.read());
                break;
            case 0xF0:
                // Put memory address $FF00+n into A. 12 cycles
                load(registerA, mmu.rawRead(0xFF00 + registerPC.read()));
                break;
            case 0x01:
                // LD BC,nn 01 12
                load(registerC, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                load(registerB, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x11:
                // LD DE,nn 11 12
            case 0x21:
                // LD HL,nn 21 12
            case 0x31:
                // LD SP,nn 31 12
        }
    }

}
