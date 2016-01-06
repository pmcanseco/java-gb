package jGBC;

import java.util.HashMap;
import java.util.Map;

public class Z80 {

	// Register set
	public static class Reg {
		public static int a=0, b=0, c=0, d=0, e=0, h=0, l=0, f=0; // 8-bit registers
		
		public static int
				pc=0, sp=0, i=0, r=0, // 16-bit registers
				m=0, t=0, // clock for last instruction
				ime=0; // TODO IME ?
		// f = flags
		//   Zero (0x80): set if last operation resulted in zero
		//   Operation (0x40): set if the last operation was subtraction
		//   Half-carry (0x20): set if, in the last operation's result, lower half of int overflowed past 15
		//   Carry (0x10): set if the last operation produced a result >255 for adds or <0 for subtracts.
		
		
		// sp = stack pointer
	}
	
	// Time Clock: The Z80 holds two types of clock (m and t)
	public static class Clock {
		static int m=0;
		static int t=0;
	}


	////////////   OPCODES AS FUNCTIONS //////////////
    public static void LDrr_bb() { /*Reg.b=Reg.b;*/ Reg.m=1; Reg.t=4; }
    public static void LDrr_bc() { Reg.b=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_bd() { Reg.b=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_be() { Reg.b=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_bh() { Reg.b=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_bl() { Reg.b=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ba() { Reg.b=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_cb() { Reg.c=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_cc() { /*Reg.c=Reg.c;*/ Reg.m=1; Reg.t=4; }
    public static void LDrr_cd() { Reg.c=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_ce() { Reg.c=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_ch() { Reg.c=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_cl() { Reg.c=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ca() { Reg.c=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_db() { Reg.d=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_dc() { Reg.d=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_dd() { /*Reg.d=Reg.d;*/ Reg.m=1; Reg.t=4; }
    public static void LDrr_de() { Reg.d=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_dh() { Reg.d=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_dl() { Reg.d=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_da() { Reg.d=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_eb() { Reg.e=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_ec() { Reg.e=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_ed() { Reg.e=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_ee() {  /*Reg.e=Reg.e;*/ Reg.m=1; Reg.t=4; }
    public static void LDrr_eh() { Reg.e=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_el() { Reg.e=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ea() { Reg.e=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_hb() { Reg.h=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_hc() { Reg.h=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_hd() { Reg.h=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_he() { Reg.h=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_hh() {  /*Reg.h=Reg.h;*/ Reg.m=1; Reg.t=4; }
    public static void LDrr_hl() { Reg.h=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ha() { Reg.h=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_lb() { Reg.l=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_lc() { Reg.l=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_ld() { Reg.l=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_le() { Reg.l=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_lh() { Reg.l=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_ll() {  /*Reg.l=Reg.l;*/ Reg.m=1; Reg.t=4; }
    public static void LDrr_la() { Reg.l=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_ab() { Reg.a=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_ac() { Reg.a=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_ad() { Reg.a=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_ae() { Reg.a=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_ah() { Reg.a=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_al() { Reg.a=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_aa() {  /*Reg.a=Reg.a;*/ Reg.m=1; Reg.t=4; }
    public static void LDrHLm_b() { Reg.b=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDrHLm_c() { Reg.c=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDrHLm_d() { Reg.d=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDrHLm_e() { Reg.e=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDrHLm_h() { Reg.h=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDrHLm_l() { Reg.l=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDrHLm_a() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_b() { MMU.wb((Reg.h<<8)+Reg.l,Reg.b); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_c() { MMU.wb((Reg.h<<8)+Reg.l,Reg.c); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_d() { MMU.wb((Reg.h<<8)+Reg.l,Reg.d); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_e() { MMU.wb((Reg.h<<8)+Reg.l,Reg.e); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_h() { MMU.wb((Reg.h<<8)+Reg.l,Reg.h); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_l() { MMU.wb((Reg.h<<8)+Reg.l,Reg.l); Reg.m=2; Reg.t=8; }
    public static void LDHLmr_a() { MMU.wb((Reg.h<<8)+Reg.l,Reg.a); Reg.m=2; Reg.t=8; }
    public static void LDrn_b() { Reg.b=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDrn_c() { Reg.c=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDrn_d() { Reg.d=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDrn_e() { Reg.e=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDrn_h() { Reg.h=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDrn_l() { Reg.l=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDrn_a() { Reg.a=MMU.rb(Reg.pc); Reg.pc++; Reg.m=2; Reg.t=8; }
    public static void LDHLmn() { MMU.wb((Reg.h<<8)+Reg.l, MMU.rb(Reg.pc)); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDBCmA() { MMU.wb((Reg.b<<8)+Reg.c, Reg.a); Reg.m=2; Reg.t=8; }
    public static void LDDEmA() { MMU.wb((Reg.d<<8)+Reg.e, Reg.a); Reg.m=2; Reg.t=8; }
    public static void LDmmA() { MMU.wb(MMU.rw(Reg.pc), Reg.a); Reg.pc+=2; Reg.m=4; Reg.t=16; }
    public static void LDABCm() { Reg.a=MMU.rb((Reg.b<<8)+Reg.c); Reg.m=2; Reg.t=8; }
    public static void LDADEm()
    {
        Reg.a=MMU.rb((Reg.d<<8)+Reg.e);
        Reg.m=2;
        Reg.t=8;
    }
    public static void LDAmm() { Reg.a=MMU.rb(MMU.rw(Reg.pc)); Reg.pc+=2; Reg.m=4; Reg.t=16; }
    public static void LDBCnn() { Reg.c=MMU.rb(Reg.pc); Reg.b=MMU.rb(Reg.pc+1); Reg.pc+=2; Reg.m=3; Reg.t=12; }
    public static void LDDEnn() { Reg.e=MMU.rb(Reg.pc); Reg.d=MMU.rb(Reg.pc+1); Reg.pc+=2; Reg.m=3; Reg.t=12; }
    public static void LDHLnn() { Reg.l=MMU.rb(Reg.pc); Reg.h=MMU.rb(Reg.pc+1); Reg.pc+=2; Reg.m=3; Reg.t=12; }
    public static void LDSPnn() { Reg.sp=MMU.rw(Reg.pc); Reg.pc+=2; Reg.m=3; Reg.t=12; }

    public static void LDHLmm() { int i=MMU.rw(Reg.pc); Reg.pc+=2; Reg.l=MMU.rb(i); Reg.h=MMU.rb(i+1); Reg.m=5; Reg.t=20; }
    public static void LDmmHL() { int i=MMU.rw(Reg.pc); Reg.pc+=2; MMU.ww(i, ((Reg.h<<8)+Reg.l)); Reg.m=5; Reg.t=20; }

    public static void LDHLIA() { MMU.wb((Reg.h<<8)+Reg.l, Reg.a); Reg.l= ((Reg.l+1)&255); if(Reg.l==0) Reg.h= ((Reg.h+1)&255); Reg.m=2; Reg.t=8; }
    public static void LDAHLI() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.l= ((Reg.l+1)&255); if(Reg.l==0) Reg.h=((Reg.h+1)&255); Reg.m=2; Reg.t=8; }

    public static void LDHLDA() { MMU.wb((Reg.h<<8)+Reg.l, Reg.a); Reg.l= ((Reg.l-1)&255); if(Reg.l==255) Reg.h= ((Reg.h-1)&255); Reg.m=2; Reg.t=8; }
    public static void LDAHLD() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.l= ((Reg.l-1)&255); if(Reg.l==255) Reg.h= ((Reg.h-1)&255); Reg.m=2; Reg.t=8; }
    
    public static void LDAIOn() { Reg.a=MMU.rb(0xFF00+MMU.rb(Reg.pc)); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDIOnA() { MMU.wb(0xFF00+MMU.rb(Reg.pc),Reg.a); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDAIOC() { Reg.a=MMU.rb(0xFF00+Reg.c); Reg.m=2; Reg.t=8; }
    public static void LDIOCA() { MMU.wb(0xFF00+Reg.c,Reg.a); Reg.m=2; Reg.t=8; }

    public static void LDHLSPn() { int i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; i+=Reg.sp; Reg.h= ((i>>8)&255); Reg.l= (i&255); Reg.m=3; Reg.t=12; }

    public static void SWAPr_b() { int tr=Reg.b; Reg.b=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_c() { int tr=Reg.c; Reg.c=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_d() { int tr=Reg.d; Reg.d=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_e() { int tr=Reg.e; Reg.e=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_h() { int tr=Reg.h; Reg.h=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_l() { int tr=Reg.l; Reg.l=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_a() { int tr=Reg.a; Reg.a=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
	
    /*--- Data processing ---*/
    public static void ADDr_b() { Reg.a+=Reg.b; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_c() { Reg.a+=Reg.c; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_d() { Reg.a+=Reg.d; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_e() { Reg.a+=Reg.e; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_h() { Reg.a+=Reg.h; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_l() { Reg.a+=Reg.l; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_a() { Reg.a+=Reg.a; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDHL() { Reg.a+=MMU.rb((Reg.h<<8)+Reg.l); fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void ADDn() { Reg.a+=MMU.rb(Reg.pc); Reg.pc++; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void ADDHLBC() { int hl= ((Reg.h<<8)+Reg.l); hl+=(Reg.b<<8)+Reg.c; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h= ((hl>>8)&255); Reg.l= (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDHLDE() { int hl= ((Reg.h<<8)+Reg.l); hl+=(Reg.d<<8)+Reg.e; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h= ((hl>>8)&255); Reg.l= (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDHLHL() { int hl= ((Reg.h<<8)+Reg.l); hl+=(Reg.h<<8)+Reg.l; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h= ((hl>>8)&255); Reg.l= (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDHLSP() { int hl= ((Reg.h<<8)+Reg.l); hl+=Reg.sp; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h= ((hl>>8)&255); Reg.l= (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDSPn() { int i=MMU.rb(Reg.pc); if(i>127) i= -((~i+1)&255); Reg.pc++; Reg.sp+=i; Reg.m=4; Reg.t=16; }
    public static void ADCr_b() { Reg.a+=Reg.b; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_c() { Reg.a+=Reg.c; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_d() { Reg.a+=Reg.d; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_e() { Reg.a+=Reg.e; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_h() { Reg.a+=Reg.h; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_l() { Reg.a+=Reg.l; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_a() { Reg.a+=Reg.a; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCHL() { Reg.a+=MMU.rb((Reg.h<<8)+Reg.l); Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void ADCn() { Reg.a+=MMU.rb(Reg.pc); Reg.pc++; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, 0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void SUBr_b() { Reg.a-=Reg.b; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_c() { Reg.a-=Reg.c; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_d() { Reg.a-=Reg.d; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_e() { Reg.a-=Reg.e; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_h() { Reg.a-=Reg.h; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_l() { Reg.a-=Reg.l; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_a() { Reg.a-=Reg.a; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBHL() { Reg.a-=MMU.rb((Reg.h<<8)+Reg.l); fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void SUBn() { Reg.a-=MMU.rb(Reg.pc); Reg.pc++; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void SBCr_b() { Reg.a-=Reg.b; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_c() { Reg.a-=Reg.c; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_d() { Reg.a-=Reg.d; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_e() { Reg.a-=Reg.e; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_h() { Reg.a-=Reg.h; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_l() { Reg.a-=Reg.l; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_a() { Reg.a-=Reg.a; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCHL() { Reg.a-=MMU.rb((Reg.h<<8)+Reg.l); Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void SBCn() { Reg.a-=MMU.rb(Reg.pc); Reg.pc++; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void CPr_b() { int i=Reg.a; i-=Reg.b; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_c() { int i=Reg.a; i-=Reg.c; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_d() { int i=Reg.a; i-=Reg.d; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_e() { int i=Reg.a; i-=Reg.e; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_h() { int i=Reg.a; i-=Reg.h; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_l() { int i=Reg.a; i-=Reg.l; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_a() { int i=Reg.a; i-=Reg.a; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPHL() { int i=Reg.a; i-=MMU.rb((Reg.h<<8)+Reg.l); fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=2; Reg.t=8; }
    public static void CPn() { int i=Reg.a; i-=MMU.rb(Reg.pc); Reg.pc++; fz(i,1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=2; Reg.t=8; }
    
    public static void ANDr_b() { Reg.a&=Reg.b; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDr_c() { Reg.a&=Reg.c; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDr_d() { Reg.a&=Reg.d; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDr_e() { Reg.a&=Reg.e; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDr_h() { Reg.a&=Reg.h; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDr_l() { Reg.a&=Reg.l; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDr_a() { Reg.a&=Reg.a; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ANDHL() { Reg.a&=MMU.rb((Reg.h<<8)+Reg.l); Reg.a&=255; fz(Reg.a, 0); Reg.m=2; Reg.t=8; }
    public static void ANDn() { Reg.a&=MMU.rb(Reg.pc); Reg.pc++; Reg.a&=255; fz(Reg.a, 0); Reg.m=2; Reg.t=8; }

    public static void ORr_b() { Reg.a|=Reg.b; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORr_c() { Reg.a|=Reg.c; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORr_d() { Reg.a|=Reg.d; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORr_e() { Reg.a|=Reg.e; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORr_h() { Reg.a|=Reg.h; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORr_l() { Reg.a|=Reg.l; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORr_a() { Reg.a|=Reg.a; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void ORHL() { Reg.a|=MMU.rb((Reg.h<<8)+Reg.l); Reg.a&=255; fz(Reg.a, 0); Reg.m=2; Reg.t=8; }
    public static void ORn() { Reg.a|=MMU.rb(Reg.pc); Reg.pc++; Reg.a&=255; fz(Reg.a, 0); Reg.m=2; Reg.t=8; }

    public static void XORr_b() { Reg.a^=Reg.b; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORr_c() { Reg.a^=Reg.c; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORr_d() { Reg.a^=Reg.d; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORr_e() { Reg.a^=Reg.e; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORr_h() { Reg.a^=Reg.h; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORr_l() { Reg.a^=Reg.l; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORr_a() { Reg.a^=Reg.a; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void XORHL() { Reg.a^=MMU.rb((Reg.h<<8)+Reg.l); Reg.a&=255; fz(Reg.a, 0); Reg.m=2; Reg.t=8; }
    public static void XORn() { Reg.a^=MMU.rb(Reg.pc); Reg.pc++; Reg.a&=255; fz(Reg.a, 0); Reg.m=2; Reg.t=8; }

    public static void INCr_b() { Reg.b++; Reg.b&=255; fz(Reg.b, 0); Reg.m=1; Reg.t=4; }
    public static void INCr_c() { Reg.c++; Reg.c&=255; fz(Reg.c, 0); Reg.m=1; Reg.t=4; }
    public static void INCr_d() { Reg.d++; Reg.d&=255; fz(Reg.d, 0); Reg.m=1; Reg.t=4; }
    public static void INCr_e() { Reg.e++; Reg.e&=255; fz(Reg.e, 0); Reg.m=1; Reg.t=4; }
    public static void INCr_h() { Reg.h++; Reg.h&=255; fz(Reg.h, 0); Reg.m=1; Reg.t=4; }
    public static void INCr_l() { Reg.l++; Reg.l&=255; fz(Reg.l, 0); Reg.m=1; Reg.t=4; }
    public static void INCr_a() { Reg.a++; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void INCHLm() { int i= (MMU.rb((Reg.h<<8)+Reg.l)+1); i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i, 0); Reg.m=3; Reg.t=12; }

    public static void DECr_b() { Reg.b--; Reg.b&=255; fz(Reg.b, 0); Reg.m=1; Reg.t=4; }
    public static void DECr_c() { Reg.c--; Reg.c&=255; fz(Reg.c, 0); Reg.m=1; Reg.t=4; }
    public static void DECr_d() { Reg.d--; Reg.d&=255; fz(Reg.d, 0); Reg.m=1; Reg.t=4; }
    public static void DECr_e() { Reg.e--; Reg.e&=255; fz(Reg.e, 0); Reg.m=1; Reg.t=4; }
    public static void DECr_h() { Reg.h--; Reg.h&=255; fz(Reg.h, 0); Reg.m=1; Reg.t=4; }
    public static void DECr_l() { Reg.l--; Reg.l&=255; fz(Reg.l, 0); Reg.m=1; Reg.t=4; }
    public static void DECr_a() { Reg.a--; Reg.a&=255; fz(Reg.a, 0); Reg.m=1; Reg.t=4; }
    public static void DECHLm() { int i= (MMU.rb((Reg.h<<8)+Reg.l)-1); i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i, 0); Reg.m=3; Reg.t=12; }

    public static void INCBC() { Reg.c= ((Reg.c+1)&255); if(Reg.c == 0) Reg.b= ((Reg.b+1)&255); Reg.m=1; Reg.t=4; }
    public static void INCDE() { Reg.e= ((Reg.e+1)&255); if(Reg.e == 0) Reg.d= ((Reg.d+1)&255); Reg.m=1; Reg.t=4; }
    public static void INCHL() { Reg.l= ((Reg.l+1)&255); if(Reg.l == 0) Reg.h= ((Reg.h+1)&255); Reg.m=1; Reg.t=4; }
    public static void INCSP() { Reg.sp= ((Reg.sp+1)&65535); Reg.m=1; Reg.t=4; }

    public static void DECBC() { Reg.c= ((Reg.c-1)&255); if(Reg.c==255) Reg.b= ((Reg.b-1)&255); Reg.m=1; Reg.t=4; }
    public static void DECDE() { Reg.e= ((Reg.e-1)&255); if(Reg.e==255) Reg.d= ((Reg.d-1)&255); Reg.m=1; Reg.t=4; }
    public static void DECHL() { Reg.l= ((Reg.l-1)&255); if(Reg.l==255) Reg.h= ((Reg.h-1)&255); Reg.m=1; Reg.t=4; }
    public static void DECSP() { Reg.sp= ((Reg.sp-1)&65535); Reg.m=1; Reg.t=4; }

    /*--- Bit manipulation ---*/
    public static void BIT0b() { fz((Reg.b&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0c() { fz((Reg.c&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0d() { fz((Reg.d&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0e() { fz((Reg.e&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0h() { fz((Reg.h&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0l() { fz((Reg.l&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0a() { fz((Reg.a&0x01), 0); Reg.m=2; Reg.t=8; }
    public static void BIT0m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x01); Reg.m=3; Reg.t=12; }

    public static void BIT1b() { fz((Reg.b&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1c() { fz((Reg.c&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1d() { fz((Reg.d&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1e() { fz((Reg.e&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1h() { fz((Reg.h&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1l() { fz((Reg.l&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1a() { fz((Reg.a&0x02), 0); Reg.m=2; Reg.t=8; }
    public static void BIT1m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x02); Reg.m=3; Reg.t=12; }

    public static void BIT2b() { fz((Reg.b&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2c() { fz((Reg.c&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2d() { fz((Reg.d&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2e() { fz((Reg.e&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2h() { fz((Reg.h&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2l() { fz((Reg.l&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2a() { fz((Reg.a&0x04), 0); Reg.m=2; Reg.t=8; }
    public static void BIT2m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x04, 0); Reg.m=3; Reg.t=12; }

    public static void BIT3b() { fz((Reg.b&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3c() { fz((Reg.c&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3d() { fz((Reg.d&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3e() { fz((Reg.e&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3h() { fz((Reg.h&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3l() { fz((Reg.l&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3a() { fz((Reg.a&0x08), 0); Reg.m=2; Reg.t=8; }
    public static void BIT3m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x08, 0); Reg.m=3; Reg.t=12; }

    public static void BIT4b() { fz((Reg.b&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4c() { fz((Reg.c&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4d() { fz((Reg.d&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4e() { fz((Reg.e&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4h() { fz((Reg.h&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4l() { fz((Reg.l&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4a() { fz((Reg.a&0x10), 0); Reg.m=2; Reg.t=8; }
    public static void BIT4m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x10, 0); Reg.m=3; Reg.t=12; }

    public static void BIT5b() { fz((Reg.b&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5c() { fz((Reg.c&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5d() { fz((Reg.d&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5e() { fz((Reg.e&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5h() { fz((Reg.h&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5l() { fz((Reg.l&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5a() { fz((Reg.a&0x20), 0); Reg.m=2; Reg.t=8; }
    public static void BIT5m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x20, 0); Reg.m=3; Reg.t=12; }

    public static void BIT6b() { fz((Reg.b&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6c() { fz((Reg.c&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6d() { fz((Reg.d&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6e() { fz((Reg.e&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6h() { fz((Reg.h&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6l() { fz((Reg.l&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6a() { fz((Reg.a&0x40), 0); Reg.m=2; Reg.t=8; }
    public static void BIT6m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x40, 0); Reg.m=3; Reg.t=12; }

    public static void BIT7b() { fz((Reg.b&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7c() { fz((Reg.c&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7d() { fz((Reg.d&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7e() { fz((Reg.e&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7h() { fz((Reg.h&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7l() { fz((Reg.l&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7a() { fz((Reg.a&0x80), 0); Reg.m=2; Reg.t=8; }
    public static void BIT7m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x80, 0); Reg.m=3; Reg.t=12; }

    public static void RLA()  { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.a&0x80)!=0?0x10:0); Reg.a= ((Reg.a<<1)+ci); Reg.a&=255; Reg.f= ((Reg.f&0xEF)+co); Reg.m=1; Reg.t=4; }
    public static void RLCA() { int ci= (((Reg.a&0x80)!=0)?1:0); int co= ((Reg.a&0x80)!=0?0x10:0); Reg.a= ((Reg.a<<1)+ci); Reg.a&=255; Reg.f= ((Reg.f&0xEF)+co); Reg.m=1; Reg.t=4; }
    public static void RRA()  { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.a&1)!=0?0x10:0); Reg.a= ((Reg.a>>1)+ci); Reg.a&=255; Reg.f= ((Reg.f&0xEF)+co); Reg.m=1; Reg.t=4; }
    public static void RRCA() { int ci= (((Reg.a&1)!=0)?0x80:0); int co= ((Reg.a&1)!=0?0x10:0); Reg.a= ((Reg.a>>1)+ci); Reg.a&=255; Reg.f= ((Reg.f&0xEF)+co); Reg.m=1; Reg.t=4; }

    public static void RLr_b() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.b&0x80)!=0?0x10:0); Reg.b= ((Reg.b<<1)+ci); Reg.b&=255; fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLr_c() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.c&0x80)!=0?0x10:0); Reg.c= ((Reg.c<<1)+ci); Reg.c&=255; fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLr_d() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.d&0x80)!=0?0x10:0); Reg.d= ((Reg.d<<1)+ci); Reg.d&=255; fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLr_e() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.e&0x80)!=0?0x10:0); Reg.e= ((Reg.e<<1)+ci); Reg.e&=255; fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLr_h() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.h&0x80)!=0?0x10:0); Reg.h= ((Reg.h<<1)+ci); Reg.h&=255; fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLr_l() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.l&0x80)!=0?0x10:0); Reg.l= ((Reg.l<<1)+ci); Reg.l&=255; fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLr_a() { int ci= (((Reg.f&0x10)!=0)?1:0); int co= ((Reg.a&0x80)!=0?0x10:0); Reg.a= ((Reg.a<<1)+ci); Reg.a&=255; fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLHL() { int i=MMU.rb((Reg.h<<8)+Reg.l); int ci= ((Reg.f&0x10)!=0?1:0); int co= ((i&0x80)!=0?0x10:0); i= ((i<<1)+ci); i&=255; fz(i); MMU.wb((Reg.h<<8)+Reg.l,i); Reg.f= ((Reg.f&0xEF)+co); Reg.m=4; Reg.t=16; }

    public static void RLCr_b()
    {
        int ci= (((Reg.b&0x80)!=0)?1:0);
        int co= ((Reg.b&0x80)!=0?0x10:0);
        Reg.b= ((Reg.b<<1)+ci);
        Reg.b&=255; fz(Reg.b);
        Reg.f= ((Reg.f&0xEF)+co);
        Reg.m=2;
        Reg.t=8;
    }
    public static void RLCr_c() { int ci= (((Reg.c&0x80)!=0)?1:0); int co= ((Reg.c&0x80)!=0?0x10:0); Reg.c= ((Reg.c<<1)+ci); Reg.c&=255; fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLCr_d() { int ci= (((Reg.d&0x80)!=0)?1:0); int co= ((Reg.d&0x80)!=0?0x10:0); Reg.d= ((Reg.d<<1)+ci); Reg.d&=255; fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLCr_e() { int ci= (((Reg.e&0x80)!=0)?1:0); int co= ((Reg.e&0x80)!=0?0x10:0); Reg.e= ((Reg.e<<1)+ci); Reg.e&=255; fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLCr_h() { int ci= (((Reg.h&0x80)!=0)?1:0); int co= ((Reg.h&0x80)!=0?0x10:0); Reg.h= ((Reg.h<<1)+ci); Reg.h&=255; fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLCr_l() { int ci= (((Reg.l&0x80)!=0)?1:0); int co= ((Reg.l&0x80)!=0?0x10:0); Reg.l= ((Reg.l<<1)+ci); Reg.l&=255; fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLCr_a() { int ci= (((Reg.a&0x80)!=0)?1:0); int co= ((Reg.a&0x80)!=0?0x10:0); Reg.a= ((Reg.a<<1)+ci); Reg.a&=255; fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RLCHL() { int i=MMU.rb((Reg.h<<8)+Reg.l); int ci= ((i&0x80)!=0?1:0); int co= ((i&0x80)!=0?0x10:0); i= ((i<<1)+ci); i&=255; fz(i); MMU.wb((Reg.h<<8)+Reg.l,i); Reg.f= ((Reg.f&0xEF)+co); Reg.m=4; Reg.t=16; }

    public static void RRr_b() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.b&1)!=0?0x10:0); Reg.b= ((Reg.b>>1)+ci); Reg.b&=255; fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRr_c() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.c&1)!=0?0x10:0); Reg.c= ((Reg.c>>1)+ci); Reg.c&=255; fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRr_d() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.d&1)!=0?0x10:0); Reg.d= ((Reg.d>>1)+ci); Reg.d&=255; fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRr_e() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.e&1)!=0?0x10:0); Reg.e= ((Reg.e>>1)+ci); Reg.e&=255; fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRr_h() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.h&1)!=0?0x10:0); Reg.h= ((Reg.h>>1)+ci); Reg.h&=255; fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRr_l() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.l&1)!=0?0x10:0); Reg.l= ((Reg.l>>1)+ci); Reg.l&=255; fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRr_a() { int ci= (((Reg.f&0x10)!=0)?0x80:0); int co= ((Reg.a&1)!=0?0x10:0); Reg.a= ((Reg.a>>1)+ci); Reg.a&=255; fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRHL() { int i=MMU.rb((Reg.h<<8)+Reg.l); int ci= ((Reg.f&0x10)!=0?0x80:0); int co= ((i&1)!=0?0x10:0); i= ((i>>1)+ci); i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i); Reg.f= ((Reg.f&0xEF)+co); Reg.m=4; Reg.t=16; }

    public static void RRCr_b() { int ci= ((Reg.b&1)!=0?0x80:0); int co= ((Reg.b&1)!=0?0x10:0); Reg.b= ((Reg.b>>1)+ci); Reg.b&=255; fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCr_c() { int ci= ((Reg.c&1)!=0?0x80:0); int co= ((Reg.c&1)!=0?0x10:0); Reg.c= ((Reg.c>>1)+ci); Reg.c&=255; fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCr_d() { int ci= ((Reg.d&1)!=0?0x80:0); int co= ((Reg.d&1)!=0?0x10:0); Reg.d= ((Reg.d>>1)+ci); Reg.d&=255; fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCr_e() { int ci= ((Reg.e&1)!=0?0x80:0); int co= ((Reg.e&1)!=0?0x10:0); Reg.e= ((Reg.e>>1)+ci); Reg.e&=255; fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCr_h() { int ci= ((Reg.h&1)!=0?0x80:0); int co= ((Reg.h&1)!=0?0x10:0); Reg.h= ((Reg.h>>1)+ci); Reg.h&=255; fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCr_l() { int ci= ((Reg.l&1)!=0?0x80:0); int co= ((Reg.l&1)!=0?0x10:0); Reg.l= ((Reg.l>>1)+ci); Reg.l&=255; fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCr_a() { int ci= ((Reg.a&1)!=0?0x80:0); int co= ((Reg.a&1)!=0?0x10:0); Reg.a= ((Reg.a>>1)+ci); Reg.a&=255; fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void RRCHL() { int i=MMU.rb((Reg.h<<8)+Reg.l); int ci= (((i&1)!=0)?0x80:0); int co= ((i&1)!=0?0x10:0); i= ((i>>1)+ci); i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i); Reg.f= ((Reg.f&0xEF)+co); Reg.m=4; Reg.t=16; }

    public static void SLAr_b() { int co= (((Reg.b&0x80)!=0)?0x10:0); Reg.b= ((Reg.b<<1)&255); fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLAr_c() { int co= (((Reg.c&0x80)!=0)?0x10:0); Reg.c= ((Reg.c<<1)&255); fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLAr_d() { int co= (((Reg.d&0x80)!=0)?0x10:0); Reg.d= ((Reg.d<<1)&255); fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLAr_e() { int co= (((Reg.e&0x80)!=0)?0x10:0); Reg.e= ((Reg.e<<1)&255); fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLAr_h() { int co= (((Reg.h&0x80)!=0)?0x10:0); Reg.h= ((Reg.h<<1)&255); fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLAr_l() { int co= (((Reg.l&0x80)!=0)?0x10:0); Reg.l= ((Reg.l<<1)&255); fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLAr_a() { int co= (((Reg.a&0x80)!=0)?0x10:0); Reg.a= ((Reg.a<<1)&255); fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }

    public static void SLLr_b() { int co= ((Reg.b&0x80)!=0?0x10:0); Reg.b= ((Reg.b<<1)&255+1); fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLLr_c() { int co= ((Reg.c&0x80)!=0?0x10:0); Reg.c= ((Reg.c<<1)&255+1); fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLLr_d() { int co= ((Reg.d&0x80)!=0?0x10:0); Reg.d= ((Reg.d<<1)&255+1); fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLLr_e() { int co= ((Reg.e&0x80)!=0?0x10:0); Reg.e= ((Reg.e<<1)&255+1); fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLLr_h() { int co= ((Reg.h&0x80)!=0?0x10:0); Reg.h= ((Reg.h<<1)&255+1); fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLLr_l() { int co= ((Reg.l&0x80)!=0?0x10:0); Reg.l= ((Reg.l<<1)&255+1); fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SLLr_a() { int co= ((Reg.a&0x80)!=0?0x10:0); Reg.a= ((Reg.a<<1)&255+1); fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }

    public static void SRAr_b() { int ci= (Reg.b&0x80); int co= (((Reg.b&1)!=0)?0x10:0); Reg.b= (((Reg.b>>1)+ci)&255); fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRAr_c() { int ci= (Reg.c&0x80); int co= (((Reg.c&1)!=0)?0x10:0); Reg.c= (((Reg.c>>1)+ci)&255); fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRAr_d() { int ci= (Reg.d&0x80); int co= (((Reg.d&1)!=0)?0x10:0); Reg.d= (((Reg.d>>1)+ci)&255); fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRAr_e() { int ci= (Reg.e&0x80); int co= (((Reg.e&1)!=0)?0x10:0); Reg.e= (((Reg.e>>1)+ci)&255); fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRAr_h() { int ci= (Reg.h&0x80); int co= (((Reg.h&1)!=0)?0x10:0); Reg.h= (((Reg.h>>1)+ci)&255); fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRAr_l() { int ci= (Reg.l&0x80); int co= (((Reg.l&1)!=0)?0x10:0); Reg.l= (((Reg.l>>1)+ci)&255); fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRAr_a() { int ci= (Reg.a&0x80); int co= (((Reg.a&1)!=0)?0x10:0); Reg.a= (((Reg.a>>1)+ci)&255); fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }

    public static void SRLr_b() { int co= (((Reg.b&1)!=0)?0x10:0); Reg.b= ((Reg.b>>1)&255); fz(Reg.b); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRLr_c() { int co= (((Reg.c&1)!=0)?0x10:0); Reg.c= ((Reg.c>>1)&255); fz(Reg.c); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRLr_d() { int co= (((Reg.d&1)!=0)?0x10:0); Reg.d= ((Reg.d>>1)&255); fz(Reg.d); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRLr_e() { int co= (((Reg.e&1)!=0)?0x10:0); Reg.e= ((Reg.e>>1)&255); fz(Reg.e); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRLr_h() { int co= (((Reg.h&1)!=0)?0x10:0); Reg.h= ((Reg.h>>1)&255); fz(Reg.h); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRLr_l() { int co= (((Reg.l&1)!=0)?0x10:0); Reg.l= ((Reg.l>>1)&255); fz(Reg.l); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }
    public static void SRLr_a() { int co= (((Reg.a&1)!=0)?0x10:0); Reg.a= ((Reg.a>>1)&255); fz(Reg.a); Reg.f= ((Reg.f&0xEF)+co); Reg.m=2; Reg.t=8; }

    public static void CPL() { Reg.a =  ((~Reg.a)&255); fz(Reg.a, 1); Reg.m=1; Reg.t=4; }
    public static void NEG() { Reg.a= (0-Reg.a); fz(Reg.a, 1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void CCF() { int ci=(((Reg.f&0x10) !=0)?0:0x10); Reg.f= ((Reg.f&0xEF)+ci); Reg.m=1; Reg.t=4; }
    public static void SCF() { Reg.f|=0x10; Reg.m=1; Reg.t=4; }

    /*--- Stack ---*/
    public static void PUSHBC() { Reg.sp--; MMU.wb(Reg.sp,Reg.b); Reg.sp--; MMU.wb(Reg.sp,Reg.c); Reg.m=3; Reg.t=12; }
    public static void PUSHDE() { Reg.sp--; MMU.wb(Reg.sp,Reg.d); Reg.sp--; MMU.wb(Reg.sp,Reg.e); Reg.m=3; Reg.t=12; }
    public static void PUSHHL() { Reg.sp--; MMU.wb(Reg.sp,Reg.h); Reg.sp--; MMU.wb(Reg.sp,Reg.l); Reg.m=3; Reg.t=12; }
    public static void PUSHAF() { Reg.sp--; MMU.wb(Reg.sp,Reg.a); Reg.sp--; MMU.wb(Reg.sp,Reg.f); Reg.m=3; Reg.t=12; }

    public static void POPBC() { Reg.c=MMU.rb(Reg.sp); Reg.sp++; Reg.b=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }
    public static void POPDE() { Reg.e=MMU.rb(Reg.sp); Reg.sp++; Reg.d=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }
    public static void POPHL() { Reg.l=MMU.rb(Reg.sp); Reg.sp++; Reg.h=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }
    public static void POPAF() { Reg.f=MMU.rb(Reg.sp); Reg.sp++; Reg.a=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }

    /*--- Jump ---*/
    public static void JPnn() { Reg.pc = MMU.rw(Reg.pc); Reg.m=3; Reg.t=12; }
    public static void JPHL() { Reg.pc= ((Reg.h<<8)+Reg.l); Reg.m=1;  }
    public static void JPNZnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x00) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }
    public static void JPZnn()  { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x80) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }
    public static void JPNCnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x00) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }
    public static void JPCnn()  { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x10) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }

    public static void JRn() { int i=MMU.rb(Reg.pc); if(i>127) i= -((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; Reg.pc+=i; Reg.m++; Reg.t+=4; }
    public static void JRNZn()
    {
        int i=MMU.rb(Reg.pc);
        if(i>127)
            i= -((~i+1)&255);
        Reg.pc++;
        Reg.m=2;
        Reg.t=8;
        if((Reg.f&0x80)==0x00)
        {
            Reg.pc+=i;
            Reg.m++;
            Reg.t+=4;
        }
    }
    public static void JRZn()  { int i=MMU.rb(Reg.pc); if(i>127) i= -((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x80)==0x80) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }
    public static void JRNCn() { int i=MMU.rb(Reg.pc); if(i>127) i= -((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x10)==0x00) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }
    public static void JRCn()  { int i=MMU.rb(Reg.pc); if(i>127) i= -((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x10)==0x10) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }

    public static void DJNZn() { int i=MMU.rb(Reg.pc); if(i>127) i= -((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; Reg.b--; if(Reg.b != 0) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }

    public static void CALLnn() { Reg.sp-=2; MMU.ww(Reg.sp, (Reg.pc+2)); Reg.pc=MMU.rw(Reg.pc); Reg.m=5; Reg.t=20; }
    public static void CALLNZnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x00) { Reg.sp-=2; MMU.ww(Reg.sp, (Reg.pc+2)); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }
    public static void CALLZnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x80) { Reg.sp-=2; MMU.ww(Reg.sp, (Reg.pc+2)); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }
    public static void CALLNCnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x00) { Reg.sp-=2; MMU.ww(Reg.sp, (Reg.pc+2)); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }
    public static void CALLCnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x10) { Reg.sp-=2; MMU.ww(Reg.sp, (Reg.pc+2)); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }

    public static void RET() { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m=3; Reg.t=12; }
    public static void RETI() { Reg.ime=1; Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m=3; Reg.t=12; }
    public static void RETNZ() { Reg.m=1; Reg.t=4; if((Reg.f&0x80)==0x00) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }
    public static void RETZ() { Reg.m=1; Reg.t=4; if((Reg.f&0x80)==0x80) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }
    public static void RETNC() { Reg.m=1; Reg.t=4; if((Reg.f&0x10)==0x00) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }
    public static void RETC() { Reg.m=1; Reg.t=4; if((Reg.f&0x10)==0x10) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }

    public static void RST00() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x00; Reg.m=3; Reg.t=12; }
    public static void RST08() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x08; Reg.m=3; Reg.t=12; }
    public static void RST10() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x10; Reg.m=3; Reg.t=12; }
    public static void RST18() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x18; Reg.m=3; Reg.t=12; }
    public static void RST20() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x20; Reg.m=3; Reg.t=12; }
    public static void RST28() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x28; Reg.m=3; Reg.t=12; }
    public static void RST30() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x30; Reg.m=3; Reg.t=12; }
    public static void RST38() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x38; Reg.m=3; Reg.t=12; }
    public static void RST40() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x40; Reg.m=3; Reg.t=12; }
    public static void RST48() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x48; Reg.m=3; Reg.t=12; }
    public static void RST50() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x50; Reg.m=3; Reg.t=12; }
    public static void RST58() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x58; Reg.m=3; Reg.t=12; }
    public static void RST60() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x60; Reg.m=3; Reg.t=12; }
    public static void XX() {System.out.println("XX - unimplemented function.");}
    public static void HALT() { 
    	//Z80._halt=1; 
    	Reg.m=1; Reg.t=4; 
    }

    public static void DI() { Reg.ime=0; Reg.m=1; Reg.t=4; }
    public static void EI() { Reg.ime=1; Reg.m=1; Reg.t=4; }

	
	// Add E to A, leaving result in A (ADD A, E)
	public static void add_r_e() {
		Reg.a += Reg.e; //perform addition
		Reg.f = 0; //clear flags
		if((Reg.a & 255)==0)  Reg.f |= 0x80; //check for zero
		if(Reg.a > 255) 	  Reg.f |= 0x10; //check for carry
		Reg.a &= 255; //mask to 8 bits
		Reg.m = 1; Reg.t = 4; // 1 M-time taken
	}
	
	// Compare B to A, setting flags (CP A, B)
	public static void cpr_b() {
		int i = Reg.a; // temp copy of a
		i -= Reg.b; // subtract b
		Reg.f |= 0x40; // set subtraction flag
		if((i & 255)==0)  Reg.f |= 0x80; //check for zero
		if(i<0) Reg.f |= 0x10; // check for underflow
		Reg.m = 1; Reg.t = 4; // 1 M-time taken
	}
	
	// No-operation (NOP)
	public static void noOp() {
		Reg.m = 1; Reg.t = 4; // 1 M-time taken
	}

	/*memory handling instructions*/
	// Push registers B and C to the stack (PUSH BC)
	public static void pushbc() {
		Reg.sp--; // drop through the stack
		MMU.wb(Reg.sp,  Reg.b); // write B
		Reg.sp--; // Drop through the stack
		MMU.wb(Reg.sp, Reg.c);
	}
	
	// Pop registers H and L off the stack (POP HL)
	public static void pophl() {
		Reg.l = MMU.rb(Reg.sp); // Read L
		Reg.sp++; // Move back up stack
		Reg.h = MMU.rb(Reg.sp); // Read H
		Reg.sp++; // Move back up the stack
		Reg.m = 3; Reg.t = 12; // 3 M-times taken
	}
	
	// Read a int from absolute location into A ( LD A, addr)
	public static void LDamm() {
		int addr = MMU.rw(Reg.pc); //get address from instr
		Reg.pc += 2; // advance PC
		Reg.a = MMU.rb(addr); // read from address
		Reg.m = 4; Reg.t = 16; // 4 M-times taken;
	}

    public static void LDmmSP() {
        System.out.println("LDmmSP called, currently unimplemented.");
    }
	
		
	// helper functions
	private static void MAPcb() {
		int i=MMU.rb(Reg.pc); Reg.pc++;
		Reg.pc &= 65535;
        // +51968 do need?
        final ServiceMethod serviceMethod = cbInstructionMap.get(i);
        if(serviceMethod != null) {
            serviceMethod.execute();
        }
	}
	private static void fz(int i, int as) {
		Reg.f = 0; // clear flags 
		if((i&255)!=0)  // if i masked to 8 bits isn't zero
			Reg.f|=128; // set flag register to 128
		Reg.f|=(as!=0)?0x40:0; // TODO some funky shite
	}
    private static void fz(int i) {
        fz(i, 0);
    }

    private static void populateInstructionMap() {
        instructionMap.put( 0x00, Z80::noOp);
        instructionMap.put( 0x01, Z80::LDBCnn);
        instructionMap.put( 0x02, Z80::LDBCmA);
        instructionMap.put( 0x03, Z80::INCBC);
        instructionMap.put( 0x04, Z80::INCr_b);
        instructionMap.put( 0x05, Z80::DECr_b);
        instructionMap.put( 0x06, Z80::LDrn_b);
        instructionMap.put( 0x07, Z80::RLCA);
        instructionMap.put( 0x08, Z80::LDmmSP); /// todo unimplemented O_o
        instructionMap.put( 0x09, Z80::ADDHLBC);
        instructionMap.put( 0x0a, Z80::LDABCm);
        instructionMap.put( 0x0b, Z80::DECBC);
        instructionMap.put( 0x0c, Z80::INCr_c);
        instructionMap.put( 0x0d, Z80::DECr_c);
        instructionMap.put( 0x0e, Z80::LDrn_c);
        instructionMap.put( 0x0f, Z80::RRCA);
        instructionMap.put( 0x10, Z80::DJNZn);
        instructionMap.put( 0x11, Z80::LDDEnn);
        instructionMap.put( 0x12, Z80::LDDEmA);
        instructionMap.put( 0x13, Z80::INCDE);
        instructionMap.put( 0x14, Z80::INCr_d);
        instructionMap.put( 0x15, Z80::DECr_d);
        instructionMap.put( 0x16, Z80::LDrn_d);
        instructionMap.put( 0x17, Z80::RLA);
        instructionMap.put( 0x18, Z80::JRn);
        instructionMap.put( 0x19, Z80::ADDHLDE);
        instructionMap.put( 0x1a, Z80::LDADEm);
        instructionMap.put( 0x1b, Z80::DECDE);
        instructionMap.put( 0x1c, Z80::INCr_e);
        instructionMap.put( 0x1d, Z80::DECr_e);
        instructionMap.put( 0x1e, Z80::LDrn_e);
        instructionMap.put( 0x1f, Z80::RRA);
        instructionMap.put( 0x20, Z80::JRNZn);
        instructionMap.put( 0x21, Z80::LDHLnn);
        instructionMap.put( 0x22, Z80::LDHLIA);
        instructionMap.put( 0x23, Z80::INCHL);
        instructionMap.put( 0x24, Z80::INCr_h);
        instructionMap.put( 0x25, Z80::DECr_h);
        instructionMap.put( 0x26, Z80::LDrn_h);
        instructionMap.put( 0x27, Z80::XX);
        instructionMap.put( 0x28, Z80::JRZn);
        instructionMap.put( 0x29, Z80::ADDHLHL);
        instructionMap.put( 0x2a, Z80::LDAHLI);
        instructionMap.put( 0x2b, Z80::DECHL);
        instructionMap.put( 0x2c, Z80::INCr_l);
        instructionMap.put( 0x2d, Z80::DECr_l);
        instructionMap.put( 0x2e, Z80::LDrn_l);
        instructionMap.put( 0x2f, Z80::CPL);
        instructionMap.put( 0x30, Z80::JRNCn);
        instructionMap.put( 0x31, Z80::LDSPnn);
        instructionMap.put( 0x32, Z80::LDHLDA);
        instructionMap.put( 0x33, Z80::INCSP);
        instructionMap.put( 0x34, Z80::INCHLm);
        instructionMap.put( 0x35, Z80::DECHLm);
        instructionMap.put( 0x36, Z80::LDHLmn);
        instructionMap.put( 0x37, Z80::SCF);
        instructionMap.put( 0x38, Z80::JRCn);
        instructionMap.put( 0x39, Z80::ADDHLSP);
        instructionMap.put( 0x3a, Z80::LDAHLD);
        instructionMap.put( 0x3b, Z80::DECSP);
        instructionMap.put( 0x3c, Z80::INCr_a);
        instructionMap.put( 0x3d, Z80::DECr_a);
        instructionMap.put( 0x3e, Z80::LDrn_a);
        instructionMap.put( 0x3f, Z80::CCF);
        instructionMap.put( 0x40, Z80::LDrr_bb);
        instructionMap.put( 0x41, Z80::LDrr_bc);
        instructionMap.put( 0x42, Z80::LDrr_bd);
        instructionMap.put( 0x43, Z80::LDrr_be);
        instructionMap.put( 0x44, Z80::LDrr_bh);
        instructionMap.put( 0x45, Z80::LDrr_bl);
        instructionMap.put( 0x46, Z80::LDrHLm_b);
        instructionMap.put( 0x47, Z80::LDrr_ba);
        instructionMap.put( 0x48, Z80::LDrr_cb);
        instructionMap.put( 0x49, Z80::LDrr_cc);
        instructionMap.put( 0x4a, Z80::LDrr_cd);
        instructionMap.put( 0x4b, Z80::LDrr_ce);
        instructionMap.put( 0x4c, Z80::LDrr_ch);
        instructionMap.put( 0x4d, Z80::LDrr_cl);
        instructionMap.put( 0x4e, Z80::LDrHLm_c);
        instructionMap.put( 0x4f, Z80::LDrr_ca);
        instructionMap.put( 0x50, Z80::LDrr_db);
        instructionMap.put( 0x51, Z80::LDrr_dc);
        instructionMap.put( 0x52, Z80::LDrr_dd);
        instructionMap.put( 0x53, Z80::LDrr_de);
        instructionMap.put( 0x54, Z80::LDrr_dh);
        instructionMap.put( 0x55, Z80::LDrr_dl);
        instructionMap.put( 0x56, Z80::LDrHLm_d);
        instructionMap.put( 0x57, Z80::LDrr_da);
        instructionMap.put( 0x58, Z80::LDrr_eb);
        instructionMap.put( 0x59, Z80::LDrr_ec);
        instructionMap.put( 0x5a, Z80::LDrr_ed);
        instructionMap.put( 0x5b, Z80::LDrr_ee);
        instructionMap.put( 0x5c, Z80::LDrr_eh);
        instructionMap.put( 0x5d, Z80::LDrr_el);
        instructionMap.put( 0x5e, Z80::LDrHLm_e);
        instructionMap.put( 0x5f, Z80::LDrr_ea);
        instructionMap.put( 0x60, Z80::LDrr_hb);
        instructionMap.put( 0x61, Z80::LDrr_hc);
        instructionMap.put( 0x62, Z80::LDrr_hd);
        instructionMap.put( 0x63, Z80::LDrr_he);
        instructionMap.put( 0x64, Z80::LDrr_hh);
        instructionMap.put( 0x65, Z80::LDrr_hl);
        instructionMap.put( 0x66, Z80::LDrHLm_h);
        instructionMap.put( 0x67, Z80::LDrr_ha);
        instructionMap.put( 0x68, Z80::LDrr_lb);
        instructionMap.put( 0x69, Z80::LDrr_lc);
        instructionMap.put( 0x6a, Z80::LDrr_ld);
        instructionMap.put( 0x6b, Z80::LDrr_le);
        instructionMap.put( 0x6c, Z80::LDrr_lh);
        instructionMap.put( 0x6d, Z80::LDrr_ll);
        instructionMap.put( 0x6e, Z80::LDrHLm_l);
        instructionMap.put( 0x6f, Z80::LDrr_la);
        instructionMap.put( 0x70, Z80::LDHLmr_b);
        instructionMap.put( 0x71, Z80::LDHLmr_c);
        instructionMap.put( 0x72, Z80::LDHLmr_d);
        instructionMap.put( 0x73, Z80::LDHLmr_e);
        instructionMap.put( 0x74, Z80::LDHLmr_h);
        instructionMap.put( 0x75, Z80::LDHLmr_l);
        instructionMap.put( 0x76, Z80::HALT);
        instructionMap.put( 0x77, Z80::LDHLmr_a);
        instructionMap.put( 0x78, Z80::LDrr_ab);
        instructionMap.put( 0x79, Z80::LDrr_ac);
        instructionMap.put( 0x7a, Z80::LDrr_ad);
        instructionMap.put( 0x7b, Z80::LDrr_ae);
        instructionMap.put( 0x7c, Z80::LDrr_ah);
        instructionMap.put( 0x7d, Z80::LDrr_al);
        instructionMap.put( 0x7e, Z80::LDrHLm_a);
        instructionMap.put( 0x7f, Z80::LDrr_aa);
        instructionMap.put( 0x80, Z80::ADDr_b);
        instructionMap.put( 0x81, Z80::ADDr_c);
        instructionMap.put( 0x82, Z80::ADDr_d);
        instructionMap.put( 0x83, Z80::ADDr_e);
        instructionMap.put( 0x84, Z80::ADDr_h);
        instructionMap.put( 0x85, Z80::ADDr_l);
        instructionMap.put( 0x86, Z80::ADDHL);
        instructionMap.put( 0x87, Z80::ADDr_a);
        instructionMap.put( 0x88, Z80::ADCr_b);
        instructionMap.put( 0x89, Z80::ADCr_c);
        instructionMap.put( 0x8a, Z80::ADCr_d);
        instructionMap.put( 0x8b, Z80::ADCr_e);
        instructionMap.put( 0x8c, Z80::ADCr_h);
        instructionMap.put( 0x8d, Z80::ADCr_l);
        instructionMap.put( 0x8e, Z80::ADCHL);
        instructionMap.put( 0x8f, Z80::ADCr_a);
        instructionMap.put( 0x90, Z80::SUBr_b);
        instructionMap.put( 0x91, Z80::SUBr_c);
        instructionMap.put( 0x92, Z80::SUBr_d);
        instructionMap.put( 0x93, Z80::SUBr_e);
        instructionMap.put( 0x94, Z80::SUBr_h);
        instructionMap.put( 0x95, Z80::SUBr_l);
        instructionMap.put( 0x96, Z80::SUBHL);
        instructionMap.put( 0x97, Z80::SUBr_a);
        instructionMap.put( 0x98, Z80::SBCr_b);
        instructionMap.put( 0x99, Z80::SBCr_c);
        instructionMap.put( 0x9a, Z80::SBCr_d);
        instructionMap.put( 0x9b, Z80::SBCr_e);
        instructionMap.put( 0x9c, Z80::SBCr_h);
        instructionMap.put( 0x9d, Z80::SBCr_l);
        instructionMap.put( 0x9e, Z80::SBCHL);
        instructionMap.put( 0x9f, Z80::SBCr_a);
        instructionMap.put( 0xa0, Z80::ANDr_b);
        instructionMap.put( 0xa1, Z80::ANDr_c);
        instructionMap.put( 0xa2, Z80::ANDr_d);
        instructionMap.put( 0xa3, Z80::ANDr_e);
        instructionMap.put( 0xa4, Z80::ANDr_h);
        instructionMap.put( 0xa5, Z80::ANDr_l);
        instructionMap.put( 0xa6, Z80::ANDHL);
        instructionMap.put( 0xa7, Z80::ANDr_a);
        instructionMap.put( 0xa8, Z80::XORr_b);
        instructionMap.put( 0xa9, Z80::XORr_c);
        instructionMap.put( 0xaa, Z80::XORr_d);
        instructionMap.put( 0xab, Z80::XORr_e);
        instructionMap.put( 0xac, Z80::XORr_h);
        instructionMap.put( 0xad, Z80::XORr_l);
        instructionMap.put( 0xae, Z80::XORHL);
        instructionMap.put( 0xaf, Z80::XORr_a);
        instructionMap.put( 0xb0, Z80::ORr_b);
        instructionMap.put( 0xb1, Z80::ORr_c);
        instructionMap.put( 0xb2, Z80::ORr_d);
        instructionMap.put( 0xb3, Z80::ORr_e);
        instructionMap.put( 0xb4, Z80::ORr_h);
        instructionMap.put( 0xb5, Z80::ORr_l);
        instructionMap.put( 0xb6, Z80::ORHL);
        instructionMap.put( 0xb7, Z80::ORr_a);
        instructionMap.put( 0xb8, Z80::CPr_b);
        instructionMap.put( 0xb9, Z80::CPr_c);
        instructionMap.put( 0xba, Z80::CPr_d);
        instructionMap.put( 0xbb, Z80::CPr_e);
        instructionMap.put( 0xbc, Z80::CPr_h);
        instructionMap.put( 0xbd, Z80::CPr_l);
        instructionMap.put( 0xbe, Z80::CPHL);
        instructionMap.put( 0xbf, Z80::CPr_a);
        instructionMap.put( 0xc0, Z80::RETNZ);
        instructionMap.put( 0xc1, Z80::POPBC);
        instructionMap.put( 0xc2, Z80::JPNZnn);
        instructionMap.put( 0xc3, Z80::JPnn);
        instructionMap.put( 0xc4, Z80::CALLNZnn);
        instructionMap.put( 0xc5, Z80::PUSHBC);
        instructionMap.put( 0xc6, Z80::ADDn);
        instructionMap.put( 0xc7, Z80::RST00);
        instructionMap.put( 0xc8, Z80::RETZ);
        instructionMap.put( 0xc9, Z80::RET);
        instructionMap.put( 0xca, Z80::JPZnn);
        instructionMap.put( 0xcb, Z80::MAPcb);
        instructionMap.put( 0xcc, Z80::CALLZnn);
        instructionMap.put( 0xcd, Z80::CALLnn);
        instructionMap.put( 0xce, Z80::ADCn);
        instructionMap.put( 0xcf, Z80::RST08);
        instructionMap.put( 0xd0, Z80::RETNC);
        instructionMap.put( 0xd1, Z80::POPDE);
        instructionMap.put( 0xd2, Z80::JPNCnn);
        instructionMap.put( 0xd3, Z80::XX);
        instructionMap.put( 0xd4, Z80::CALLNCnn);
        instructionMap.put( 0xd5, Z80::PUSHDE);
        instructionMap.put( 0xd6, Z80::SUBn);
        instructionMap.put( 0xd7, Z80::RST10);
        instructionMap.put( 0xd8, Z80::RETC);
        instructionMap.put( 0xd9, Z80::RETI);
        instructionMap.put( 0xda, Z80::JPCnn);
        instructionMap.put( 0xdb, Z80::XX);
        instructionMap.put( 0xdc, Z80::CALLCnn);
        instructionMap.put( 0xdd, Z80::XX);
        instructionMap.put( 0xde, Z80::SBCn);
        instructionMap.put( 0xdf, Z80::RST18);
        instructionMap.put( 0xe0, Z80::LDIOnA);
        instructionMap.put( 0xe1, Z80::POPHL);
        instructionMap.put( 0xe2, Z80::LDIOCA);
        instructionMap.put( 0xe3, Z80::XX);
        instructionMap.put( 0xe4, Z80::XX);
        instructionMap.put( 0xe5, Z80::PUSHHL);
        instructionMap.put( 0xe6, Z80::ANDn);
        instructionMap.put( 0xe7, Z80::RST20);
        instructionMap.put( 0xe8, Z80::ADDSPn);
        instructionMap.put( 0xe9, Z80::JPHL);
        instructionMap.put( 0xea, Z80::LDmmA);
        instructionMap.put( 0xeb, Z80::XX);
        instructionMap.put( 0xec, Z80::XX);
        instructionMap.put( 0xed, Z80::XX);
        instructionMap.put( 0xee, Z80::ORn);
        instructionMap.put( 0xef, Z80::RST28);
        instructionMap.put( 0xf0, Z80::LDAIOn);
        instructionMap.put( 0xf1, Z80::POPAF);
        instructionMap.put( 0xf2, Z80::LDAIOC);
        instructionMap.put( 0xf3, Z80::DI);
        instructionMap.put( 0xf4, Z80::XX);
        instructionMap.put( 0xf5, Z80::PUSHAF);
        instructionMap.put( 0xf6, Z80::XORn);
        instructionMap.put( 0xf7, Z80::RST30);
        instructionMap.put( 0xf8, Z80::LDHLSPn);
        instructionMap.put( 0xf9, Z80::XX);
        instructionMap.put( 0xfa, Z80::LDAmm);
        instructionMap.put( 0xfb, Z80::EI);
        instructionMap.put( 0xfc, Z80::XX);
        instructionMap.put( 0xfd, Z80::XX);
        instructionMap.put( 0xfe, Z80::CPn);
        instructionMap.put( 0xff, Z80::RST38);
    }
    private static void populateCBmap() {
        // CB00
        cbInstructionMap.put( 0x00, Z80::RLCr_b);
        cbInstructionMap.put( 0x01, Z80::RLCr_c);
        cbInstructionMap.put( 0x02, Z80::RLCr_d);
        cbInstructionMap.put( 0x03, Z80::RLCr_e);
        cbInstructionMap.put( 0x04, Z80::RLCr_h);
        cbInstructionMap.put( 0x05, Z80::RLCr_l);
        cbInstructionMap.put( 0x06, Z80::RLCHL);
        cbInstructionMap.put( 0x07, Z80::RLCr_a);
        cbInstructionMap.put( 0x08, Z80::RRCr_b);
        cbInstructionMap.put( 0x09, Z80::RRCr_c);
        cbInstructionMap.put( 0x0A, Z80::RRCr_d);
        cbInstructionMap.put( 0x0B, Z80::RRCr_e);
        cbInstructionMap.put( 0x0C, Z80::RRCr_h);
        cbInstructionMap.put( 0x0D, Z80::RRCr_l);
        cbInstructionMap.put( 0x0E, Z80::RRCHL);
        cbInstructionMap.put( 0x0F, Z80::RRCr_a);
        // CB10
        cbInstructionMap.put( 0x10, Z80::RLr_b);
        cbInstructionMap.put( 0x11, Z80::RLr_c);
        cbInstructionMap.put( 0x12, Z80::RLr_d);
        cbInstructionMap.put( 0x13, Z80::RLr_e);
        cbInstructionMap.put( 0x14, Z80::RLr_h);
        cbInstructionMap.put( 0x15, Z80::RLr_l);
        cbInstructionMap.put( 0x16, Z80::RLHL);
        cbInstructionMap.put( 0x17, Z80::RLr_a);
        cbInstructionMap.put( 0x18, Z80::RRr_b);
        cbInstructionMap.put( 0x19, Z80::RRr_c);
        cbInstructionMap.put( 0x1A, Z80::RRr_d);
        cbInstructionMap.put( 0x1B, Z80::RRr_e);
        cbInstructionMap.put( 0x1C, Z80::RRr_h);
        cbInstructionMap.put( 0x1D, Z80::RRr_l);
        cbInstructionMap.put( 0x1E, Z80::RRHL);
        cbInstructionMap.put( 0x1F, Z80::RRr_a);
        //CB20
        cbInstructionMap.put( 0x20, Z80::SLAr_b);
        cbInstructionMap.put( 0x21, Z80::SLAr_c);
        cbInstructionMap.put( 0x22, Z80::SLAr_d);
        cbInstructionMap.put( 0x23, Z80::SLAr_e);
        cbInstructionMap.put( 0x24, Z80::SLAr_h);
        cbInstructionMap.put( 0x25, Z80::SLAr_l);
        cbInstructionMap.put( 0x26, Z80::XX);
        cbInstructionMap.put( 0x27, Z80::SLAr_a);
        cbInstructionMap.put( 0x28, Z80::SRAr_b);
        cbInstructionMap.put( 0x29, Z80::SRAr_c);
        cbInstructionMap.put( 0x2A, Z80::SRAr_d);
        cbInstructionMap.put( 0x2B, Z80::SRAr_e);
        cbInstructionMap.put( 0x2C, Z80::SRAr_h);
        cbInstructionMap.put( 0x2D, Z80::SRAr_l);
        cbInstructionMap.put( 0x2E, Z80::XX);
        cbInstructionMap.put( 0x2F, Z80::SRAr_a);
        // CB30
        cbInstructionMap.put( 0x30, Z80::SWAPr_b);
        cbInstructionMap.put( 0x31, Z80::SWAPr_c);
        cbInstructionMap.put( 0x32, Z80::SWAPr_d);
        cbInstructionMap.put( 0x33, Z80::SWAPr_e);
        cbInstructionMap.put( 0x34, Z80::SWAPr_h);
        cbInstructionMap.put( 0x35, Z80::SWAPr_l);
        cbInstructionMap.put( 0x36, Z80::XX);
        cbInstructionMap.put( 0x37, Z80::SWAPr_a);
        cbInstructionMap.put( 0x38, Z80::SRLr_b);
        cbInstructionMap.put( 0x39, Z80::SRLr_c);
        cbInstructionMap.put( 0x3A, Z80::SRLr_d);
        cbInstructionMap.put( 0x3B, Z80::SRLr_e);
        cbInstructionMap.put( 0x3C, Z80::SRLr_h);
        cbInstructionMap.put( 0x3D, Z80::SRLr_l);
        cbInstructionMap.put( 0x3E, Z80::XX);
        cbInstructionMap.put( 0x3F, Z80::SRLr_a);
        // CB40
        cbInstructionMap.put( 0x40, Z80::BIT0b);
        cbInstructionMap.put( 0x41, Z80::BIT0c);
        cbInstructionMap.put( 0x42, Z80::BIT0d);
        cbInstructionMap.put( 0x43, Z80::BIT0e);
        cbInstructionMap.put( 0x44, Z80::BIT0h);
        cbInstructionMap.put( 0x45, Z80::BIT0l);
        cbInstructionMap.put( 0x46, Z80::BIT0m);
        cbInstructionMap.put( 0x47, Z80::BIT0a);
        cbInstructionMap.put( 0x48, Z80::BIT1b);
        cbInstructionMap.put( 0x49, Z80::BIT1c);
        cbInstructionMap.put( 0x4A, Z80::BIT1d);
        cbInstructionMap.put( 0x4B, Z80::BIT1e);
        cbInstructionMap.put( 0x4C, Z80::BIT1h);
        cbInstructionMap.put( 0x4D, Z80::BIT1l);
        cbInstructionMap.put( 0x4E, Z80::BIT1m);
        cbInstructionMap.put( 0x4F, Z80::BIT1a);
        // CB50
        cbInstructionMap.put( 0x50, Z80::BIT2b);
        cbInstructionMap.put( 0x51, Z80::BIT2c);
        cbInstructionMap.put( 0x52, Z80::BIT2d);
        cbInstructionMap.put( 0x53, Z80::BIT2e);
        cbInstructionMap.put( 0x54, Z80::BIT2h);
        cbInstructionMap.put( 0x55, Z80::BIT2l);
        cbInstructionMap.put( 0x56, Z80::BIT2m);
        cbInstructionMap.put( 0x57, Z80::BIT2a);
        cbInstructionMap.put( 0x58, Z80::BIT3b);
        cbInstructionMap.put( 0x59, Z80::BIT3c);
        cbInstructionMap.put( 0x5A, Z80::BIT3d);
        cbInstructionMap.put( 0x5B, Z80::BIT3e);
        cbInstructionMap.put( 0x5C, Z80::BIT3h);
        cbInstructionMap.put( 0x5D, Z80::BIT3l);
        cbInstructionMap.put( 0x5E, Z80::BIT3m);
        cbInstructionMap.put( 0x5F, Z80::BIT3a);
        // CB60
        cbInstructionMap.put( 0x60, Z80::BIT4b);
        cbInstructionMap.put( 0x61, Z80::BIT4c);
        cbInstructionMap.put( 0x62, Z80::BIT4d);
        cbInstructionMap.put( 0x63, Z80::BIT4e);
        cbInstructionMap.put( 0x64, Z80::BIT4h);
        cbInstructionMap.put( 0x65, Z80::BIT4l);
        cbInstructionMap.put( 0x66, Z80::BIT4m);
        cbInstructionMap.put( 0x67, Z80::BIT4a);
        cbInstructionMap.put( 0x68, Z80::BIT5b);
        cbInstructionMap.put( 0x69, Z80::BIT5c);
        cbInstructionMap.put( 0x6A, Z80::BIT5d);
        cbInstructionMap.put( 0x6B, Z80::BIT5e);
        cbInstructionMap.put( 0x6C, Z80::BIT5h);
        cbInstructionMap.put( 0x6D, Z80::BIT5l);
        cbInstructionMap.put( 0x6E, Z80::BIT5m);
        cbInstructionMap.put( 0x6F, Z80::BIT5a);
        // CB70
        cbInstructionMap.put( 0x70, Z80::BIT6b);
        cbInstructionMap.put( 0x71, Z80::BIT6c);
        cbInstructionMap.put( 0x72, Z80::BIT6d);
        cbInstructionMap.put( 0x73, Z80::BIT6e);
        cbInstructionMap.put( 0x74, Z80::BIT6h);
        cbInstructionMap.put( 0x75, Z80::BIT6l);
        cbInstructionMap.put( 0x76, Z80::BIT6m);
        cbInstructionMap.put( 0x77, Z80::BIT6a);
        cbInstructionMap.put( 0x78, Z80::BIT7b);
        cbInstructionMap.put( 0x79, Z80::BIT7c);
        cbInstructionMap.put( 0x7A, Z80::BIT7d);
        cbInstructionMap.put( 0x7B, Z80::BIT7e);
        cbInstructionMap.put( 0x7C, Z80::BIT7h);
        cbInstructionMap.put( 0x7D, Z80::BIT7l);
        cbInstructionMap.put( 0x7E, Z80::BIT7m);
        cbInstructionMap.put( 0x7F, Z80::BIT7a);
        /*cbInstructionMap.put( 0x80, Z80::RES0b);
        cbInstructionMap.put( 0x81, Z80::RES0c);
        cbInstructionMap.put( 0x82, Z80::RES0d);
        cbInstructionMap.put( 0x83, Z80::RES0e);
        cbInstructionMap.put( 0x84, Z80::RES0h);
        cbInstructionMap.put( 0x85, Z80::RES0l);
        cbInstructionMap.put( 0x86, Z80::RES0m);
        cbInstructionMap.put( 0x87, Z80::RES0a);
        cbInstructionMap.put( 0x88, Z80::RES1b);
        cbInstructionMap.put( 0x89, Z80::RES1c);
        cbInstructionMap.put( 0x8A, Z80::RES1d);
        cbInstructionMap.put( 0x8B, Z80::RES1e);
        cbInstructionMap.put( 0x8C, Z80::RES1h);
        cbInstructionMap.put( 0x8D, Z80::RES1l);
        cbInstructionMap.put( 0x8E, Z80::RES1m);
        cbInstructionMap.put( 0x8F, Z80::RES1a);
        cbInstructionMap.put( 0x90, Z80::RES2b);
        cbInstructionMap.put( 0x91, Z80::RES2c);
        cbInstructionMap.put( 0x92, Z80::RES2d);
        cbInstructionMap.put( 0x93, Z80::RES2e);
        cbInstructionMap.put( 0x94, Z80::RES2h);
        cbInstructionMap.put( 0x95, Z80::RES2l);
        cbInstructionMap.put( 0x96, Z80::RES2m);
        cbInstructionMap.put( 0x97, Z80::RES2a);
        cbInstructionMap.put( 0x98, Z80::RES3b);
        cbInstructionMap.put( 0x99, Z80::RES3c);
        cbInstructionMap.put( 0x9A, Z80::RES3d);
        cbInstructionMap.put( 0x9B, Z80::RES3e);
        cbInstructionMap.put( 0x9C, Z80::RES3h);
        cbInstructionMap.put( 0x9D, Z80::RES3l);
        cbInstructionMap.put( 0x9E, Z80::RES3m);
        cbInstructionMap.put( 0x9F, Z80::RES3a);
        cbInstructionMap.put( 0xA0, Z80::RES4b);
        cbInstructionMap.put( 0xA1, Z80::RES4c);
        cbInstructionMap.put( 0xA2, Z80::RES4d);
        cbInstructionMap.put( 0xA3, Z80::RES4e);
        cbInstructionMap.put( 0xA4, Z80::RES4h);
        cbInstructionMap.put( 0xA5, Z80::RES4l);
        cbInstructionMap.put( 0xA6, Z80::RES4m);
        cbInstructionMap.put( 0xA7, Z80::RES4a);
        cbInstructionMap.put( 0xA8, Z80::RES5b);
        cbInstructionMap.put( 0xA9, Z80::RES5c);
        cbInstructionMap.put( 0xAA, Z80::RES5d);
        cbInstructionMap.put( 0xAB, Z80::RES5e);
        cbInstructionMap.put( 0xAC, Z80::RES5h);
        cbInstructionMap.put( 0xAD, Z80::RES5l);
        cbInstructionMap.put( 0xAE, Z80::RES5m);
        cbInstructionMap.put( 0xAF, Z80::RES5a);
        cbInstructionMap.put( 0xB0, Z80::RES6b);
        cbInstructionMap.put( 0xB1, Z80::RES6c);
        cbInstructionMap.put( 0xB2, Z80::RES6d);
        cbInstructionMap.put( 0xB3, Z80::RES6e);
        cbInstructionMap.put( 0xB4, Z80::RES6h);
        cbInstructionMap.put( 0xB5, Z80::RES6l);
        cbInstructionMap.put( 0xB6, Z80::RES6m);
        cbInstructionMap.put( 0xB7, Z80::RES6a);
        cbInstructionMap.put( 0xB8, Z80::RES7b);
        cbInstructionMap.put( 0xB9, Z80::RES7c);
        cbInstructionMap.put( 0xBA, Z80::RES7d);
        cbInstructionMap.put( 0xBB, Z80::RES7e);
        cbInstructionMap.put( 0xBC, Z80::RES7h);
        cbInstructionMap.put( 0xBD, Z80::RES7l);
        cbInstructionMap.put( 0xBE, Z80::RES7m);
        cbInstructionMap.put( 0xBF, Z80::RES7a);
        cbInstructionMap.put( 0xC0, Z80::SET0b);
        cbInstructionMap.put( 0xC1, Z80::SET0c);
        cbInstructionMap.put( 0xC2, Z80::SET0d);
        cbInstructionMap.put( 0xC3, Z80::SET0e);
        cbInstructionMap.put( 0xC4, Z80::SET0h);
        cbInstructionMap.put( 0xC5, Z80::SET0l);
        cbInstructionMap.put( 0xC6, Z80::SET0m);
        cbInstructionMap.put( 0xC7, Z80::SET0a);
        cbInstructionMap.put( 0xC8, Z80::SET1b);
        cbInstructionMap.put( 0xC9, Z80::SET1c);
        cbInstructionMap.put( 0xCA, Z80::SET1d);
        cbInstructionMap.put( 0xCB, Z80::SET1e);
        cbInstructionMap.put( 0xCC, Z80::SET1h);
        cbInstructionMap.put( 0xCD, Z80::SET1l);
        cbInstructionMap.put( 0xCE, Z80::SET1m);
        cbInstructionMap.put( 0xCF, Z80::SET1a);
        cbInstructionMap.put( 0xD0, Z80::SET2b);
        cbInstructionMap.put( 0xD1, Z80::SET2c);
        cbInstructionMap.put( 0xD2, Z80::SET2d);
        cbInstructionMap.put( 0xD3, Z80::SET2e);
        cbInstructionMap.put( 0xD4, Z80::SET2h);
        cbInstructionMap.put( 0xD5, Z80::SET2l);
        cbInstructionMap.put( 0xD6, Z80::SET2m);
        cbInstructionMap.put( 0xD7, Z80::SET2a);
        cbInstructionMap.put( 0xD8, Z80::SET3b);
        cbInstructionMap.put( 0xD9, Z80::SET3c);
        cbInstructionMap.put( 0xDA, Z80::SET3d);
        cbInstructionMap.put( 0xDB, Z80::SET3e);
        cbInstructionMap.put( 0xDC, Z80::SET3h);
        cbInstructionMap.put( 0xDD, Z80::SET3l);
        cbInstructionMap.put( 0xDE, Z80::SET3m);
        cbInstructionMap.put( 0xDF, Z80::SET3a);
        cbInstructionMap.put( 0xE0, Z80::SET4b);
        cbInstructionMap.put( 0xE1, Z80::SET4c);
        cbInstructionMap.put( 0xE2, Z80::SET4d);
        cbInstructionMap.put( 0xE3, Z80::SET4e);
        cbInstructionMap.put( 0xE4, Z80::SET4h);
        cbInstructionMap.put( 0xE5, Z80::SET4l);
        cbInstructionMap.put( 0xE6, Z80::SET4m);
        cbInstructionMap.put( 0xE7, Z80::SET4a);
        cbInstructionMap.put( 0xE8, Z80::SET5b);
        cbInstructionMap.put( 0xE9, Z80::SET5c);
        cbInstructionMap.put( 0xEA, Z80::SET5d);
        cbInstructionMap.put( 0xEB, Z80::SET5e);
        cbInstructionMap.put( 0xEC, Z80::SET5h);
        cbInstructionMap.put( 0xED, Z80::SET5l);
        cbInstructionMap.put( 0xEE, Z80::SET5m);
        cbInstructionMap.put( 0xEF, Z80::SET5a);
        cbInstructionMap.put( 0xF0, Z80::SET6b);
        cbInstructionMap.put( 0xF1, Z80::SET6c);
        cbInstructionMap.put( 0xF2, Z80::SET6d);
        cbInstructionMap.put( 0xF3, Z80::SET6e);
        cbInstructionMap.put( 0xF4, Z80::SET6h);
        cbInstructionMap.put( 0xF5, Z80::SET6l);
        cbInstructionMap.put( 0xF6, Z80::SET6m);
        cbInstructionMap.put( 0xF7, Z80::SET6a);
        cbInstructionMap.put( 0xF8, Z80::SET7b);
        cbInstructionMap.put( 0xF9, Z80::SET7c);
        cbInstructionMap.put( 0xFA, Z80::SET7d);
        cbInstructionMap.put( 0xFB, Z80::SET7e);
        cbInstructionMap.put( 0xFC, Z80::SET7h);
        cbInstructionMap.put( 0xFD, Z80::SET7l);
        cbInstructionMap.put( 0xFE, Z80::SET7m);
        cbInstructionMap.put( 0xFF, Z80::SET7a);*/
    }

    @FunctionalInterface
    public interface ServiceMethod {
        void execute();
    }
    static Map<Integer, ServiceMethod> instructionMap;
    static Map<Integer, ServiceMethod> cbInstructionMap;

	///// RESET /////
	public static void reset() {
		Reg.a=0; Reg.b=0; Reg.c=0; Reg.d=0; Reg.e=0; Reg.h=0; Reg.l=0; Reg.f=0;
		Reg.sp=0; // clear stack pointer
		Reg.pc=0; // start execution at 0
		Clock.m=0; Clock.t=0; // reset clock
        instructionMap = new HashMap<>();
        cbInstructionMap = new HashMap<>();
	}

	///// DISPATCHER /////
	public static void dispatcher(GPU gpu) {
        populateInstructionMap();
        populateCBmap();
		while(true) {
            System.out.println("Reading rb(addr: 0x" + Integer.toHexString(Reg.pc).toUpperCase() + ")");
			int op = MMU.rb(Reg.pc); // fetch instruction
            Reg.pc++;
            //op &= 0xFF;
			// run corresponding function TODO
            final ServiceMethod serviceMethod = instructionMap.get(op);
            if(serviceMethod != null) {
                System.out.println("----Executing OpCode: 0x" + Integer.toHexString(op) + "   Function: " + instructionMap.get(op));
                serviceMethod.execute();
            }
			Reg.pc &= 65535; //mask program counter to 16bits
			Clock.m += Reg.m; //add time to CPU clock
			Clock.t += Reg.t;
            gpu.step();

            assert(Reg.a>0 && Reg.b>0 && Reg.c>0 && Reg.d>0 && Reg.e>0 && Reg.h>0 && Reg.l>0);
            assert(Reg.a<255 && Reg.b<255 && Reg.c<255 && Reg.d<255 && Reg.e<255 && Reg.h<255 && Reg.l<255);
		}
	}
}

