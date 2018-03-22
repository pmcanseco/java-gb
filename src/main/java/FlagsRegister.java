public class FlagsRegister extends Register {

    // the ordinal number of each element here is used
    // as an index to the corresponding bit in the register.
    public enum FlagIndex {
        InvalidBit0,
        InvalidBit1,
        InvalidBit2,
        InvalidBit3,
        C,
        H,
        N,
        Z
    }

    FlagsRegister(String regName, int regSize, int regVal) {
        super(regName, regSize, regVal);
    }

    public boolean readZ() {
        return readBit(FlagIndex.Z.ordinal());
    }
    public void setZ() {
        writeBit(FlagIndex.Z.ordinal(), true);
    }
    public void clearZ() {
        writeBit(FlagIndex.Z.ordinal() ,false);
    }

    public boolean readN() {
        return readBit(FlagIndex.N.ordinal());
    }
    public void setN() {
        writeBit(FlagIndex.N.ordinal(), true);
    }
    public void clearN() {
        writeBit(FlagIndex.N.ordinal(), false);
    }

    public boolean readH() {
        return readBit(FlagIndex.H.ordinal());
    }
    public void setH() {
        writeBit(FlagIndex.H.ordinal(), true);
    }
    public void clearH() {
        writeBit(FlagIndex.H.ordinal(), false);
    }

    public boolean readC() {
        return readBit(FlagIndex.C.ordinal());
    }
    public void setC() {
        writeBit(FlagIndex.C.ordinal(), true);
    }
    public void clearC() {
        writeBit(FlagIndex.C.ordinal(), false);
    }

    @Override
    public void write(int value) {
        // the lower 4 bits are always zero
        value &= 0b11110000;
        super.write(value);
    }
}
