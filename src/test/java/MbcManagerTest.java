import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pablo Canseco on 4/17/2018.
 */
public class MbcManagerTest extends UnitTest {
    private final String cpuInstrsPath = "src/test/resources/gb-test-roms/cpu_instrs/cpu_instrs.gb";
    private final MbcManager cartMbc = new MbcManager(new Cartridge(cpuInstrsPath, true));

    @Test
    public void testMbc1BankSwitch() {
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
}
