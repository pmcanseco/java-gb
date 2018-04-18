# java-gb [![Build Status](https://travis-ci.org/pmcanseco/java-gb.png?branch=master)](https://travis-ci.org/pmcanseco/java-gb)

## Docs
 * http://imrannazar.com/GameBoy-Emulation-in-JavaScript:-The-CPU
 * Gameboy Instruction Set: http://marc.rawer.de/Gameboy/Docs/GBCPU_Instr.html
 * Gameboy Opcode Summary: http://marc.rawer.de/Gameboy/Docs/Opcodes.htm
 * Gameboy Manual: http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf


## Progress:
![Tetris Main Screen](https://i.imgur.com/JBRPeL2.gif) &nbsp; ![CPU_INSTRS](https://i.imgur.com/rD1P93j.png)

**Blargg CPU_INSTRS: ✅**
- [X] 01-special
- [X] 02-interrupts
- [X] 03-op sp,hl
- [X] 04-op r,imm
- [X] 05-op rp
- [X] 06-ld r,r
- [X] 07-jr,jp,call,ret,rst
- [X] 08-misc instrs
- [X] 09-op r,r
- [X] 10-bit ops
- [X] 11-op a,(hl)

**Blargg INSTR_TIMING: ❌**
- Fails with error "FAILED #255" , issue with timer not working right. https://github.com/afishberg/feo-boy/commit/3b2973154671a0cd00b47a81071a5fb85aa4629e has some potentially relevant info.
