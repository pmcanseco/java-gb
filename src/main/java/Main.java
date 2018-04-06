
public class Main {

    public static void main(String[] args) {
        Logger log = new Logger("Main");

        Cartridge cart = new Cartridge(Main.class.getResource("cpu_instrs.gb").getPath(), true);
        Gpu gpu = new Gpu(false);
        MemoryManager mmu = new MemoryManager(cart, gpu);
        Cpu cpu = new Cpu(mmu, gpu);

        cpu.main();
    }
}
