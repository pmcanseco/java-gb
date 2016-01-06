package jGBC;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GPU extends JPanel{

	private static final long serialVersionUID = 4550660364461408923L;
	
	private BufferedImage canvas;
	
	static int[] screen = new int[160*144*4];
	static int[] vram = new int[8192];
	private static int[][] palette = new int[4][4];
	private int mode = 0;
	private int modeclock = 0;
	private static int line = 0;
	private static int bgmap = 0;
	private static int bgtile = 0;
	private static int switchbg = 0;
	private static int switchlcd = 0;
	private static int scy = 0;
	private static int scx = 0;
	static int[][][] tileset = new int[384][8][8];
	
	public Random rng = new Random();
	public GPU(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        fillCanvas(Color.BLUE);
        for(int i=0; i<160*144*4; i++){
        	screen[i] = rng.nextInt(255);
        }
    }
	

	public void step() {
		modeclock += Z80.Reg.t;
		switch(mode) {
		// object attribute memory mode, scanline active
		case 2:
			if(modeclock >= 80) { //80 clocks spent here
				modeclock = 0;
				mode = 3;
			}
			break;
		
		// VRAM read mode, scanline active
		// end of mode 3 is end of scanline
		case 3:
			if(modeclock >= 172) { // 172 clocks spent here
				modeclock  = 0;
				mode = 0;
				
				renderscan();
			}
			break;
			
		// Hblank
		// After the last hblank, push the screen data to canvas
		case 0:
			if(modeclock >= 204) { // 204 clocks spent here
				modeclock = 0;
				line++;
				if(line == 143) {
					mode = 1;
					drawScreen();
				}
				else mode = 2;
			}
		break;
		
		//Vblank (10 lines)
		case 1:
			if(modeclock >= 456) { // 456 clocks spent here
				modeclock = 0;
				line++;
				if(line > 153) {
					// restart scanning modes
					mode = 2;
					line = 0;
				}
			}
			break;
		}
	}

	public void reset() {
		tileset = new int[384][8][8];
		for(int i=0; i<384; i++) {
			for(int j=0; j<8; j++) {
				for(int k=0; k<8; k++) {
					tileset[i][j][k] = 0;
				}
			}
		}
		System.out.println("GPU Reset");
	}
	
	public static void updatetile(int addr, int val) {
		// get the base address for thie tile row
		addr &= 0xFF;
		addr &= 0x1FFE;
		
		//evaluate which tile and row was updated
		int tile = (addr >> 4) & 511;
		int y = (addr >> 1) & 7;
		
		int sx;
		for(int x=0; x<8; x++) {
			sx = 1 << (7-x); // find bit index for this pixel
			tileset[tile][y][x] =  ((((vram[addr] & sx)!=0) ? 1 : 0) + (((vram[addr+1] & sx)!=0) ? 2 : 0));
		}
	}

	public static void renderscan() {
		// VRAM offset for the tile map
		int mapOffsets =  (bgmap!=0 ? 0x1C00 : 0x1800);
		
		// which line of tiles to use in the map
		mapOffsets += ((line + scy) & 255) >> 3;
			
		// which tile to start with in the map line
		int lineOffsets = (scx >> 3);
		
		// which line of pixels to use inthe tiles
		int y = ((line + scy) & 7);
		
		// where in the tile line to start
		int x =  (scx & 7);
		
		// where to render on the canvas
		int canvasOffsets =  (line * 160 * 4);
		
		// read tile index from the background map
		int[] color;
		int tile = vram[mapOffsets + lineOffsets];
		
		// if tile data set is #1, use signed indices, calc real tile offset
		if(bgtile == 1 && tile < 128) tile += 256;
		
		for(int i=0; i<160; i++) {
			//re-map the tile pixel through the palette
			color = palette[tileset[tile][y][x]];
			
			//plot pixel to canvas
			screen[canvasOffsets] = color[0];
			screen[canvasOffsets+1] = color[1];
			screen[canvasOffsets+2] = color[2];
			screen[canvasOffsets+3] = color[3];
			canvasOffsets += 4;
			
			//when this tile ends, read another
			x++;
			if(x==8) {
				x=0;
				lineOffsets =  ((lineOffsets + 1) & 31);
				tile = vram[mapOffsets + lineOffsets];
				if(bgtile == 1 && tile < 128) tile += 256;
			}
		}
	}

	public static int rb(int addr) {
		switch(addr) {
		// LCD Control
		case 0xFF40:
			return   
				   ((switchbg!=0  ? 0x01 : 0x00) |
				    (bgmap!=0     ? 0x08 : 0x00) |
				    (bgtile!=0    ? 0x10 : 0x00) |
				    (switchlcd!=0 ? 0x80 : 0x00));
		
		// Scroll Y
		case 0xFF42:
			return scy;
			
		// Scroll X
		case 0xFF43:
			return scx;
		
		// Current Scanline
		case 0xFF44:
			return line;
		}
		return 0;
	}

	public static void wb(int addr, int val) {
		switch(addr) {
		// LCD Control
		case 0xFF40:
			GPU.switchbg  = (((val & 0x01) !=0) ? 1 : 0);
			GPU.bgmap     = (((val & 0x08) !=0) ? 1 : 0);
			GPU.bgtile    = (((val & 0x10) !=0) ? 1 : 0);
			GPU.switchlcd = (((val & 0x80) !=0) ? 1 : 0);
			break;
			
		// Scroll Y
		case 0xFF42:
			scy = val;
			break;
			
		// Scroll X
		case 0xFF43:
			scx = val;
			break;
			
		// Background palette
		case 0xFF47:
			for(int i=0; i<4; i++) {
				switch((val >> (i*2)) & 3) {
				case 0: palette[i][0] = palette[i][1] = palette[i][2] = palette[i][3] = 255; break;
				case 1: palette[i][0] = palette[i][1] = palette[i][2] =  192; palette[i][3] =  255; break;
				case 2: palette[i][0] = palette[i][1] = palette[i][2] =  96; palette[i][3] =  255; break;
				case 3: palette[i][0] = palette[i][1] = palette[i][2] =  0; palette[i][3] =  255; break;
				}
			}
			break;
		}
	}
	
	///// canvas helper functions /////
	public void fillCanvas(Color c) {
        int color = c.getRGB();
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }
	
	public void drawScreen() {
       //for(int i=0; i<160*144*4; i++){
       // 	screen[i] =  255;
       // }

		for(int i=0; i<160; i++) {
			for(int j=0; j<144; j++) {
				int r = screen[i*j];
				int g = screen[i*j+1];
				int b = screen[i*j+2];
				if(r>255) r = 255;
				if(g>255) g = 255;
				if(b>255) b = 255;
				canvas.setRGB(i, j, new Color(r, g, b).getRGB());
			}
		}


		repaint();
		//System.out.println("repaint");
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

	
	public static void main(String[] args) {
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
        MMU.reset();
        Z80.reset();
        
        try {
			MMU.load("C:\\roms\\TESTGAME.GB");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        Z80.dispatcher(gpu);

	}
}
