import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pablo Canseco on 4/17/2018.
 */
public class TimerTest extends UnitTest {
    private TimerService timer = TimerService.getInstance();

    @Test
    public void testDivIncrementAndRate() {
        TimerService.reset();
        timer = TimerService.getInstance();

        timer.setControl(0b0000_0000); // timer set to 4096Hz, disabled
        // DIV should increment every 256 clocks regardless of the TAC timer enable bit.

        timer.step(4); // spend 4 clocks
        assertEquals(0, timer.getCounter());
        assertEquals(0, timer.getDivider());

        timer.step(251); // spend another 251, total so far = 255
        assertEquals(0, timer.getDivider());
        assertEquals(0, timer.getCounter());

        timer.step(1); // spend another 1, total so far = 256
        assertEquals(1, timer.getDivider());
        assertEquals(0, timer.getCounter());

        timer.step(256); // spend another 256, total so far = 512
        assertEquals(2, timer.getDivider());
        assertEquals(0, timer.getCounter());

        timer.step(255); // spend another 255, total so far = 767
        assertEquals(2, timer.getDivider());
        assertEquals(0, timer.getCounter());

        timer.step(1); // spend another 1, total so far = 768
        assertEquals(3, timer.getDivider());
        assertEquals(0, timer.getCounter());
    }

    @Test
    public void testDivAndCounterIncrement() {
        TimerService.reset();
        timer = TimerService.getInstance();

        timer.clearDivider();
        timer.setControl(0b0000_0100); // timer set to 4096Hz, enabled

        timer.step(255);
        assertEquals(0, timer.getDivider());
        assertEquals(0, timer.getCounter());

        timer.step(5);
        assertEquals(1, timer.getDivider());
        assertEquals(0, timer.getCounter());

        timer.step(255);
        timer.step(255);
        timer.step(255);

        // 1025 clocks have passed. div should be 4 and counter should be 1
        assertEquals(4, timer.getDivider());
        assertEquals(1, timer.getCounter());

        //NOT YET: obscure behavior: this should clear both divider and counter
        timer.clearDivider();
        assertEquals(0, timer.getDivider());
        assertEquals(0, timer.getCounter());
    }

    @Test
    public void testCounterVariousRates() {
        TimerService.reset();
        timer = TimerService.getInstance();

        timer.setControl(0b0000_0100); // enabled, 4096Hz
        assertEquals(0, timer.getCounter());
        timer.step(256);
        timer.step(256);
        timer.step(256);
        timer.step(256);
        assertEquals(1, timer.getCounter());
        timer.step(256);
        timer.step(256);
        timer.step(256);
        timer.step(256);
        assertEquals(2, timer.getCounter());

        timer.setCounter(0);
        timer.setControl(0b0000_0101); // enabled, 262144Hz
        assertEquals(0, timer.getCounter());
        timer.step(16);
        assertEquals(1, timer.getCounter());
        timer.step(16);
        assertEquals(2, timer.getCounter());

        timer.setCounter(0);
        timer.setControl(0b0000_0110); // enabled, 65536Hz
        assertEquals(0, timer.getCounter());
        timer.step(64);
        assertEquals(1, timer.getCounter());
        timer.step(64);
        assertEquals(2, timer.getCounter());

        timer.setCounter(0);
        timer.setControl(0b0000_0111); // enabled, 16384Hz
        assertEquals(0, timer.getCounter());
        timer.step(256);
        assertEquals(1, timer.getCounter());
        timer.step(256);
        assertEquals(2, timer.getCounter());
    }

    @Test
    public void testCounterGetsModuloOnOverflow() {
        Random rng = new Random();

        TimerService.reset();
        timer = TimerService.getInstance();

        timer.setControl(0b0000_0100); // enabled, 4096Hz
        assertEquals(0, timer.getCounter());

        int randomModulo = rng.nextInt(256);
        timer.setCounter(254);
        timer.setModulo(randomModulo);
        assertEquals(randomModulo, timer.getModulo());

        timer.step(256);
        timer.step(256);
        timer.step(256);
        timer.step(255);
        assertEquals(254, timer.getCounter());
        timer.step(1);
        assertEquals(255, timer.getCounter());
        timer.step(256);
        timer.step(256);
        timer.step(256);
        timer.step(256);

        // counter (TIMA) has now overflowed to 256,
        // so it gets overwritten with what's in modulo (TMA)
        assertEquals(randomModulo, timer.getCounter());
    }

}
