import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) {
        Logger log = new Logger("Main");

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


        //Cartridge cart = new Cartridge("src/test/resources/gb-test-roms/cpu_instrs/cpu_instrs.gb", true);
        Cartridge cart = new Cartridge("src/main/resources/tetris.gb", true);
        Gpu gpu = new Gpu();
        MemoryManager mmu = new MemoryManager(cart, gpu);
        Cpu cpu = new Cpu(mmu, gpu);

        cpu.main();
    }
}

