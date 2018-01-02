import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Pablo Canseco on 12/24/2017.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemoryManagerTest {

    private MemoryManager mmu;
    private Random rng;

    @BeforeAll
    void setUp() {
        System.out.println("=== Starting MemoryManagerTest");
        Cartridge cart = new Cartridge("C:\\Users\\Pablo\\Desktop\\cpu_instrs.gb");
        mmu = new MemoryManager(cart);
        rng = new Random();
    }

    @AfterAll
    void tearDown() {
        System.out.println("=== Ending MemoryManagerTest");
        System.out.println();
    }

    @Test
    void testZeroize() {
        for (int i = 0; i < mmu.memorySize; i++) {
            mmu.rawWrite(i, rng.nextInt(256));
        }
        mmu.zeroize();
        assertArrayEquals(new int[mmu.memorySize], mmu.getRawRam());
    }

    @Test
    void testGetBiosLogo() {
        int[] expected = MemoryManager.hexStringToByteArray(
                "CEED6666CC0D000B03730083000C000D" +
                "0008111F8889000EDCCC6EE6DDDDD999" +
                "BBBB67636E0EECCCDDDC999FBBB9333E");
        int[] actual = MemoryManager.getBiosLogo();

        assertArrayEquals(expected,actual);
    }

    @Test
    void testEightBitReadWrite() {
        for (int i = 0; i < 100; i++) {
            int address = rng.nextInt(mmu.memorySize);
            int value = rng.nextInt(256);
            mmu.rawWrite(address, value);
            assertEquals(value, mmu.rawRead(address));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> mmu.rawRead(mmu.memorySize));
        assertThrows(IndexOutOfBoundsException.class, () -> mmu.rawRead(-1));

        // address 0
        int value = rng.nextInt(256);
        mmu.rawWrite(0, value);
        assertEquals(value, mmu.rawRead(0));

        // max valid address
        value = rng.nextInt(256);
        mmu.rawWrite(mmu.memorySize - 1, value);
        assertEquals(value, mmu.rawRead(mmu.memorySize - 1));
    }

    @Test
    void testSixteenBitReadWrite() {
        for (int i = 0; i < 100; i ++) {
            int address = rng.nextInt(mmu.memorySize - 1);
            int value = rng.nextInt(65536);

            // test 16bit write
            mmu.writeWord(address, value);
            assertEquals(value & 0b00000000_11111111, mmu.rawRead(address));
            assertEquals((value & 0b11111111_00000000) >> 8, mmu.rawRead(address + 1));

            // test 16-bit read
            address = rng.nextInt(mmu.memorySize - 1);
            int upperValue = rng.nextInt(256);
            int lowerValue = rng.nextInt(256);
            mmu.rawWrite(address, lowerValue);
            mmu.rawWrite(address + 1, upperValue);
            int expectedValue = (upperValue << 8) + lowerValue;
            assertEquals(expectedValue, mmu.readWord(address));
        }
    }
}
