package jGBC;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GPU extends JPanel{

	private static final long serialVersionUID = 4550660364461408923L;
	
	private BufferedImage canvas;

	public GPU(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        fillCanvas(Color.BLUE);
    }
	
	public void fillCanvas(Color c) {
        int color = c.getRGB();
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

	
	public static void main(String[] args) {
		int width = 640;
        int height = 480;
        JFrame frame = new JFrame("Direct draw demo");

        GPU gpu = new GPU(width, height);

        frame.setSize(width,  height);
        frame.add(gpu);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Integer i=0;
        while(true) {
        	gpu.fillCanvas(Color.decode(i.toString()));
        	i++;
        }
	}
}
