import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pablo Canseco on 4/17/2018.
 */
public class MbcManager {

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
        public final MbcType mbcType;
        public final boolean hasRam;
        public final boolean hasBattery;
        public final boolean hasTimer;
        public final boolean hasRumble;
        public final boolean hasAccelerometer;
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
        cartridgeTypes.put(0x00, new MbcManager.CartridgeType(0x00, "ROM Only",             MbcType.ROM_ONLY, false,false, false, false, false));
        cartridgeTypes.put(0x01, new MbcManager.CartridgeType(0x01, "MBC1",                 MbcType.MBC1, false,false, false, false, false));
        cartridgeTypes.put(0x02, new MbcManager.CartridgeType(0x02, "MBC1 + RAM",           MbcType.MBC1, true, false, false, false, false));
        cartridgeTypes.put(0x03, new MbcManager.CartridgeType(0x03, "MBC1 + RAM + Battery", MbcType.MBC1, true, true,  false, false, false));
        cartridgeTypes.put(0x05, new MbcManager.CartridgeType(0x05, "MBC2",                 MbcType.MBC2, false,false, false, false, false));
        cartridgeTypes.put(0x06, new MbcManager.CartridgeType(0x06, "MBC2 + RAM + Battery", MbcType.MBC2, true, true,  false, false, false));
        cartridgeTypes.put(0x08, new MbcManager.CartridgeType(0x08, "ROM + RAM",            MbcType.ROM_ONLY, true, false, false, false, false));
        cartridgeTypes.put(0x09, new MbcManager.CartridgeType(0x09, "ROM + RAM + Battery",  MbcType.ROM_ONLY, true, true,  false, false, false));
        /*cartridgeTypes.put(0x0B, new CartridgeType(0x0B, "MMM01"));
        cartridgeTypes.put(0x0C, new CartridgeType(0x0C, "MMM01 + RAM"));
        cartridgeTypes.put(0x0D, new CartridgeType(0x0D, "MMM01 + RAM + Battery"));
        cartridgeTypes.put(0x0F, new CartridgeType(0x0F, "MBC3 + Timer + Battery"));
        cartridgeTypes.put(0x10, new CartridgeType(0x10, "MBC3 + RAM + Timer + Battery"));
        cartridgeTypes.put(0x11, new CartridgeType(0x11, "MBC3"));
        cartridgeTypes.put(0x12, new CartridgeType(0x12, "MBC3 + RAM"));
        cartridgeTypes.put(0x13, new CartridgeType(0x13, "MBC3 + RAM + Battery"));
        cartridgeTypes.put(0x19, new CartridgeType(0x19, "MBC5"));
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
    private int romBankSelected;
    private boolean ramEnabled;
    private int ramBankSelected;
    private boolean isRomMode;

    public MbcManager(Cartridge cart) {
        this.cart = cart;
    }

    public int readFromAddress(int address) {
        return cart.readFromAddress(address);
    }

    public MbcType getMbcType() {
        return this.cart.getCartridgeType().mbcType;
    }


}
