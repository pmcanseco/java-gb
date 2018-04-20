import helpers.Logger;

import java.util.Random;

/**
 * Created by Pablo Canseco on 12/24/2017.
 */
public class MemoryManagerTest extends UnitTest {

    private MbcManager cartMbc = new MbcManager(new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/cpu_instrs.gb"), Logger.Level.FATAL);
    private MemoryManager mmu = new MemoryManager(cartMbc);
    private Random rng = new Random();


}
