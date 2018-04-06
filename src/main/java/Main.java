import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        Logger log = new Logger("Main");

        Cartridge cart = new Cartridge("src/main/resources/gb-test-roms/cpu_instrs/individual/10-bit ops.gb", true);
        Gpu gpu = new Gpu(false);
        MemoryManager mmu = new MemoryManager(cart, gpu);
        Cpu cpu = new Cpu(mmu, gpu);

        cpu.main();
    }
}
