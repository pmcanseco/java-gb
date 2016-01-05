package jGBC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Pablo on 1/5/2016.
 */
public class HexPrinter {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("C:\\Users\\Pablo\\Desktop\\opcodes.txt");
        Scanner sc = new Scanner(file);
        for(int i=128; i<256; i++) {
            System.out.println("cbInstructionMap.put((byte) 0xCB" + Integer.toHexString(i).toUpperCase() + ", Z80::" + sc.next() + ");");
        }
    }
}
