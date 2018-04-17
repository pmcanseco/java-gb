import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import static org.kohsuke.args4j.OptionHandlerFilter.ALL;

public class Main {

    @Option(name = "-sb", aliases = { "--skip-bootrom" }, /*required = false,*/
            usage = "If ran with the -sb flag, the bootrom will be skipped and the" +
                    "game will immediately begin executing.")
    public static boolean skipBootRom;

    public static void main(String[] args) {
        Logger log = new Logger("Main");
        CmdLineParser parser = new CmdLineParser(Main.class);
        try {
            parser.parseArgument(args);
        }
        catch(CmdLineException e ) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java Main "+parser.printExample(ALL));

            return;
        }

        //log.info("Skip Bootrom set to " + skipBootrom);


        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/01-special.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/02-interrupts.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/03-op sp,hl.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/04-op r,imm.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/05-op rp.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/06-ld r,r.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/07-jr,jp,call,ret,rst.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/09-op r,r.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/individual/10-bit ops.gb", true);


        // mem_timing
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/mem_timing/individual/01-read_timing.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/mem_timing/individual/02-write_timing.gb", true);

        // instr_timing
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/instr_timing/instr_timing.gb", true);

        // mem_timing
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/mem_timing/mem_timing.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/mem_timing-2/mem_timing.gb", true);



        // mooneye
        //Cartridge cart = new Cartridge("src/test/resources/mooneye-gb-test-roms/tests/acceptance/bits/reg_f.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/mooneye-gb-test-roms/tests/acceptance/bits/unused_hwio-GS.gb", true);

        //Cartridge cart = new Cartridge("src/test/resources/mooneye-gb-test-roms/tests/acceptance/interrupts/ie_push.gb", true);

        //Cartridge cart = new Cartridge("src/test/resources/mooneye-gb-test-roms/tests/acceptance/timer/div_write.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/mooneye-gb-test-roms/tests/acceptance/timer/rapid_toggle.gb", true);
        //Cartridge cart = new Cartridge("src/test/resources/mooneye-gb-test-roms/tests/acceptance/timer/tim00.gb", true);


        Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/cpu_instrs.gb", true);
        //Cartridge cart = new Cartridge("src/main/resources/tetris.gb", true);

        MbcManager mbc = new MbcManager(cart);
        Gpu gpu = new Gpu();
        MemoryManager mmu = new MemoryManager(mbc, gpu);
        Cpu cpu = new Cpu(mmu, gpu);

        cpu.main();
    }
}

