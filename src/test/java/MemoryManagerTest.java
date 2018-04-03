import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManagerTest extends AbstractTest {

    private Cartridge cart = new Cartridge(getClass().getResource("cpu_instrs.gb").getPath());
    private MemoryManager mmu = new MemoryManager(cart);
    private Random rng = new Random();

    @Test
    public void testZeroize() {
        for (int i = 0; i < mmu.memorySize; i++) {
            mmu.writeByte(i, rng.nextInt(256));
        }
        mmu.zeroize();
        assertArrayEquals(new int[mmu.memorySize], mmu.getRawRam());
    }

    @Test
    public void testGetBiosLogo() {
        int[] expected = MemoryManager.hexStringToByteArray(
                "CEED6666CC0D000B03730083000C000D" +
                "0008111F8889000EDCCC6EE6DDDDD999" +
                "BBBB67636E0EECCCDDDC999FBBB9333E");
        int[] actual = MemoryManager.getBiosLogo();

        assertArrayEquals(expected,actual);
    }

    /*@Test
    public void testEightBitReadWrite() {
        for (int i = 0; i < 100; i++) {
            int address = rng.nextInt(mmu.memorySize);
            int value = rng.nextInt(256);
            mmu.writeByte(address, value);
            assertEquals(value, mmu.readByte(address));
        }

        try {
            mmu.readByte(mmu.memorySize);
            fail("should not be able to do mmu.readByte(mmu.memorySize) without an exception");
        }
        catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            mmu.readByte(-1);
            fail("should not be able to do mmu.readByte(-1) without an exception");
        }
        catch (IndexOutOfBoundsException e) {
            // OK
        }

        // address 0
        int value = rng.nextInt(256);
        mmu.writeByte(0, value);
        assertEquals(value, mmu.readByte(0));

        // max valid address
        value = rng.nextInt(256);
        mmu.writeByte(mmu.memorySize - 1, value);
        assertEquals(value, mmu.readByte(mmu.memorySize - 1));

        // write value higher than max possible
        try {
            mmu.writeByte(rng.nextInt(mmu.memorySize), 256);
            fail("should not be able to write values higher than 255 to memory");
        }
        catch (NumberFormatException e) {
            // OK
        }

        // write value lower than 0
        try {
            mmu.writeByte(rng.nextInt(mmu.memorySize), -1);
            fail("should not be able to write values lower than 0 to memory");
        }
        catch (NumberFormatException e) {
            // OK
        }
    }*/

    /*@Test
    public void testSixteenBitReadWrite() {
        for (int i = 0; i < 100; i ++) {
            int address = rng.nextInt(mmu.memorySize - 1);
            int value = rng.nextInt(65536);

            // test 16bit write
            mmu.writeWord(address, value);
            assertEquals(value & 0b00000000_11111111, mmu.readByte(address));
            assertEquals((value & 0b11111111_00000000) >> 8, mmu.readByte(address + 1));

            // test 16-bit read
            address = rng.nextInt(mmu.memorySize - 1);
            int upperValue = rng.nextInt(256);
            int lowerValue = rng.nextInt(256);
            mmu.writeByte(address, lowerValue);
            mmu.writeByte(address + 1, upperValue);
            int expectedValue = (upperValue << 8) + lowerValue;
            assertEquals(expectedValue, mmu.readWord(address));
        }
    }*/
}
