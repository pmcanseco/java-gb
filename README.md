# java-gb [![Build Status](https://travis-ci.org/pmcanseco/java-gb.png?branch=master)](https://travis-ci.org/pmcanseco/java-gb)

![Tetris Main Screen](https://i.imgur.com/6zbdaB3.gif) &nbsp; ![CPU_INSTRS](https://i.imgur.com/rD1P93j.png) &nbsp; ![Super Mario Land](https://i.imgur.com/8BErEun.png) &nbsp; ![Dr. Mario](https://i.imgur.com/8jKMhHN.png) &nbsp; ![Pokemon Blue JP](https://i.imgur.com/dHA67P8.png)

## Docs
 * http://imrannazar.com/GameBoy-Emulation-in-JavaScript:-The-CPU
 * Gameboy Instruction Set: http://marc.rawer.de/Gameboy/Docs/GBCPU_Instr.html
 * Gameboy Opcode Summary: http://marc.rawer.de/Gameboy/Docs/Opcodes.htm
 * Gameboy Manual: http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf


## Progress:
| Features          | Status    | Notes    |
| ------------------|:---------:|----------|
| ⚔️Sprites       | ✔️    |  |
| 🕹 Joypad          | ✔️       |  |
| 📚 MBC             | ⚠️    | MBC1 and optional RAM implemented. No battery or any other MBC yet. |
| 🔊 Sound           | ❌       |  |
| ⏱ Frame Scheduling | ❌      |  |



| Test Roms                   | Status    | Notes     |
| ----------------------------|:---------:| ----------|
| Blargg CPU_INSTRS           | ✔️     |  |
| Blargg INSTR_TIMING         | ❌        |  |
| Blargg INTERRUPT_TIME       | ❌        |  |
| Blargg MEM_TIMING           | ❌        |  |
| Blargg MEM_TIMING-2         | ❌        |  |
| Mooneye bits/mem_oam        |           |  |
| Mooneye bits/reg_f          | ✔️     |  |
| Mooneye bits/unused_hwio-GS | ️     |  |
| Mooneye timer/div_write     | ✔️     |  |
| Mooneye timer/rapid_toggle  | ✔️     |  |
| Mooneye timer/tim00         | ✔️     |  |
| Mooneye timer/tim01         | ✔️     |  |
| Mooneye timer/tim10         | ✔️     |  |
| Mooneye timer/tim11         | ✔️     |  |
| Mooneye timer/tima_reload   | ✔️     |  |
| Mooneye timer/tima_write_reloading  | ✔️     |  |
| Mooneye timer/tma_write_reloading   | ✔️     |  |


**Blargg INSTR_TIMING: ❌**
- Fails with error "FAILED #255" , issue with timer not working right. https://github.com/afishberg/feo-boy/commit/3b2973154671a0cd00b47a81071a5fb85aa4629e has some potentially relevant info.
