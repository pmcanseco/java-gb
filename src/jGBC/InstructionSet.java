package jGBC;

import java.lang.reflect.Method;

public interface InstructionSet
{
    void fetchOp(int addr);
    Method decodeOp(int opCode);
    void executeInstruction(Method instruction) throws Exception;
}
