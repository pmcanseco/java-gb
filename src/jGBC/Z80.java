package jGBC;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;

public class Z80 implements InstructionSet
{
    private class Register
    {
        int value;
    }

    /* 
    Z80 8-bit Registers. 
    Can be combined to make 
    a single 16-bit register
    */
    Register A;
    Register B;
    Register C;
    Register D;
    Register E;
    Register F; // Flag register
    Register H;
    Register L;

    // 16-bit Program Counter
    Register PC;
    // 16-bit Stack Pointer
    Register SP;

    Z80()
    {
        // Init registers
        A = new Register();
        B = new Register();
        C = new Register();
        D = new Register();
        E = new Register();
        F = new Register();
        H = new Register();
        L = new Register();
        PC = new Register();
        SP = new Register();
    }

    /*
        Implementation of InstructionSet attempts to emulate actual
        CPU operations as closely as possible.
     */
    public int fetchOp(int addr)
    {
      return addr;  
    }

    public Method decodeOp(int opCode)
    {
        throw new NotImplementedException();
    }

    public void executeOp(Method op) throws Exception {
        if(op != null)
        {
        }
        else
        {
            throw new Exception("stuff");
        }
    }

    /*
        Methods to perform Z80 Instructions
     */

    /***************************
     *       8-bit Loads       *
     ***************************/

    /**
     * Loads Register s into Register d
     * @param s - Source Register
     * @param d - Destination Register
     */
    private void LD(Register s, Register d)
    {
        s = d;
    }
}
