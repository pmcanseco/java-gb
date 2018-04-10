import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stdout = new PrintStream(new FileOutputStream(FileDescriptor.out));
        while (true) {

            // send stuff to the console so travis doesn't error out.
            if ((i % 10000 == 0) && (i != 0)) {
                System.out.print(".");
                if( i % 500000 == 0) {
                    System.out.println();
                }
            }

            // capture stdout to get test results
            baos.reset();
            System.setOut(new PrintStream(baos));

            // cycle the cpu
            stepUut();

            // collect output
            String output = baos.toString();

            // revert stdout to default
            System.setOut(stdout);

            // process runningLog
            if(output.length() > 0) {
                runningLog.append(output);
            }

            // if test is done, break out of loop
            if(runningLog.toString().contains("Passed") || runningLog.toString().contains("Failed")) {
                System.out.println();
                break;
            }

            // timeout to catch infinite loops
            if(i >= timeoutCycles) {
                // revert stdout
                System.setOut(stdout);
                fail("test timed out at " + i + " cycles: \n" + runningLog.toString());
                break;
            }

            i++;
        }

        // revert stdout
        System.setOut(stdout);

        if(runningLog.toString().contains("Passed")) {
            log("\n" + runningLog.toString());
        }
        else {
            fail(runningLog.toString());
        }
    }

    @Test
    public void special01() {
        initRomSubtest("01-special.gb");

        runSubtest();
    }

    @Test
    public void interrupts02() {
        initRomSubtest("02-interrupts.gb");
        warning("This test is not yet expected to pass");
        //runSubtest();
    }

    @Test
    public void opsphl03() {
        initRomSubtest("03-op sp,hl.gb");

        runSubtest();
    }

    @Test
    public void oprimm04() {
        initRomSubtest("04-op r,imm.gb");

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
    public void jrjpcallretrst07() {
        initRomSubtest("07-jr,jp,call,ret,rst.gb");

        runSubtest();
    }

    @Test
    public void miscinstrs08() {
        initRomSubtest("08-misc instrs.gb");

        runSubtest();
    }

    @Test
    public void oprr09() {
        initRomSubtest("09-op r,r.gb");

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

        runSubtest();
    }
}
