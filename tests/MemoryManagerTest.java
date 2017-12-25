import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

/**
 * Created by Pablo Canseco on 12/24/2017.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemoryManagerTest {

    private MemoryManager mmu;

    @BeforeAll
    void setUp() {
        Cartridge cart = new Cartridge("C:\\Users\\Pablo\\Desktop\\cpu_instrs\\cpu_instrs.gb");
        mmu = new MemoryManager(cart);
    }

    @Test
    void testZeroize() {
        Random rng = new Random();
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
}
