import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Pablo Canseco on 12/23/2017.
 */
public class Z80Test extends AbstractTest{

    private Cartridge cart = new Cartridge(getClass().getResource("cpu_instrs.gb").getPath());
    private MemoryManager mmu = new MemoryManager(cart);
    private Z80 z80uut = new Z80(mmu);
    private Random rng = new Random();

    // utility methods
    private void randomizeRegisters() {
        String[] registers = { "A", "B", "C", "D", "E", "H", "L", "PC", "SP"};
        for (String register : registers) {
            Register reg = z80uut.search(register);
            z80uut.load(reg, rng.nextInt(reg.getSize() == 8 ? 256 : 65536));
        }

    }
    private void randomizeMemory() {
        for (int i = 0; i < mmu.memorySize; i++) {
            mmu.rawWrite(i, rng.nextInt(256));
        }
    }
    private int as8bitNumber(int number) {
        if (number > 255 || number < 0) {
            number &= 255;
        }

        return number;
    }

    // tests
    @Test
    public void test8bitRegister() {
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
        final String[] registers = {"A", "B", "C", "D", "E", "H", "L"};

        for (final String register : registers) {
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
        try {
            z80uut.setRegisterValue("C", 0b01101100);
            z80uut.setRegisterValue("D", 0b01111111);
            assertEquals(0b01101100_01111111, z80uut.readCombinedRegisters("C", "D"));
        } catch (Exception ex) {
            // do nothing
        }
    }

    @Test
    public void testAdd() {
        for(int i = 0; i < 10; i++) {
            try {
                Map<Integer, Integer> opcodeToExpectedValueMap = new HashMap<>();
                randomizeRegisters();
                randomizeMemory();
                final int currentRegisterAValue = z80uut.getRegisterValue("A");

                opcodeToExpectedValueMap.put(0x87, currentRegisterAValue + z80uut.getRegisterValue("A"));
                opcodeToExpectedValueMap.put(0x80, currentRegisterAValue + z80uut.getRegisterValue("B"));
                opcodeToExpectedValueMap.put(0x81, currentRegisterAValue + z80uut.getRegisterValue("C"));
                opcodeToExpectedValueMap.put(0x82, currentRegisterAValue + z80uut.getRegisterValue("D"));
                opcodeToExpectedValueMap.put(0x83, currentRegisterAValue + z80uut.getRegisterValue("E"));
                opcodeToExpectedValueMap.put(0x84, currentRegisterAValue + z80uut.getRegisterValue("H"));
                opcodeToExpectedValueMap.put(0x85, currentRegisterAValue + z80uut.getRegisterValue("L"));
                opcodeToExpectedValueMap.put(0x86, currentRegisterAValue + mmu.rawRead(z80uut.readCombinedRegisters("H", "L")));
                opcodeToExpectedValueMap.put(0xC6, currentRegisterAValue + mmu.rawRead(z80uut.getRegisterValue("PC")));

                for (final int opcode : opcodeToExpectedValueMap.keySet()) {
                    z80uut.add(opcode);
                    final int expected = as8bitNumber(opcodeToExpectedValueMap.get(opcode)); // account for overflow
                    final int actual = z80uut.getRegisterValue("A");
                    assertEquals(expected, actual);

                    // clean up A back to previous value
                    z80uut.load(z80uut.search("A"), currentRegisterAValue);
                }
            }
            catch (Exception e) {
                //fail("this test should not throw an exception"); //todo fix this, combined registers no longer throw exception
            }
        }
    }

    @Test
    public void testSub() {
        for(int i = 0; i < 10; i++) {
            try {
                Map<Integer, Integer> opcodeToExpectedValueMap = new HashMap<>();
                randomizeRegisters();
                randomizeMemory();
                final int currentRegisterAValue = z80uut.getRegisterValue("A");

                opcodeToExpectedValueMap.put(0x97, currentRegisterAValue - z80uut.getRegisterValue("A"));
                opcodeToExpectedValueMap.put(0x90, currentRegisterAValue - z80uut.getRegisterValue("B"));
                opcodeToExpectedValueMap.put(0x91, currentRegisterAValue - z80uut.getRegisterValue("C"));
                opcodeToExpectedValueMap.put(0x92, currentRegisterAValue - z80uut.getRegisterValue("D"));
                opcodeToExpectedValueMap.put(0x93, currentRegisterAValue - z80uut.getRegisterValue("E"));
                opcodeToExpectedValueMap.put(0x94, currentRegisterAValue - z80uut.getRegisterValue("H"));
                opcodeToExpectedValueMap.put(0x95, currentRegisterAValue - z80uut.getRegisterValue("L"));
                opcodeToExpectedValueMap.put(0x96, currentRegisterAValue - mmu.rawRead(z80uut.readCombinedRegisters("H", "L")));
                opcodeToExpectedValueMap.put(0xD6, currentRegisterAValue - mmu.rawRead(z80uut.getRegisterValue("PC")));

                for (final int opcode : opcodeToExpectedValueMap.keySet()) {
                    z80uut.sub(opcode);
                    final int expected = as8bitNumber(opcodeToExpectedValueMap.get(opcode)); // account for overflow
                    final int actual = z80uut.getRegisterValue("A");
                    //log("expected = " + expected + ", actual = " + actual);
                    assertEquals(expected, actual);

                    // clean up A back to previous value
                    z80uut.load(z80uut.search("A"), currentRegisterAValue);
                }
            }
            catch (Exception e) {
                //fail("this test should not throw an exception"); //todo fix this, combined registers no longer throw exception
            }
        }
    }
}