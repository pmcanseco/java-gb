import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pablo Canseco on 12/23/2017.
 */
public class Z80Test {


    private Cartridge cart = new Cartridge(getClass().getResource("cpu_instrs.gb").getPath());
    private MemoryManager mmu = new MemoryManager(cart);
    private Z80 z80uut = new Z80(mmu);

    @Test
    public void test8bitRegister() {
        System.out.println("Testing test8bitRegister()");
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
    public void test8bitReadBitFunction() {
        System.out.println("Testing test8bitReadBitFunction()");
        Random rng = new Random();
        String[] registers = {"A", "B", "C", "D", "E", "H", "L"};

        for (String register : registers) {
            // generate random number between 0 and 255.
            int randomEightBitUnsignedInt = rng.nextInt(256);

            // put it in register
            z80uut.setRegisterValue(register, randomEightBitUnsignedInt);

            // ensure each bit is expected value
            for (int j = 0; j < 8; j++) {
                org.junit.Assert.assertEquals(((randomEightBitUnsignedInt >> j) & 1) == 1, z80uut.getRegisterBit(register, j));
            }
        }


    }

    @Test
    public void testCombinedRegisterWrite() {
        System.out.println("Testing testCombinedRegisterWrite()");
        try {
            z80uut.writeCombinedRegisters("A", "B", 0b11110001_10011110);
            assertEquals(0b11110001, z80uut.getRegisterValue("A"));
            assertEquals(0b10011110, z80uut.getRegisterValue("B"));
        } catch (Exception ex) {
            // do nothing
        }
    }

    @Test
    public void testCombinedRegisterRead() {
        System.out.println("Testing testCombinedRegisterRead()");
        try {
            z80uut.setRegisterValue("C", 0b01101100);
            z80uut.setRegisterValue("D", 0b01111111);
            assertEquals(0b01101100_01111111, z80uut.readCombinedRegisters("C", "D"));
        } catch (Exception ex) {
            // do nothing
        }
    }
}