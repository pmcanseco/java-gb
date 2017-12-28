import org.junit.jupiter.api.*;

import java.util.BitSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Pablo Canseco on 12/27/2017.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegisterTest {

    private Z80 z80uut;
    private Random rng;

    @BeforeAll
    void setUp() {
        System.out.println("=== Starting RegisterTest");
        rng = new Random();
    }

    @AfterAll
    void tearDown() {
        System.out.println("=== Ending RegisterTest");
        System.out.println();
    }

    @Test
    void readBitTest() {
        System.out.println("Testing readBit()");
        int eightbitregs = 0;
        int sixteenbitregs = 0;

        // make a register and bitset
        // test each bit in bitset against each bit in register
        for (int i = 0; i < 1000; i++) {
            boolean registerSize = rng.nextBoolean();
            int registerValue = rng.nextInt((registerSize) ? 256 : 65536);

            // keep track of how many of each we've done
            if (registerSize) eightbitregs++;
            else sixteenbitregs++;

            Register r = new Register("R", registerSize ? 8 : 16, 0);
            r.write(registerValue);

            BitSet bs = BitSet.valueOf(new long[] {registerValue});
            for (int j = 0; j < (registerSize ? 8 : 16); j++) {
                assertEquals(bs.get(j), r.readBit(j));
            }
            //System.out.println(r);
        }
        System.out.println("Tested " + eightbitregs + " 8-bit registers.");
        System.out.println("Tested " + sixteenbitregs + " 16-bit registers.");
    }

    @Test
    void writeBitTest() {
        System.out.println("Testing writeBit()");
        int eightBitRegs = 0 ;
        int sixteenBitRegs = 0 ;

        for (int j = 0; j < 1000; j++) {
            int registerSize = rng.nextBoolean() ? 8 : 16;

            if (registerSize == 8) eightBitRegs++;
            else sixteenBitRegs++;

            Register r = new Register("R", registerSize, 0);
            BitSet bs = new BitSet();

            for (int i = 0; i < registerSize; i++) {
                boolean bit = rng.nextBoolean();
                if (bit) {
                    bs.set(i);
                    r.writeBit(i, true);
                } else {
                    bs.clear(i);
                    r.writeBit(i, false);
                }
                assertEquals(bs.get(i), r.readBit(i));
            }

            //System.out.println(r);
            if (r.read() == 0) {
                assertEquals(0, bs.toLongArray().length);
            }
            else {
                assertEquals(bs.toLongArray()[0], r.read());
            }
        }

        System.out.println("Tested " + eightBitRegs + " 8-bit registers.");
        System.out.println("Tested " + sixteenBitRegs + " 16-bit registers.");
    }

    @Test
    void testFlagsRegisterFunctions() {
        FlagsRegister fr = new FlagsRegister("F", 8, 0);
        fr.write(rng.nextInt(256));
    }
}