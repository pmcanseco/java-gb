package jGBC;

public class Main {

    private static int width = 300;
    private static int height = 300;

    public static void main(String[] args) {
        Cartridge cart = new Cartridge("C:\\roms\\mario.gb");

        GPU gpu = new GPU(width, height);

        gpu.reset();
        MMU.reset(cart);
        Z80.reset();

        System.out.println(GPU.BLACK.getRGB());
        System.out.println(GPU.DARK_GRAY.getRGB());
        System.out.println(GPU.LIGHT_GRAY.getRGB());
        System.out.println(GPU.WHITE.getRGB());

        Z80.dispatcher(gpu);
    }
}
