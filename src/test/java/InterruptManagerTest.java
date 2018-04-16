import jdk.nashorn.internal.scripts.JO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pablo Canseco on 4/10/2018.
 */
public class InterruptManagerTest extends UnitTest {
    private InterruptManager getIM() {
        return InterruptManager.getInstance();
    }

    @Test
    public void testInterruptEnable() {
        InterruptManager.reset();

        getIM().enableInterrupt(InterruptManager.InterruptTypes.VBLANK);
        assertEquals(1, getIM().getInterruptsEnabled());

        getIM().enableInterrupt(InterruptManager.InterruptTypes.LCDC_STATUS);
        assertEquals(3, getIM().getInterruptsEnabled());

        getIM().enableInterrupt(InterruptManager.InterruptTypes.TIMER_OVERFLOW);
        assertEquals(7, getIM().getInterruptsEnabled());

        getIM().enableInterrupt(InterruptManager.InterruptTypes.SERIAL_TRANSFER_COMPLETE);
        assertEquals(15, getIM().getInterruptsEnabled());

        getIM().enableInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
        assertEquals(31, getIM().getInterruptsEnabled());

        getIM().disableInterrupt(InterruptManager.InterruptTypes.VBLANK);
        assertEquals(30, getIM().getInterruptsEnabled());

        getIM().disableInterrupt(InterruptManager.InterruptTypes.SERIAL_TRANSFER_COMPLETE);
        assertEquals(22, getIM().getInterruptsEnabled());
    }

    @Test
    public void testInterruptRaised() {
        InterruptManager.reset();

        getIM().raiseInterrupt(InterruptManager.InterruptTypes.VBLANK);
        assertEquals(1, getIM().getInterruptsRaised());
        assertEquals(1, getIM().getRaisedInterrupts().size());

        getIM().raiseInterrupt(InterruptManager.InterruptTypes.TIMER_OVERFLOW);
        assertEquals(5, getIM().getInterruptsRaised());
        assertEquals(2, getIM().getRaisedInterrupts().size());

        getIM().clearInterrupt(InterruptManager.InterruptTypes.VBLANK);
        assertEquals(4, getIM().getInterruptsRaised());
        assertEquals(1, getIM().getRaisedInterrupts().size());

        getIM().raiseInterrupt(InterruptManager.InterruptTypes.JOYPAD_INPUT);
        assertEquals(20, getIM().getInterruptsRaised());
        assertEquals(2, getIM().getRaisedInterrupts().size());
    }

}