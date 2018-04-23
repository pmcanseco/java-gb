import helpers.Logger;

/**
 * File created on 4/10/2018.
 * Implementation lifted from https://github.com/trekawek/coffee-gb
 */
public class TimerService {
    private Logger log = new Logger("TIM", Logger.Level.INFO);

    // Singleton
    private static TimerService instance;

    public static TimerService getInstance() {
        if (instance == null) {
            instance = new TimerService();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private TimerService() {
    }

    private static final int[] FREQ_TO_BIT = {9, 3, 5, 7};

    private int div, tac, tma, tima;

    private boolean previousBit;

    private boolean overflow;

    private int ticksSinceOverflow;

    public void step(int numSteps) {
        for (int i = 0; i < numSteps; i++) {
            tick();
        }
    }

    public void tick() {
        updateDiv((div + 1) & 0xffff);
        if (overflow) {
            ticksSinceOverflow++;
            if (ticksSinceOverflow == 4) {
                InterruptManager.getInstance().raiseInterrupt(InterruptManager.InterruptTypes.TIMER_OVERFLOW);
            }
            if (ticksSinceOverflow == 5) {
                tima = tma;
            }
            if (ticksSinceOverflow == 6) {
                tima = tma;
                overflow = false;
                ticksSinceOverflow = 0;
            }
        }
    }

    private void incTima() {
        tima++;
        tima %= 0x100;
        if (tima == 0) {
            overflow = true;
            ticksSinceOverflow = 0;
        }
    }

    public void clearDivider() {
        updateDiv(0);
    }

    public int getDivider() {
        return div >> 8;
    }

    public int getCounter() {
        return this.tima;
    }

    public int getModulo() {
        return this.tma;
    }

    public int getControl() {
        return this.tac;
    }

    public void setCounter(int value) {
        if (ticksSinceOverflow < 5) {
            tima = value;
            overflow = false;
            ticksSinceOverflow = 0;
        }
    }

    public void setModulo(int value) {
        this.tma = value;
    }

    public void setControl(int value) {
        this.tac = value;
    }

    private void updateDiv(int newDiv) {
        this.div = newDiv;
        int bitPos = FREQ_TO_BIT[tac & 0b11];
        //bitPos <<= 2 - 1; // uncomment for double speed mode
        boolean bit = (div & (1 << bitPos)) != 0;
        bit &= (tac & (1 << 2)) != 0;
        if (!bit && previousBit) {
            incTima();
        }
        previousBit = bit;
    }

    public void setDivBypass(int value) {
        this.div = value;
    }
}
