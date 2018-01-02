
public class Main {

    public static void main(String[] args) {
        Cartridge cart = new Cartridge("C:\\Users\\Pablo\\Desktop\\cpu_instrs.gb", true);
        MemoryManager mmu = new MemoryManager(cart);
        Z80 z80 = new Z80(mmu);
        z80.getRegisterValue("A");

        int width = 300;
        int height = 300;
        main.java.GPU gpu = new main.java.GPU(width, height);

        gpu.reset();
        //LegacyMMU.reset(cart);
        //LegacyZ80.reset();

        System.out.println(main.java.GPU.BLACK.getRGB());
        System.out.println(main.java.GPU.DARK_GRAY.getRGB());
        System.out.println(main.java.GPU.LIGHT_GRAY.getRGB());
        System.out.println(main.java.GPU.WHITE.getRGB());

        //LegacyZ80.dispatcher(gpu);
    }
}
