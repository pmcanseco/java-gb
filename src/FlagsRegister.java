public class FlagsRegister extends Register {

    FlagsRegister(String regName, int regSize, int regVal) {
        super(regName, regSize, regVal);
    }

    public boolean readZ() {
        return readBit(0);
    }
    public void setZ() {
        writeBit(0, true);
    }
    public void clearZ() {
        writeBit(0 ,false);
    }

    public boolean readN() {
        return readBit(1);
    }
    public void setN() {
        writeBit(1, true);
    }
    public void clearN() {
        writeBit(1, false);
    }

    public boolean readH() {
        return readBit(2);
    }
    public void setH() {
        writeBit(2, true);
    }
    public void clearH() {
        writeBit(2, false);
    }

    public boolean readC() {
        return readBit(3);
    }
    public void setC() {
        writeBit(3, true);
    }
    public void clearC() {
        writeBit(3, false);
    }

}
