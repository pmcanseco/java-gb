import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Pablo Canseco on 12/23/2017.
 */
public class CpuTest extends AbstractTest{

    private Cartridge cart = new Cartridge(getClass().getResource("cpu_instrs.gb").getPath());
    private MemoryManager mmu = new MemoryManager(cart);
    private Cpu cpuUut = new Cpu(mmu, Logger.Level.FATAL);
    private Random rng = new Random();

    // utility methods
    private void randomizeRegisters() {
        String[] registers = { "A", "B", "C", "D", "E", "H", "L", "PC", "SP"};
        for (String register : registers) {
            Register reg = cpuUut.search(register);
            cpuUut.load(reg, rng.nextInt(reg.getSize() == 8 ? 256 : 65536));
        }

    }
    private void randomizeMemory() {
        for (int i = 0; i < mmu.memorySize; i++) {
            mmu.writeByte(i, rng.nextInt(256));
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
        cpuUut.setRegisterValue("A", 0);
        assertEquals(0, cpuUut.getRegisterValue("A"));

        cpuUut.setRegisterValue("A", 128);
        assertEquals(128, cpuUut.getRegisterValue("A"));

        cpuUut.setRegisterValue("A", 255);
        assertEquals(255, cpuUut.getRegisterValue("A"));

        cpuUut.setRegisterValue("A", -1);
        assertEquals(255, cpuUut.getRegisterValue("A"));

        cpuUut.setRegisterValue("A", 128);
        cpuUut.setRegisterValue("A", 256);
        assertEquals(128, cpuUut.getRegisterValue("A"));
    }

    @Test
    public void test8bitReadBitFunction() {
        final String[] registers = {"A", "B", "C", "D", "E", "H", "L"};

        for (final String register : registers) {
            // generate random number between 0 and 255.
            int randomEightBitUnsignedInt = rng.nextInt(256);

            // put it in register
            cpuUut.setRegisterValue(register, randomEightBitUnsignedInt);

            // ensure each bit is expected value
            for (int j = 0; j < 8; j++) {
                org.junit.Assert.assertEquals(((randomEightBitUnsignedInt >> j) & 1) == 1, cpuUut.getRegisterBit(register, j));
            }
        }


    }

    @Test
    public void testCombinedRegisterWrite() {
        try {
            cpuUut.writeCombinedRegisters("A", "B", 0b11110001_10011110);
            assertEquals(0b11110001, cpuUut.getRegisterValue("A"));
            assertEquals(0b10011110, cpuUut.getRegisterValue("B"));
        } catch (Exception ex) {
            // do nothing
        }
    }

    @Test
    public void testCombinedRegisterRead() {
        try {
            cpuUut.setRegisterValue("C", 0b01101100);
            cpuUut.setRegisterValue("D", 0b01111111);
            assertEquals(0b01101100_01111111, cpuUut.readCombinedRegisters("C", "D"));
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
                final int currentRegisterAValue = cpuUut.getRegisterValue("A");

                opcodeToExpectedValueMap.put(0x87, currentRegisterAValue + cpuUut.getRegisterValue("A"));
                opcodeToExpectedValueMap.put(0x80, currentRegisterAValue + cpuUut.getRegisterValue("B"));
                opcodeToExpectedValueMap.put(0x81, currentRegisterAValue + cpuUut.getRegisterValue("C"));
                opcodeToExpectedValueMap.put(0x82, currentRegisterAValue + cpuUut.getRegisterValue("D"));
                opcodeToExpectedValueMap.put(0x83, currentRegisterAValue + cpuUut.getRegisterValue("E"));
                opcodeToExpectedValueMap.put(0x84, currentRegisterAValue + cpuUut.getRegisterValue("H"));
                opcodeToExpectedValueMap.put(0x85, currentRegisterAValue + cpuUut.getRegisterValue("L"));
                opcodeToExpectedValueMap.put(0x86, currentRegisterAValue + mmu.readByte(cpuUut.readCombinedRegisters("H", "L")));
                opcodeToExpectedValueMap.put(0xC6, currentRegisterAValue + mmu.readByte(cpuUut.getRegisterValue("PC")));

                for (final int opcode : opcodeToExpectedValueMap.keySet()) {
                    cpuUut.add(opcode);
                    final int expected = as8bitNumber(opcodeToExpectedValueMap.get(opcode)); // account for overflow
                    final int actual = cpuUut.getRegisterValue("A");
                    assertEquals(expected, actual);

                    // clean up A back to previous value
                    cpuUut.load(cpuUut.search("A"), currentRegisterAValue);
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
                final int currentRegisterAValue = cpuUut.getRegisterValue("A");

                opcodeToExpectedValueMap.put(0x97, currentRegisterAValue - cpuUut.getRegisterValue("A"));
                opcodeToExpectedValueMap.put(0x90, currentRegisterAValue - cpuUut.getRegisterValue("B"));
                opcodeToExpectedValueMap.put(0x91, currentRegisterAValue - cpuUut.getRegisterValue("C"));
                opcodeToExpectedValueMap.put(0x92, currentRegisterAValue - cpuUut.getRegisterValue("D"));
                opcodeToExpectedValueMap.put(0x93, currentRegisterAValue - cpuUut.getRegisterValue("E"));
                opcodeToExpectedValueMap.put(0x94, currentRegisterAValue - cpuUut.getRegisterValue("H"));
                opcodeToExpectedValueMap.put(0x95, currentRegisterAValue - cpuUut.getRegisterValue("L"));
                opcodeToExpectedValueMap.put(0x96, currentRegisterAValue - mmu.readByte(cpuUut.readCombinedRegisters("H", "L")));
                opcodeToExpectedValueMap.put(0xD6, currentRegisterAValue - mmu.readByte(cpuUut.getRegisterValue("PC")));

                for (final int opcode : opcodeToExpectedValueMap.keySet()) {
                    cpuUut.sub(opcode);
                    final int expected = as8bitNumber(opcodeToExpectedValueMap.get(opcode)); // account for overflow
                    final int actual = cpuUut.getRegisterValue("A");
                    //log("expected = " + expected + ", actual = " + actual);
                    assertEquals(expected, actual);

                    // clean up A back to previous value
                    cpuUut.load(cpuUut.search("A"), currentRegisterAValue);
                }
            }
            catch (Exception e) {
                //fail("this test should not throw an exception"); //todo fix this, combined registers no longer throw exception
            }
        }
    }
}