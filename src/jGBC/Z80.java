package jGBC;

public class Z80 {

	// Register set
	public static class Reg {
		public static byte a=0, b=0, c=0, d=0, e=0, h=0, l=0, f=0; // 8-bit registers
		public static short pc=0, sp=0, i=0, r=0, // 16-bit registers
				m=0, t=0, // clock for last instruction
				ime=0; // TODO IME ?
		// f = flags
		//   Zero (0x80): set if last operation resulted in zero
		//   Operation (0x40): set if the last operation was subtraction
		//   Half-carry (0x20): set if, in the last operation's result, lower half of byte overflowed past 15
		//   Carry (0x10): set if the last operation produced a result >255 for adds or <0 for subtracts.
		
		
		// sp = stack pointer
	}
	
	// Time Clock: The Z80 hopublic static void LDs two types of clock (m and t)
	public static class Clock {
		static int m=0;
		static int t=0;
	}
	
	public enum OpCodes {
		// 00
		NOP, LDBCnn, LDBCmA, INCBC, INCr_b, DECr_b, LDrn_b, RLCA,
		LDmmSP, ADDHLBC, LDABCm, DECBC, INCr_c, DECr_c, LDrn_c, RRCA,

		// 10
		DJNZn, LDDEnn, LDDEmA, INCDE, INCr_d, DECr_d, LDrn_d, RLA, JRn,
		ADDHLDE, LDADEm, DECDE, INCr_e, DECr_e, LDrn_e, RRA,

		// 20
		JRNZn, LDHLnn, LDHLIA, INCHL, INCr_h, DECr_h, LDrn_h, XXop27,
		JRZn, ADDHLHL, LDAHLI, DECHL, INCr_l, DECr_l, LDrn_l, CPL,

		// 30
		JRNCn, LDSPnn, LDHLDA, INCSP, INCHLm, DECHLm, LDHLmn, SCF,
		JRCn, ADDHLSP, LDAHLD, DECSP, INCr_a, DECr_a, LDrn_a, CCF,

		// 40
		LDrr_bb, LDrr_bc, LDrr_bd, LDrr_be, LDrr_bh, LDrr_bl, LDrHLm_b,	LDrr_ba,
		LDrr_cb, LDrr_cc, LDrr_cd, LDrr_ce, LDrr_ch, LDrr_cl, LDrHLm_c,	LDrr_ca,

		// 50
		LDrr_db, LDrr_dc, LDrr_dd, LDrr_de, LDrr_dh, LDrr_dl, LDrHLm_d, LDrr_da,
		LDrr_eb, LDrr_ec, LDrr_ed, LDrr_ee, LDrr_eh, LDrr_el, LDrHLm_e, LDrr_ea,

		// 60
		LDrr_hb,LDrr_hc,	LDrr_hd,	LDrr_he,	LDrr_hh,	LDrr_hl,	LDrHLm_h,	LDrr_ha,
		LDrr_lb,LDrr_lc,	LDrr_ld,	LDrr_le,	LDrr_lh,  	LDrr_ll,  	LDrHLm_l,  	LDrr_la,

		// 70
		LDHLmr_b,LDHLmr_c,	LDHLmr_d,	LDHLmr_e,	LDHLmr_h,	LDHLmr_l,	HALT,	LDHLmr_a,
		LDrr_ab,LDrr_ac,	LDrr_ad,	LDrr_ae,	LDrr_ah,	LDrr_al,	LDrHLm_a,	LDrr_aa,

		// 80
		ADDr_b,	ADDr_c,		ADDr_d,		ADDr_e,		ADDr_h,		ADDr_l,		ADDHL,	ADDr_a,
		ADCr_b,	ADCr_c,		ADCr_d,		ADCr_e,		ADCr_h,		ADCr_l,		ADCHL,	ADCr_a,

		// 90
		SUBr_b,	SUBr_c,		SUBr_d,		SUBr_e,		SUBr_h,		SUBr_l,		SUBHL,	SUBr_a,
		SBCr_b,	SBCr_c,		SBCr_d,		SBCr_e,		SBCr_h,		SBCr_l,		SBCHL,	SBCr_a,

		// A0
		ANDr_b,	ANDr_c,		ANDr_d,		ANDr_e,		ANDr_h,		ANDr_l,		ANDHL,	ANDr_a,
		XORr_b,	XORr_c,		XORr_d,		XORr_e,		XORr_h,		XORr_l,		XORHL,	XORr_a,

		// B0
		ORr_b, ORr_c,	ORr_d,	ORr_e,	ORr_h,	ORr_l,	ORHL,	ORr_a,
		CPr_b, CPr_c,	CPr_d,	CPr_e,	CPr_h,	CPr_l,	CPHL,	CPr_a,

		// C0
		RETNZ, POPBC,	JPNZnn,	JPnn,	CALLNZnn,	PUSHBC,	ADDn,	RST00,
		RETZ, RET,	JPZnn,	MAPcb,	CALLZnn,	CALLnn,	ADCn,	RST08,

		// D0
		RETNC, POPDE,	JPNCnn,	XXopD3,	CALLNCnn,	PUSHDE,	SUBn,	RST10,
		RETC, RETI,	JPCnn,	XXopdb,	CALLCnn, XXopdd,	SBCn,	RST18,

		// E0
		LDIOnA,	POPHL,		LDIOCA,		XXope3,		XXope4,		PUSHHL,		ANDn,		RST20,
		ADDSPn,	JPHL,		LDmmA,		XXopeb,		XXopec,		XXoped,		ORn,		RST28,

		// F0
		LDAIOn,	POPAF,		LDAIOC,		DI,		XXopf4,		PUSHAF,		XORn,		RST30,
		LDHLSPn, XXopf9,		LDAmm,		EI,		XXopfc,		XXopfd,		CPn,		RST38
	}

	public enum CBMap {
	  // CB00
	  RLCr_b,		  RLCr_c,		  RLCr_d,		  RLCr_e,
	  RLCr_h,		  RLCr_l,		  RLCHL,		  RLCr_a,
	  RRCr_b,		  RRCr_c,		  RRCr_d,		  RRCr_e,
	  RRCr_h,		  RRCr_l,		  RRCHL,		  RRCr_a,

	  // CB10
	  RLr_b,		  RLr_c,		  RLr_d,		  RLr_e,
	  RLr_h,		  RLr_l,		  RLHL,		  RLr_a,
	  RRr_b,		  RRr_c,		  RRr_d,		  RRr_e,
	  RRr_h,		  RRr_l,		  RRHL,		  RRr_a,

	  // CB20
	  SLAr_b,		  SLAr_c,		  SLAr_d,		  SLAr_e,
	  SLAr_h,		  SLAr_l,		  XXcb26,		  SLAr_a,
	  SRAr_b,		  SRAr_c,		  SRAr_d,		  SRAr_e,
	  SRAr_h,		  SRAr_l,		  XXcb2e,		  SRAr_a,

	  // CB30
	  SWAPr_b,		  SWAPr_c,		  SWAPr_d,		  SWAPr_e,
	  SWAPr_h,		  SWAPr_l,		  XXcb36,		  SWAPr_a,
	  SRLr_b,		  SRLr_c,		  SRLr_d,		  SRLr_e,
	  SRLr_h,		  SRLr_l,		  XX,		  SRLr_a,

	  // CB40
	  BIT0b,	  BIT0c,	  BIT0d,	  BIT0e,
	  BIT0h,	  BIT0l,	  BIT0m,	  BIT0a,
	  BIT1b,	  BIT1c,	  BIT1d,	  BIT1e,
	  BIT1h,	  BIT1l,	  BIT1m,	  BIT1a,

	  // CB50
	  BIT2b,	  BIT2c,	  BIT2d,	  BIT2e,
	  BIT2h,	  BIT2l,	  BIT2m,	  BIT2a,
	  BIT3b,	  BIT3c,	  BIT3d,	  BIT3e,
	  BIT3h,	  BIT3l,	  BIT3m,	  BIT3a,

	  // CB60
	  BIT4b,	  BIT4c,	  BIT4d,	  BIT4e,
	  BIT4h,	  BIT4l,	  BIT4m,	  BIT4a,
	  BIT5b,	  BIT5c,	  BIT5d,	  BIT5e,
	  BIT5h,	  BIT5l,	  BIT5m,	  BIT5a,

	  // CB70
	  BIT6b,	  BIT6c,	  BIT6d,	  BIT6e,
	  BIT6h,	  BIT6l,	  BIT6m,	  BIT6a,
	  BIT7b,	  BIT7c,	  BIT7d,	  BIT7e,
	  BIT7h,	  BIT7l,	  BIT7m,	  BIT7a,

	  // CB80
	  XXcb80,
	  XXcb81,
	  XXcb82,
	  XXcb83,
	  XXcb84,
	  XXcb85
	  // ...... until CBFF
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
    public static void LDADEm() { Reg.a=MMU.rb((Reg.d<<8)+Reg.e); Reg.m=2; Reg.t=8; }
    public static void LDAmm() { Reg.a=MMU.rb(MMU.rw(Reg.pc)); Reg.pc+=2; Reg.m=4; Reg.t=16; }
    public static void LDBCnn() { Reg.c=MMU.rb(Reg.pc); Reg.b=MMU.rb(Reg.pc+1); Reg.pc+=2; Reg.m=3; Reg.t=12; }
    public static void LDDEnn() { Reg.e=MMU.rb(Reg.pc); Reg.d=MMU.rb(Reg.pc+1); Reg.pc+=2; Reg.m=3; Reg.t=12; }
    public static void LDHLnn() { Reg.l=MMU.rb(Reg.pc); Reg.h=MMU.rb(Reg.pc+1); Reg.pc+=2; Reg.m=3; Reg.t=12; }
    public static void LDSPnn() { Reg.sp=MMU.rw(Reg.pc); Reg.pc+=2; Reg.m=3; Reg.t=12; }

    public static void LDHLmm() { int i=MMU.rw(Reg.pc); Reg.pc+=2; Reg.l=MMU.rb(i); Reg.h=MMU.rb(i+1); Reg.m=5; Reg.t=20; }
    public static void LDmmHL() { int i=MMU.rw(Reg.pc); Reg.pc+=2; MMU.ww(i,(short) ((Reg.h<<8)+Reg.l)); Reg.m=5; Reg.t=20; }

    public static void LDHLIA() { MMU.wb((Reg.h<<8)+Reg.l, Reg.a); Reg.l=(byte) ((Reg.l+1)&255); if(Reg.l==0) Reg.h=(byte) ((Reg.h+1)&255); Reg.m=2; Reg.t=8; }
    public static void LDAHLI() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.l=(byte) ((Reg.l+1)&255); if(Reg.l==0) Reg.h=(byte) ((Reg.h+1)&255); Reg.m=2; Reg.t=8; }

    public static void LDHLDA() { MMU.wb((Reg.h<<8)+Reg.l, Reg.a); Reg.l=(byte) ((Reg.l-1)&255); if(Reg.l==255) Reg.h=(byte) ((Reg.h-1)&255); Reg.m=2; Reg.t=8; }
    public static void LDAHLD() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.l=(byte) ((Reg.l-1)&255); if(Reg.l==255) Reg.h=(byte) ((Reg.h-1)&255); Reg.m=2; Reg.t=8; }
    
    public static void LDAIOn() { Reg.a=MMU.rb(0xFF00+MMU.rb(Reg.pc)); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDIOnA() { MMU.wb(0xFF00+MMU.rb(Reg.pc),Reg.a); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDAIOC() { Reg.a=MMU.rb(0xFF00+Reg.c); Reg.m=2; Reg.t=8; }
    public static void LDIOCA() { MMU.wb(0xFF00+Reg.c,Reg.a); Reg.m=2; Reg.t=8; }

    public static void LDHLSPn() { int i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; i+=Reg.sp; Reg.h=(byte) ((i>>8)&255); Reg.l=(byte) (i&255); Reg.m=3; Reg.t=12; }

    public static void SWAPr_b() { byte tr=Reg.b; Reg.b=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_c() { byte tr=Reg.c; Reg.c=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_d() { byte tr=Reg.d; Reg.d=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_e() { byte tr=Reg.e; Reg.e=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_h() { byte tr=Reg.h; Reg.h=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_l() { byte tr=Reg.l; Reg.l=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
    public static void SWAPr_a() { byte tr=Reg.a; Reg.a=MMU.rb((Reg.h<<8)+Reg.l); MMU.wb((Reg.h<<8)+Reg.l,tr); Reg.m=4; Reg.t=16; }
	
    /*--- Data processing ---*/
    public static void ADDr_b() { Reg.a+=Reg.b; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_c() { Reg.a+=Reg.c; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_d() { Reg.a+=Reg.d; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_e() { Reg.a+=Reg.e; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_h() { Reg.a+=Reg.h; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_l() { Reg.a+=Reg.l; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDr_a() { Reg.a+=Reg.a; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADDHL() { Reg.a+=MMU.rb((Reg.h<<8)+Reg.l); fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void ADDn() { Reg.a+=MMU.rb(Reg.pc); Reg.pc++; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void ADDHLBC() { byte hl=(byte) ((Reg.h<<8)+Reg.l); hl+=(Reg.b<<8)+Reg.c; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h=(byte) ((hl>>8)&255); Reg.l=(byte) (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDHLDE() { byte hl=(byte) ((Reg.h<<8)+Reg.l); hl+=(Reg.d<<8)+Reg.e; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h=(byte) ((hl>>8)&255); Reg.l=(byte) (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDHLHL() { byte hl=(byte) ((Reg.h<<8)+Reg.l); hl+=(Reg.h<<8)+Reg.l; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h=(byte) ((hl>>8)&255); Reg.l=(byte) (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDHLSP() { byte hl=(byte) ((Reg.h<<8)+Reg.l); hl+=Reg.sp; if(hl>65535) Reg.f|=0x10; else Reg.f&=0xEF; Reg.h=(byte) ((hl>>8)&255); Reg.l=(byte) (hl&255); Reg.m=3; Reg.t=12; }
    public static void ADDSPn() { byte i=MMU.rb(Reg.pc); if(i>127) i=(byte) -((~i+1)&255); Reg.pc++; Reg.sp+=i; Reg.m=4; Reg.t=16; }
    public static void ADCr_b() { Reg.a+=Reg.b; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_c() { Reg.a+=Reg.c; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_d() { Reg.a+=Reg.d; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_e() { Reg.a+=Reg.e; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_h() { Reg.a+=Reg.h; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_l() { Reg.a+=Reg.l; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCr_a() { Reg.a+=Reg.a; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void ADCHL() { Reg.a+=MMU.rb((Reg.h<<8)+Reg.l); Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void ADCn() { Reg.a+=MMU.rb(Reg.pc); Reg.pc++; Reg.a+=((Reg.f&0x10)!=0)?1:0; fz(Reg.a, (byte)0); if(Reg.a>255) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void SUBr_b() { Reg.a-=Reg.b; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_c() { Reg.a-=Reg.c; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_d() { Reg.a-=Reg.d; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_e() { Reg.a-=Reg.e; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_h() { Reg.a-=Reg.h; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_l() { Reg.a-=Reg.l; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBr_a() { Reg.a-=Reg.a; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SUBHL() { Reg.a-=MMU.rb((Reg.h<<8)+Reg.l); fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void SUBn() { Reg.a-=MMU.rb(Reg.pc); Reg.pc++; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void SBCr_b() { Reg.a-=Reg.b; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_c() { Reg.a-=Reg.c; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_d() { Reg.a-=Reg.d; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_e() { Reg.a-=Reg.e; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_h() { Reg.a-=Reg.h; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_l() { Reg.a-=Reg.l; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCr_a() { Reg.a-=Reg.a; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=1; Reg.t=4; }
    public static void SBCHL() { Reg.a-=MMU.rb((Reg.h<<8)+Reg.l); Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }
    public static void SBCn() { Reg.a-=MMU.rb(Reg.pc); Reg.pc++; Reg.a-=((Reg.f&0x10)!=0)?1:0; fz(Reg.a,(byte)1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    public static void CPr_b() { byte i=Reg.a; i-=Reg.b; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_c() { byte i=Reg.a; i-=Reg.c; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_d() { byte i=Reg.a; i-=Reg.d; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_e() { byte i=Reg.a; i-=Reg.e; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_h() { byte i=Reg.a; i-=Reg.h; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_l() { byte i=Reg.a; i-=Reg.l; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPr_a() { byte i=Reg.a; i-=Reg.a; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=1; Reg.t=4; }
    public static void CPHL() { byte i=Reg.a; i-=MMU.rb((Reg.h<<8)+Reg.l); fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=2; Reg.t=8; }
    public static void CPn() { byte i=Reg.a; i-=MMU.rb(Reg.pc); Reg.pc++; fz(i,(byte)1); if(i<0) Reg.f|=0x10; i&=255; Reg.m=2; Reg.t=8; }
    
    public static void ANDr_b() { Reg.a&=Reg.b; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDr_c() { Reg.a&=Reg.c; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDr_d() { Reg.a&=Reg.d; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDr_e() { Reg.a&=Reg.e; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDr_h() { Reg.a&=Reg.h; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDr_l() { Reg.a&=Reg.l; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDr_a() { Reg.a&=Reg.a; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ANDHL() { Reg.a&=MMU.rb((Reg.h<<8)+Reg.l); Reg.a&=255; fz(Reg.a); Reg.m=2; Reg.t=8; }
    public static void ANDn() { Reg.a&=MMU.rb(Reg.pc); Reg.pc++; Reg.a&=255; fz(Reg.a); Reg.m=2; Reg.t=8; }

    public static void ORr_b() { Reg.a|=Reg.b; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORr_c() { Reg.a|=Reg.c; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORr_d() { Reg.a|=Reg.d; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORr_e() { Reg.a|=Reg.e; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORr_h() { Reg.a|=Reg.h; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORr_l() { Reg.a|=Reg.l; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORr_a() { Reg.a|=Reg.a; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void ORHL() { Reg.a|=MMU.rb((Reg.h<<8)+Reg.l); Reg.a&=255; fz(Reg.a); Reg.m=2; Reg.t=8; }
    public static void ORn() { Reg.a|=MMU.rb(Reg.pc); Reg.pc++; Reg.a&=255; fz(Reg.a); Reg.m=2; Reg.t=8; }

    public static void XORr_b() { Reg.a^=Reg.b; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORr_c() { Reg.a^=Reg.c; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORr_d() { Reg.a^=Reg.d; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORr_e() { Reg.a^=Reg.e; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORr_h() { Reg.a^=Reg.h; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORr_l() { Reg.a^=Reg.l; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORr_a() { Reg.a^=Reg.a; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void XORHL() { Reg.a^=MMU.rb((Reg.h<<8)+Reg.l); Reg.a&=255; fz(Reg.a); Reg.m=2; Reg.t=8; }
    public static void XORn() { Reg.a^=MMU.rb(Reg.pc); Reg.pc++; Reg.a&=255; fz(Reg.a); Reg.m=2; Reg.t=8; }

    public static void INCr_b() { Reg.b++; Reg.b&=255; fz(Reg.b); Reg.m=1; Reg.t=4; }
    public static void INCr_c() { Reg.c++; Reg.c&=255; fz(Reg.c); Reg.m=1; Reg.t=4; }
    public static void INCr_d() { Reg.d++; Reg.d&=255; fz(Reg.d); Reg.m=1; Reg.t=4; }
    public static void INCr_e() { Reg.e++; Reg.e&=255; fz(Reg.e); Reg.m=1; Reg.t=4; }
    public static void INCr_h() { Reg.h++; Reg.h&=255; fz(Reg.h); Reg.m=1; Reg.t=4; }
    public static void INCr_l() { Reg.l++; Reg.l&=255; fz(Reg.l); Reg.m=1; Reg.t=4; }
    public static void INCr_a() { Reg.a++; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void INCHLm() { byte i=(byte) (MMU.rb((Reg.h<<8)+Reg.l)+1); i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i); Reg.m=3; Reg.t=12; }

    public static void DECr_b() { Reg.b--; Reg.b&=255; fz(Reg.b); Reg.m=1; Reg.t=4; }
    public static void DECr_c() { Reg.c--; Reg.c&=255; fz(Reg.c); Reg.m=1; Reg.t=4; }
    public static void DECr_d() { Reg.d--; Reg.d&=255; fz(Reg.d); Reg.m=1; Reg.t=4; }
    public static void DECr_e() { Reg.e--; Reg.e&=255; fz(Reg.e); Reg.m=1; Reg.t=4; }
    public static void DECr_h() { Reg.h--; Reg.h&=255; fz(Reg.h); Reg.m=1; Reg.t=4; }
    public static void DECr_l() { Reg.l--; Reg.l&=255; fz(Reg.l); Reg.m=1; Reg.t=4; }
    public static void DECr_a() { Reg.a--; Reg.a&=255; fz(Reg.a); Reg.m=1; Reg.t=4; }
    public static void DECHLm() { byte i=(byte) (MMU.rb((Reg.h<<8)+Reg.l)-1); i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i); Reg.m=3; Reg.t=12; }

    public static void INCBC() { Reg.c=(Reg.c+1)&255; if(!Reg.c) Reg.b=(Reg.b+1)&255; Reg.m=1; Reg.t=4; }
    public static void INCDE() { Reg.e=(Reg.e+1)&255; if(!Reg.e) Reg.d=(Reg.d+1)&255; Reg.m=1; Reg.t=4; }
    public static void INCHL() { Reg.l=(Reg.l+1)&255; if(!Reg.l) Reg.h=(Reg.h+1)&255; Reg.m=1; Reg.t=4; }
    public static void INCSP() { Reg.sp=(Reg.sp+1)&65535; Reg.m=1; Reg.t=4; }

    public static void DECBC() { Reg.c=(Reg.c-1)&255; if(Reg.c==255) Reg.b=(Reg.b-1)&255; Reg.m=1; Reg.t=4; }
    public static void DECDE() { Reg.e=(Reg.e-1)&255; if(Reg.e==255) Reg.d=(Reg.d-1)&255; Reg.m=1; Reg.t=4; }
    public static void DECHL() { Reg.l=(Reg.l-1)&255; if(Reg.l==255) Reg.h=(Reg.h-1)&255; Reg.m=1; Reg.t=4; }
    public static void DECSP() { Reg.sp=(Reg.sp-1)&65535; Reg.m=1; Reg.t=4; }

    /*--- Bit manipulation ---*/
    public static void BIT0b() { fz(Reg.b&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0c() { fz(Reg.c&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0d() { fz(Reg.d&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0e() { fz(Reg.e&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0h() { fz(Reg.h&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0l() { fz(Reg.l&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0a() { fz(Reg.a&0x01); Reg.m=2; Reg.t=8; }
    public static void BIT0m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x01); Reg.m=3; Reg.t=12; }

    public static void BIT1b() { fz(Reg.b&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1c() { fz(Reg.c&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1d() { fz(Reg.d&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1e() { fz(Reg.e&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1h() { fz(Reg.h&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1l() { fz(Reg.l&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1a() { fz(Reg.a&0x02); Reg.m=2; Reg.t=8; }
    public static void BIT1m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x02); Reg.m=3; Reg.t=12; }

    public static void BIT2b() { fz(Reg.b&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2c() { fz(Reg.c&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2d() { fz(Reg.d&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2e() { fz(Reg.e&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2h() { fz(Reg.h&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2l() { fz(Reg.l&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2a() { fz(Reg.a&0x04); Reg.m=2; Reg.t=8; }
    public static void BIT2m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x04); Reg.m=3; Reg.t=12; }

    public static void BIT3b() { fz(Reg.b&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3c() { fz(Reg.c&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3d() { fz(Reg.d&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3e() { fz(Reg.e&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3h() { fz(Reg.h&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3l() { fz(Reg.l&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3a() { fz(Reg.a&0x08); Reg.m=2; Reg.t=8; }
    public static void BIT3m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x08); Reg.m=3; Reg.t=12; }

    public static void BIT4b() { fz(Reg.b&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4c() { fz(Reg.c&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4d() { fz(Reg.d&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4e() { fz(Reg.e&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4h() { fz(Reg.h&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4l() { fz(Reg.l&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4a() { fz(Reg.a&0x10); Reg.m=2; Reg.t=8; }
    public static void BIT4m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x10); Reg.m=3; Reg.t=12; }

    public static void BIT5b() { fz(Reg.b&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5c() { fz(Reg.c&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5d() { fz(Reg.d&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5e() { fz(Reg.e&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5h() { fz(Reg.h&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5l() { fz(Reg.l&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5a() { fz(Reg.a&0x20); Reg.m=2; Reg.t=8; }
    public static void BIT5m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x20); Reg.m=3; Reg.t=12; }

    public static void BIT6b() { fz(Reg.b&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6c() { fz(Reg.c&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6d() { fz(Reg.d&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6e() { fz(Reg.e&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6h() { fz(Reg.h&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6l() { fz(Reg.l&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6a() { fz(Reg.a&0x40); Reg.m=2; Reg.t=8; }
    public static void BIT6m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x40); Reg.m=3; Reg.t=12; }

    public static void BIT7b() { fz(Reg.b&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7c() { fz(Reg.c&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7d() { fz(Reg.d&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7e() { fz(Reg.e&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7h() { fz(Reg.h&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7l() { fz(Reg.l&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7a() { fz(Reg.a&0x80); Reg.m=2; Reg.t=8; }
    public static void BIT7m() { fz(MMU.rb((Reg.h<<8)+Reg.l)&0x80); Reg.m=3; Reg.t=12; }

    RLA() { var ci=Reg.f&0x10?1:0; var co=Reg.a&0x80?0x10:0; Reg.a=(Reg.a<<1)+ci; Reg.a&=255; Reg.f=(Reg.f&0xEF)+co; Reg.m=1; Reg.t=4; }
    RLCA() { var ci=Reg.a&0x80?1:0; var co=Reg.a&0x80?0x10:0; Reg.a=(Reg.a<<1)+ci; Reg.a&=255; Reg.f=(Reg.f&0xEF)+co; Reg.m=1; Reg.t=4; }
    RRA() { var ci=Reg.f&0x10?0x80:0; var co=Reg.a&1?0x10:0; Reg.a=(Reg.a>>1)+ci; Reg.a&=255; Reg.f=(Reg.f&0xEF)+co; Reg.m=1; Reg.t=4; }
    RRCA() { var ci=Reg.a&1?0x80:0; var co=Reg.a&1?0x10:0; Reg.a=(Reg.a>>1)+ci; Reg.a&=255; Reg.f=(Reg.f&0xEF)+co; Reg.m=1; Reg.t=4; }

    RLr_b() { var ci=Reg.f&0x10?1:0; var co=Reg.b&0x80?0x10:0; Reg.b=(Reg.b<<1)+ci; Reg.b&=255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLr_c() { var ci=Reg.f&0x10?1:0; var co=Reg.c&0x80?0x10:0; Reg.c=(Reg.c<<1)+ci; Reg.c&=255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLr_d() { var ci=Reg.f&0x10?1:0; var co=Reg.d&0x80?0x10:0; Reg.d=(Reg.d<<1)+ci; Reg.d&=255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLr_e() { var ci=Reg.f&0x10?1:0; var co=Reg.e&0x80?0x10:0; Reg.e=(Reg.e<<1)+ci; Reg.e&=255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLr_h() { var ci=Reg.f&0x10?1:0; var co=Reg.h&0x80?0x10:0; Reg.h=(Reg.h<<1)+ci; Reg.h&=255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLr_l() { var ci=Reg.f&0x10?1:0; var co=Reg.l&0x80?0x10:0; Reg.l=(Reg.l<<1)+ci; Reg.l&=255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLr_a() { var ci=Reg.f&0x10?1:0; var co=Reg.a&0x80?0x10:0; Reg.a=(Reg.a<<1)+ci; Reg.a&=255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLHL() { var i=MMU.rb((Reg.h<<8)+Reg.l); var ci=Reg.f&0x10?1:0; var co=i&0x80?0x10:0; i=(i<<1)+ci; i&=255; fz(i); MMU.wb((Reg.h<<8)+Reg.l,i); Reg.f=(Reg.f&0xEF)+co; Reg.m=4; Reg.t=16; }

    RLCr_b() { var ci=Reg.b&0x80?1:0; var co=Reg.b&0x80?0x10:0; Reg.b=(Reg.b<<1)+ci; Reg.b&=255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCr_c() { var ci=Reg.c&0x80?1:0; var co=Reg.c&0x80?0x10:0; Reg.c=(Reg.c<<1)+ci; Reg.c&=255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCr_d() { var ci=Reg.d&0x80?1:0; var co=Reg.d&0x80?0x10:0; Reg.d=(Reg.d<<1)+ci; Reg.d&=255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCr_e() { var ci=Reg.e&0x80?1:0; var co=Reg.e&0x80?0x10:0; Reg.e=(Reg.e<<1)+ci; Reg.e&=255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCr_h() { var ci=Reg.h&0x80?1:0; var co=Reg.h&0x80?0x10:0; Reg.h=(Reg.h<<1)+ci; Reg.h&=255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCr_l() { var ci=Reg.l&0x80?1:0; var co=Reg.l&0x80?0x10:0; Reg.l=(Reg.l<<1)+ci; Reg.l&=255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCr_a() { var ci=Reg.a&0x80?1:0; var co=Reg.a&0x80?0x10:0; Reg.a=(Reg.a<<1)+ci; Reg.a&=255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RLCHL() { var i=MMU.rb((Reg.h<<8)+Reg.l); var ci=i&0x80?1:0; var co=i&0x80?0x10:0; i=(i<<1)+ci; i&=255; fz(i); MMU.wb((Reg.h<<8)+Reg.l,i); Reg.f=(Reg.f&0xEF)+co; Reg.m=4; Reg.t=16; }

    RRr_b() { var ci=Reg.f&0x10?0x80:0; var co=Reg.b&1?0x10:0; Reg.b=(Reg.b>>1)+ci; Reg.b&=255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRr_c() { var ci=Reg.f&0x10?0x80:0; var co=Reg.c&1?0x10:0; Reg.c=(Reg.c>>1)+ci; Reg.c&=255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRr_d() { var ci=Reg.f&0x10?0x80:0; var co=Reg.d&1?0x10:0; Reg.d=(Reg.d>>1)+ci; Reg.d&=255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRr_e() { var ci=Reg.f&0x10?0x80:0; var co=Reg.e&1?0x10:0; Reg.e=(Reg.e>>1)+ci; Reg.e&=255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRr_h() { var ci=Reg.f&0x10?0x80:0; var co=Reg.h&1?0x10:0; Reg.h=(Reg.h>>1)+ci; Reg.h&=255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRr_l() { var ci=Reg.f&0x10?0x80:0; var co=Reg.l&1?0x10:0; Reg.l=(Reg.l>>1)+ci; Reg.l&=255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRr_a() { var ci=Reg.f&0x10?0x80:0; var co=Reg.a&1?0x10:0; Reg.a=(Reg.a>>1)+ci; Reg.a&=255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRHL() { var i=MMU.rb((Reg.h<<8)+Reg.l); var ci=Reg.f&0x10?0x80:0; var co=i&1?0x10:0; i=(i>>1)+ci; i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i); Reg.f=(Reg.f&0xEF)+co; Reg.m=4; Reg.t=16; }

    RRCr_b() { var ci=Reg.b&1?0x80:0; var co=Reg.b&1?0x10:0; Reg.b=(Reg.b>>1)+ci; Reg.b&=255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCr_c() { var ci=Reg.c&1?0x80:0; var co=Reg.c&1?0x10:0; Reg.c=(Reg.c>>1)+ci; Reg.c&=255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCr_d() { var ci=Reg.d&1?0x80:0; var co=Reg.d&1?0x10:0; Reg.d=(Reg.d>>1)+ci; Reg.d&=255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCr_e() { var ci=Reg.e&1?0x80:0; var co=Reg.e&1?0x10:0; Reg.e=(Reg.e>>1)+ci; Reg.e&=255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCr_h() { var ci=Reg.h&1?0x80:0; var co=Reg.h&1?0x10:0; Reg.h=(Reg.h>>1)+ci; Reg.h&=255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCr_l() { var ci=Reg.l&1?0x80:0; var co=Reg.l&1?0x10:0; Reg.l=(Reg.l>>1)+ci; Reg.l&=255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCr_a() { var ci=Reg.a&1?0x80:0; var co=Reg.a&1?0x10:0; Reg.a=(Reg.a>>1)+ci; Reg.a&=255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    RRCHL() { var i=MMU.rb((Reg.h<<8)+Reg.l); var ci=i&1?0x80:0; var co=i&1?0x10:0; i=(i>>1)+ci; i&=255; MMU.wb((Reg.h<<8)+Reg.l,i); fz(i); Reg.f=(Reg.f&0xEF)+co; Reg.m=4; Reg.t=16; }

    SLAr_b() { var co=Reg.b&0x80?0x10:0; Reg.b=(Reg.b<<1)&255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLAr_c() { var co=Reg.c&0x80?0x10:0; Reg.c=(Reg.c<<1)&255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLAr_d() { var co=Reg.d&0x80?0x10:0; Reg.d=(Reg.d<<1)&255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLAr_e() { var co=Reg.e&0x80?0x10:0; Reg.e=(Reg.e<<1)&255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLAr_h() { var co=Reg.h&0x80?0x10:0; Reg.h=(Reg.h<<1)&255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLAr_l() { var co=Reg.l&0x80?0x10:0; Reg.l=(Reg.l<<1)&255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLAr_a() { var co=Reg.a&0x80?0x10:0; Reg.a=(Reg.a<<1)&255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }

    SLLr_b() { var co=Reg.b&0x80?0x10:0; Reg.b=(Reg.b<<1)&255+1; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLLr_c() { var co=Reg.c&0x80?0x10:0; Reg.c=(Reg.c<<1)&255+1; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLLr_d() { var co=Reg.d&0x80?0x10:0; Reg.d=(Reg.d<<1)&255+1; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLLr_e() { var co=Reg.e&0x80?0x10:0; Reg.e=(Reg.e<<1)&255+1; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLLr_h() { var co=Reg.h&0x80?0x10:0; Reg.h=(Reg.h<<1)&255+1; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLLr_l() { var co=Reg.l&0x80?0x10:0; Reg.l=(Reg.l<<1)&255+1; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SLLr_a() { var co=Reg.a&0x80?0x10:0; Reg.a=(Reg.a<<1)&255+1; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }

    SRAr_b() { var ci=Reg.b&0x80; var co=Reg.b&1?0x10:0; Reg.b=((Reg.b>>1)+ci)&255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRAr_c() { var ci=Reg.c&0x80; var co=Reg.c&1?0x10:0; Reg.c=((Reg.c>>1)+ci)&255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRAr_d() { var ci=Reg.d&0x80; var co=Reg.d&1?0x10:0; Reg.d=((Reg.d>>1)+ci)&255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRAr_e() { var ci=Reg.e&0x80; var co=Reg.e&1?0x10:0; Reg.e=((Reg.e>>1)+ci)&255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRAr_h() { var ci=Reg.h&0x80; var co=Reg.h&1?0x10:0; Reg.h=((Reg.h>>1)+ci)&255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRAr_l() { var ci=Reg.l&0x80; var co=Reg.l&1?0x10:0; Reg.l=((Reg.l>>1)+ci)&255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRAr_a() { var ci=Reg.a&0x80; var co=Reg.a&1?0x10:0; Reg.a=((Reg.a>>1)+ci)&255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }

    SRLr_b() { var co=Reg.b&1?0x10:0; Reg.b=(Reg.b>>1)&255; fz(Reg.b); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRLr_c() { var co=Reg.c&1?0x10:0; Reg.c=(Reg.c>>1)&255; fz(Reg.c); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRLr_d() { var co=Reg.d&1?0x10:0; Reg.d=(Reg.d>>1)&255; fz(Reg.d); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRLr_e() { var co=Reg.e&1?0x10:0; Reg.e=(Reg.e>>1)&255; fz(Reg.e); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRLr_h() { var co=Reg.h&1?0x10:0; Reg.h=(Reg.h>>1)&255; fz(Reg.h); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRLr_l() { var co=Reg.l&1?0x10:0; Reg.l=(Reg.l>>1)&255; fz(Reg.l); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }
    SRLr_a() { var co=Reg.a&1?0x10:0; Reg.a=(Reg.a>>1)&255; fz(Reg.a); Reg.f=(Reg.f&0xEF)+co; Reg.m=2; Reg.t=8; }

    CPL() { Reg.a = (~Reg.a)&255; fz(Reg.a,1); Reg.m=1; Reg.t=4; }
    NEG() { Reg.a=0-Reg.a; fz(Reg.a,1); if(Reg.a<0) Reg.f|=0x10; Reg.a&=255; Reg.m=2; Reg.t=8; }

    CCF() { var ci=Reg.f&0x10?0:0x10; Reg.f=(Reg.f&0xEF)+ci; Reg.m=1; Reg.t=4; }
    SCF() { Reg.f|=0x10; Reg.m=1; Reg.t=4; }

    /*--- Stack ---*/
    PUSHBC() { Reg.sp--; MMU.wb(Reg.sp,Reg.b); Reg.sp--; MMU.wb(Reg.sp,Reg.c); Reg.m=3; Reg.t=12; }
    PUSHDE() { Reg.sp--; MMU.wb(Reg.sp,Reg.d); Reg.sp--; MMU.wb(Reg.sp,Reg.e); Reg.m=3; Reg.t=12; }
    PUSHHL() { Reg.sp--; MMU.wb(Reg.sp,Reg.h); Reg.sp--; MMU.wb(Reg.sp,Reg.l); Reg.m=3; Reg.t=12; }
    PUSHAF() { Reg.sp--; MMU.wb(Reg.sp,Reg.a); Reg.sp--; MMU.wb(Reg.sp,Reg.f); Reg.m=3; Reg.t=12; }

    POPBC() { Reg.c=MMU.rb(Reg.sp); Reg.sp++; Reg.b=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }
    POPDE() { Reg.e=MMU.rb(Reg.sp); Reg.sp++; Reg.d=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }
    POPHL() { Reg.l=MMU.rb(Reg.sp); Reg.sp++; Reg.h=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }
    POPAF() { Reg.f=MMU.rb(Reg.sp); Reg.sp++; Reg.a=MMU.rb(Reg.sp); Reg.sp++; Reg.m=3; Reg.t=12; }

    /*--- Jump ---*/
    JPnn() { Reg.pc = MMU.rw(Reg.pc); Reg.m=3; Reg.t=12; }
    JPHL() { Reg.pc=Reg.hl; Reg.m=1; Reg.t=4; }
    JPNZnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x00) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }
    JPZnn()  { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x80) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }
    JPNCnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x00) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }
    JPCnn()  { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x10) { Reg.pc=MMU.rw(Reg.pc); Reg.m++; Reg.t+=4; } else Reg.pc+=2; }

    JRn() { var i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; Reg.pc+=i; Reg.m++; Reg.t+=4; }
    JRNZn() { var i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x80)==0x00) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }
    JRZn()  { var i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x80)==0x80) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }
    JRNCn() { var i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x10)==0x00) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }
    JRCn()  { var i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; if((Reg.f&0x10)==0x10) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }

    DJNZn() { var i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; Reg.m=2; Reg.t=8; Reg.b--; if(Reg.b) { Reg.pc+=i; Reg.m++; Reg.t+=4; } }

    CALLnn() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc+2); Reg.pc=MMU.rw(Reg.pc); Reg.m=5; Reg.t=20; }
    CALLNZnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x00) { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc+2); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }
    CALLZnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x80)==0x80) { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc+2); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }
    CALLNCnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x00) { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc+2); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }
    CALLCnn() { Reg.m=3; Reg.t=12; if((Reg.f&0x10)==0x10) { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc+2); Reg.pc=MMU.rw(Reg.pc); Reg.m+=2; Reg.t+=8; } else Reg.pc+=2; }

    RET() { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m=3; Reg.t=12; }
    RETI() { Reg.ime=1; Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m=3; Reg.t=12; }
    RETNZ() { Reg.m=1; Reg.t=4; if((Reg.f&0x80)==0x00) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }
    RETZ() { Reg.m=1; Reg.t=4; if((Reg.f&0x80)==0x80) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }
    RETNC() { Reg.m=1; Reg.t=4; if((Reg.f&0x10)==0x00) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }
    RETC() { Reg.m=1; Reg.t=4; if((Reg.f&0x10)==0x10) { Reg.pc=MMU.rw(Reg.sp); Reg.sp+=2; Reg.m+=2; Reg.t+=8; } }

    RST00() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x00; Reg.m=3; Reg.t=12; }
    RST08() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x08; Reg.m=3; Reg.t=12; }
    RST10() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x10; Reg.m=3; Reg.t=12; }
    RST18() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x18; Reg.m=3; Reg.t=12; }
    RST20() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x20; Reg.m=3; Reg.t=12; }
    RST28() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x28; Reg.m=3; Reg.t=12; }
    RST30() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x30; Reg.m=3; Reg.t=12; }
    RST38() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x38; Reg.m=3; Reg.t=12; }
    RST40() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x40; Reg.m=3; Reg.t=12; }
    RST48() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x48; Reg.m=3; Reg.t=12; }
    RST50() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x50; Reg.m=3; Reg.t=12; }
    RST58() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x58; Reg.m=3; Reg.t=12; }
    RST60() { Reg.sp-=2; MMU.ww(Reg.sp,Reg.pc); Reg.pc=0x60; Reg.m=3; Reg.t=12; }

    NOP() { Reg.m=1; Reg.t=4; }
    HALT() { Z80._halt=1; Reg.m=1; Reg.t=4; }

    DI() { Reg.ime=0; Reg.m=1; Reg.t=4; }
    EI() { Reg.ime=1; Reg.m=1; Reg.t=4; }

    
    
	
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
	public static void nop() {
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
	
	// Read a byte from absolute location into A ( LD A, addr)
	public static void LDamm() {
		int addr = MMU.rw(Reg.pc); //get address from instr
		Reg.pc += 2; // advance PC
		Reg.a = MMU.rb(addr); // read from address
		Reg.m = 4; Reg.t = 16; // 4 M-times taken;
	}
	
		
	// helper functions
	public static void fz(byte i, byte as) { 
		Reg.f = 0; // clear flags 
		if((i&255)!=0)  // if i masked to 8 bites isn't zero
			Reg.f|=128; // set flag register to 128
		Reg.f|=(as!=0)?0x40:0; 
	}
	
	
	///// RESET /////
	public static void reset() {
		Reg.a=0; Reg.b=0; Reg.c=0; Reg.d=0; Reg.e=0; Reg.h=0; Reg.l=0; Reg.f=0;
		Reg.sp=0; // clear stack pointer
		Reg.pc=0; // start execution at 0
		Clock.m=0; Clock.t=0; // reset clocks
	}
}

