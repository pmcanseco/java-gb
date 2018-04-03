import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.fail;

/**
 * Created by Pablo Canseco on 3/27/2018.
 */
public class InstructionsTest extends AbstractTest {

    private int a, f, b, c;
    private int d, e, h, l;
    private int sp, pc;

    private int expected_a, expected_f, expected_b, expected_c;
    private int expected_d, expected_e, expected_h, expected_l;
    private int expected_sp, expected_pc;

    private JSONArray biosValues;
    private MemoryManager mmu;
    private Cpu cpu;
    private Gpu gpu = new Gpu();

    private void readUutRegisterValues() {
        a = cpu.getRegisterValue("A");
        f = cpu.getRegisterValue("Flags");
        b = cpu.getRegisterValue("B");
        c = cpu.getRegisterValue("C");
        d = cpu.getRegisterValue("D");
        e = cpu.getRegisterValue("E");
        h = cpu.getRegisterValue("H");
        l = cpu.getRegisterValue("L");
        sp = cpu.getRegisterValue("SP");
        pc = cpu.getRegisterValue("PC");
    }
    private void stepUut() {
        int opcode = cpu.fetch();
        cpu.execute(opcode);
        gpu.step(cpu.lastInstructionCycles);
    }
    private void readExpectedRegisterValues(int index) {
        expected_a  = biosValues.getJSONObject(index).getInt("a");
        expected_b  = biosValues.getJSONObject(index).getInt("b");
        expected_c  = biosValues.getJSONObject(index).getInt("c");
        expected_d  = biosValues.getJSONObject(index).getInt("d");
        expected_e  = biosValues.getJSONObject(index).getInt("e");
        expected_f  = biosValues.getJSONObject(index).getInt("f");
        expected_h  = biosValues.getJSONObject(index).getInt("h");
        expected_l  = biosValues.getJSONObject(index).getInt("l");
        expected_sp = biosValues.getJSONObject(index).getInt("sp");
        expected_pc = biosValues.getJSONObject(index).getInt("pc");
    }

    @Test
    public void testInstructions() {
        mmu = new MemoryManager(new Cartridge(getClass().getResource("cpu_instrs.gb").getPath()));
        cpu = new Cpu(mmu, Logger.Level.FATAL);
        try {
            String biosJson = new String(
                    Files.readAllBytes(Paths.get(getClass().getResource("full-bios.txt").toURI())));
            biosValues = new JSONArray(biosJson);
            log("Imported bios values json.");
        }
        catch (URISyntaxException | IOException e) {
            error("Error: " + e.getMessage());
        }

        log("Instantiated memory manager and cpu.");

        // step the cpu through the ram init process
        while (pc < 0x0C) {
            stepUut();
            readUutRegisterValues();
        }

        for (int i = 0; pc < 0x100; i++) {
            readUutRegisterValues();

            try {
                readExpectedRegisterValues(i);
            }
            catch (org.json.JSONException e) {
                // most likely the end of the json was reached
                if (i >= 3721) {
                    break;
                }
                else {
                    fail("Unknown Exception: " + e.getMessage());
                }
            }

            boolean success = true;
            if (a  != expected_a)  {
                error("\nIndex " + i + ": mismatch at register A  -- actual=" + a + "   expected=" + expected_a);
                success = false;
            }
            if (f  != expected_f)  {
                error("\nIndex " + i + ": mismatch at register F  -- actual=" + f + "   expected=" + expected_f);
                success = false;
            }
            if (b  != expected_b)  {
                error("\nIndex " + i + ": mismatch at register B  -- actual=" + b + "   expected=" + expected_b);
                success = false;
            }
            if (c  != expected_c)  {
                error("\nIndex " + i + ": mismatch at register C  -- actual=" + c + "   expected=" + expected_c);
                success = false;
            }
            if (d  != expected_d)  {
                error("\nIndex " + i + ": mismatch at register D  -- actual=" + d + "   expected=" + expected_d);
                success = false;
            }
            if (e  != expected_e)  {
                error("\nIndex " + i + ": mismatch at register E  -- actual=" + e + "   expected=" + expected_e);
                success = false;
            }
            if (h  != expected_h)  {
                error("\nIndex " + i + ": mismatch at register H  -- actual=" + h + "   expected=" + expected_h);
                success = false;
            }
            if (l  != expected_l)  {
                error("\nIndex " + i + ": mismatch at register L  -- actual=" + l + "   expected=" + expected_l);
                success = false;
            }
            if (sp != expected_sp) {
                error("\nIndex " + i + ": mismatch at register SP -- actual=" + sp + "   expected=" + expected_sp);
                success = false;
            }
            if (pc != expected_pc) {
                error("\nIndex " + i + ": mismatch at register PC -- actual=" + pc + "   expected=" + expected_pc);
                success = false;
            }


            if (!success) {
                fail();
            }


            stepUut();
        }
        log("End of JSON reached.");
    }

    @Test
    public void testBiosCompletion() {
        mmu = new MemoryManager(new Cartridge(getClass().getResource("cpu_instrs.gb").getPath()), gpu);
        cpu = new Cpu(mmu);
        for (int i=0; cpu.getRegisterValue("PC") <  0x100; i++) {
            stepUut();

            if (i > 2100000) {
                fail("cpu reached 2.1 million cycles without breaking out of bios.");
            }
        }
    }
}
