
public class Main {

    public static void main(String[] args) {
        Logger log = new Logger("Main");

        Cartridge cart = new Cartridge(Main.class.getResource("cpu_instrs.gb").getPath(), true);
        Gpu gpu = new Gpu(false);
        MemoryManager mmu = new MemoryManager(cart, gpu);
        Cpu cpu = new Cpu(mmu, gpu);


        //int width = 300;
        //int height = 300;
        //Gpu gpu = new Gpu(width, height);

        //gpu.reset();
        //LegacyMMU.reset(cart);
        //LegacyZ80.reset();

        //System.out.println(Gpu.BLACK.getRGB());
        //System.out.println(Gpu.DARK_GRAY.getRGB());
        //System.out.println(Gpu.LIGHT_GRAY.getRGB());
        //System.out.println(Gpu.WHITE.getRGB());

        //LegacyZ80.dispatcher(gpu);
        cpu.main();
    }
}
