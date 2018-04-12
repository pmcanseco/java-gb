import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pablo Canseco on 4/10/2018.
 */
public class InterruptManager {
    Logger log = new Logger("INT");

    public enum InterruptTypes {

        JOYPAD_INPUT(16, 0x60),
        SERIAL_TRANSFER_COMPLETE(8, 0x58),
        TIMER_OVERFLOW(4, 0x50),
        LCDC_STATUS(2, 0x48),
        VBLANK(1, 0x40);

        public final int bit;
        public final int handler;
        InterruptTypes(int bit, int handler) {
            this.bit = bit;
            this.handler = handler;
        }
    }

    public class Interrupt {
        public final String name;
        private boolean isRaised;
        private boolean isEnabled;

        private Interrupt(String name) {
            this.name = name;
            this.isRaised = false;
            this.isEnabled = false;
        }
        //<editor-fold desc=" IMPLEMENTATION " default-state="collapsed">
        public boolean isRaised() {
            return isRaised;
        }
        public boolean isEnabled() {
            return isEnabled;
        }
        public void clear() {
            this.isRaised = false;
        }
        public void disable() {
            this.isEnabled = false;
        }
        public void raise() {
            this.isRaised = true;
        }
        public void enable() {
            this.isEnabled = true;
        }
        //</editor-fold>
    }

    private Map<InterruptTypes, Interrupt> ints = new HashMap<>();
    private boolean masterEnable = false;

    // Singleton
    private static InterruptManager instance;
    public static InterruptManager getInstance() {
        if(instance == null) {
            instance = new InterruptManager();
            return instance;
        }
        return instance;
    }
    public static void reset() {
        instance = null;
    }

    private InterruptManager() {
        for (InterruptTypes i : InterruptTypes.values()) {
            ints.put(i, new Interrupt(i.name()));
        }
    }

    public void masterEnable() {
        this.masterEnable = true;
    }

    public void masterDisable() {
        this.masterEnable = false;
    }

    public boolean isMasterEnabled() {
        return this.masterEnable;
    }

    public Map<InterruptTypes, Interrupt> getRaisedInterrupts() {
        Map<InterruptTypes, Interrupt> raisedInts = new HashMap<>();
        for (Map.Entry<InterruptTypes, Interrupt> e : this.ints.entrySet()) {
            if (e.getValue().isRaised) {
                raisedInts.put(e.getKey(), e.getValue());
            }
        }
        return raisedInts;
    }

    public void clearInterrupt(InterruptTypes i) {
        this.ints.get(i).clear();
    }

    public void raiseInterrupt(InterruptTypes i) {
        this.ints.get(i).raise();
    }

    public void enableInterrupt(InterruptTypes i) {
        this.ints.get(i).enable();
    }

    public void disableInterrupt(InterruptTypes i) {
        this.ints.get(i).disable();
    }

    public void raiseInterrupt(int registerValue) {
        if (registerValue > 0b0001_1111) {
            log.fatal(registerValue + " is out of range of possible interrupt flag register values");
            registerValue &= 0b0001_1111;
        }

        for (InterruptTypes i : InterruptTypes.values()) {
            if ((registerValue / i.bit) == 1) {
                log.info("Raising " + i.name());
                ints.get(i).raise();
            }
            else {
                ints.get(i).clear();
            }
            registerValue %= i.bit;
        }
    }

    public void enableInterrupt(int registerValue) {
        if (registerValue > 0b0001_1111) {
            log.fatal(registerValue + " is out of range of possible interrupt enable register values");
            registerValue &= 0b0001_1111;
        }

        for (InterruptTypes i : InterruptTypes.values()) {
            if ((registerValue / i.bit) == 1) {
                log.info("Enabling " + i.name());
                ints.get(i).enable();
            }
            else {
                ints.get(i).clear();
                ints.get(i).disable();
            }
            registerValue %= i.bit;
        }
    }

    public int getInterruptsEnabled() {
        int i = 0;
        for (InterruptTypes interrupt : InterruptTypes.values()) {
            if (ints.get(interrupt).isEnabled()) {
                i |= interrupt.bit;
            }
        }
        return i;
    }

    public int getInterruptsRaised() {
        int i = 0;
        for (InterruptTypes interrupt : InterruptTypes.values()) {
            if (ints.get(interrupt).isRaised()) {
                i |= interrupt.bit;
            }
        }
        return i;
    }

}
