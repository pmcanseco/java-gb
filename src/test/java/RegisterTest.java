import org.junit.Test;

import java.util.BitSet;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pablo Canseco on 12/27/2017.
 */
public class RegisterTest extends AbstractTest {

    private Random rng = new Random();

    @Test
    public void readBitTest() {
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
            //log(r);
        }
        log("Tested " + eightbitregs + " 8-bit registers.");
        log("Tested " + sixteenbitregs + " 16-bit registers.");
    }

    @Test
    public void writeBitTest() {
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

        log("Tested " + eightBitRegs + " 8-bit registers.");
        log("Tested " + sixteenBitRegs + " 16-bit registers.");
    }

    @Test
    public void testFlagsRegisterFunctions() {
        FlagsRegister fr = new FlagsRegister("F", 8, 0);
        int value = rng.nextInt(256);
        fr.write(value);
        value &= 0b11110000;
        assertEquals(value, fr.read());
        // TODO finish testing register flags
    }

    @Test
    public void testReadWriteHighLow() {
        Register r = new Register("R", 16, 0);
        int value = rng.nextInt(65536);
        r.write(value);

        assertEquals(value & 0b11111111_00000000, r.readHigh());
        assertEquals(value & 0b00000000_11111111, r.readLow());
        // TODO test writes
    }

    @Test
    public void testIncDec() {
        Register r = new Register("R", 8, 0);

        assertEquals(0, r.read());
        r.inc();
        assertEquals(1, r.read());

        r.write(255);
        assertEquals(255, r.read());

        r.inc();
        assertEquals(0, r.read());

        r.dec();
        assertEquals(255, r.read());

        r.write(1);
        r.dec();
        assertEquals(0, r.read());
    }
}