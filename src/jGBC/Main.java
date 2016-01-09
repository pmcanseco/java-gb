package jGBC;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Cartridge cart = new Cartridge("C:\\roms\\mario.gb");



        int width = 300;
        int height = 300;
        JFrame frame = new JFrame("Gameboy");

        GPU gpu = new GPU(width, height);

        frame.setSize(width,  height);
        frame.add(gpu);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gpu.drawScreen();

        gpu.reset();
        MMU.reset(cart);
        Z80.reset();

        Z80.dispatcher(gpu);
    }
}
