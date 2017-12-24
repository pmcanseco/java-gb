import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Pablo Canseco on 12/23/2017.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Z80Test {

    private Z80 z80uut;

    @BeforeAll
    void setUp() {
        z80uut = new Z80();
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
        z80uut.setRegisterValue("A", 0b00010101);

        assertEquals(true, z80uut.getRegisterBit("A", 0));
        assertEquals(false, z80uut.getRegisterBit("A", 1));
        assertEquals(true, z80uut.getRegisterBit("A", 2));
        assertEquals(false, z80uut.getRegisterBit("A", 3));
        assertEquals(true, z80uut.getRegisterBit("A", 4));
        assertEquals(false, z80uut.getRegisterBit("A", 5));
        assertEquals(false, z80uut.getRegisterBit("A", 6));
        assertEquals(false, z80uut.getRegisterBit("A", 7));
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