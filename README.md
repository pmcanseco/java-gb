# java-gb [![Build Status](https://travis-ci.org/pmcanseco/java-gb.png?branch=master)](https://travis-ci.org/pmcanseco/java-gb)

## Docs
 * http://imrannazar.com/GameBoy-Emulation-in-JavaScript:-The-CPU
 * Gameboy Instruction Set: http://marc.rawer.de/Gameboy/Docs/GBCPU_Instr.html
 * Gameboy Opcode Summary: http://marc.rawer.de/Gameboy/Docs/Opcodes.htm
 * Gameboy Manual: http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf


## Progress:
![Tetris Main Screen](https://i.imgur.com/6zbdaB3.gif) &nbsp; ![CPU_INSTRS](https://i.imgur.com/rD1P93j.png)

| Features          | Status    | Notes    |
| ------------------|:---------:|----------|
| ⚔️ Sprites          | ⚠️       | Glitchy sprite rendering, but almost there |
| 🕹 Joypad            | ❌       |  |
| 📚 MBC               | ⚠️       | MBC1 and optional RAM implemented. No battery or any other MBC yet. |
| 🔊 Sound            | ❌       |  |
| ⏱ Frame Scheduling | ❌       |  |



| Test Roms                   | Status    | Notes     |
| ----------------------------|:---------:| ----------|
| Blargg CPU_INSTRS           | ✔️       |  |
| Blargg INSTR_TIMING         | ❌       |  |
| Blargg INTERRUPT_TIME       | ❌       |  |
| Blargg MEM_TIMING           | ❌       |  |
| Blargg MEM_TIMING-2         | ❌       |  |
| Mooneye bits/mem_oam        |          |  |
| Mooneye bits/reg_f          | ✔️       |  |
| Mooneye bits/unused_hwio-GS | ✔️       |  |


**Blargg INSTR_TIMING: ❌**
- Fails with error "FAILED #255" , issue with timer not working right. https://github.com/afishberg/feo-boy/commit/3b2973154671a0cd00b47a81071a5fb85aa4629e has some potentially relevant info.
