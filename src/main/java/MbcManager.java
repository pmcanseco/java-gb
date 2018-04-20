import helpers.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pablo Canseco on 4/17/2018.
 */
public class MbcManager {
    private final String name = "MBC";
    private Logger log = new Logger(name, Logger.Level.WARN);

    public enum MbcType {
        ROM_ONLY,
        MBC1,
        MBC2,
        MBC3,
        MBC5,
        MBC07,
        MMM01, // ??
        HuC1,  // mbc1 with infrared hardware
        HuC3   // ??
    }

    public static class CartridgeType {
        public final int id;
        public final String name;
        private final MbcType mbcType;
        private final boolean hasRam;
        private final boolean hasBattery;
        private final boolean hasTimer;
        private final boolean hasRumble;
        private final boolean hasAccelerometer;
        public CartridgeType(int id, String name, MbcType mbcType,
                             boolean ram, boolean bat,
                             boolean tmr, boolean rmbl,
                             boolean accl) {
            this.id = id;
            this.name = name;
            this.mbcType = mbcType;
            this.hasRam = ram;
            this.hasBattery = bat;
            this.hasTimer = tmr;
            this.hasRumble = rmbl;
            this.hasAccelerometer = accl;
        }
    }
    public static final Map<Integer, CartridgeType> cartridgeTypes = new HashMap<>();
    static {
        cartridgeTypes.put(0x00, new CartridgeType(0x00, "ROM Only",             MbcType.ROM_ONLY, false,false, false, false, false));
        cartridgeTypes.put(0x01, new CartridgeType(0x01, "MBC1",                 MbcType.MBC1, false,false, false, false, false));
        cartridgeTypes.put(0x02, new CartridgeType(0x02, "MBC1 + RAM",           MbcType.MBC1, true, false, false, false, false));
        cartridgeTypes.put(0x03, new CartridgeType(0x03, "MBC1 + RAM + Battery", MbcType.MBC1, true, true,  false, false, false));
        cartridgeTypes.put(0x05, new CartridgeType(0x05, "MBC2",                 MbcType.MBC2, false,false, false, false, false));
        cartridgeTypes.put(0x06, new CartridgeType(0x06, "MBC2 + RAM + Battery", MbcType.MBC2, true, true,  false, false, false));
        cartridgeTypes.put(0x08, new CartridgeType(0x08, "ROM + RAM",            MbcType.ROM_ONLY, true, false, false, false, false));
        cartridgeTypes.put(0x09, new CartridgeType(0x09, "ROM + RAM + Battery",  MbcType.ROM_ONLY, true, true,  false, false, false));
        /*cartridgeTypes.put(0x0B, new CartridgeType(0x0B, "MMM01"));
        cartridgeTypes.put(0x0C, new CartridgeType(0x0C, "MMM01 + RAM"));
        cartridgeTypes.put(0x0D, new CartridgeType(0x0D, "MMM01 + RAM + Battery"));*/
        cartridgeTypes.put(0x0F, new CartridgeType(0x0F, "MBC3 + Timer + Battery",  MbcType.MBC3, false, true, true, false, false));
        cartridgeTypes.put(0x10, new CartridgeType(0x10, "MBC3 + RAM + Timer + Battery", MbcType.MBC3, true, true, true, false, false));
        cartridgeTypes.put(0x11, new CartridgeType(0x11, "MBC3",                    MbcType.MBC3, false, false, false, false, false));
        cartridgeTypes.put(0x12, new CartridgeType(0x12, "MBC3 + RAM",              MbcType.MBC3, true, false, false, false, false));
        cartridgeTypes.put(0x13, new CartridgeType(0x13, "MBC3 + RAM + Battery",    MbcType.MBC3, true, true, false, false, false));
        /*cartridgeTypes.put(0x19, new CartridgeType(0x19, "MBC5"));
        cartridgeTypes.put(0x1A, new CartridgeType(0x1A, "MBC5 + RAM"));
        cartridgeTypes.put(0x1B, new CartridgeType(0x1B, "MBC5 + RAM + Battery"));
        cartridgeTypes.put(0x1C, new CartridgeType(0x1C, "MBC5 + Rumble"));
        cartridgeTypes.put(0x1D, new CartridgeType(0x1D, "MBC5 + RAM + Rumble"));
        cartridgeTypes.put(0x1E, new CartridgeType(0x1E, "MBC5 + RAM + Battery + Rumble"));
        cartridgeTypes.put(0x20, new CartridgeType(0x20, "MBC6 + RAM + Battery"));
        cartridgeTypes.put(0x21, new CartridgeType(0x21, "Unused"));
        cartridgeTypes.put(0x22, new CartridgeType(0x22, "MBC7 + RAM + Bat. + Accelerometer"));
        cartridgeTypes.put(0xFC, new CartridgeType(0xFC, "POCKET CAMERA"));
        cartridgeTypes.put(0xFD, new CartridgeType(0xFD, "BANDAI TAMA5"));
        cartridgeTypes.put(0xFE, new CartridgeType(0xFE, "HuC3"));
        cartridgeTypes.put(0xFF, new CartridgeType(0xFF, "HuC1 + RAM + Battery"));*/
    }

    private final Cartridge cart;
    private final MbcType mbcType;
    private int romBankSelected = 1;
    private boolean ramEnabled;
    private int ramBankSelected = 0;
    private boolean isRomMode = true;
    private final boolean hasRam;
    private final boolean hasBattery;
    private final boolean hasTimer;
    private int[] ram;

    MbcManager(Cartridge cart) {
        this.cart = cart;
        this.mbcType    = cart.getCartridgeType().mbcType;
        this.hasRam     = cart.getCartridgeType().hasRam;
        this.hasBattery = cart.getCartridgeType().hasBattery;
        this.hasTimer   = cart.getCartridgeType().hasTimer;
        this.ram        = new int[cart.getRamSize()];
        for(int i : ram) {
            i = 0xFF;
        }
    }
    MbcManager(Cartridge cart, Logger.Level logLevel) {
        this(cart);
        this.log = new Logger(name, logLevel);
    }

    public int mbcRead(final int address) {
        switch (mbcType) {
            case ROM_ONLY:
                return cart.readFromAddress(address);
            case MBC1:
                if (address < 0x4000) {
                    return cart.readFromAddress(address);
                }
                else if (address < 0x8000){
                    int effectiveAddress = (romBankSelected * 0x4000) + (address - 0x4000);
                    return cart.readFromAddress(effectiveAddress);
                }
                else if (address >= 0xA000 && address <= 0xBFFF) {
                    if (!ramEnabled || !hasRam) {
                        return 0xFF;
                    }
                    else if (ramBankSelected == 0) {
                        log.debug(String.format("read from effective address 0x%04X, array 0x%04X", address, address - 0xA000));
                        return ram[address - 0xA000];
                    }
                    else {
                        int effectiveAddress = ((ramBankSelected * 0x2000) + (address - 0xA000));
                        log.debug(String.format("read from effective address 0x%04X, array 0x%04X", address, effectiveAddress));
                        return ram[effectiveAddress];
                    }
                }
                else {
                    log.error(String.format("mbcRead() at address 0x%04X is not supported", address));
                }
            default:
                log.warning(mbcType.name() + " is not implemented yet. Reading from provided address");
                return cart.readFromAddress(address);
        }
    }

    public void mbcWrite(final int address, final int value) {
        switch (mbcType) {
            case ROM_ONLY:
                log.debug("Cartridge is ROM_ONLY so writing to ROM won't do anything");
                break;
            case MBC1:

                if (address <= 0x1FFF) {
                    // lower 4 bits == 0xA means enable. Anything else disable.
                    ramEnabled = ((value & 0b0000_1111) == 0xA);
                    log.debug("ram enabled set to " + ramEnabled);
                }
                else if (address <= 0x3FFF) {
                    // write the lower 5 bits of romBank selection
                    romBankSelected = (value & 0b0001_1111);
                    log.debug("selected " + romBankSelected + " for rom bank low");

                    if (romBankSelected == 0) {
                        romBankSelected++;
                    }
                }
                else if (address <= 0x5FFF) {
                    if (isRomMode) {
                        romBankSelected &= 0b1001_1111; // clear bits 5 and 6
                        romBankSelected |= (value & 0b0000_0011) << 5; // replace bits 1 and 2 from value
                        log.debug("selected " + romBankSelected + " for rom bank high");
                        if (romBankSelected == 0 ||
                                romBankSelected == 0x20 ||
                                romBankSelected == 0x40 ||
                                romBankSelected == 0x60) {
                            romBankSelected++;
                        }
                    }
                    else {
                        ramBankSelected = (value & 0b0000_0011); // select a bank from 0 to 3
                        log.debug("selected ram bank " + ramBankSelected);
                    }
                }
                else if (address <= 0x7FFF) {
                    isRomMode = (value == 0); // 0=rom, 1=ram
                    log.debug("rom mode set to " + isRomMode);
                }
                else if (address <= 0xBFFF && address >= 0xA000) {
                    if (!ramEnabled || !hasRam) {
                        return;
                    }
                    else if (ramBankSelected == 0) {
                        log.debug(String.format("write ram at effective address 0x%04X, array 0x%04X", address, address - 0xA000));
                        ram[address - 0xA000] = value;
                    }
                    else {
                        int effectiveAddress = ((ramBankSelected * 0x2000) + (address - 0xA000));
                        log.debug(String.format("write ram at effective address 0x%04X, array 0x%04X", address, effectiveAddress));
                        ram[effectiveAddress] = value;
                    }
                }

                break;
            default:
                log.warning(mbcType.name() + " is not implemented yet. Rom write ignored.");
                break;
        }
    }
}
