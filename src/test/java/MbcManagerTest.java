import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pablo Canseco on 4/17/2018.
 */
public class MbcManagerTest extends UnitTest {
    private final String cpuInstrsPath = "src/test/resources/gb-test-roms/cpu_instrs/cpu_instrs.gb";
    private MbcManager cartMbc = new MbcManager(new Cartridge(cpuInstrsPath, true));

    @Test
    public void testMbc1RomBankSwitch() {
        log("0x3fff = " + cartMbc.mbcRead(0x3fff));
        log("0x4000 = " + cartMbc.mbcRead(0x4000));
        log("0x4001 = " + cartMbc.mbcRead(0x4001));
        log("0x4002 = " + cartMbc.mbcRead(0x4002));
        log("0x4003 = " + cartMbc.mbcRead(0x4003));
        log("0x4004 = " + cartMbc.mbcRead(0x4004));
        log("0x4005 = " + cartMbc.mbcRead(0x4005));

        cartMbc.mbcWrite(0x2000, 2);

        log("0x3fff = " + cartMbc.mbcRead(0x3fff));
        log("0x4000 = " + cartMbc.mbcRead(0x4000));
        log("0x4001 = " + cartMbc.mbcRead(0x4001));
        log("0x4002 = " + cartMbc.mbcRead(0x4002));
        log("0x4003 = " + cartMbc.mbcRead(0x4003));
        log("0x4004 = " + cartMbc.mbcRead(0x4004));
        log("0x4005 = " + cartMbc.mbcRead(0x4005));

        cartMbc.mbcWrite(0x2000, 3);

        log("0x3fff = " + cartMbc.mbcRead(0x3fff));
        log("0x4000 = " + cartMbc.mbcRead(0x4000));
        log("0x4001 = " + cartMbc.mbcRead(0x4001));
        log("0x4002 = " + cartMbc.mbcRead(0x4002));
        log("0x4003 = " + cartMbc.mbcRead(0x4003));
        log("0x4004 = " + cartMbc.mbcRead(0x4004));
        log("0x4005 = " + cartMbc.mbcRead(0x4005));
    }

    @Test
    public void testMbc1RamBankSwitch() {
        cartMbc = new MbcManager(new Cartridge("src/main/resources/pokebluejp.gb", true));

        cartMbc.mbcWrite(0x1FFF, 0xA); // enable RAM
        cartMbc.mbcWrite(0x7FFF, 1);   // set mbc to ram mode

        // effective address as-is, ram bank 0
        log("0xA000 = " + cartMbc.mbcRead(0xA000));
        log("0xA001 = " + cartMbc.mbcRead(0xA001));
        log("0xA002 = " + cartMbc.mbcRead(0xA002));
        cartMbc.mbcWrite(0xA002, 50);
        assertEquals(50, cartMbc.mbcRead(0xA002));

        cartMbc.mbcWrite(0x5FFF, 1); // select bank 1
        log("0xA000 = " + cartMbc.mbcRead(0xA000));
        log("0xA001 = " + cartMbc.mbcRead(0xA001));
        log("0xA002 = " + cartMbc.mbcRead(0xA002));
        cartMbc.mbcWrite(0xA002, 123);
        assertEquals(123, cartMbc.mbcRead(0xA002));

        cartMbc.mbcWrite(0x5FFF, 2); // select bank 2
        log("0xA000 = " + cartMbc.mbcRead(0xA000));
        log("0xA001 = " + cartMbc.mbcRead(0xA001));
        log("0xA002 = " + cartMbc.mbcRead(0xA002));
        cartMbc.mbcWrite(0xA002, 99);
        assertEquals(99, cartMbc.mbcRead(0xA002));

        cartMbc.mbcWrite(0x5FFF, 0); // select bank 0
        assertEquals(50, cartMbc.mbcRead(0xA002));
        cartMbc.mbcWrite(0x5FFF, 1); // select bank 1
        assertEquals(123, cartMbc.mbcRead(0xA002));
        cartMbc.mbcWrite(0x5FFF, 2); // select bank 1
        assertEquals(99, cartMbc.mbcRead(0xA002));


        cartMbc.mbcWrite(0x1FFF, 0x0); // disable RAM
        assertEquals(255, cartMbc.mbcRead(0xA002));
    }
}
