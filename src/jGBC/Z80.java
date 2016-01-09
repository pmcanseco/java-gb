package jGBC;

import java.lang.reflect.Method;

public class Z80 implements InstructionSet
{
    public void fetchOp(int addr) {

    }

    public Method decodeOp(int opCode) {
        return null;
    }

    public void executeInstruction(Method instruction) throws Exception {
        if(instruction != null)
        {
            // do stuff
        }
        else
        {
            throw new Exception("stuff");
        }
    }
}
