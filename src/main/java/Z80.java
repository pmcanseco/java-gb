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
    private FlagsRegister registerFlags;

    // 16-bit registers, also constructed in the LegacyZ80 constructor.
    private Register registerPC; // program counter
    private Register registerSP; // stack pointer
    //private Register registerI;
    //private Register registerR;
    //private Register registerM;  // m-time for last instruction
    //private Register registerT;  // t-time for last instruction
    //private Register registerIME;

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
        registerFlags = new FlagsRegister("Flags", 8, 0);
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
        //registerI   = new Register("I",   16, 0);
        //registerR   = new Register("R",   16, 0);
        //registerM   = new Register("M",   16, 0);
        //registerT   = new Register("T",   16, 0);
        //registerIME = new Register("IME", 16, 0);
        sixteenBitRegisters.put("PC", registerPC);
        sixteenBitRegisters.put("SP", registerSP);
        //sixteenBitRegisters.put("I",  registerI);
        //sixteenBitRegisters.put("R",  registerR);
        //sixteenBitRegisters.put("M",  registerM);
        //sixteenBitRegisters.put("T",  registerT);
        //sixteenBitRegisters.put("IME", registerIME);

        this.mmu = memMgr;
    }

    // utility functions
    public Register search(final String name) {
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
    public void load(Register destinationRegister, int number) {
        destinationRegister.write(number);
    }
    public void load(int opcode) throws InvalidPropertiesFormatException {
        // temp variables to temporarily hold stuff
        int address;
        int temp;
        int result;
        int lowerValue;
        int upperValue;
        switch (opcode) {
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
            case 0x06:
                load(registerB, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x0E:
                load(registerC, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x16:
                load(registerD, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x1E:
                load(registerE, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x26:
                load(registerH, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x2E:
                load(registerL, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            //</editor-fold>
            //<editor-fold desc="3.3.1.2 8-Bit Loads - LD r1,r2" defaultstate="collapsed">
            /*
             *   2. LD r1,r2
             *   Description:
             *      Put value r2 into r1.
             *   Use with:
             *      r1,r2 = A,B,C,D,E,H,L,(HL)
             */
            case 0x78:
                load(registerA, registerB);
                break;
            case 0x7F:
                load(registerA, registerA);
                break;
            case 0x79:
                load(registerA, registerC);
                break;
            case 0x7A:
                load(registerA, registerD);
                break;
            case 0x7B:
                load(registerA, registerE);
                break;
            case 0x7C:
                load(registerA, registerH);
                break;
            case 0x7D:
                load(registerA, registerL);
                break;
            case 0x7E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.rawRead(address));
                break;
            case 0x40:
                load(registerB, registerB);
                break;
            case 0x41:
                load(registerB, registerC);
                break;
            case 0x42:
                load(registerB, registerD);
                break;
            case 0x43:
                load(registerB, registerE);
                break;
            case 0x44:
                load(registerB, registerH);
                break;
            case 0x45:
                load(registerB, registerL);
                break;
            case 0x46:
                address = readCombinedRegisters(registerH, registerL);
                load(registerB, mmu.rawRead(address));
                break;
            case 0x48:
                load(registerC, registerB);
                break;
            case 0x49:
                load(registerC, registerC);
                break;
            case 0x4A:
                load(registerC, registerD);
                break;
            case 0x4B:
                load(registerC, registerE);
                break;
            case 0x4C:
                load(registerC, registerH);
                break;
            case 0x4D:
                load(registerC, registerL);
                break;
            case 0x4E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerC, mmu.rawRead(address));
                break;
            case 0x50:
                load(registerD, registerB);
                break;
            case 0x51:
                load(registerD, registerC);
                break;
            case 0x52:
                load(registerD, registerD);
                break;
            case 0x53:
                load(registerD, registerE);
                break;
            case 0x54:
                load(registerD, registerH);
                break;
            case 0x55:
                load(registerD, registerL);
                break;
            case 0x56:
                address = readCombinedRegisters(registerH, registerL);
                load(registerD, mmu.rawRead(address));
                break;
            case 0x58:
                load(registerE, registerB);
                break;
            case 0x59:
                load(registerE, registerC);
                break;
            case 0x5A:
                load(registerE, registerD);
                break;
            case 0x5B:
                load(registerE, registerE);
                break;
            case 0x5C:
                load(registerE, registerH);
                break;
            case 0x5D:
                load(registerE, registerL);
                break;
            case 0x5E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerE, mmu.rawRead(address));
                break;
            case 0x60:
                load(registerH, registerB);
                break;
            case 0x61:
                load(registerH, registerC);
                break;
            case 0x62:
                load(registerH, registerD);
                break;
            case 0x63:
                load(registerH, registerE);
                break;
            case 0x64:
                load(registerH, registerH);
                break;
            case 0x65:
                load(registerH, registerL);
                break;
            case 0x66:
                address = readCombinedRegisters(registerH, registerL);
                load(registerH, mmu.rawRead(address));
                break;
            case 0x68:
                load(registerL, registerB);
                break;
            case 0x69:
                load(registerL, registerC);
                break;
            case 0x6A:
                load(registerL, registerD);
                break;
            case 0x6B:
                load(registerL, registerE);
                break;
            case 0x6C:
                load(registerL, registerH);
                break;
            case 0x6D:
                load(registerL, registerL);
                break;
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
            case 0x0A:
                load(registerA, mmu.rawRead(readCombinedRegisters(registerB, registerC)));
                break;  // LD A,(BC) 0A 8
            case 0x1A:
                load(registerA, mmu.rawRead(readCombinedRegisters(registerD, registerE)));
                break;  // LD A,(DE) 1A 8
            case 0xFA: // 16 cycles
                load(registerA, mmu.rawRead(mmu.readWord(registerPC.read())));
                registerPC.inc();
                registerPC.inc();
                break;
            case 0x3E:
                load(registerA, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;  // LD A,# 3E 8
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
            case 0x47:
                load(registerB, registerA);
                break; // LD B,A 47 4
            case 0x4F:
                load(registerC, registerA);
                break; // LD C,A 4F 4
            case 0x57:
                load(registerD, registerA);
                break; // LD D,A 57 4
            case 0x5F:
                load(registerE, registerA);
                break; // LD E,A 5F 4
            case 0x67:
                load(registerH, registerA);
                break; // LD H,A 67 4
            case 0x6F:
                load(registerL, registerA);
                break; // LD L,A 6F 4
            case 0x02:
                mmu.rawWrite(mmu.rawRead(readCombinedRegisters(registerB, registerC)), registerA.read());
                break; // LD (BC),A 02 8
            case 0x12:
                mmu.rawWrite(mmu.rawRead(readCombinedRegisters(registerD, registerE)), registerA.read());
                break; // LD (DE),A 12 8
            case 0x77:
                mmu.rawWrite(mmu.rawRead(readCombinedRegisters(registerH, registerL)), registerA.read());
                break; // LD (HL),A 77 8
            case 0xEA: // 16 cycles
                mmu.rawWrite(mmu.readWord(registerPC.read()), registerA.read());
                registerPC.inc();
                registerPC.inc();
                break; // LD (nn),A EA 16
            //</editor-fold>
            //<editor-fold desc="3.3.1.5 -- 3.3.1.20 8-bit Loads" defaultstate="collapsed">
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
            //</editor-fold>
            //<editor-fold desc="3.3.2.1 -- 3.3.2.5 16-bit Loads" defaultstate="collapsed">
            case 0x01:
                // LD BC,nn 01 12
                load(registerC, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                load(registerB, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x11:
                // LD DE,nn 11 12
                load(registerE, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                load(registerD, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x21:
                // LD HL,nn 21 12
                load(registerL, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                load(registerH, mmu.rawRead(registerPC.read()));
                registerPC.inc();
                break;
            case 0x31:
                // LD SP,nn 31 12
                load(registerSP, mmu.readWord(registerPC.read()));
                registerPC.inc();
                registerPC.inc();
                break;
            case 0xF9:
                // LD SP,HL F9 8
                load(registerSP, readCombinedRegisters(registerH, registerL));
                break;
            case 0xF8:
                // LDHL SP,n F8 12
                // Put SP + n effective address into HL. (n is signed here!)
                temp = mmu.rawRead(registerPC.read());
                if (temp > 127) {
                    temp = -((~temp + 1) & 255); // 2's complement
                }
                registerPC.inc();
                result = temp + registerSP.read();
                registerH.write((result >> 8) & 255);
                registerL.write(result & 255); // this is fishy

                // flags affected
                registerFlags.clearZ();
                registerFlags.clearN();
                if (((registerSP.read() ^ temp ^ result) & 0x100) == 0x100) {
                    registerFlags.setC();
                }
                if (((registerSP.read() ^ temp ^ result) & 0x10) == 0x10) {
                    registerFlags.setH();
                }
                break;
            case 0x08:
                // LD (nn),SP 08 20 (TWENTY CYCLES)
                // Put Stack Pointer (SP) at address n.
                // LD (nn),SP
                lowerValue = mmu.rawRead(registerPC.read());
                registerPC.inc();
                upperValue = mmu.rawRead(registerPC.read());
                registerPC.inc();
                address = ((upperValue << 8) + lowerValue);
                mmu.rawWrite(address, registerSP.readLow());
                mmu.rawWrite(address + 1, registerSP.readHigh());
                break;
            //</editor-fold>
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to load(int opcode) . ", opcode));
        }
    }

    public void push(int opcode) throws InvalidPropertiesFormatException {
        // 3.3.2.6 PUSH nn
        int temp;
        switch (opcode) {
            // PUSH AF F5 16
            // PUSH BC C5 16
            // PUSH DE D5 16
            // PUSH HL E5 16
            case 0xF5:
                temp = readCombinedRegisters(registerA, registerFlags);
                break;
            case 0xC5:
                temp = readCombinedRegisters(registerB, registerC);
                break;
            case 0xD5:
                temp = readCombinedRegisters(registerD, registerE);
                break;
            case 0xE5:
                temp = readCombinedRegisters(registerH, registerL);
                break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to push(int opcode) . ", opcode));
                return;
        }

        // Description:
        //   Push register pair nn onto stack.
        //   Decrement Stack Pointer (SP) twice.
        registerSP.dec();
        mmu.rawWrite(registerSP.read(), temp & 0b11111111_00000000);
        registerSP.dec();
        mmu.rawWrite(registerSP.read(), temp & 0b00000000_11111111);

    }
    public void pop(int opcode) {
        // 3.3.2.7 POP nn
        Register upperRegister;
        Register lowerRegister;
        switch(opcode) {
            // POP AF F1 12
            // POP BC C1 12
            // POP DE D1 12
            // POP HL E1 12
            case 0xF1:
                upperRegister = registerA; lowerRegister = registerFlags; break;
            case 0xC1:
                upperRegister = registerB; lowerRegister = registerC; break;
            case 0xD1:
                upperRegister = registerD; lowerRegister = registerE; break;
            case 0xE1:
                upperRegister = registerH; lowerRegister = registerL; break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to pop(int opcode) . ", opcode));
                return;
        }

        if (upperRegister != null && lowerRegister != null) {
            // execute instruction

            // Description:
            //   Pop two bytes off stack into register pair nn.
            //   Increment Stack Pointer (SP) twice
            load(upperRegister, mmu.rawRead(registerSP.read()));
            registerSP.inc();
            load(lowerRegister, mmu.rawRead(registerSP.read()));
            registerSP.inc();
        }
        else {
            // error out
            System.out.println("Error: found call to pop() but either upper or lower register didn't get populated.");
        }
    }

    public void add(int opcode) throws InvalidPropertiesFormatException {
        /*  3.3.3.1 ADD A,n
            1. ADD A,n
            Description:
               Add n to A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Set if carry from bit 3.
               C - Set if carry from bit 7.
            Opcodes:
            Instruction Parameters Opcode Cycles
             ADD        A,A         87      4
             ADD        A,B         80      4
             ADD        A,C         81      4
             ADD        A,D         82      4
             ADD        A,E         83      4
             ADD        A,H         84      4
             ADD        A,L         85      4
             ADD        A,(HL)      86      8
             ADD        A,#         C6      8
         */
        int second;
        // determine value to add based on opcode
        switch (opcode) {
            case 0x87: second = registerA.read(); break;
            case 0x80: second = registerB.read(); break;
            case 0x81: second = registerC.read(); break;
            case 0x82: second = registerD.read(); break;
            case 0x83: second = registerE.read(); break;
            case 0x84: second = registerH.read(); break;
            case 0x85: second = registerL.read(); break;
            case 0x86: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xC6: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to add(int opcode) . ", opcode));
                return;
        }
        // do the addition
        int result = registerA.read() + second;

        // flags affected
        registerFlags.clearN();
        if (result > 255) {
            registerFlags.setC();
            result &= 0b1111_1111;
        }
        if (result == 0) {
            registerFlags.setZ();
        }
        if (result > 0b0000_1111) {
            registerFlags.setH();
        }

        // save result
        load(registerA, result);
    }
    public void adc(int opcode) throws InvalidPropertiesFormatException {
        /*  3.3.3.2 ADC A,n
            Description:
               Add n + Carry flag to A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Set if carry from bit 3.
               C - Set if carry from bit 7.
            Opcodes:
            Instruction Parameters Opcode Cycles
              ADC         A,A        8F     4
              ADC         A,B        88     4
              ADC         A,C        89     4
              ADC         A,D        8A     4
              ADC         A,E        8B     4
              ADC         A,H        8C     4
              ADC         A,L        8D     4
              ADC         A,(HL)     8E     8
              ADC         A,#        CE     8
         */
        int second;
        switch (opcode) {
            case 0x8F: second = registerA.read(); break;
            case 0x88: second = registerB.read(); break;
            case 0x89: second = registerC.read(); break;
            case 0x8A: second = registerD.read(); break;
            case 0x8B: second = registerE.read(); break;
            case 0x8C: second = registerH.read(); break;
            case 0x8D: second = registerL.read(); break;
            case 0x8E: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xCE: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to adc(int opcode) . ", opcode));
                return;
        }

        // do the addition
        int result = registerA.read() + second;
        result += (registerFlags.readC()) ? 1 : 0;

        // flags affected
        registerFlags.clearN();
        if (result > 255) {
            registerFlags.setC();
            result &= 0b1111_1111;
        }
        if (result == 0) {
            registerFlags.setZ();
        }
        if (result > 0b0000_1111) {
            registerFlags.setH();
        }

        // save result
        load(registerA, result);
    }

    public void sub(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.3 SUB n
            Description:
               Subtract n from A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Set.
               H - Set if no borrow from bit 4.
               C - Set if no borrow.
            Opcodes:
            Instruction Parameters Opcode Cycles
             SUB          A          97     4
             SUB          B          90     4
             SUB          C          91     4
             SUB          D          92     4
             SUB          E          93     4
             SUB          H          94     4
             SUB          L          95     4
             SUB          (HL)       96     8
             SUB          #          D6     8
         */
        int second;
        switch (opcode) {
            case 0x97: second = registerA.read(); break;
            case 0x90: second = registerB.read(); break;
            case 0x91: second = registerC.read(); break;
            case 0x92: second = registerD.read(); break;
            case 0x93: second = registerE.read(); break;
            case 0x94: second = registerH.read(); break;
            case 0x95: second = registerL.read(); break;
            case 0x96: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xD6: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to sub(int opcode) . ", opcode));
                return;
        }

        // do the subtraction
        int result = registerA.read() - second;

        // flags affected
        registerFlags.setN();
        if (result == 0) {
            registerFlags.setZ();
        }
        if (result < 0) {
            registerFlags.setC();
            result &= 0b1111_1111;
        }
        if (result < 0b0000_1111) {
            registerFlags.setH();
        }

        // save result
        load(registerA, result);
    }
    public void sbc(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.4 SBC A,n
            Description:
               Subtract n + Carry flag from A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Set.
               H - Set if no borrow from bit 4.
               C - Set if no borrow.
            Opcodes:
            Instruction Parameters Opcode Cycles
             SBC          A,A        9F     4
             SBC          A,B        98     4
             SBC          A,C        99     4
             SBC          A,D        9A     4
             SBC          A,E        9B     4
             SBC          A,H        9C     4
             SBC          A,L        9D     4
             SBC          A,(HL)     9E     8
             SBC          A,#        ??     ?
         */
        int second;
        switch (opcode) {
            case 0x9F: second = registerA.read(); break;
            case 0x98: second = registerB.read(); break;
            case 0x99: second = registerC.read(); break;
            case 0x9A: second = registerD.read(); break;
            case 0x9B: second = registerE.read(); break;
            case 0x9C: second = registerH.read(); break;
            case 0x9D: second = registerL.read(); break;
            case 0x9E: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to sbc(int opcode) . ", opcode));
                return;
        }

        // do the subtraction
        second += registerFlags.readC() ? 1 : 0;
        int result = registerA.read() - second;

        // flags affected
        registerFlags.setN();
        if (result == 0) {
            registerFlags.setZ();
        }
        if (result < 0) {
            registerFlags.setC();
            result &= 0b1111_1111;
        }
        if (result < 0b0000_1111) {
            registerFlags.setH();
        }

        // save result
        load(registerA, result);
    }

    public void and(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.5 AND n
            Description:
               Logically AND n with A, result in A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Set.
               C - Reset.
            Opcodes:
            Instruction Parameters Opcode Cycles
             AND            A        A7     4
             AND            B        A0     4
             AND            C        A1     4
             AND            D        A2     4
             AND            E        A3     4
             AND            H        A4     4
             AND            L        A5     4
             AND            (HL)     A6     8
             AND            #        E6     8
         */
        int second;
        switch (opcode) {
            case 0xA7: second = registerA.read(); break;
            case 0xA0: second = registerB.read(); break;
            case 0xA1: second = registerC.read(); break;
            case 0xA2: second = registerD.read(); break;
            case 0xA3: second = registerE.read(); break;
            case 0xA4: second = registerH.read(); break;
            case 0xA5: second = registerL.read(); break;
            case 0xA6: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xE6: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to and(int opcode) . ", opcode));
                return;
        }

        // do the and
        load(registerA, registerA.read() & second);

        // flags affected
        if (registerA.read() == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearN();
        registerFlags.setH();
        registerFlags.clearC();
    }
    public void or(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.6. OR n
            Description:
               Logical OR n with register A, result in A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Reset.
               C - Reset.
            Opcodes:
            Instruction Parameters Opcode Cycles
             OR             A        B7     4
             OR             B        B0     4
             OR             C        B1     4
             OR             D        B2     4
             OR             E        B3     4
             OR             H        B4     4
             OR             L        B5     4
             OR             (HL)     B6     8
             OR             #        F6     8
        */
        int second;
        switch (opcode) {
            case 0xB7: second = registerA.read(); break;
            case 0xB0: second = registerB.read(); break;
            case 0xB1: second = registerC.read(); break;
            case 0xB2: second = registerD.read(); break;
            case 0xB3: second = registerE.read(); break;
            case 0xB4: second = registerH.read(); break;
            case 0xB5: second = registerL.read(); break;
            case 0xB6: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xF6: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to or(int opcode) . ", opcode));
                return;
        }

        // do the or
        load(registerA, registerA.read() | second);

        // flags affected
        if (registerA.read() == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.clearC();
    }
    public void xor(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.7 XOR n
            Description:
               Logical exclusive OR n with register A, result in A.
            Use with:
               n = A,B,C,D,E,H,L,(HL),#
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Reset.
               C - Reset.
            Opcodes:
            Instruction Parameters Opcode Cycles
             XOR            A       AF      4
             XOR            B       A8      4
             XOR            C       A9      4
             XOR            D       AA      4
             XOR            E       AB      4
             XOR            H       AC      4
             XOR            L       AD      4
             XOR            (HL)    AE      8
             XOR            *       EE      8
        */
        int second;
        switch (opcode) {
            case 0xAF: second = registerA.read(); break;
            case 0xA8: second = registerB.read(); break;
            case 0xA9: second = registerC.read(); break;
            case 0xAA: second = registerD.read(); break;
            case 0xAB: second = registerE.read(); break;
            case 0xAC: second = registerH.read(); break;
            case 0xAD: second = registerL.read(); break;
            case 0xAE: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xEE: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to xor(int opcode) . ", opcode));
                return;
        }

        // do the xor
        load(registerA, registerA.read() ^ second);

        // flags affected
        if (registerA.read() == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.clearC();
    }
    public void cp(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.8. CP n
           Description:
               Compare A with n. This is basically an A - n
               subtraction instruction but the results are thrown
               away.
           Use with:
               n = A,B,C,D,E,H,L,(HL),#
           Flags affected:
               Z - Set if result is zero. (Set if A = n.)
               N - Set.
               H - Set if no borrow from bit 4.
               C - Set for no borrow. (Set if A < n.)
           Opcodes:
           Instruction Parameters Opcode Cycles
                CP          A       BF      4
                CP          B       B8      4
                CP          C       B9      4
                CP          D       BA      4
                CP          E       BB      4
                CP          H       BC      4
                CP          L       BD      4
                CP          (HL)    BE      8
                CP          #       FE      8
         */
        int second;
        switch (opcode) {
            case 0xBF: second = registerA.read(); break;
            case 0xB8: second = registerB.read(); break;
            case 0xB9: second = registerC.read(); break;
            case 0xBA: second = registerD.read(); break;
            case 0xBB: second = registerE.read(); break;
            case 0xBC: second = registerH.read(); break;
            case 0xBD: second = registerL.read(); break;
            case 0xBE: second = mmu.rawRead(readCombinedRegisters(registerH, registerL)); break;
            case 0xFE: second = mmu.rawRead(registerPC.read()); registerPC.inc(); break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to cp(int opcode) . ", opcode));
                return;
        }

        // do the subtraction
        int result = registerA.read() - second;

        // flags affected
        registerFlags.setN();
        if (result == 0) {
            registerFlags.setZ();
        }
        if (result < 0) {
            registerFlags.setC();
            result &= 0b1111_1111;
        }
        if (result < 0b0000_1111) {
            registerFlags.setH();
        }

        // throw away result :-)
    }
    public void inc(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.9. INC n
            Description:
               Increment register n.
            Use with:
               n = A,B,C,D,E,H,L,(HL)
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Set if carry from bit 3.
               C - Not affected.
            Opcodes:
            Instruction Parameters Opcode Cycles
             INC            A       3C      4
             INC            B       04      4
             INC            C       0C      4
             INC            D       14      4
             INC            E       1C      4
             INC            H       24      4
             INC            L       2C      4
             INC            (HL)    34      12
        */
        int value;
        switch (opcode) {
            case 0x3C: registerA.inc(); value = registerA.read(); break;
            case 0x04: registerB.inc(); value = registerB.read(); break;
            case 0x0C: registerC.inc(); value = registerC.read(); break;
            case 0x14: registerD.inc(); value = registerD.read(); break;
            case 0x1C: registerE.inc(); value = registerE.read(); break;
            case 0x24: registerH.inc(); value = registerH.read(); break;
            case 0x2C: registerL.inc(); value = registerL.read(); break;
            case 0x34:
                int address = readCombinedRegisters(registerH, registerL);
                value = mmu.rawRead(address);
                value += 1;
                mmu.rawWrite(address, value);
                break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to inc(int opcode) . ", opcode));
                return;
        }

        // flags affected
        if (value == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearN();
        if (value > 0b0000_1111) {
            registerFlags.setH();
        }
    }
    public void dec(int opcode) throws InvalidPropertiesFormatException {
        /* 3.3.3.10. DEC n
            Description:
               Decrement register n.
            Use with:
               n = A,B,C,D,E,H,L,(HL)
            Flags affected:
               Z - Set if reselt is zero.
               N - Set.
               H - Set if no borrow from bit 4.
               C - Not affected.
            Opcodes:
            Instruction Parameters Opcode Cycles
               DEC          A       3D      4
               DEC          B       05      4
               DEC          C       0D      4
               DEC          D       15      4
               DEC          E       1D      4
               DEC          H       25      4
               DEC          L       2D      4
               DEC          (HL)    35      12
         */
        int value;
        switch (opcode) {
            case 0x3D: registerA.dec(); value = registerA.read(); break;
            case 0x05: registerB.dec(); value = registerB.read(); break;
            case 0x0D: registerC.dec(); value = registerC.read(); break;
            case 0x15: registerD.dec(); value = registerD.read(); break;
            case 0x1D: registerE.dec(); value = registerE.read(); break;
            case 0x25: registerH.dec(); value = registerH.read(); break;
            case 0x2D: registerL.dec(); value = registerL.read(); break;
            case 0x35:
                int address = readCombinedRegisters(registerH, registerL);
                value = mmu.rawRead(address);
                value -= 1;
                mmu.rawWrite(address, value);
                break;
            default:
                System.out.println(String.format("Error: Opcode %05X does not belong to dec(int opcode) . ", opcode));
                return;
        }

        // flags affected
        if (value == 0) {
            registerFlags.setZ();
        }
        registerFlags.setN();
        if (value < 0b0000_1111) {
            registerFlags.setH();
        }
    }
}
