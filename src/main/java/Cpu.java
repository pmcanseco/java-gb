import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pablo Canseco on 12/22/2017.
 * An Object-Oriented Approach to a Gameboy Z80 Processor Emulator
 */
public class Cpu {
    private final String name = "CPU";
    private Logger log = new Logger(name, Logger.Level.WARN);

    // 8-bit registers
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

    // 16-bit registers
    private Register registerPC; // program counter
    private Register registerSP; // stack pointer
    //private Register registerI;
    //private Register registerR;
    //private Register registerM;  // m-time for last instruction
    //private Register registerT;  // t-time for last instruction
    //private Register registerIME;

    public int lastInstructionCycles = 0;

    private Map<String, Register> eightBitRegisters = new HashMap<>();
    private Map<String, Register> sixteenBitRegisters = new HashMap<>();

    private MemoryManager mmu;
    private Gpu gpu;

    private boolean pendingInterruptDisable = false;
    private boolean pendingInterruptEnable = false;
    public  boolean interruptMasterEnable = true;
    enum Interrupts {
        VBLANK,
        LCDSTAT,
        TIMER,
        SERIAL,
        JOYPAD
    }

    private Map<Integer, Runnable> instructionMap = new HashMap<>();

    Cpu(MemoryManager memMgr, Gpu gpu) {
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
        this.gpu = gpu;
        log.debug("initialized.");

        //<editor-fold desc="Normal Instruction Map Entries" defaultstate="collapsed">
        log.info("Populating normal instructions...");
        instructionMap.put(0x00, () -> nopHaltStop(0x00));
        instructionMap.put(0x01, () -> load(0x01));
        instructionMap.put(0x02, () -> load(0x02));
        instructionMap.put(0x03, () -> inc16(0x03));
        instructionMap.put(0x04, () -> inc(0x04));
        instructionMap.put(0x05, () -> dec(0x05));
        instructionMap.put(0x06, () -> load(0x06));
        instructionMap.put(0x07, () -> rlca(0x07));
        instructionMap.put(0x08, () -> rst(0x08));
        instructionMap.put(0x09, () -> add16(0x09));
        instructionMap.put(0x0a, () -> load(0x0a));
        instructionMap.put(0x0b, () -> dec16(0x0b));
        instructionMap.put(0x0c, () -> inc(0x0c));
        instructionMap.put(0x0d, () -> dec(0x0d));
        instructionMap.put(0x0e, () -> load(0x0e));
        instructionMap.put(0x0f, () -> rrca(0x0f));
        instructionMap.put(0x10, () -> nopHaltStop(0x10));
        instructionMap.put(0x11, () -> load(0x11));
        instructionMap.put(0x12, () -> load(0x12));
        instructionMap.put(0x13, () -> inc16(0x13));
        instructionMap.put(0x14, () -> inc(0x14));
        instructionMap.put(0x15, () -> dec(0x15));
        instructionMap.put(0x16, () -> load(0x16));
        instructionMap.put(0x17, () -> rla(0x17));
        instructionMap.put(0x18, () -> jr(0x18));
        instructionMap.put(0x19, () -> add16(0x19));
        instructionMap.put(0x1a, () -> load(0x1a));
        instructionMap.put(0x1b, () -> dec16(0x1b));
        instructionMap.put(0x1c, () -> inc(0x1c));
        instructionMap.put(0x1d, () -> dec(0x1d));
        instructionMap.put(0x1e, () -> load(0x1e));
        instructionMap.put(0x1f, () -> rra(0x1f));
        instructionMap.put(0x20, () -> jrcc(0x20));
        instructionMap.put(0x21, () -> load(0x21));
        instructionMap.put(0x22, () -> load(0x22));
        instructionMap.put(0x23, () -> inc16(0x23));
        instructionMap.put(0x24, () -> inc(0x24));
        instructionMap.put(0x25, () -> dec(0x25));
        instructionMap.put(0x26, () -> load(0x26));
        instructionMap.put(0x27, () -> daa(0x27));
        instructionMap.put(0x28, () -> jrcc(0x28));
        instructionMap.put(0x29, () -> add16(0x29));
        instructionMap.put(0x2a, () -> load(0x2a));
        instructionMap.put(0x2b, () -> dec16(0x2b));
        instructionMap.put(0x2c, () -> inc(0x2c));
        instructionMap.put(0x2d, () -> dec(0x2d));
        instructionMap.put(0x2e, () -> load(0x2e));
        instructionMap.put(0x2f, () -> cpl(0x2f));
        instructionMap.put(0x30, () -> jrcc(0x30));
        instructionMap.put(0x31, () -> load(0x31));
        instructionMap.put(0x32, () -> load(0x32));
        instructionMap.put(0x33, () -> inc16(0x33));
        instructionMap.put(0x34, () -> inc16(0x34));
        instructionMap.put(0x35, () -> dec16(0x35));
        instructionMap.put(0x36, () -> load(0x36));
        instructionMap.put(0x37, () -> scf(0x37));
        instructionMap.put(0x38, () -> jrcc(0x38));
        instructionMap.put(0x39, () -> add16(0x39));
        instructionMap.put(0x3a, () -> load(0x3a));
        instructionMap.put(0x3b, () -> dec16(0x3b));
        instructionMap.put(0x3c, () -> inc(0x3c));
        instructionMap.put(0x3d, () -> dec(0x3d));
        instructionMap.put(0x3e, () -> load(0x3e));
        instructionMap.put(0x3f, () -> ccf(0x3f));
        instructionMap.put(0x40, () -> load(0x40));
        instructionMap.put(0x41, () -> load(0x41));
        instructionMap.put(0x42, () -> load(0x42));
        instructionMap.put(0x43, () -> load(0x43));
        instructionMap.put(0x44, () -> load(0x44));
        instructionMap.put(0x45, () -> load(0x45));
        instructionMap.put(0x46, () -> load(0x46));
        instructionMap.put(0x47, () -> load(0x47));
        instructionMap.put(0x48, () -> load(0x48));
        instructionMap.put(0x49, () -> load(0x49));
        instructionMap.put(0x4a, () -> load(0x4a));
        instructionMap.put(0x4b, () -> load(0x4b));
        instructionMap.put(0x4c, () -> load(0x4c));
        instructionMap.put(0x4d, () -> load(0x4d));
        instructionMap.put(0x4e, () -> load(0x4e));
        instructionMap.put(0x4f, () -> load(0x4f));
        instructionMap.put(0x50, () -> load(0x50));
        instructionMap.put(0x51, () -> load(0x51));
        instructionMap.put(0x52, () -> load(0x52));
        instructionMap.put(0x53, () -> load(0x53));
        instructionMap.put(0x54, () -> load(0x54));
        instructionMap.put(0x55, () -> load(0x55));
        instructionMap.put(0x56, () -> load(0x56));
        instructionMap.put(0x57, () -> load(0x57));
        instructionMap.put(0x58, () -> load(0x58));
        instructionMap.put(0x59, () -> load(0x59));
        instructionMap.put(0x5a, () -> load(0x5a));
        instructionMap.put(0x5b, () -> load(0x5b));
        instructionMap.put(0x5c, () -> load(0x5c));
        instructionMap.put(0x5d, () -> load(0x5d));
        instructionMap.put(0x5e, () -> load(0x5e));
        instructionMap.put(0x5f, () -> load(0x5f));
        instructionMap.put(0x60, () -> load(0x60));
        instructionMap.put(0x61, () -> load(0x61));
        instructionMap.put(0x62, () -> load(0x62));
        instructionMap.put(0x63, () -> load(0x63));
        instructionMap.put(0x64, () -> load(0x64));
        instructionMap.put(0x65, () -> load(0x65));
        instructionMap.put(0x66, () -> load(0x66));
        instructionMap.put(0x67, () -> load(0x67));
        instructionMap.put(0x68, () -> load(0x68));
        instructionMap.put(0x69, () -> load(0x69));
        instructionMap.put(0x6a, () -> load(0x6a));
        instructionMap.put(0x6b, () -> load(0x6b));
        instructionMap.put(0x6c, () -> load(0x6c));
        instructionMap.put(0x6d, () -> load(0x6d));
        instructionMap.put(0x6e, () -> load(0x6e));
        instructionMap.put(0x6f, () -> load(0x6f));
        instructionMap.put(0x70, () -> load(0x70));
        instructionMap.put(0x71, () -> load(0x71));
        instructionMap.put(0x72, () -> load(0x72));
        instructionMap.put(0x73, () -> load(0x73));
        instructionMap.put(0x74, () -> load(0x74));
        instructionMap.put(0x75, () -> load(0x75));
        instructionMap.put(0x76, () -> nopHaltStop(0x76));
        instructionMap.put(0x77, () -> load(0x77));
        instructionMap.put(0x78, () -> load(0x78));
        instructionMap.put(0x79, () -> load(0x79));
        instructionMap.put(0x7a, () -> load(0x7a));
        instructionMap.put(0x7b, () -> load(0x7b));
        instructionMap.put(0x7c, () -> load(0x7c));
        instructionMap.put(0x7d, () -> load(0x7d));
        instructionMap.put(0x7e, () -> load(0x7e));
        instructionMap.put(0x7f, () -> load(0x7f));
        instructionMap.put(0x80, () -> add(0x80));
        instructionMap.put(0x81, () -> add(0x81));
        instructionMap.put(0x82, () -> add(0x82));
        instructionMap.put(0x83, () -> add(0x83));
        instructionMap.put(0x84, () -> add(0x84));
        instructionMap.put(0x85, () -> add(0x85));
        instructionMap.put(0x86, () -> add(0x86));
        instructionMap.put(0x87, () -> add(0x87));
        instructionMap.put(0x88, () -> adc(0x88));
        instructionMap.put(0x89, () -> adc(0x89));
        instructionMap.put(0x8a, () -> adc(0x8a));
        instructionMap.put(0x8b, () -> adc(0x8b));
        instructionMap.put(0x8c, () -> adc(0x8c));
        instructionMap.put(0x8d, () -> adc(0x8d));
        instructionMap.put(0x8e, () -> adc(0x8e));
        instructionMap.put(0x8f, () -> adc(0x8f));
        instructionMap.put(0x90, () -> sub(0x90));
        instructionMap.put(0x91, () -> sub(0x91));
        instructionMap.put(0x92, () -> sub(0x92));
        instructionMap.put(0x93, () -> sub(0x93));
        instructionMap.put(0x94, () -> sub(0x94));
        instructionMap.put(0x95, () -> sub(0x95));
        instructionMap.put(0x96, () -> sub(0x96));
        instructionMap.put(0x97, () -> sub(0x97));
        instructionMap.put(0x98, () -> sbc(0x98));
        instructionMap.put(0x99, () -> sbc(0x99));
        instructionMap.put(0x9a, () -> sbc(0x9a));
        instructionMap.put(0x9b, () -> sbc(0x9b));
        instructionMap.put(0x9c, () -> sbc(0x9c));
        instructionMap.put(0x9d, () -> sbc(0x9d));
        instructionMap.put(0x9e, () -> sbc(0x9e));
        instructionMap.put(0x9f, () -> sbc(0x9f));
        instructionMap.put(0xa0, () -> and(0xa0));
        instructionMap.put(0xa1, () -> and(0xa1));
        instructionMap.put(0xa2, () -> and(0xa2));
        instructionMap.put(0xa3, () -> and(0xa3));
        instructionMap.put(0xa4, () -> and(0xa4));
        instructionMap.put(0xa5, () -> and(0xa5));
        instructionMap.put(0xa6, () -> and(0xa6));
        instructionMap.put(0xa7, () -> and(0xa7));
        instructionMap.put(0xa8, () -> xor(0xa8));
        instructionMap.put(0xa9, () -> xor(0xa9));
        instructionMap.put(0xaa, () -> xor(0xaa));
        instructionMap.put(0xab, () -> xor(0xab));
        instructionMap.put(0xac, () -> xor(0xac));
        instructionMap.put(0xad, () -> xor(0xad));
        instructionMap.put(0xae, () -> xor(0xae));
        instructionMap.put(0xaf, () -> xor(0xaf));
        instructionMap.put(0xb0, () -> or(0xb0));
        instructionMap.put(0xb1, () -> or(0xb1));
        instructionMap.put(0xb2, () -> or(0xb2));
        instructionMap.put(0xb3, () -> or(0xb3));
        instructionMap.put(0xb4, () -> or(0xb4));
        instructionMap.put(0xb5, () -> or(0xb5));
        instructionMap.put(0xb6, () -> or(0xb6));
        instructionMap.put(0xb7, () -> or(0xb7));
        instructionMap.put(0xb8, () -> cp(0xb8));
        instructionMap.put(0xb9, () -> cp(0xb9));
        instructionMap.put(0xba, () -> cp(0xba));
        instructionMap.put(0xbb, () -> cp(0xbb));
        instructionMap.put(0xbc, () -> cp(0xbc));
        instructionMap.put(0xbd, () -> cp(0xbd));
        instructionMap.put(0xbe, () -> cp(0xbe));
        instructionMap.put(0xbf, () -> cp(0xbf));
        instructionMap.put(0xc0, () -> retcc(0xc0));
        instructionMap.put(0xc1, () -> pop(0xc1));
        instructionMap.put(0xc2, () -> jpcc(0xc2));
        instructionMap.put(0xc3, () -> jump(0xc3));
        instructionMap.put(0xc4, () -> callcc(0xc4));
        instructionMap.put(0xc5, () -> push(0xc5));
        instructionMap.put(0xc6, () -> add(0xc6));
        instructionMap.put(0xc7, () -> rst(0xc7));
        instructionMap.put(0xc8, () -> retcc(0xc8));
        instructionMap.put(0xc9, () -> ret(0xc9));
        instructionMap.put(0xca, () -> jpcc(0xca));
        instructionMap.put(0xcb, () -> log.fatal("Opcode 0xCB shouldn't be executed as-is."));
        instructionMap.put(0xcc, () -> callcc(0xcc));
        instructionMap.put(0xcd, () -> call(0xcd));
        instructionMap.put(0xce, () -> adc(0xce));
        instructionMap.put(0xcf, () -> rst(0xcf));
        instructionMap.put(0xd0, () -> retcc(0xd0));
        instructionMap.put(0xd1, () -> pop(0xd1));
        instructionMap.put(0xd2, () -> jpcc(0xd2));
        instructionMap.put(0xd3, () -> log.fatal("Opcode 0xD3 is invalid."));
        instructionMap.put(0xd4, () -> callcc(0xd4));
        instructionMap.put(0xd5, () -> push(0xd5));
        instructionMap.put(0xd6, () -> sub(0xd6));
        instructionMap.put(0xd7, () -> rst(0xd7));
        instructionMap.put(0xd8, () -> retcc(0xd8));
        instructionMap.put(0xd9, () -> reti(0xd9));
        instructionMap.put(0xda, () -> jpcc(0xda));
        instructionMap.put(0xdb, () -> log.fatal("Opcode 0xDB is invalid."));
        instructionMap.put(0xdc, () -> callcc(0xdc));
        instructionMap.put(0xdd, () -> log.fatal("Opcode 0xDD is invalid."));
        instructionMap.put(0xde, () -> sbc(0xde));
        instructionMap.put(0xdf, () -> rst(0xdf));
        instructionMap.put(0xe0, () -> load(0xe0));
        instructionMap.put(0xe1, () -> pop(0xe1));
        instructionMap.put(0xe2, () -> load(0xe2));
        instructionMap.put(0xe3, () -> log.fatal("Opcode 0xE3 is invalid."));
        instructionMap.put(0xe4, () -> log.fatal("Opcode 0xE4 is invalid."));
        instructionMap.put(0xe5, () -> push(0xe5));
        instructionMap.put(0xe6, () -> and(0xe6));
        instructionMap.put(0xe7, () -> rst(0xe7));
        instructionMap.put(0xe8, () -> add16(0xe8));
        instructionMap.put(0xe9, () -> jphl(0xe9));
        instructionMap.put(0xea, () -> load(0xea));
        instructionMap.put(0xeb, () -> log.fatal("Opcode 0xEB is invalid."));
        instructionMap.put(0xec, () -> log.fatal("Opcode 0xEC is invalid."));
        instructionMap.put(0xed, () -> log.fatal("Opcode 0xED is invalid."));
        instructionMap.put(0xee, () -> xor(0xee));
        instructionMap.put(0xef, () -> rst(0xef));
        instructionMap.put(0xf0, () -> load(0xf0));
        instructionMap.put(0xf1, () -> pop(0xf1));
        instructionMap.put(0xf2, () -> load(0xf2));
        instructionMap.put(0xf3, () -> diEi(0xf3));
        instructionMap.put(0xf4, () -> log.fatal("Opcode 0xF4 is invalid."));
        instructionMap.put(0xf5, () -> push(0xf5));
        instructionMap.put(0xf6, () -> or(0xf6));
        instructionMap.put(0xf7, () -> rst(0xf7));
        instructionMap.put(0xf8, () -> load(0xf8));
        instructionMap.put(0xf9, () -> load(0xf9));
        instructionMap.put(0xfa, () -> load(0xfa));
        instructionMap.put(0xfb, () -> diEi(0xfb));
        instructionMap.put(0xfc, () -> log.fatal("Opcode 0xFC is invalid."));
        instructionMap.put(0xfd, () -> log.fatal("Opcode 0xFD is invalid."));
        instructionMap.put(0xfe, () -> cp(0xfe));
        instructionMap.put(0xff, () -> rst(0xff));
        log.info("Populated normal instructions.");
        //</editor-fold>

        //<editor-fold desc="CB Instruction Map Entries" defaultstate="collapsed">
        log.info("Populating cb instructions...");
        instructionMap.put(0xcb00, () -> rlc(0xcb00));
        instructionMap.put(0xcb01, () -> rlc(0xcb01));
        instructionMap.put(0xcb02, () -> rlc(0xcb02));
        instructionMap.put(0xcb03, () -> rlc(0xcb03));
        instructionMap.put(0xcb04, () -> rlc(0xcb04));
        instructionMap.put(0xcb05, () -> rlc(0xcb05));
        instructionMap.put(0xcb06, () -> rlc(0xcb06));
        instructionMap.put(0xcb07, () -> rlc(0xcb07));
        instructionMap.put(0xcb08, () -> rrc(0xcb08));
        instructionMap.put(0xcb09, () -> rrc(0xcb09));
        instructionMap.put(0xcb0a, () -> rrc(0xcb0a));
        instructionMap.put(0xcb0b, () -> rrc(0xcb0b));
        instructionMap.put(0xcb0c, () -> rrc(0xcb0c));
        instructionMap.put(0xcb0d, () -> rrc(0xcb0d));
        instructionMap.put(0xcb0e, () -> rrc(0xcb0e));
        instructionMap.put(0xcb0f, () -> rrc(0xcb0f));
        instructionMap.put(0xcb10, () -> rl(0xcb10));
        instructionMap.put(0xcb11, () -> rl(0xcb11));
        instructionMap.put(0xcb12, () -> rl(0xcb12));
        instructionMap.put(0xcb13, () -> rl(0xcb13));
        instructionMap.put(0xcb14, () -> rl(0xcb14));
        instructionMap.put(0xcb15, () -> rl(0xcb15));
        instructionMap.put(0xcb16, () -> rl(0xcb16));
        instructionMap.put(0xcb17, () -> rl(0xcb17));
        instructionMap.put(0xcb18, () -> rr(0xcb18));
        instructionMap.put(0xcb19, () -> rr(0xcb19));
        instructionMap.put(0xcb1a, () -> rr(0xcb1a));
        instructionMap.put(0xcb1b, () -> rr(0xcb1b));
        instructionMap.put(0xcb1c, () -> rr(0xcb1c));
        instructionMap.put(0xcb1d, () -> rr(0xcb1d));
        instructionMap.put(0xcb1e, () -> rr(0xcb1e));
        instructionMap.put(0xcb1f, () -> rr(0xcb1f));
        instructionMap.put(0xcb20, () -> sla(0xcb20));
        instructionMap.put(0xcb21, () -> sla(0xcb21));
        instructionMap.put(0xcb22, () -> sla(0xcb22));
        instructionMap.put(0xcb23, () -> sla(0xcb23));
        instructionMap.put(0xcb24, () -> sla(0xcb24));
        instructionMap.put(0xcb25, () -> sla(0xcb25));
        instructionMap.put(0xcb26, () -> sla(0xcb26));
        instructionMap.put(0xcb27, () -> sla(0xcb27));
        instructionMap.put(0xcb28, () -> sra(0xcb28));
        instructionMap.put(0xcb29, () -> sra(0xcb29));
        instructionMap.put(0xcb2a, () -> sra(0xcb2a));
        instructionMap.put(0xcb2b, () -> sra(0xcb2b));
        instructionMap.put(0xcb2c, () -> sra(0xcb2c));
        instructionMap.put(0xcb2d, () -> sra(0xcb2d));
        instructionMap.put(0xcb2e, () -> sra(0xcb2e));
        instructionMap.put(0xcb2f, () -> sra(0xcb2f));
        instructionMap.put(0xcb30, () -> swap(0xcb30));
        instructionMap.put(0xcb31, () -> swap(0xcb31));
        instructionMap.put(0xcb32, () -> swap(0xcb32));
        instructionMap.put(0xcb33, () -> swap(0xcb33));
        instructionMap.put(0xcb34, () -> swap(0xcb34));
        instructionMap.put(0xcb35, () -> swap(0xcb35));
        instructionMap.put(0xcb36, () -> swap(0xcb36));
        instructionMap.put(0xcb37, () -> swap(0xcb37));
        instructionMap.put(0xcb38, () -> srl(0xcb38));
        instructionMap.put(0xcb39, () -> srl(0xcb39));
        instructionMap.put(0xcb3a, () -> srl(0xcb3a));
        instructionMap.put(0xcb3b, () -> srl(0xcb3b));
        instructionMap.put(0xcb3c, () -> srl(0xcb3c));
        instructionMap.put(0xcb3d, () -> srl(0xcb3d));
        instructionMap.put(0xcb3e, () -> srl(0xcb3e));
        instructionMap.put(0xcb3f, () -> srl(0xcb3f));
        instructionMap.put(0xcb40, () -> bit(0xcb40));
        instructionMap.put(0xcb41, () -> bit(0xcb41));
        instructionMap.put(0xcb42, () -> bit(0xcb42));
        instructionMap.put(0xcb43, () -> bit(0xcb43));
        instructionMap.put(0xcb44, () -> bit(0xcb44));
        instructionMap.put(0xcb45, () -> bit(0xcb45));
        instructionMap.put(0xcb46, () -> bit(0xcb46));
        instructionMap.put(0xcb47, () -> bit(0xcb47));
        instructionMap.put(0xcb48, () -> bit(0xcb48));
        instructionMap.put(0xcb49, () -> bit(0xcb49));
        instructionMap.put(0xcb4a, () -> bit(0xcb4a));
        instructionMap.put(0xcb4b, () -> bit(0xcb4b));
        instructionMap.put(0xcb4c, () -> bit(0xcb4c));
        instructionMap.put(0xcb4d, () -> bit(0xcb4d));
        instructionMap.put(0xcb4e, () -> bit(0xcb4e));
        instructionMap.put(0xcb4f, () -> bit(0xcb4f));
        instructionMap.put(0xcb50, () -> bit(0xcb50));
        instructionMap.put(0xcb51, () -> bit(0xcb51));
        instructionMap.put(0xcb52, () -> bit(0xcb52));
        instructionMap.put(0xcb53, () -> bit(0xcb53));
        instructionMap.put(0xcb54, () -> bit(0xcb54));
        instructionMap.put(0xcb55, () -> bit(0xcb55));
        instructionMap.put(0xcb56, () -> bit(0xcb56));
        instructionMap.put(0xcb57, () -> bit(0xcb57));
        instructionMap.put(0xcb58, () -> bit(0xcb58));
        instructionMap.put(0xcb59, () -> bit(0xcb59));
        instructionMap.put(0xcb5a, () -> bit(0xcb5a));
        instructionMap.put(0xcb5b, () -> bit(0xcb5b));
        instructionMap.put(0xcb5c, () -> bit(0xcb5c));
        instructionMap.put(0xcb5d, () -> bit(0xcb5d));
        instructionMap.put(0xcb5e, () -> bit(0xcb5e));
        instructionMap.put(0xcb5f, () -> bit(0xcb5f));
        instructionMap.put(0xcb60, () -> bit(0xcb60));
        instructionMap.put(0xcb61, () -> bit(0xcb61));
        instructionMap.put(0xcb62, () -> bit(0xcb62));
        instructionMap.put(0xcb63, () -> bit(0xcb63));
        instructionMap.put(0xcb64, () -> bit(0xcb64));
        instructionMap.put(0xcb65, () -> bit(0xcb65));
        instructionMap.put(0xcb66, () -> bit(0xcb66));
        instructionMap.put(0xcb67, () -> bit(0xcb67));
        instructionMap.put(0xcb68, () -> bit(0xcb68));
        instructionMap.put(0xcb69, () -> bit(0xcb69));
        instructionMap.put(0xcb6a, () -> bit(0xcb6a));
        instructionMap.put(0xcb6b, () -> bit(0xcb6b));
        instructionMap.put(0xcb6c, () -> bit(0xcb6c));
        instructionMap.put(0xcb6d, () -> bit(0xcb6d));
        instructionMap.put(0xcb6e, () -> bit(0xcb6e));
        instructionMap.put(0xcb6f, () -> bit(0xcb6f));
        instructionMap.put(0xcb70, () -> bit(0xcb70));
        instructionMap.put(0xcb71, () -> bit(0xcb71));
        instructionMap.put(0xcb72, () -> bit(0xcb72));
        instructionMap.put(0xcb73, () -> bit(0xcb73));
        instructionMap.put(0xcb74, () -> bit(0xcb74));
        instructionMap.put(0xcb75, () -> bit(0xcb75));
        instructionMap.put(0xcb76, () -> bit(0xcb76));
        instructionMap.put(0xcb77, () -> bit(0xcb77));
        instructionMap.put(0xcb78, () -> bit(0xcb78));
        instructionMap.put(0xcb79, () -> bit(0xcb79));
        instructionMap.put(0xcb7a, () -> bit(0xcb7a));
        instructionMap.put(0xcb7b, () -> bit(0xcb7b));
        instructionMap.put(0xcb7c, () -> bit(0xcb7c));
        instructionMap.put(0xcb7d, () -> bit(0xcb7d));
        instructionMap.put(0xcb7e, () -> bit(0xcb7e));
        instructionMap.put(0xcb7f, () -> bit(0xcb7f));
        instructionMap.put(0xcb80, () -> res(0xcb80));
        instructionMap.put(0xcb81, () -> res(0xcb81));
        instructionMap.put(0xcb82, () -> res(0xcb82));
        instructionMap.put(0xcb83, () -> res(0xcb83));
        instructionMap.put(0xcb84, () -> res(0xcb84));
        instructionMap.put(0xcb85, () -> res(0xcb85));
        instructionMap.put(0xcb86, () -> res(0xcb86));
        instructionMap.put(0xcb87, () -> res(0xcb87));
        instructionMap.put(0xcb88, () -> res(0xcb88));
        instructionMap.put(0xcb89, () -> res(0xcb89));
        instructionMap.put(0xcb8a, () -> res(0xcb8a));
        instructionMap.put(0xcb8b, () -> res(0xcb8b));
        instructionMap.put(0xcb8c, () -> res(0xcb8c));
        instructionMap.put(0xcb8d, () -> res(0xcb8d));
        instructionMap.put(0xcb8e, () -> res(0xcb8e));
        instructionMap.put(0xcb8f, () -> res(0xcb8f));
        instructionMap.put(0xcb90, () -> res(0xcb90));
        instructionMap.put(0xcb91, () -> res(0xcb91));
        instructionMap.put(0xcb92, () -> res(0xcb92));
        instructionMap.put(0xcb93, () -> res(0xcb93));
        instructionMap.put(0xcb94, () -> res(0xcb94));
        instructionMap.put(0xcb95, () -> res(0xcb95));
        instructionMap.put(0xcb96, () -> res(0xcb96));
        instructionMap.put(0xcb97, () -> res(0xcb97));
        instructionMap.put(0xcb98, () -> res(0xcb98));
        instructionMap.put(0xcb99, () -> res(0xcb99));
        instructionMap.put(0xcb9a, () -> res(0xcb9a));
        instructionMap.put(0xcb9b, () -> res(0xcb9b));
        instructionMap.put(0xcb9c, () -> res(0xcb9c));
        instructionMap.put(0xcb9d, () -> res(0xcb9d));
        instructionMap.put(0xcb9e, () -> res(0xcb9e));
        instructionMap.put(0xcb9f, () -> res(0xcb9f));
        instructionMap.put(0xcba0, () -> res(0xcba0));
        instructionMap.put(0xcba1, () -> res(0xcba1));
        instructionMap.put(0xcba2, () -> res(0xcba2));
        instructionMap.put(0xcba3, () -> res(0xcba3));
        instructionMap.put(0xcba4, () -> res(0xcba4));
        instructionMap.put(0xcba5, () -> res(0xcba5));
        instructionMap.put(0xcba6, () -> res(0xcba6));
        instructionMap.put(0xcba7, () -> res(0xcba7));
        instructionMap.put(0xcba8, () -> res(0xcba8));
        instructionMap.put(0xcba9, () -> res(0xcba9));
        instructionMap.put(0xcbaa, () -> res(0xcbaa));
        instructionMap.put(0xcbab, () -> res(0xcbab));
        instructionMap.put(0xcbac, () -> res(0xcbac));
        instructionMap.put(0xcbad, () -> res(0xcbad));
        instructionMap.put(0xcbae, () -> res(0xcbae));
        instructionMap.put(0xcbaf, () -> res(0xcbaf));
        instructionMap.put(0xcbb0, () -> res(0xcbb0));
        instructionMap.put(0xcbb1, () -> res(0xcbb1));
        instructionMap.put(0xcbb2, () -> res(0xcbb2));
        instructionMap.put(0xcbb3, () -> res(0xcbb3));
        instructionMap.put(0xcbb4, () -> res(0xcbb4));
        instructionMap.put(0xcbb5, () -> res(0xcbb5));
        instructionMap.put(0xcbb6, () -> res(0xcbb6));
        instructionMap.put(0xcbb7, () -> res(0xcbb7));
        instructionMap.put(0xcbb8, () -> res(0xcbb8));
        instructionMap.put(0xcbb9, () -> res(0xcbb9));
        instructionMap.put(0xcbba, () -> res(0xcbba));
        instructionMap.put(0xcbbb, () -> res(0xcbbb));
        instructionMap.put(0xcbbc, () -> res(0xcbbc));
        instructionMap.put(0xcbbd, () -> res(0xcbbd));
        instructionMap.put(0xcbbe, () -> res(0xcbbe));
        instructionMap.put(0xcbbf, () -> res(0xcbbf));
        instructionMap.put(0xcbc0, () -> set(0xcbc0));
        instructionMap.put(0xcbc1, () -> set(0xcbc1));
        instructionMap.put(0xcbc2, () -> set(0xcbc2));
        instructionMap.put(0xcbc3, () -> set(0xcbc3));
        instructionMap.put(0xcbc4, () -> set(0xcbc4));
        instructionMap.put(0xcbc5, () -> set(0xcbc5));
        instructionMap.put(0xcbc6, () -> set(0xcbc6));
        instructionMap.put(0xcbc7, () -> set(0xcbc7));
        instructionMap.put(0xcbc8, () -> set(0xcbc8));
        instructionMap.put(0xcbc9, () -> set(0xcbc9));
        instructionMap.put(0xcbca, () -> set(0xcbca));
        instructionMap.put(0xcbcb, () -> set(0xcbcb));
        instructionMap.put(0xcbcc, () -> set(0xcbcc));
        instructionMap.put(0xcbcd, () -> set(0xcbcd));
        instructionMap.put(0xcbce, () -> set(0xcbce));
        instructionMap.put(0xcbcf, () -> set(0xcbcf));
        instructionMap.put(0xcbd0, () -> set(0xcbd0));
        instructionMap.put(0xcbd1, () -> set(0xcbd1));
        instructionMap.put(0xcbd2, () -> set(0xcbd2));
        instructionMap.put(0xcbd3, () -> set(0xcbd3));
        instructionMap.put(0xcbd4, () -> set(0xcbd4));
        instructionMap.put(0xcbd5, () -> set(0xcbd5));
        instructionMap.put(0xcbd6, () -> set(0xcbd6));
        instructionMap.put(0xcbd7, () -> set(0xcbd7));
        instructionMap.put(0xcbd8, () -> set(0xcbd8));
        instructionMap.put(0xcbd9, () -> set(0xcbd9));
        instructionMap.put(0xcbda, () -> set(0xcbda));
        instructionMap.put(0xcbdb, () -> set(0xcbdb));
        instructionMap.put(0xcbdc, () -> set(0xcbdc));
        instructionMap.put(0xcbdd, () -> set(0xcbdd));
        instructionMap.put(0xcbde, () -> set(0xcbde));
        instructionMap.put(0xcbdf, () -> set(0xcbdf));
        instructionMap.put(0xcbe0, () -> set(0xcbe0));
        instructionMap.put(0xcbe1, () -> set(0xcbe1));
        instructionMap.put(0xcbe2, () -> set(0xcbe2));
        instructionMap.put(0xcbe3, () -> set(0xcbe3));
        instructionMap.put(0xcbe4, () -> set(0xcbe4));
        instructionMap.put(0xcbe5, () -> set(0xcbe5));
        instructionMap.put(0xcbe6, () -> set(0xcbe6));
        instructionMap.put(0xcbe7, () -> set(0xcbe7));
        instructionMap.put(0xcbe8, () -> set(0xcbe8));
        instructionMap.put(0xcbe9, () -> set(0xcbe9));
        instructionMap.put(0xcbea, () -> set(0xcbea));
        instructionMap.put(0xcbeb, () -> set(0xcbeb));
        instructionMap.put(0xcbec, () -> set(0xcbec));
        instructionMap.put(0xcbed, () -> set(0xcbed));
        instructionMap.put(0xcbee, () -> set(0xcbee));
        instructionMap.put(0xcbef, () -> set(0xcbef));
        instructionMap.put(0xcbf0, () -> set(0xcbf0));
        instructionMap.put(0xcbf1, () -> set(0xcbf1));
        instructionMap.put(0xcbf2, () -> set(0xcbf2));
        instructionMap.put(0xcbf3, () -> set(0xcbf3));
        instructionMap.put(0xcbf4, () -> set(0xcbf4));
        instructionMap.put(0xcbf5, () -> set(0xcbf5));
        instructionMap.put(0xcbf6, () -> set(0xcbf6));
        instructionMap.put(0xcbf7, () -> set(0xcbf7));
        instructionMap.put(0xcbf8, () -> set(0xcbf8));
        instructionMap.put(0xcbf9, () -> set(0xcbf9));
        instructionMap.put(0xcbfa, () -> set(0xcbfa));
        instructionMap.put(0xcbfb, () -> set(0xcbfb));
        instructionMap.put(0xcbfc, () -> set(0xcbfc));
        instructionMap.put(0xcbfd, () -> set(0xcbfd));
        instructionMap.put(0xcbfe, () -> set(0xcbfe));
        instructionMap.put(0xcbff, () -> set(0xcbff));
        //</editor-fold>
    }
    Cpu(MemoryManager memMgr) {
        this(memMgr, new Gpu(Logger.Level.FATAL));
    }
    Cpu(MemoryManager memMgr, Logger.Level level) {
        this(memMgr);
        this.log = new Logger(name, level);
    }

    public int fetch() {
        int opcode = mmu.readByte(registerPC.read());
        log.debug(String.format("PC: 0x%04X    OP: 0x%04X", registerPC.read(), opcode));
        registerPC.inc();

        if (opcode == 0xcb) {
            opcode <<= 8;
            opcode |= mmu.readByte(registerPC.read());
            registerPC.inc();
        }

        return opcode;
    }
    public void execute(int opcode) {
        instructionMap.get(opcode).run();
    }
    public void skipBios() {
        registerA.write(0x11);
        registerB.write(0x00);
        registerC.write(0x00);
        registerD.write(0xFF);
        registerE.write(0x56);
        registerH.write(0x00);
        registerL.write(0x0D);
        registerFlags.setZ();
        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.clearC();
        registerSP.write(0xFFFE);
        registerPC.write(0x0100);
        mmu.writeByte(0xFF05, 0x00); // TIMA
        mmu.writeByte(0xFF06, 0x00); // TMA
        mmu.writeByte(0xFF07, 0x00); // TAC
        mmu.writeByte(0xFF10, 0x80); // NR10
        mmu.writeByte(0xFF11, 0xBF); // NR11
        mmu.writeByte(0xFF12, 0xF3); // NR12
        mmu.writeByte(0xFF14, 0xBF); // NR14
        mmu.writeByte(0xFF16, 0x3F); // NR21
        mmu.writeByte(0xFF17, 0x00); // NR22
        mmu.writeByte(0xFF19, 0xBF); // NR24
        mmu.writeByte(0xFF1A, 0x7F); // NR30
        mmu.writeByte(0xFF1B, 0xFF); // NR31
        mmu.writeByte(0xFF1C, 0x9F); // NR32
        mmu.writeByte(0xFF1E, 0xBF); // NR33
        mmu.writeByte(0xFF20, 0xFF); // NR41
        mmu.writeByte(0xFF21, 0x00); // NR42
        mmu.writeByte(0xFF22, 0x00); // NR43
        mmu.writeByte(0xFF23, 0xBF); // NR30
        mmu.writeByte(0xFF24, 0x77); // NR50
        mmu.writeByte(0xFF25, 0xF3); // NR51
        mmu.writeByte(0xFF26, 0xF1); // NR52
        mmu.writeByte(0xFF40, 0x91); // LCDC
        mmu.writeByte(0xFF42, 0x00); // SCY
        mmu.writeByte(0xFF43, 0x00); // SCX
        mmu.writeByte(0xFF45, 0x00); // LYC
        mmu.writeByte(0xFF47, 0xFC); // BGP
        mmu.writeByte(0xFF48, 0xFF); // OBP0
        mmu.writeByte(0xFF49, 0xFF); // OBP1
        mmu.writeByte(0xFF4A, 0x00); // WY
        mmu.writeByte(0xFF4B, 0x00); // WX
        mmu.writeByte(0xFFFF, 0x00); // IE
    }

    // main loop
    public void main() {
        //skipBios();

        while(true) {
            int opcode = fetch();
            execute(opcode);

            if (registerPC.read() == 0x100) log = new Logger(name, Logger.Level.DEBUG);

            // step the GPU, check if vblank interrupt triggered.
            boolean vblankInterruptFired = gpu.step(lastInstructionCycles);

            // process EI/DI instruction effects (execute one more instruction prior
            // to enabling / disabling interrupts)
            if (pendingInterruptDisable && opcode != 0xF3) {
                pendingInterruptDisable = false;
                interruptMasterEnable = false;
            }
            if (pendingInterruptEnable && opcode != 0xFB) {
                pendingInterruptEnable = false;
                interruptMasterEnable = true;
            }

            /*if (vblankInterruptFired && interruptMasterEnable) {
                mmu.writeByte(0xFF0F, 1);

                pushHelper(registerPC.read());
                registerPC.write(0x40);
            }*/
        }
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
            log.debug("Unknown register");
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
            log.error("Unknown register");
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
            log.error("Unknown register");
        }
    }
    public boolean getRegisterBit(final String name, int index) {
        Register r = search(name);
        return r != null && r.readBit(index);
    }
    public int readCombinedRegisters(final String upper, final String lower) {
        Register u = search(upper);
        Register l = search(lower);
        if(u != null && l != null) {
            return readCombinedRegisters(u, l);
        }
        else {
            log.debug("Did not find one of registers " + upper + ", " + lower);
            return -1;
        }
    }
    public void writeCombinedRegisters(final String upper, final String lower, int value) {
        Register u = search(upper);
        Register l = search(lower);
        if(u != null && l != null) {
            writeCombinedRegisters(u, l, value);
        }
        else {
            log.debug("Did not find one of registers " + upper + ", " + lower);
        }
    }

    private int readCombinedRegisters(Register upper, Register lower) {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            log.fatal("one of the registers to combine wasn't an 8-bit register");
            return -1;
        }
        int result = upper.read();
        result <<= 8;
        result |= lower.read();
        return result;
    }
    private void writeCombinedRegisters(Register upper, Register lower, final int value) {
        if (upper.getSize() != 8 || lower.getSize() != 8) {
            log.fatal("one of the registers to combine wasn't an 8-bit register");
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

    // opcode implementations
    public void load(int opcode) {
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
                load(registerB, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
                break;
            case 0x0E:
                load(registerC, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
                break;
            case 0x16:
                load(registerD, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
                break;
            case 0x1E:
                load(registerE, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
                break;
            case 0x26:
                load(registerH, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
                break;
            case 0x2E:
                load(registerL, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
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
                lastInstructionCycles = 4;
                break;
            case 0x7F:
                load(registerA, registerA);
                lastInstructionCycles = 4;
                break;
            case 0x79:
                load(registerA, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x7A:
                load(registerA, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x7B:
                load(registerA, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x7C:
                load(registerA, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x7D:
                load(registerA, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x7E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x40:
                load(registerB, registerB);
                lastInstructionCycles = 4;
                break;
            case 0x41:
                load(registerB, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x42:
                load(registerB, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x43:
                load(registerB, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x44:
                load(registerB, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x45:
                load(registerB, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x46:
                address = readCombinedRegisters(registerH, registerL);
                load(registerB, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x48:
                load(registerC, registerB);
                lastInstructionCycles = 4;
                break;
            case 0x49:
                load(registerC, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x4A:
                load(registerC, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x4B:
                load(registerC, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x4C:
                load(registerC, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x4D:
                load(registerC, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x4E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerC, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x50:
                load(registerD, registerB);
                lastInstructionCycles = 4;
                break;
            case 0x51:
                load(registerD, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x52:
                load(registerD, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x53:
                load(registerD, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x54:
                load(registerD, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x55:
                load(registerD, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x56:
                address = readCombinedRegisters(registerH, registerL);
                load(registerD, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x58:
                load(registerE, registerB);
                lastInstructionCycles = 4;
                break;
            case 0x59:
                load(registerE, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x5A:
                load(registerE, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x5B:
                load(registerE, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x5C:
                load(registerE, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x5D:
                load(registerE, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x5E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerE, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x60:
                load(registerH, registerB);
                lastInstructionCycles = 4;
                break;
            case 0x61:
                load(registerH, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x62:
                load(registerH, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x63:
                load(registerH, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x64:
                load(registerH, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x65:
                load(registerH, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x66:
                address = readCombinedRegisters(registerH, registerL);
                load(registerH, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x68:
                load(registerL, registerB);
                lastInstructionCycles = 4;
                break;
            case 0x69:
                load(registerL, registerC);
                lastInstructionCycles = 4;
                break;
            case 0x6A:
                load(registerL, registerD);
                lastInstructionCycles = 4;
                break;
            case 0x6B:
                load(registerL, registerE);
                lastInstructionCycles = 4;
                break;
            case 0x6C:
                load(registerL, registerH);
                lastInstructionCycles = 4;
                break;
            case 0x6D:
                load(registerL, registerL);
                lastInstructionCycles = 4;
                break;
            case 0x6E:
                address = readCombinedRegisters(registerH, registerL);
                load(registerL, mmu.readByte(address));
                lastInstructionCycles = 8;
                break;
            case 0x70:
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, registerB.read());
                lastInstructionCycles = 8;
                break;
            case 0x71:
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, registerC.read());
                lastInstructionCycles = 8;
                break;
            case 0x72:
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, registerD.read());
                lastInstructionCycles = 8;
                break;
            case 0x73:
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, registerE.read());
                lastInstructionCycles = 8;
                break;
            case 0x74:
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, registerH.read());
                lastInstructionCycles = 8;
                break;
            case 0x75:
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, registerL.read());
                lastInstructionCycles = 8;
                break;
            case 0x36: // 12 cycles
                address = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(address, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 12;
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
                load(registerA, mmu.readByte(readCombinedRegisters(registerB, registerC)));
                lastInstructionCycles = 8;
                break;  // LD A,(BC) 0A 8
            case 0x1A:
                load(registerA, mmu.readByte(readCombinedRegisters(registerD, registerE)));
                lastInstructionCycles = 8;
                break;  // LD A,(DE) 1A 8
            case 0xFA: // 16 cycles
                load(registerA, mmu.readByte(mmu.readWord(registerPC.read())));
                registerPC.inc();
                registerPC.inc();
                lastInstructionCycles = 16;
                break;
            case 0x3E:
                load(registerA, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 8;
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
                lastInstructionCycles = 4;
                break; // LD B,A 47 4
            case 0x4F:
                load(registerC, registerA);
                lastInstructionCycles = 4;
                break; // LD C,A 4F 4
            case 0x57:
                load(registerD, registerA);
                lastInstructionCycles = 4;
                break; // LD D,A 57 4
            case 0x5F:
                load(registerE, registerA);
                lastInstructionCycles = 4;
                break; // LD E,A 5F 4
            case 0x67:
                load(registerH, registerA);
                lastInstructionCycles = 4;
                break; // LD H,A 67 4
            case 0x6F:
                load(registerL, registerA);
                lastInstructionCycles = 4;
                break; // LD L,A 6F 4
            case 0x02:
                mmu.writeByte(mmu.readByte(readCombinedRegisters(registerB, registerC)), registerA.read());
                lastInstructionCycles = 8;
                break; // LD (BC),A 02 8
            case 0x12:
                mmu.writeByte(mmu.readByte(readCombinedRegisters(registerD, registerE)), registerA.read());
                lastInstructionCycles = 8;
                break; // LD (DE),A 12 8
            case 0x77:
                mmu.writeByte(mmu.readByte(readCombinedRegisters(registerH, registerL)), registerA.read());
                lastInstructionCycles = 8;
                break; // LD (HL),A 77 8
            case 0xEA: // 16 cycles
                mmu.writeByte(mmu.readWord(registerPC.read()), registerA.read());
                registerPC.inc();
                registerPC.inc();
                lastInstructionCycles = 16;
                break; // LD (nn),A EA 16
            //</editor-fold>
            //<editor-fold desc="3.3.1.5 -- 3.3.1.20 8-bit Loads" defaultstate="collapsed">
            case 0xF2:
                // Put value at address $FF00 + register C into A , takes 8 cycles
                load(registerA, mmu.readByte(registerC.read() + 0xFF00));
                lastInstructionCycles = 8;
                break;
            case 0xE2:
                // Put A into address $FF00 + register C , takes 8 cycles
                mmu.writeByte(0xFF00 + registerC.read(), registerA.read());
                lastInstructionCycles = 8;
                break;
            case 0x3A:
                // Put value at address HL into A, Decrement HL. Takes 8 cycles
                temp = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.readByte(temp));
                writeCombinedRegisters(registerH, registerL, temp - 1);
                lastInstructionCycles = 8;
                break;
            case 0x32:
                // put A into memory address HL. Decrement HL. Takes 8 cycles.
                temp = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(temp, registerA.read());
                writeCombinedRegisters(registerH, registerL, temp - 1);
                lastInstructionCycles = 8;
                break;
            case 0x2A:
                // Put value at address HL into A, Increment HL. Takes 8 cycles
                temp = readCombinedRegisters(registerH, registerL);
                load(registerA, mmu.readByte(temp));
                writeCombinedRegisters(registerH, registerL, temp + 1);
                lastInstructionCycles = 8;
                break;
            case 0x22:
                // put A into memory address HL. Increment HL. Takes 8 cycles.
                temp = readCombinedRegisters(registerH, registerL);
                mmu.writeByte(temp, registerA.read());
                writeCombinedRegisters(registerH, registerL, temp + 1);
                lastInstructionCycles = 8;
                break;
            case 0xE0:
                // Put A into memory address $FF00+n . 12 cycles
                temp = 0xFF00 + mmu.readByte(registerPC.read());
                mmu.writeByte(temp, registerA.read());
                registerPC.inc();
                lastInstructionCycles = 12;
                break;
            case 0xF0:
                // Put memory address $FF00+n into A. 12 cycles
                temp = 0xFF00 + mmu.readByte(registerPC.read());
                load(registerA, mmu.readByte(temp));
                registerPC.inc();
                lastInstructionCycles = 12;
                break;
            //</editor-fold>
            //<editor-fold desc="3.3.2.1 -- 3.3.2.5 16-bit Loads" defaultstate="collapsed">
            case 0x01:
                // LD BC,nn 01 12
                load(registerC, mmu.readByte(registerPC.read()));
                registerPC.inc();
                load(registerB, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 12;
                break;
            case 0x11:
                // LD DE,nn 11 12
                load(registerE, mmu.readByte(registerPC.read()));
                registerPC.inc();
                load(registerD, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 12;
                break;
            case 0x21:
                // LD HL,nn 21 12
                load(registerL, mmu.readByte(registerPC.read()));
                registerPC.inc();
                load(registerH, mmu.readByte(registerPC.read()));
                registerPC.inc();
                lastInstructionCycles = 12;
                break;
            case 0x31:
                // LD SP,nn 31 12
                load(registerSP, mmu.readWord(registerPC.read()));
                registerPC.inc();
                registerPC.inc();
                lastInstructionCycles = 12;
                break;
            case 0xF9:
                // LD SP,HL F9 8
                load(registerSP, readCombinedRegisters(registerH, registerL));
                lastInstructionCycles = 8;
                break;
            case 0xF8:
                // LDHL SP,n F8 12
                // Put SP + n effective address into HL. (n is signed here!)
                temp = mmu.readByte(registerPC.read());
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

                lastInstructionCycles = 12;
                break;
            case 0x08:
                // LD (nn),SP 08 20 (TWENTY CYCLES)
                // Put Stack Pointer (SP) at address n.
                // LD (nn),SP
                lowerValue = mmu.readByte(registerPC.read());
                registerPC.inc();
                upperValue = mmu.readByte(registerPC.read());
                registerPC.inc();
                address = ((upperValue << 8) + lowerValue);
                mmu.writeByte(address, registerSP.readLow());
                mmu.writeByte(address + 1, registerSP.readHigh());
                lastInstructionCycles = 20;
                break;
            //</editor-fold>
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to load(int opcode) . ", opcode));
        }
    }

    private void pushHelper(int value) {
        // Description:
        //   Push register pair nn onto stack.
        //   Decrement Stack Pointer (SP) twice.
        registerSP.dec();
        mmu.writeByte(registerSP.read(), (value & 0b11111111_00000000) >> 8);
        registerSP.dec();
        mmu.writeByte(registerSP.read(), value & 0b00000000_11111111);
    }
    private int popHelper() {
        int low = mmu.readByte(registerSP.read());
        mmu.writeByte(registerSP.read(), 0);
        registerSP.inc();
        int high  = mmu.readByte(registerSP.read());
        mmu.writeByte(registerSP.read(), 0);
        registerSP.inc();
        high <<= 8;
        return (high | low);

    }
    public void push(int opcode) {
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
                log.debug(String.format("log.error: Opcode %05X does not belong to push(int opcode) . ", opcode));
                return;
        }

        pushHelper(temp);
        lastInstructionCycles = 16;
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
                log.debug(String.format("log.error: Opcode %05X does not belong to pop(int opcode) . ", opcode));
                return;
        }

        if (upperRegister != null && lowerRegister != null) {
            // execute instruction

            // Description:
            //   Pop two bytes off stack into register pair nn.
            //   Increment Stack Pointer (SP) twice
            writeCombinedRegisters(upperRegister, lowerRegister, popHelper());
            lastInstructionCycles = 12;
        }
        else {
            // log.error out
            log.debug("log.error: found call to pop() but either upper or lower register didn't get populated.");
        }
    }

    public void add(int opcode) {
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
            case 0x87: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0x80: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0x81: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0x82: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0x83: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0x84: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0x85: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0x86: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xC6: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to add(int opcode) . ", opcode));
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
    public void adc(int opcode) {
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
            case 0x8F: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0x88: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0x89: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0x8A: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0x8B: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0x8C: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0x8D: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0x8E: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xCE: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to adc(int opcode) . ", opcode));
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
    public void add16(int opcode) {
        /*  3.3.4.1. ADD HL,n
            Description:
               Add n to HL.
            Use with:
               n = BC,DE,HL,SP
            Flags affected:
               Z - Not affected.
               N - Reset.
               H - Set if carry from bit 11.
               C - Set if carry from bit 15.
            Opcodes:
            Instruction Parameters Opcode Cycles
             ADD         HL,BC      09      8
             ADD         HL,DE      19      8
             ADD         HL,HL      29      8
             ADD         HL,SP      39      8

             Flags affected:
               Z - Reset.
               N - Reset.
               H - Set or reset according to operation.
               C - Set or reset according to operation.
             ADD         SP,#       E8      16
        */
        int value;
        Register destReg = null;
        switch (opcode) {
            case 0x09: value = readCombinedRegisters(registerB, registerC); lastInstructionCycles = 8; break;
            case 0x19: value = readCombinedRegisters(registerD, registerE); lastInstructionCycles = 8; break;
            case 0x29: value = readCombinedRegisters(registerH, registerL); lastInstructionCycles = 8; break;
            case 0x39: value = registerSP.read(); lastInstructionCycles = 8; break;
            case 0xE8:
                value = mmu.readByte(registerPC.read()); registerPC.inc();
                if (value > 127) {
                    value = -((~value + 1) & 255); // 2's complement
                }
                lastInstructionCycles = 16;
                break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to add16(int opcode) . ", opcode));
                return;

        }

        // do the add
        if (opcode == 0xE8) {
            value += registerSP.read();

            // flags affected
            registerFlags.clearZ();
            registerFlags.clearN();
            if (value > 0b00001111_11111111) {
                registerFlags.setH();
            }
            if (value > 65535) {
                registerFlags.setC();
                value &= 0b11111111_11111111;
            }

            // save result
            registerSP.write(value);
        }
        else {
            value += readCombinedRegisters(registerH, registerL);

            // flags affected
            registerFlags.clearN();
            if (value > 0b00001111_11111111) {
                registerFlags.setH();
            }
            if (value > 65535) {
                registerFlags.setC();
                value &= 0b11111111_11111111;
            }

            // save result
            writeCombinedRegisters(registerH, registerL, value);
        }
    }

    public void sub(int opcode) {
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
        int oldValue = registerA.read();
        switch (opcode) {
            case 0x97: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0x90: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0x91: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0x92: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0x93: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0x94: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0x95: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0x96: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xD6: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to sub(int opcode) . ", opcode));
                return;
        }

        // do the subtraction
        int result = registerA.read() - second;

        // flags affected
        registerFlags.setN();
        if (result == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }

        if (second > registerA.read()) {
            registerFlags.clearC();
        }
        else {
            registerFlags.setC();
        }
        if ((oldValue & 0b0000_1111) < (0b0000_1111 & second)) {
            registerFlags.setH();
        }
        else {
            registerFlags.clearH();
        }

        // account for overflow
        if (result > 255 || result < 0) {
            result &= 255;
        }

        // save result
        load(registerA, result);
    }
    public void sbc(int opcode)  {
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
        int oldValue = registerA.read();
        switch (opcode) {
            case 0x9F: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0x98: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0x99: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0x9A: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0x9B: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0x9C: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0x9D: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0x9E: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to sbc(int opcode) . ", opcode));
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
        else {
            registerFlags.clearZ();
        }
        if (result < 0) {
            registerFlags.setC();
            result &= 0b1111_1111;
        }
        else {
            registerFlags.clearC();
        }
        if ((oldValue & 0b0000_1111) < (0b0000_1111 & second)) {
            registerFlags.setH();
        }
        else {
            registerFlags.clearH();
        }

        // account for overflow
        if (result > 255 || result < 0) {
            result &= 255;
        }

        // save result
        load(registerA, result);
    }

    public void and(int opcode)  {
        /* 3.3.3.5 AND n
            Description:
               log.logDebugically AND n with A, result in A.
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
            case 0xA7: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0xA0: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0xA1: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0xA2: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0xA3: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0xA4: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0xA5: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0xA6: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xE6: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to and(int opcode) . ", opcode));
                return;
        }

        // do the and
        load(registerA, registerA.read() & second);

        // flags affected
        if (registerA.read() == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }
        registerFlags.clearN();
        registerFlags.setH();
        registerFlags.clearC();
    }
    public void or(int opcode)  {
        /* 3.3.3.6. OR n
            Description:
               log.logDebugical OR n with register A, result in A.
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
            case 0xB7: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0xB0: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0xB1: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0xB2: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0xB3: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0xB4: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0xB5: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0xB6: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xF6: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to or(int opcode) . ", opcode));
                return;
        }

        // do the or
        load(registerA, registerA.read() | second);

        // flags affected
        if (registerA.read() == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.clearC();
    }
    public void xor(int opcode)  {
        /* 3.3.3.7 XOR n
            Description:
               log.logDebugical exclusive OR n with register A, result in A.
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
            case 0xAF: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0xA8: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0xA9: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0xAA: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0xAB: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0xAC: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0xAD: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0xAE: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xEE: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to xor(int opcode) . ", opcode));
                return;
        }

        // do the xor
        load(registerA, registerA.read() ^ second);

        // flags affected
        if (registerA.read() == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.clearC();
    }
    public void cp(int opcode)  {
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
            case 0xBF: second = registerA.read(); lastInstructionCycles = 4; break;
            case 0xB8: second = registerB.read(); lastInstructionCycles = 4; break;
            case 0xB9: second = registerC.read(); lastInstructionCycles = 4; break;
            case 0xBA: second = registerD.read(); lastInstructionCycles = 4; break;
            case 0xBB: second = registerE.read(); lastInstructionCycles = 4; break;
            case 0xBC: second = registerH.read(); lastInstructionCycles = 4; break;
            case 0xBD: second = registerL.read(); lastInstructionCycles = 4; break;
            case 0xBE: second = mmu.readByte(readCombinedRegisters(registerH, registerL)); lastInstructionCycles = 8; break;
            case 0xFE: second = mmu.readByte(registerPC.read()); registerPC.inc(); lastInstructionCycles = 8; break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to cp(int opcode) . ", opcode));
                return;
        }

        // do the subtraction
        int result = registerA.read() - second;

        // flags affected
        registerFlags.setN();
        if (result == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }

        if (second > registerA.read()) {
            registerFlags.setC();
        }
        else {
            registerFlags.clearC();
        }

        if ((registerA.read() & 0b0000_1111) < (0b0000_1111 & second)) {
            registerFlags.setH();
        }
        else {
            registerFlags.clearH();
        }

        // throw away result :-)
    }

    public void inc(int opcode)  {
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
            case 0x3C: registerA.inc(); value = registerA.read(); lastInstructionCycles = 4; break;
            case 0x04: registerB.inc(); value = registerB.read(); lastInstructionCycles = 4; break;
            case 0x0C: registerC.inc(); value = registerC.read(); lastInstructionCycles = 4; break;
            case 0x14: registerD.inc(); value = registerD.read(); lastInstructionCycles = 4; break;
            case 0x1C: registerE.inc(); value = registerE.read(); lastInstructionCycles = 4; break;
            case 0x24: registerH.inc(); value = registerH.read(); lastInstructionCycles = 4; break;
            case 0x2C: registerL.inc(); value = registerL.read(); lastInstructionCycles = 4; break;
            case 0x34:
                int address = readCombinedRegisters(registerH, registerL);
                value = mmu.readByte(address);
                value += 1;
                mmu.writeByte(address, value);
                lastInstructionCycles = 12;
                break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to inc(int opcode) . ", opcode));
                return;
        }

        // flags affected
        if (value == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }
        registerFlags.clearN();
        if ((value & 0b0000_1111) == 0b0000_1111) {
            registerFlags.setH();
        }
        else {
            registerFlags.clearH();
        }


    }
    public void inc16(int opcode)  {
        /* 3.3.4.3. INC nn
            Description:
               Increment register nn.
            Use with:
               nn = BC,DE,HL,SP
            Flags affected:
               None.
            Opcodes:
            Instruction Parameters Opcode Cycles
             INC            BC      03      8
             INC            DE      13      8
             INC            HL      23      8
             INC            SP      33      8
        */
        int value;
        Register highReg = null;
        Register lowReg = null;
        switch (opcode) {
            case 0x03:
                highReg = registerB;
                lowReg = registerC;
                break;
            case 0x13:
                highReg = registerD;
                lowReg = registerE;
                break;
            case 0x23:
                highReg = registerH;
                lowReg = registerL;
                break;
            case 0x33:
                registerSP.inc();
                return;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to inc16(int opcode) . ", opcode));
                return;
        }
        value = readCombinedRegisters(highReg, lowReg);
        value += 1;
        writeCombinedRegisters(highReg, lowReg, value);
        lastInstructionCycles = 8;
    }

    public void dec(int opcode)  {
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
        int value, oldValue;
        switch (opcode) {
            case 0x3D: oldValue = registerA.read(); registerA.dec(); value = registerA.read(); lastInstructionCycles = 4; break;
            case 0x05: oldValue = registerB.read(); registerB.dec(); value = registerB.read(); lastInstructionCycles = 4; break;
            case 0x0D: oldValue = registerC.read(); registerC.dec(); value = registerC.read(); lastInstructionCycles = 4; break;
            case 0x15: oldValue = registerD.read(); registerD.dec(); value = registerD.read(); lastInstructionCycles = 4; break;
            case 0x1D: oldValue = registerE.read(); registerE.dec(); value = registerE.read(); lastInstructionCycles = 4; break;
            case 0x25: oldValue = registerH.read(); registerH.dec(); value = registerH.read(); lastInstructionCycles = 4; break;
            case 0x2D: oldValue = registerL.read(); registerL.dec(); value = registerL.read(); lastInstructionCycles = 4; break;
            case 0x35:
                int address = readCombinedRegisters(registerH, registerL);
                value = mmu.readByte(address);
                oldValue = value;
                value -= 1;
                mmu.writeByte(address, value);
                lastInstructionCycles = 12;
                break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to dec(int opcode) . ", opcode));
                return;
        }

        // flags affected
        if (value == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }

        registerFlags.setN();

        if ((oldValue & 0b0000_1111) < 1) {
            registerFlags.setH();
        }
        else {
            registerFlags.clearH();
        }
    }
    public void dec16(int opcode)  {
        /* 3.3.4.4. DEC nn
            Description:
               Decrement register nn.
            Use with:
               nn = BC,DE,HL,SP
            Flags affected:
               None.
            Opcodes:
            Instruction Parameters Opcode Cycles
             DEC            BC      0B      8
             DEC            DE      1B      8
             DEC            HL      2B      8
             DEC            SP      3B      8
        */
        int value;
        Register highReg = null;
        Register lowReg = null;
        switch (opcode) {
            case 0x0B:
                highReg = registerB;
                lowReg = registerC;
                break;
            case 0x1B:
                highReg = registerD;
                lowReg = registerE;
                break;
            case 0x2B:
                highReg = registerH;
                lowReg = registerL;
                break;
            case 0x3B:
                registerSP.dec();
                return;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to dec16(int opcode) . ", opcode));
                return;
        }
        value = readCombinedRegisters(highReg, lowReg);
        value -= 1;
        writeCombinedRegisters(highReg, lowReg, value);
        lastInstructionCycles = 8;
    }

    private int swapHelper(int value) {
        int upper = value & 0b1111_0000;
        int lower = value & 0b0000_1111;
        lower <<= 4;
        upper >>= 4;
        value = 0;
        value |= upper;
        value |= lower;
        return value;
    }
    public void swap(int opcode)  {
        /* 3.3.5.1. SWAP n
            Description:
               Swap upper & lower nibles of n.
            Use with:
               n = A,B,C,D,E,H,L,(HL)
            Flags affected:
               Z - Set if result is zero.
               N - Reset.
               H - Reset.
               C - Reset.
            Opcodes:
            Instruction Parameters Opcode Cycles
               SWAP         A       CB 37   8
               SWAP         B       CB 30   8
               SWAP         C       CB 31   8
               SWAP         D       CB 32   8
               SWAP         E       CB 33   8
               SWAP         H       CB 34   8
               SWAP         L       CB 35   8
               SWAP         (HL)    CB 36   16
         */
        int value;
        switch (opcode) {
            case 0xCB37: value = swapHelper(registerA.read()); registerA.write(value); lastInstructionCycles = 8;break;
            case 0xCB30: value = swapHelper(registerB.read()); registerB.write(value); lastInstructionCycles = 8;break;
            case 0xCB31: value = swapHelper(registerC.read()); registerC.write(value); lastInstructionCycles = 8;break;
            case 0xCB32: value = swapHelper(registerD.read()); registerD.write(value); lastInstructionCycles = 8;break;
            case 0xCB33: value = swapHelper(registerE.read()); registerE.write(value); lastInstructionCycles = 8;break;
            case 0xCB34: value = swapHelper(registerH.read()); registerH.write(value); lastInstructionCycles = 8;break;
            case 0xCB35: value = swapHelper(registerL.read()); registerL.write(value); lastInstructionCycles = 8;break;
            case 0xCB36:
                int address = readCombinedRegisters(registerH, registerL);
                value = swapHelper(mmu.readByte(address));
                mmu.writeByte(address, value);
                lastInstructionCycles = 16;
                break;
            default:
                log.debug(String.format("log.error: Opcode %05X does not belong to swap(int opcode) . ", opcode));
                return;
        }

        // flags affected
        if (value == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.clearC();
    }

    public void daa(int opcode) {
        if (opcode != 0x27) {
            log.debug("Why are we even in daa() if opcode " + opcode + " isn't 0x27?");
            return;
        }
        /* 3.3.5.2. DAA
            Description:
             Decimal adjust register A.
             This instruction adjusts register A so that the
             correct representation of Binary Coded Decimal (BCD)
             is obtained.
            Flags affected:
             Z - Set if register A is zero.
             N - Not affected.
             H - Reset.
             C - Set or reset according to operation.

            Opcodes:
            Instruction Parameters Opcode Cycles
                DAA         -/-     27      4
        */
        // The above doesn't explain much. Going with this implementation:
        // http://z80-heaven.wikidot.com/instructions-set:daa
        /*
           When this instruction is executed, the A register is BCD corrected using the contents of the flags.
           The exact process is the following: if the least significant four bits of A contain a non-BCD digit
           (i. e. it is greater than 9) or the H flag is set, then $06 is added to the register. Then the four
           most significant bits are checked. If this more significant digit also happens to be greater than 9
           or the C flag is set, then $60 is added. ( $ = hex )
         */
        int value = registerA.read();
        int lower = value & 0b0000_1111;
        if (lower > 9 || registerFlags.readH()) {
            value += 0x06;
        }
        int upper = value & 0b1111_0000;
        if (upper > 9 || registerFlags.readC()) {
            value += 0x60;
        }

        // flags affected
        if (value > 255) {
            registerFlags.setC();
            value &= 255;
        }
        if (value == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearH();

        // save result
        registerA.write(value);
        lastInstructionCycles = 4;
    }

    public void cpl(int opcode) {
        /* 3.3.5.3. CPL
            Description:
             Complement A register. (Flip all bits.)
            Flags affected:
             Z - Not affected.
             N - Set.
             H - Set.
             C - Not affected.
            Opcodes:
            Instruction Parameters Opcode Cycles
             CPL           -/-      2F      4
         */
        if (opcode != 0x2F) {
            log.debug("Why are we even in cpl() if opcode " + opcode + " isn't 0x2F?");
            return;
        }

        // do the flip
        registerA.write( ~ registerA.read() );

        // flags affected
        registerFlags.setN();
        registerFlags.setH();

        lastInstructionCycles = 4;
    }
    public void ccf(int opcode) {
        /*
            3.3.5.4. CCF
            Description:
             Complement carry flag.
             If C flag is set, then reset it.
             If C flag is reset, then set it.
            Flags affected:
             Z - Not affected.
             N - Reset.
             H - Reset.
             C - Complemented.
            Opcodes:
            Instruction Parameters Opcode Cycles
             CCF -/- 3F 4
         */
        if (opcode != 0x3F) {
            log.debug("Why are we even in ccf() if opcode " + opcode + " isn't 0x3F?");
            return;
        }

        registerFlags.clearN();
        registerFlags.clearH();

        if (registerFlags.readC())
            registerFlags.clearC();
        else
            registerFlags.setC();

        lastInstructionCycles = 4;
    }
    public void scf(int opcode) {
        /*
            3.3.5.5. SCF
            Description:
             Set Carry flag.
            Flags affected:
             Z - Not affected.
             N - Reset.
             H - Reset.
             C - Set.
            Opcodes:
            Instruction Parameters Opcode Cycles
             SCF            -/-      37     4
         */
        if (opcode != 0x37) {
            log.debug("Why are we even in scf() if opcode " + opcode + " isn't 0x37?");
            return;
        }

        registerFlags.clearN();
        registerFlags.clearH();
        registerFlags.setC();

        lastInstructionCycles = 4;
    }

    public void nopHaltStop(int opcode) {
        if (opcode == 0x00) {
            // NOP - 4 cycles, 0x00 opcode
            log.debug("NOP - No operation.");
            lastInstructionCycles = 4;
        }
        else if (opcode == 0x76) {
            // HALT - power down CPU until interrupt occurs. Opcode 0x76. 4 cycles.
            log.debug("HALT");
            /*while (true) {
                // TODO - wait until an interrupt happens then break
            }*/
            lastInstructionCycles = 4;
        }
        else if (opcode == 0x1000) {
            // STOP - halt cpu and lcd display until button pressed. Opcode 0x1000. 4 cycles.
            log.debug("STOP");
            /*while (true) {
                // TODO - wait until button is pressed then break
            }*/
            lastInstructionCycles = 4;
        }
        else {
            log.debug("We're in nopHaltStop() but opcode " + opcode + " isn't 0x00, 0x76, or 0x1000");
        }
    }
    public void diEi(int opcode) {
        /*
            9. DI
            Description:
             This instruction disables interrupts but not
             immediately. Interrupts are disabled after
             instruction after DI is executed.
            Flags affected:
             None.
            Opcodes:
            Instruction Parameters Opcode Cycles
             DI -/- F3 4


            10. EI
            Description:
             Enable interrupts. This intruction enables interrupts
             but not immediately. Interrupts are enabled after
             instruction after EI is executed.
            Flags affected:
             None.
            Opcodes:
            Instruction Parameters Opcode Cycles
            EI -/- FB 4
         */
        if (opcode == 0xF3) {
            pendingInterruptDisable = true;
            lastInstructionCycles = 4;
        }
        else if (opcode == 0xFB) {
            pendingInterruptEnable = true;
            lastInstructionCycles = 4;
        }
        else {
            log.debug("Why are we even in diEi() if opcode " + opcode + " isn't 0xF3 or 0xFB?");
        }
    }

    public void rlca(int opcode) {
        /*
        1. RLCA
        Description:
         Rotate A left. Old bit 7 to Carry flag.
        Flags affected:
        Z - Set if result is zero.
         N - Reset.
         H - Reset.
         C - Contains old bit 7 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
         RLCA -/- 07 4
         */
        if (opcode != 0x07) {
            log.debug("Wrong opcode handler. " + opcode + " shouldn't be handled by rlca()");
            return;
        }

        // perform operation
        int regA = registerA.read();
        int result = (regA << 1) | (regA >> 7);
        registerA.write(result);
        boolean bit7 = ((registerA.read() & 0b10000000) >> 7) == 1;

        // flags affected
        if (result == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        if (bit7) {
            registerFlags.setC();
        }
        else {
            registerFlags.clearC();
        }

        lastInstructionCycles = 4;
    }
    public void rla(int opcode) {
        /*
        2. RLA
            Description:
                Rotate A left through Carry flag.
            Flags affected:
                Z - Set if result is zero.
                N - Reset.
                H - Reset.
                C - Contains old bit 7 data.
            Opcodes:
            Instruction Parameters Opcode Cycles
            RLA         -/-         17      4

            NOTE according to https://github.com/simias/gb-rs,
            the above is wrong and the below implementation is right.
        */
        if (opcode != 0x17) {
            log.error("opcode 0x17 doesn't belong in rla()");
            return;
        }

        int result = registerA.read();

        boolean newcarry = (result >> 7) != 0;
        int oldcarry = registerFlags.readC() ? 1 : 0;

        registerA.write(((result << 1) | oldcarry) & 0b1111_1111);

        if (newcarry) {
            registerFlags.setC();
        }
        else {
            registerFlags.clearC();
        }
        registerFlags.clearZ();
        registerFlags.clearH();
        registerFlags.clearN();

        lastInstructionCycles = 4;
    }

    public void rrca(int opcode) {
        /*
        3. RRCA
        Description:
            Rotate A right. Old bit 0 to Carry flag.
        Flags affected:
            Z - Set if result is zero.
            N - Reset.
            H - Reset.
            C - Contains old bit 0 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RRCA        -/-         0F      4
        */
        if (opcode != 0x0F) {
            log.error("opcode 0x0F doesn't belong in rrca()");
            return;
        }

        int value = registerA.read();
        int oldbit0 = value & 0b00000001;
        value >>= 1;
        registerA.write(value | (oldbit0 << 7));

        // flags affected
        if (oldbit0 != 0) registerFlags.setC();
        else              registerFlags.clearC();
        registerFlags.clearH();
        registerFlags.clearN();
        registerFlags.clearZ();

        lastInstructionCycles = 4;
    }
    public void rra(int opcode) {
        /*
        4. RRA
        Description:
            Rotate A right through Carry flag.
        Flags affected:
            Z - Set if result is zero.
            N - Reset.
            H - Reset.
            C - Contains old bit 0 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RRA -/- 1F 4
        */
        if (opcode != 0x1F) {
            log.error("opcode 0x1F doesn't belong in rra()");
            return;
        }

        int value = registerA.read();
        int newcarry = value & 0b00000001;
        int oldcarry = registerFlags.readC() ? 1 : 0;
        registerA.write((value >> 1) | (oldcarry << 7));

        if (newcarry == 1) registerFlags.setC();
        else               registerFlags.clearC();
        registerFlags.clearZ();
        registerFlags.clearH();
        registerFlags.clearN();

        lastInstructionCycles = 4;
    }

    public void rlc(int opcode)  {
        /*
        5. RLC n
        Description:
            Rotate n left. Old bit 7 to Carry flag.
        Use with:
            n = A,B,C,D,E,H,L,(HL)
        Flags affected:
            Z - Set if result is zero.
            N - Reset.
            H - Reset.
            C - Contains old bit 7 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RLC         A           CB 07   8
        RLC         B           CB 00   8
        RLC         C           CB 01   8
        RLC         D           CB 02   8
        RLC         E           CB 03   8
        RLC         H           CB 04   8
        RLC         L           CB 05   8
        RLC         (HL)        CB 06   16
        */

        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB07: value = registerA.read(); break;
            case 0xCB00: value = registerB.read(); break;
            case 0xCB01: value = registerC.read(); break;
            case 0xCB02: value = registerD.read(); break;
            case 0xCB03: value = registerE.read(); break;
            case 0xCB04: value = registerH.read(); break;
            case 0xCB05: value = registerL.read(); break;
            case 0xCB06: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
            default:
                log.error("opcode " + opcode + " doesn't belong in rlc()");
                return;
        }

        // perform operation
        int result = (value << 1) | (value >> 7);
        boolean bit7 = ((registerA.read() & 0b10000000) >> 7) == 1;

        // write result
        switch(opcode) {
            case 0xCB07: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB00: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB01: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB02: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB03: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB04: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB05: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB06: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (bit7) registerFlags.setC();
        else      registerFlags.clearC();
        registerFlags.clearH();
        registerFlags.clearN();
        registerFlags.clearZ();
    }
    public void rl(int opcode)  {
        /*
        6. RL n
        Description:
            Rotate n left through Carry flag.
        Use with:
            n = A,B,C,D,E,H,L,(HL)
        Flags affected:
            Z - Set if result is zero.
            N - Reset.
            H - Reset.
            C - Contains old bit 7 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RL              A       CB 17   8
        RL              B       CB 10   8
        RL              C       CB 11   8
        RL              D       CB 12   8
        RL              E       CB 13   8
        RL              H       CB 14   8
        RL              L       CB 15   8
        RL              (HL)    CB 16   16
        */
        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB17: value = registerA.read(); break;
            case 0xCB10: value = registerB.read(); break;
            case 0xCB11: value = registerC.read(); break;
            case 0xCB12: value = registerD.read(); break;
            case 0xCB13: value = registerE.read(); break;
            case 0xCB14: value = registerH.read(); break;
            case 0xCB15: value = registerL.read(); break;
            case 0xCB16: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
        }

        // perform operation
        boolean bit7 = ((value & 0b10000000) > 0);
        boolean oldcarry = registerFlags.readC();
        int result = (value << 1) & 0b1111_1111;
        result |= oldcarry ? 1 : 0;

        // write result
        switch(opcode) {
            case 0xCB17: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB10: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB11: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB12: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB13: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB14: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB15: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB16: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (result == 0) {
            registerFlags.setZ();
        }
        else {
            registerFlags.clearZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
        if (bit7) {
            registerFlags.setC();
        }
        else {
            registerFlags.clearC();
        }
    }

    public void rrc(int opcode)  {
        /*
        7. RRC n
        Description:
        Rotate n right. Old bit 0 to Carry flag.
        Use with:
        n = A,B,C,D,E,H,L,(HL)
        Flags affected:
        Z - Set if result is zero.
        N - Reset.
        H - Reset.
        C - Contains old bit 0 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RRC A CB 0F 8
        RRC B CB 08 8
        RRC C CB 09 8
        RRC D CB 0A 8
        RRC E CB 0B 8
        RRC H CB 0C 8
        RRC L CB 0D 8
        RRC (HL) CB 0E 16
        */
        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB0F: value = registerA.read(); break;
            case 0xCB08: value = registerB.read(); break;
            case 0xCB09: value = registerC.read(); break;
            case 0xCB0A: value = registerD.read(); break;
            case 0xCB0B: value = registerE.read(); break;
            case 0xCB0C: value = registerH.read(); break;
            case 0xCB0D: value = registerL.read(); break;
            case 0xCB0E: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
        }

        // perform operation
        int oldbit0 = value & 0b00000001;
        value >>= 1;
        int result = value | (oldbit0 << 7);

        // write result
        switch(opcode) {
            case 0xCB0F: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB08: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB09: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB0A: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB0B: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB0C: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB0D: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB0E: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (result == 0) {
            registerFlags.setZ();
        }
        registerFlags.clearN();
        registerFlags.clearH();
    }
    public void rr(int opcode)  {
        /*
        8. RR n
        Description:
        Rotate n right through Carry flag.
        Use with:
        n = A,B,C,D,E,H,L,(HL)
        Flags affected:
        Z - Set if result is zero.
        N - Reset.
        H - Reset.
        C - Contains old bit 0 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RR A CB 1F 8
        RR B CB 18 8
        RR C CB 19 8
        RR D CB 1A 8
        RR E CB 1B 8
        RR H CB 1C 8
        RR L CB 1D 8
        RR (HL) CB 1E 16
        */
        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB1F: value = registerA.read(); break;
            case 0xCB18: value = registerB.read(); break;
            case 0xCB19: value = registerC.read(); break;
            case 0xCB1A: value = registerD.read(); break;
            case 0xCB1B: value = registerE.read(); break;
            case 0xCB1C: value = registerH.read(); break;
            case 0xCB1D: value = registerL.read(); break;
            case 0xCB1E: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
        }

        // perform operation
        int newcarry = value & 0b00000001;
        int oldcarry = registerFlags.readC() ? 1 : 0;
        int result = (value >> 1) | (oldcarry << 7);

        // write result
        switch(opcode) {
            case 0xCB1F: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB18: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB19: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB1A: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB1B: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB1C: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB1D: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB1E: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (newcarry == 1) registerFlags.setC();
        else               registerFlags.clearC();
        if (result == 0)   registerFlags.setZ();
        else               registerFlags.clearZ();
        registerFlags.clearN();
        registerFlags.clearH();
    }

    public void sla(int opcode)  {
        /*
        9. SLA n
        Description:
        Shift n left into Carry. LSB of n set to 0.
        Use with:
        n = A,B,C,D,E,H,L,(HL)
        Flags affected:
        Z - Set if result is zero.
        N - Reset.
        H - Reset.
        C - Contains old bit 7 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        SLA A CB 27 8
        SLA B CB 20 8
        SLA C CB 21 8
        SLA D CB 22 8
        SLA E CB 23 8
        SLA H CB 24 8
        SLA L CB 25 8
        SLA (HL) CB 26 16
        */
        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB27: value = registerA.read(); break;
            case 0xCB20: value = registerB.read(); break;
            case 0xCB21: value = registerC.read(); break;
            case 0xCB22: value = registerD.read(); break;
            case 0xCB23: value = registerE.read(); break;
            case 0xCB24: value = registerH.read(); break;
            case 0xCB25: value = registerL.read(); break;
            case 0xCB26: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
        }

        // perform operation
        boolean carry = (((value & 0b10000000) >> 8) == 1);
        int result = value << 1;

        // store result
        switch(opcode) {
            case 0xCB27: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB20: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB21: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB22: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB23: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB24: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB25: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB26: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (result == 0) registerFlags.setZ();
        else             registerFlags.clearZ();
        if (carry)       registerFlags.setC();
        else             registerFlags.clearC();
        registerFlags.clearN();
        registerFlags.clearH();
    }
    public void sra(int opcode)  {
        /*
        10. SRA n
        Description:
            Shift n right into Carry. MSB doesn't change.
        Use with:
            n = A,B,C,D,E,H,L,(HL)
        Flags affected:
            Z - Set if result is zero.
            N - Reset.
            H - Reset.
            C - Contains old bit 0 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        SRA A    CB 2F 8
        SRA B    CB 28 8
        SRA C    CB 29 8
        SRA D    CB 2A 8
        SRA E    CB 2B 8
        SRA H    CB 2C 8
        SRA L    CB 2D 8
        SRA (HL) CB 2E 16
        */
        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB2F: value = registerA.read(); break;
            case 0xCB28: value = registerB.read(); break;
            case 0xCB29: value = registerC.read(); break;
            case 0xCB2A: value = registerD.read(); break;
            case 0xCB2B: value = registerE.read(); break;
            case 0xCB2C: value = registerH.read(); break;
            case 0xCB2D: value = registerL.read(); break;
            case 0xCB2E: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
        }

        // perform the operation
        boolean carry = ( (value & 1) != 0 );
        int result = (value >> 1) | (value & 0b10000000);

        // store result
        switch(opcode) {
            case 0xCB2F: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB28: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB29: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB2A: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB2B: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB2C: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB2D: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB2E: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (carry)       registerFlags.setC();
        else             registerFlags.clearC();
        if (result == 0) registerFlags.setZ();
        else             registerFlags.clearZ();
        registerFlags.clearH();
        registerFlags.clearN();
    }
    public void srl(int opcode)  {
        /*
        11. SRL n
        Description:
        Shift n right into Carry. MSB set to 0.
        Use with:
        n = A,B,C,D,E,H,L,(HL)
        Flags affected:
        Z - Set if result is zero.
        N - Reset.
        H - Reset.
        C - Contains old bit 0 data.
        Opcodes:
        Instruction Parameters Opcode Cycles
        SRL A CB 3F 8
        SRL B CB 38 8
        SRL C CB 39 8
        SRL D CB 3A 8
        SRL E CB 3B 8
        SRL H CB 3C 8
        SRL L CB 3D 8
        SRL (HL) CB 3E 16
        */
        // read value
        int value = 0;
        switch(opcode) {
            case 0xCB3F: value = registerA.read(); break;
            case 0xCB38: value = registerB.read(); break;
            case 0xCB39: value = registerC.read(); break;
            case 0xCB3A: value = registerD.read(); break;
            case 0xCB3B: value = registerE.read(); break;
            case 0xCB3C: value = registerH.read(); break;
            case 0xCB3D: value = registerL.read(); break;
            case 0xCB3E: value = mmu.readByte(readCombinedRegisters(registerH, registerL)); break;
        }

        // perform the operation
        boolean carry = (value & 0b00000001) != 0;
        int result = value >> 1;

        // store result
        switch(opcode) {
            case 0xCB3F: registerA.write(result); lastInstructionCycles = 8; break;
            case 0xCB38: registerB.write(result); lastInstructionCycles = 8; break;
            case 0xCB39: registerC.write(result); lastInstructionCycles = 8; break;
            case 0xCB3A: registerD.write(result); lastInstructionCycles = 8; break;
            case 0xCB3B: registerE.write(result); lastInstructionCycles = 8; break;
            case 0xCB3C: registerH.write(result); lastInstructionCycles = 8; break;
            case 0xCB3D: registerL.write(result); lastInstructionCycles = 8; break;
            case 0xCB3E: mmu.writeByte(readCombinedRegisters(registerH, registerL), result); lastInstructionCycles = 16; break;
        }

        // flags affected
        if (result == 0) registerFlags.setZ();
        else             registerFlags.clearZ();
        if (carry)       registerFlags.setC();
        else             registerFlags.clearC();
        registerFlags.clearN();
        registerFlags.clearH();
    }

    private int cbHelperRead(int opcode) {
        // see which register we'll use by looking only at
        // the least significant hex digit (0x000F, or 0b00001111 mask)
        int value = 0;
        switch(opcode & 0b00001111) {
            case 0x0: case 0x8:
                value = registerB.read(); lastInstructionCycles = 8; break;
            case 0x1: case 0x9:
                value = registerC.read(); lastInstructionCycles = 8; break;
            case 0x2: case 0xA:
                value = registerD.read(); lastInstructionCycles = 8; break;
            case 0x3: case 0xB:
                value = registerE.read(); lastInstructionCycles = 8; break;
            case 0x4: case 0xC:
                value = registerH.read(); lastInstructionCycles = 8; break;
            case 0x5: case 0xD:
                value = registerL.read(); lastInstructionCycles = 8; break;
            case 0x6: case 0xE:
                value = mmu.readByte(readCombinedRegisters(registerH, registerL));
                lastInstructionCycles = 16; break;
            case 0x7: case 0xF:
                value = registerA.read(); lastInstructionCycles = 8; break;
        }

        return value;
    }
    private void cbHelperWrite(int opcode, int value)  {
        // see which register we'll use by looking only at the least significant
        // hex digit (0x000F or 0b00001111 mask)
        switch(opcode & 0b00001111) {
            case 0x0: case 0x8:
                registerB.write(value); lastInstructionCycles = 8; break;
            case 0x1: case 0x9:
                registerC.write(value); lastInstructionCycles = 8; break;
            case 0x2: case 0xA:
                registerD.write(value); lastInstructionCycles = 8; break;
            case 0x3: case 0xB:
                registerE.write(value); lastInstructionCycles = 8; break;
            case 0x4: case 0xC:
                registerH.write(value); lastInstructionCycles = 8; break;
            case 0x5: case 0xD:
                registerL.write(value); lastInstructionCycles = 8; break;
            case 0x6: case 0xE:
                mmu.writeByte(readCombinedRegisters(registerH, registerL), value);
                lastInstructionCycles = 16;
                break;
            case 0x7: case 0xF:
                registerA.write(value); lastInstructionCycles = 8; break;
        }
    }
    public void bit(int opcode)  {
        // this is going to be an interesting one....
        // starting at opcode 0xCB4X where X is:
        //    opcode:     0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  0
        //    register:   B  C  D  E  H  L mHL A  B  C  D  E  H  L mHL A  B
        //    bit:        0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  2
        //    ... and so on.
        // Flags affected:
        //    Z - Set if bit b of register r is 0.
        //    N - Reset.
        //    H - Set.
        //    C - Not affected.
        // Timing:  any CPU register is 8 cycles. Memory location is 16 cycles.
        //          the cycles timing is handled in the helper functions
        ////////////////////////////////////////////////////////////////////////

        // validate opcode actually belongs in this function:
        if (opcode < 0xCB40 || opcode > 0xCB7F) {
            log.error("Opcode " + opcode + " doesn't belong in bit()");
            return;
        }

        // get the value we'll be looking at
        int value = cbHelperRead(opcode);

        // next let's grab the bit index
        int bitIndex = (opcode - 0xCB40) / 8;

        // read the bit at `bitindex` of the value we found earlier:
        boolean bitValue = ((value >> bitIndex) & 1) == 1;

        // flags affected
        if (bitValue) registerFlags.clearZ();
        else          registerFlags.setZ();
        registerFlags.setH();
        registerFlags.clearN();
    }
    public void res(int opcode)  {
        // see bit function above. This works the same way except
        // instead of reading the bit, it just sets it to 0 and affects
        // no flags. Starts at 0xCB80

        // validate opcode actually belongs in this function:
        if (opcode < 0xCB80 || opcode > 0xCBBF) {
            log.error("Opcode " + opcode + " doesn't belong in res()");
            return;
        }

        // get the value we'll be looking at
        int value = cbHelperRead(opcode);

        // next let's grab the bit index
        int bitIndex = (opcode - 0xCB80) / 8;
        int mask = 1 << bitIndex; // shift 1 to it's spot based on the index
        mask = ~mask; // negate it so every bit is 1 except for the one we're clearing

        // now let's set it to 0.
        value = (value & mask); // AND the value with the mask in order to clear the specified bit.

        // store result
        cbHelperWrite(opcode, value);
    }
    public void set(int opcode)  {
        // see bit instruction above. This one works the same way except
        // instead of reading the bit, it will just set the bit in question to 1
        // with no flags affected. Starts at 0xCBC0 and ends at 0xCC00.

        // validate opcode actually belongs in this function:
        if (opcode < 0xCBC0 || opcode > 0xCBFF) {
            log.error("Opcode " + opcode + " doesn't belong in set()");
            return;
        }

        // get the value we'll be looking at
        int value = cbHelperRead(opcode);

        // next let's grab the bit index
        int bitIndex = (opcode - 0xCBC0) / 8;
        int mask = 1 << bitIndex; // shift 1 to it's spot based on the index

        // now let's set it to 0.
        value = (value | mask); // AND the value with the mask in order to clear the specified bit.

        // store result
        cbHelperWrite(opcode, value);
    }

    public void jump(int opcode) {
        /*
        1. JP nn
        Description:
        Jump to address nn.
        Use with:
        nn = two byte immediate value. (LS byte first.)
        Opcodes:
        Instruction Parameters Opcode Cycles
        JP nn C3 12
        */
        if (opcode != 0xC3) {
            log.error("Opcode " + opcode + " doesn't belong in jump()");
            return;
        }

        int address = mmu.readByte(registerPC.read()); // least significant byte
        registerPC.inc();
        int temp = mmu.readByte(registerPC.read()); // most significant byte
        registerPC.inc();
        temp <<= 8;
        address |= temp;
        load(registerPC, address); // load it into PC so it will be executed next.
        lastInstructionCycles = 12;
    }
    public void jpcc(int opcode) {
        /*
        2. JP cc,nn
        Description:
        Jump to address n if following condition is true:
        cc = NZ, Jump if Z flag is reset.
        cc = Z, Jump if Z flag is set.
        cc = NC, Jump if C flag is reset.
        cc = C, Jump if C flag is set.
        Use with:
        nn = two byte immediate value. (LS byte first.)
        Opcodes:
        Instruction Parameters Opcode Cycles
        JP NZ,nn C2 12
        JP Z,nn CA 12
        JP NC,nn D2 12
        JP C,nn DA 12
        */
        if (opcode != 0xC2 &&
            opcode != 0xCA &&
            opcode != 0xD2 &&
            opcode != 0xDA) {
            log.error("Opcode " + opcode + " doesn't belong in jpcc()");
            return;
        }

        boolean condition = false;
        switch (opcode) {
            case 0xC2:
                condition = (!registerFlags.readZ());
                break;
            case 0xCA:
                condition = registerFlags.readZ();
                break;
            case 0xD2:
                condition = (!registerFlags.readC());
                break;
            case 0xDA:
                condition = registerFlags.readC();
                break;
        }

        int address = mmu.readByte(registerPC.read()); // least significant byte
        registerPC.inc();

        // if our condition for jumping is false, don't jump
        if (!condition) {
            return;
        }

        // else perform jump
        int temp = mmu.readByte(registerPC.read()); // most significant byte
        registerPC.inc();
        temp <<= 8;
        address |= temp;
        load(registerPC, address); // load it into PC so it will be executed next.
        lastInstructionCycles = 12;
    }
    public void jphl(int opcode)  {
        /*
        3. JP (HL)
        Description:
        Jump to address contained in HL.
        Opcodes:
        Instruction Parameters Opcode Cycles
        JP (HL) E9 4
        */
        if (opcode != 0xE9) {
            log.error("Opcode " + opcode + " doesn't belong in jphl()");
            return;
        }

        load(registerPC, mmu.readByte(readCombinedRegisters(registerH, registerL)));
        lastInstructionCycles = 4;
    }
    public void jr(int opcode) {
        /*
        4. JR n
        Description:
        Add n to current address and jump to it.
        Use with:
        n = one byte signed immediate value
        Opcodes:
        Instruction Parameters Opcode Cycles
        JR n 18 8
        */
        if (opcode != 0x18) {
            log.error("Opcode " + opcode + " doesn't belong in jr()");
            return;
        }

        int n = mmu.readByte(registerPC.read());
        registerPC.inc();
        if (n > 127) {
            n = -((~n + 1) & 255); // 2's complement
        }

        int address = registerPC.read();
        registerPC.inc();
        address += n;
        load(registerPC, address);
        lastInstructionCycles = 8;
    }
    public void jrcc(int opcode) {
        /*
        5. JR cc,n
        Description:
        If following condition is true then add n to current
        address and jump to it:
        Use with:
        n = one byte signed immediate value
        cc = NZ, Jump if Z flag is reset.
        cc = Z, Jump if Z flag is set.
        cc = NC, Jump if C flag is reset.
        cc = C, Jump if C flag is set.
        Opcodes:
        Instruction Parameters Opcode Cycles
        JR NZ,* 20 8
        JR Z,* 28 8
        JR NC,* 30 8
        JR C,* 38 8
        */
        if (opcode != 0x20 &&
            opcode != 0x28 &&
            opcode != 0x30 &&
            opcode != 0x38) {
            log.error("Opcode " + opcode + " doesn't belong in jrcc()");
            return;
        }

        boolean condition = false;
        switch (opcode) {
            case 0x20:
                condition = (!registerFlags.readZ());
                break;
            case 0x28:
                condition = registerFlags.readZ();
                break;
            case 0x30:
                condition = (!registerFlags.readC());
                break;
            case 0x38:
                condition = registerFlags.readC();
                break;
        }

        int n = mmu.readByte(registerPC.read());
        registerPC.inc();

        // if our condition for jumping is false, don't jump
        if (!condition) {
            return;
        }

        if (n > 127) {
            n = -((~n + 1) & 255); // 2's complement
        }
        int address = registerPC.read();
        address += n;
        load(registerPC, address);
        lastInstructionCycles = 8;
    }

    public void call(int opcode) {
        /*
        1. CALL nn
        Description:
        Push address of next instruction onto stack and then
        jump to address nn.
        Use with:
        nn = two byte immediate value. (LS byte first.)
        Opcodes:
        Instruction Parameters Opcode Cycles
        CALL nn CD 12
        */
        if (opcode != 0xCD) {
            log.error("Opcode " + opcode + " doesn't belong in call()");
            return;
        }


        // jump to address nn two byte immediate value. (LS byte first)
        int address = mmu.readByte(registerPC.read()); // least significant byte
        registerPC.inc();
        int temp = mmu.readByte(registerPC.read());    // most significant byte
        registerPC.inc();
        temp <<= 8;
        address |= temp;                              // combine

        // push address of next instruction onto stack.
        pushHelper(registerPC.read());

        load(registerPC, address);                    // jump to this address.
        lastInstructionCycles = 12;
    }
    public void callcc(int opcode) {
        /*2. CALL cc,nn
        Description:
        Call address n if following condition is true:
        cc = NZ, Call if Z flag is reset.
        cc = Z, Call if Z flag is set.
        cc = NC, Call if C flag is reset.
        cc = C, Call if C flag is set.
        Use with:
        nn = two byte immediate value. (LS byte first.)
        Opcodes:
        Instruction Parameters Opcode Cycles
        CALL NZ,nn C4 12
        CALL Z,nn CC 12
        CALL NC,nn D4 12
        CALL C,nn DC 12
        */
        if (opcode != 0xC4 &&
            opcode != 0xCC &&
            opcode != 0xD4 &&
            opcode != 0xDC) {
            log.error("Opcode " + opcode + " doesn't belong in callcc()");
            return;
        }

        boolean condition = false;
        switch (opcode) {
            case 0xC4:
                condition = (!registerFlags.readZ());
                break;
            case 0xCC:
                condition = registerFlags.readZ();
                break;
            case 0xD4:
                condition = (!registerFlags.readC());
                break;
            case 0xDC:
                condition = registerFlags.readC();
                break;
        }

        // jump to address nn two byte immediate value. (LS byte first)
        int address = mmu.readByte(registerPC.read()); // least significant byte
        registerPC.inc();

        // if our condition for jumping is false, don't jump
        if (!condition) {
            return;
        }

        int temp = mmu.readByte(registerPC.read());    // most significant byte
        temp <<= 8;
        address |= temp;                              // combine
        load(registerPC, address);                    // jump to this address.
        lastInstructionCycles = 12;
    }

    public void rst(int opcode) {
        /*
        1. RST n
        Description:
        Push present address onto stack.
        Jump to address $0000 + n.
        Use with:
        n = $00,$08,$10,$18,$20,$28,$30,$38
        Opcodes:
        Instruction Parameters Opcode Cycles
        RST 00H C7 32
        RST 08H CF 32
        RST 10H D7 32
        RST 18H DF 32
        RST 20H E7 32
        RST 28H EF 32
        RST 30H F7 32
        RST 38H FF 32
        */
        if (opcode != 0xC7 &&
            opcode != 0xCF &&
            opcode != 0xD7 &&
            opcode != 0xDF &&
            opcode != 0xE7 &&
            opcode != 0xEF &&
            opcode != 0xF7 &&
            opcode != 0xFF) {
            log.error("Opcode " + opcode + " doesn't belong in rst()");
            return;
        }

        // push address of instruction onto stack.
        pushHelper(registerPC.read());

        int address = 0;
        switch (opcode) {
            case 0xC7: address = 0x00; break;
            case 0xCF: address = 0x08; break;
            case 0xD7: address = 0x10; break;
            case 0xDF: address = 0x18; break;
            case 0xE7: address = 0x20; break;
            case 0xEF: address = 0x28; break;
            case 0xF7: address = 0x30; break;
            case 0xFF: address = 0x38; break;
        }
        load(registerPC, address);

        lastInstructionCycles = 32;
    }

    private void retHelper() {
        int address = popHelper();
        load(registerPC, address);
        lastInstructionCycles = 8;
    }
    public void ret(int opcode) {
        /*
        1. RET
        Description:
        Pop two bytes from stack & jump to that address.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RET -/- C9 8
        */
        if (opcode != 0xC9) {
            log.error("Opcode " + opcode + " doesn't belong in ret()");
            return;
        }

        retHelper();
    }
    public void retcc(int opcode) {
        /*
        2. RET cc
        Description:
        Return if following condition is true:
        Use with:
        cc = NZ, Return if Z flag is reset.
        cc = Z, Return if Z flag is set.
        cc = NC, Return if C flag is reset.
        cc = C, Return if C flag is set.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RET NZ C0 8
        RET Z C8 8
        RET NC D0 8
        RET C D8 8
        */
        if (opcode != 0xC0 &&
            opcode != 0xC8 &&
            opcode != 0xD0 &&
            opcode != 0xD8) {
            log.error("Opcode " + opcode + " doesn't belong in retcc()");
            return;
        }

        boolean condition = false;
        switch (opcode) {
            case 0xC0:
                condition = (!registerFlags.readZ());
                break;
            case 0xC8:
                condition = registerFlags.readZ();
                break;
            case 0xD0:
                condition = (!registerFlags.readC());
                break;
            case 0xD8:
                condition = registerFlags.readC();
                break;
        }

        // if our condition for jumping is false, don't jump
        if (!condition) {
            return;
        }

        // actually return/jump
        retHelper();
    }
    public void reti(int opcode) {
        /*3. RETI
        Description:
        Pop two bytes from stack & jump to that address then
        enable interrupts.
        Opcodes:
        Instruction Parameters Opcode Cycles
        RETI -/- D9 8
        */
        if (opcode != 0xD9) {
            log.error("Opcode " + opcode + " doesn't belong in ret()");
            return;
        }

        retHelper();
        interruptMasterEnable = true;
    }
}
