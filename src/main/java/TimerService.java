/**
 * Created by Pablo Canseco on 4/10/2018.
 */
public class TimerService {
    private Logger log = new Logger("TIM");

    // Singleton
    private static TimerService instance;
    public static TimerService getInstance() {
        if (instance == null) {
            instance = new TimerService();
            return instance;
        }
        return instance;
    }
    public static void reset() {
        instance = null;
    }
    private TimerService() {}

    // members
    private static final int gameboyCpuSpeed = 4194304;

    private int divider = 0;
    private int counter = 0;
    private int modulo = 0;
    private boolean isRunning = false;
    private TimerSpeed speed = TimerSpeed.HZ_4096;
    public enum TimerSpeed {
        HZ_4096  (0, 4096  ),
        HZ_262144(1, 262144),
        HZ_65536 (2, 65536 ),
        HZ_16384 (3, 16384 );

        //<editor-fold desc=" IMPLEMENTATION " default-state="collapsed">
        private int controlValue;
        private int speedInHertz;
        TimerSpeed(int controlValue, int hertz) {
            this.controlValue = controlValue;
            this.speedInHertz = hertz;
        }
        static TimerSpeed findByControlValue(int ctrlVal) {
            for (TimerSpeed ts : TimerSpeed.values()) {
                if (ts.controlValue == ctrlVal) {
                    return ts;
                }
            }
            return null;
        }
        //</editor-fold>
    }
    private int divClock = 0;
    private int timerClock = 0;

    // functions
    public void step(int clocksElapsed) {
        divClock += clocksElapsed;
        if (divClock >= 255) {
            divider++;
            divider &= 0b1111_1111;
            divClock %= 256;
            //log.info("DIV=" + divider);
        }

        if (isRunning) {
            timerClock += clocksElapsed;
            int numCyclesToIncrement = getnumCyclesToIncrement();

            if (timerClock >= numCyclesToIncrement) {
                counter++;
                timerClock %= numCyclesToIncrement;
                if (counter >= 256) { // overflow
                    counter = modulo;
                    InterruptManager.getInstance().raiseInterrupt(
                            InterruptManager.InterruptTypes.TIMER_OVERFLOW);
                }
            }
        }
    }
    public void clearDivider() {
        this.divider = 0;
        this.counter = 0;

        // obscure hardware behavior
        if (timerClock >= (getnumCyclesToIncrement() / 2)) {
            counter++;
        }

        log.warning("cleared divider");
    }
    public int getDivider() {
        return this.divider;
    }
    public void setCounter(int value) {
        this.counter = value;
        log.warning("setting counter to " + value);
    }
    public int getCounter() {
        log.warning("read counter = " + this.counter);
        return this.counter;
    }
    public void setControl(int value) {
        int speedValue   = value & 0b0000_0011;
        int runningValue = value & 0b0000_0100;

        // obscure behavior in the hardware
        if (this.speed.controlValue == 0 && speedValue == 1 && runningValue != 0) {
            counter++;
        }

        this.speed = TimerSpeed.findByControlValue(speedValue);
        this.isRunning = runningValue != 0;

        // obscure behavior in the hardware:
        if (!this.isRunning) {
            if (timerClock >= (getnumCyclesToIncrement() / 2)) {
                log.warning("OBSCURE BEHAVIOR 1");
                this.counter++;
            }
        }

        log.warning("Configured timer for:" +
                "\n\tspeed=" + this.speed.name() +
                "\n\trunning=" + isRunning +
                "\n\tclocksToIncrementCounter=" + getnumCyclesToIncrement());
    }
    public int getControl() {
        int retval = 0;
        retval |= (this.speed.controlValue); // 0000_0001
        retval |= ((this.isRunning ? 1 : 0) << 2);
        return retval;
    }
    public void setModulo(int value) {
        log.warning("setting modulo to " + value);
        this.modulo = value;
    }
    public int getModulo() {
        return this.modulo;
    }
    private int getnumCyclesToIncrement() {
        return (gameboyCpuSpeed / speed.speedInHertz);
    }
}
