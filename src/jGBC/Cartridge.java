package jGBC;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class Cartridge {
    private int[] rom; // the entire contents
    private String title; // the game title
    private String locale; // japanese or non-japanese?
    private int checksum; // see verifyRom()
    private int expectedChecksum; // see verifyRom()
    private int logoChecksum; // see verifyRom()
    private int expectedLogoChecksum; // see verifyRom()

    Cartridge(String path) {
        loadRomFile(path);
        System.out.println("Title:\t\t"+title);
        System.out.println("Locale:\t\t"+locale);
        System.out.println("Checksum:\t( "+checksum+" == "+expectedChecksum+" ) is "+(checksum==expectedChecksum));
        System.out.println("Logo Check:\t( "+logoChecksum+" == "+expectedLogoChecksum+" ) is "+(logoChecksum==expectedLogoChecksum));
    }

    private void loadRomFile(String romPath) {
        System.out.println("Loading ROM: " + romPath);
        try {
            byte[] tempRom = Files.readAllBytes(new File(romPath).toPath());
            rom = new int[tempRom.length];
            for(int i = 0; i < tempRom.length; i++) {
                rom[i] = tempRom[i] & 0xFF;
            }
            verifyRom();
        } catch(Exception ex) {
            System.out.println("Exception: " + ex);
            System.out.println("Please enter valid Rom file path: ");
            Scanner sc = new Scanner(System.in);
            loadRomFile(sc.nextLine());
        }
    }
    private void verifyRom() {
        // bytes at 0x0134 through 0x0143 contain the title
        StringBuilder sb = new StringBuilder();
        for(int i=0x134; i<0x143; i++) sb.append((char) rom[i]);
        title = sb.toString();

        // 0x014A contains the destination code. 0 = Japan, 1 = not Japan
        if(rom[0x14A] == 0x00)  locale = "Japanese";
        else if(rom[0x14A] == 0x01) locale = "Non-Japanese";
        else locale = "Unknown";

        // 0x014D is the header checksum.
        // Contains an 8 bit checksum across the cartridge header bytes 0134-014C.
        // Formula: x=0:FOR i=0134h TO 014Ch:x=x-MEM[i]-1:NEXT
        // The lower 8 bits of the result must be the same than the value in this entry.
        // The GAME WON'T WORK if this checksum is incorrect.
        checksum = rom[0x014D];
        expectedChecksum = 0;
        for(int i=0x0134; i<=0x014C; i++) {
            expectedChecksum=expectedChecksum-rom[i]-1;
        }
        expectedChecksum &= 255; // mask to lower 8 bits

        //0x0104 through 0x0133 are the Nintendo Logo bitmap bytes.
        //They are verified here by adding up the ones in the bios
        //and comparing them to the ones in the rom.
        expectedLogoChecksum = 0;
        logoChecksum = 0;
        for (int i : MMU.logo) expectedLogoChecksum += i;
        for(int i=0x104; i <= 0x0133; i++) logoChecksum += rom[i];
    }

    public int readFromAddress(int address) {
        return rom[address];
    }


}
