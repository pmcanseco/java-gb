public class Main {

    public static void main(String[] args) {
        Cartridge cart = new Cartridge("C:\\Users\\Pablo\\Desktop\\cpu_instrs\\cpu_instrs.gb");
        MemoryManager mmu = new MemoryManager(cart);
        Z80 z80 = new Z80(mmu);
        z80.getRegisterValue("A");

        int width = 300;
        int height = 300;
        GPU gpu = new GPU(width, height);

        gpu.reset();
        //LegacyMMU.reset(cart);
        //LegacyZ80.reset();

        System.out.println(GPU.BLACK.getRGB());
        System.out.println(GPU.DARK_GRAY.getRGB());
        System.out.println(GPU.LIGHT_GRAY.getRGB());
        System.out.println(GPU.WHITE.getRGB());

        //LegacyZ80.dispatcher(gpu);
    }
}
