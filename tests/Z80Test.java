import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Pablo Canseco on 12/23/2017.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Z80Test {

    private Z80 z80uut;

    @BeforeAll
    void setUp() {
        Cartridge cart = new Cartridge("C:\\Users\\Pablo\\Desktop\\cpu_instrs\\cpu_instrs.gb");
        MemoryManager mmu = new MemoryManager(cart);
        z80uut = new Z80(mmu);
    }

    @AfterAll
    void tearDown() {
    }

    @Test
    void test8bitRegister() {
        z80uut.setRegisterValue("A", 0);
        assertEquals(0, z80uut.getRegisterValue("A"));

        z80uut.setRegisterValue("A", 128);
        assertEquals(128, z80uut.getRegisterValue("A"));

        z80uut.setRegisterValue("A", 255);
        assertEquals(255, z80uut.getRegisterValue("A"));

        z80uut.setRegisterValue("A", -1);
        assertEquals(255, z80uut.getRegisterValue("A"));

        z80uut.setRegisterValue("A", 128);
        z80uut.setRegisterValue("A", 256);
        assertEquals(128, z80uut.getRegisterValue("A"));
    }

    @Test
    void test8bitReadBitFunction() {
        Random rng = new Random();
        String[] registers = {"A", "B", "C", "D", "E", "H", "L"};

        for (String register : registers) {
            // generate random number between 0 and 255.
            int randomEightBitUnsignedInt = rng.nextInt(256);

            // put it in register
            z80uut.setRegisterValue(register, randomEightBitUnsignedInt);

            // ensure each bit is expected value
            for (int j = 0; j < 8; j++) {
                assertEquals(((randomEightBitUnsignedInt >> j) & 1) == 1, z80uut.getRegisterBit(register, j));
            }
        }


    }

    @Test
    void testCombinedRegisterWrite() {
        try {
            z80uut.writeCombined8bitRegisters("A", "B", 0b1111000110011110);
            assertEquals(0b11110001, z80uut.getRegisterValue("A"));
            assertEquals(0b10011110, z80uut.getRegisterValue("B"));
        } catch (Exception ex) {
            // do nothing
        }
    }

    @Test
    void testCombinedRegisterRead() {
        try {
            z80uut.setRegisterValue("C", 0b01101100);
            z80uut.setRegisterValue("D", 0b01111111);
            assertEquals(0b01101100_01111111, z80uut.readCombined8bitRegisters("C", "D"));
        } catch (Exception ex) {
            // do nothing
        }
    }
}