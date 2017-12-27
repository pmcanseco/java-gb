import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Pablo Canseco on 12/27/2017.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegisterTest {

    private Z80 z80uut;

    @BeforeAll
    void setUp() {
        System.out.println("=== Starting RegisterTest");
        z80uut = new Z80(new MemoryManager(new Cartridge("C:\\Users\\Pablo\\Desktop\\cpu_instrs\\cpu_instrs.gb")));
    }

    @AfterAll
    void tearDown() {
        System.out.println("=== Ending RegisterTest");
    }

    @Test
    void readBitTest() {
        // test reading each flag ZNHC
    }

    @Test
    void writeBitTest() {
        // test writing each flag ZNHC
    }
}