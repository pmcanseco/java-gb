import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import static org.junit.Assert.fail;

/**
 * Created by Pablo Canseco on 4/6/2018.
 */
public class CpuInstrsTest extends UnitTest {
    // this class will take all the Blargg cpu_instrs subtests and run them sequentially,
    // comparing the test results from the console.

    private final String baseFilePath = "src/test/resources/gb-test-roms/cpu_instrs/individual/";
    private Cartridge cart;
    private MemoryManager mmu;
    private Cpu cpuUut;
    private Gpu gpu;

    private int timeoutCycles = 10000000;

    private void initRomSubtest(final String romName) {
        gpu = new Gpu();
        cart = new Cartridge(baseFilePath + romName);
        mmu = new MemoryManager(cart, gpu);
        cpuUut = new Cpu(mmu, Logger.Level.FATAL);
        cpuUut.skipBios();
    }
    private void stepUut() {
        int opcode = cpuUut.fetch();
        cpuUut.execute(opcode);
        gpu.step(cpuUut.lastInstructionCycles);
    }
    private void runSubtest() {
        int i = 0;
        StringBuilder runningLog = new StringBuilder();
        while (true) {

            // capture stdout to get test results
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));

            // cycle the cpu
            stepUut();

            // collect output
            String output = baos.toString(); //.toLowerCase().replaceAll("\\p{Z}","");

            // revert stdout to default
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

            // process runningLog
            if(output.length() > 0) {
                runningLog.append(output);
            }

            // if test is done, break out of loop
            if(runningLog.toString().contains("Passed") || runningLog.toString().contains("Failed")) {
                break;
            }

            // timeout to catch infinite loops
            if(i >= timeoutCycles) {
                // revert stdout
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                fail("test timed out at " + i + " cycles: \n" + runningLog.toString());
                break;
            }

            i++;
        }

        if(runningLog.toString().contains("Passed")) {
            log(runningLog.toString());
            return;
        }
        else {
            // revert stdout
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            fail(runningLog.toString());
        }

        // revert stdout
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @Test
    public void special01() {
        initRomSubtest("01-special.gb");
        warning("This test is not yet expected to pass");
        //runSubtest();
    }

    @Test
    public void opsphl03() {
        initRomSubtest("03-op sp,hl.gb");

        runSubtest();
    }

    @Test
    public void oprp05() {
        initRomSubtest("05-op rp.gb");

        runSubtest();
    }

    @Test
    public void ldrr06() {
        initRomSubtest("06-ld r,r.gb");

        runSubtest();
    }

    @Test
    public void bitops10() {
        initRomSubtest("10-bit ops.gb");

        runSubtest();
    }

    @Test
    public void opahl11() {
        initRomSubtest("11-op a,(hl).gb");
        warning("This test is not yet expected to pass");
        //runSubtest();
    }
}
