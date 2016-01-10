package jGBC;

import java.lang.reflect.Method;

public interface InstructionSet
{
    int fetchOp(int addr);
    Method decodeOp(int opCode);
    void executeOp(Method op) throws Exception;
}
