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
		JRNZn, LDHLnn, LDHLIA, INCHL, INCr_h, DECr_h, LDrn_h, XX,
		JRZn, ADDHLHL, LDAHLI, DECHL, INCr_l, DECr_l, LDrn_l, CPL,

		// 30
		JRNCn, LDSPnn, LDHLDA, INCSP, INCHLm, DECHLm, LDHLmn, SCF,
		JRCn, ADDHLSP, LDAHLD, DECSP, INCr_a, DECr_a, LDrn_a, CCF,

		// 40
		LDrr_bb, LDrr_bc, LDrr_bd, LDrr_be, LDrr_bh, LDrr_bl, LDrHLm_b,	LDrr_ba,
		LDrr_cb, LDrr_cc, LDrr_cd, LDrr_ce, LDrr_ch, LDrr_cl, LDrHLm_c,	LDrr_ca,

		// 50
		LDrr_db,LDrr_dc,		LDrr_dd,		LDrr_de,		LDrr_dh,		LDrr_dl,		LDrHLm_d,		LDrr_da,
		LDrr_eb,LDrr_ec,		LDrr_ed,		LDrr_ee,		LDrr_eh,		LDrr_el,		LDrHLm_e,		LDrr_ea,

		// 60
		LDrr_hb,LDrr_hc,		LDrr_hd,		LDrr_he,		LDrr_hh,		LDrr_hl,		LDrHLm_h,		LDrr_ha,
		LDrr_lb,LDrr_lc,		LDrr_ld,	  	LDrr_le,	  	LDrr_lh,	  	LDrr_ll,	  	LDrHLm_l,	  	LDrr_la,

		// 70
		LDHLmr_b,LDHLmr_c,		LDHLmr_d,		LDHLmr_e,		LDHLmr_h,		LDHLmr_l,		HALT,		LDHLmr_a,
		LDrr_ab,LDrr_ac,		LDrr_ad,		LDrr_ae,		LDrr_ah,		LDrr_al,		LDrHLm_a,		LDrr_aa,

		// 80
		ADDr_b,	ADDr_c,		ADDr_d,		ADDr_e,		ADDr_h,		ADDr_l,		ADDHL,		ADDr_a,
		ADCr_b,	ADCr_c,		ADCr_d,		ADCr_e,		ADCr_h,		ADCr_l,		ADCHL,		ADCr_a,

		// 90
		SUBr_b,	SUBr_c,		SUBr_d,		SUBr_e,		SUBr_h,		SUBr_l,		SUBHL,		SUBr_a,
		SBCr_b,	SBCr_c,		SBCr_d,		SBCr_e,		SBCr_h,		SBCr_l,		SBCHL,		SBCr_a,

		// A0
		ANDr_b,	ANDr_c,		ANDr_d,		ANDr_e,		ANDr_h,		ANDr_l,		ANDHL,		ANDr_a,
		XORr_b,	XORr_c,		XORr_d,		XORr_e,		XORr_h,		XORr_l,		XORHL,		XORr_a,

		// B0
		ORr_b, ORr_c,		ORr_d,		ORr_e,		ORr_h,		ORr_l,		ORHL,		ORr_a,
		CPr_b, CPr_c,		CPr_d,		CPr_e,		CPr_h,		CPr_l,		CPHL,		CPr_a,

		// C0
		RETNZ, POPBC,		JPNZnn,		JPnn,		CALLNZnn,		PUSHBC,		ADDn,		RST00,
		RETZ, RET,		JPZnn,		MAPcb,		CALLZnn,		CALLnn,		ADCn,		RST08,

		// D0
		RETNC, POPDE,		JPNCnn,		XX,		CALLNCnn,		PUSHDE,		SUBn,		RST10,
		RETC, RETI,		JPCnn,		XX,		CALLCnn,		XX,		SBCn,		RST18,

		// E0
		LDIOnA,	POPHL,		LDIOCA,		XX,		XX,		PUSHHL,		ANDn,		RST20,
		ADDSPn,	JPHL,		LDmmA,		XX,		XX,		XX,		ORn,		RST28,

		// F0
		LDAIOn,	POPAF,		LDAIOC,		DI,		XX,		PUSHAF,		XORn,		RST30,
		LDHLSPn, XX,		LDAmm,		EI,		XX,		XX,		CPn,		RST38
	}

		public enum CBMap {
		  // CB00
		  OpCodes.RLCr_b,
		  OpCodes.RLCr_c,
		  OpCodes.RLCr_d,
		  OpCodes.RLCr_e,
		  OpCodes.RLCr_h,
		  OpCodes.RLCr_l,
		  OpCodes.RLCHL,
		  OpCodes.RLCr_a,
		  OpCodes.RRCr_b,
		  OpCodes.RRCr_c,
		  OpCodes.RRCr_d,
		  OpCodes.RRCr_e,
		  OpCodes.RRCr_h,
		  OpCodes.RRCr_l,
		  OpCodes.RRCHL,
		  OpCodes.RRCr_a,

		  // CB10
		  OpCodes.RLr_b,
		  OpCodes.RLr_c,
		  OpCodes.RLr_d,
		  OpCodes.RLr_e,
		  OpCodes.RLr_h,
		  OpCodes.RLr_l,
		  OpCodes.RLHL,
		  OpCodes.RLr_a,
		  OpCodes.RRr_b,
		  OpCodes.RRr_c,
		  OpCodes.RRr_d,
		  OpCodes.RRr_e,
		  OpCodes.RRr_h,
		  OpCodes.RRr_l,
		  OpCodes.RRHL,
		  OpCodes.RRr_a,

		  // CB20
		  OpCodes.SLAr_b,
		  OpCodes.SLAr_c,
		  OpCodes.SLAr_d,
		  OpCodes.SLAr_e,
		  OpCodes.SLAr_h,
		  OpCodes.SLAr_l,
		  OpCodes.XX,
		  OpCodes.SLAr_a,
		  OpCodes.SRAr_b,
		  OpCodes.SRAr_c,
		  OpCodes.SRAr_d,
		  OpCodes.SRAr_e,
		  OpCodes.SRAr_h,
		  OpCodes.SRAr_l,
		  OpCodes.XX,
		  OpCodes.SRAr_a,

		  // CB30
		  OpCodes.SWAPr_b,
		  OpCodes.SWAPr_c,
		  OpCodes.SWAPr_d,
		  OpCodes.SWAPr_e,
		  OpCodes.SWAPr_h,
		  OpCodes.SWAPr_l,
		  OpCodes.XX,
		  OpCodes.SWAPr_a,
		  OpCodes.SRLr_b,
		  OpCodes.SRLr_c,
		  OpCodes.SRLr_d,
		  OpCodes.SRLr_e,
		  OpCodes.SRLr_h,
		  OpCodes.SRLr_l,
		  OpCodes.XX,
		  OpCodes.SRLr_a,

		  // CB40
		  OpCodes.BIT0b,
		  OpCodes.BIT0c,
		  OpCodes.BIT0d,
		  OpCodes.BIT0e,
		  OpCodes.BIT0h,
		  OpCodes.BIT0l,
		  OpCodes.BIT0m,
		  OpCodes.BIT0a,
		  OpCodes.BIT1b,
		  OpCodes.BIT1c,
		  OpCodes.BIT1d,
		  OpCodes.BIT1e,
		  OpCodes.BIT1h,
		  OpCodes.BIT1l,
		  OpCodes.BIT1m,
		  OpCodes.BIT1a,

		  // CB50
		  OpCodes.BIT2b,
		  OpCodes.BIT2c,
		  OpCodes.BIT2d,
		  OpCodes.BIT2e,
		  OpCodes.BIT2h,
		  OpCodes.BIT2l,
		  OpCodes.BIT2m,
		  OpCodes.BIT2a,
		  OpCodes.BIT3b,
		  OpCodes.BIT3c,
		  OpCodes.BIT3d,
		  OpCodes.BIT3e,
		  OpCodes.BIT3h,
		  OpCodes.BIT3l,
		  OpCodes.BIT3m,
		  OpCodes.BIT3a,

		  // CB60
		  OpCodes.BIT4b,
		  OpCodes.BIT4c,
		  OpCodes.BIT4d,
		  OpCodes.BIT4e,
		  OpCodes.BIT4h,
		  OpCodes.BIT4l,
		  OpCodes.BIT4m,
		  OpCodes.BIT4a,
		  OpCodes.BIT5b,
		  OpCodes.BIT5c,
		  OpCodes.BIT5d,
		  OpCodes.BIT5e,
		  OpCodes.BIT5h,
		  OpCodes.BIT5l,
		  OpCodes.BIT5m,
		  OpCodes.BIT5a,

		  // CB70
		  OpCodes.BIT6b,
		  OpCodes.BIT6c,
		  OpCodes.BIT6d,
		  OpCodes.BIT6e,
		  OpCodes.BIT6h,
		  OpCodes.BIT6l,
		  OpCodes.BIT6m,
		  OpCodes.BIT6a,
		  OpCodes.BIT7b,
		  OpCodes.BIT7c,
		  OpCodes.BIT7d,
		  OpCodes.BIT7e,
		  OpCodes.BIT7h,
		  OpCodes.BIT7l,
		  OpCodes.BIT7m,
		  OpCodes.BIT7a,

		  // CB80
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CB90
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CBA0
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CBB0
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CBC0
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CBD0
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CBE0
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,

		  // CBF0
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX,
		  OpCodes.XX
	}
	
	////////////   OPCODES AS FUNCTIONS //////////////
    public static void LDrr_bb() { Reg.b=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_bc() { Reg.b=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_bd() { Reg.b=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_be() { Reg.b=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_bh() { Reg.b=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_bl() { Reg.b=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ba() { Reg.b=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_cb() { Reg.c=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_cc() { Reg.c=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_cd() { Reg.c=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_ce() { Reg.c=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_ch() { Reg.c=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_cl() { Reg.c=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ca() { Reg.c=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_db() { Reg.d=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_dc() { Reg.d=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_dd() { Reg.d=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_de() { Reg.d=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_dh() { Reg.d=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_dl() { Reg.d=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_da() { Reg.d=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_eb() { Reg.e=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_ec() { Reg.e=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_ed() { Reg.e=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_ee() { Reg.e=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_eh() { Reg.e=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_el() { Reg.e=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ea() { Reg.e=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_hb() { Reg.h=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_hc() { Reg.h=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_hd() { Reg.h=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_he() { Reg.h=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_hh() { Reg.h=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_hl() { Reg.h=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_ha() { Reg.h=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_lb() { Reg.l=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_lc() { Reg.l=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_ld() { Reg.l=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_le() { Reg.l=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_lh() { Reg.l=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_ll() { Reg.l=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_la() { Reg.l=Reg.a; Reg.m=1; Reg.t=4; }
    public static void LDrr_ab() { Reg.a=Reg.b; Reg.m=1; Reg.t=4; }
    public static void LDrr_ac() { Reg.a=Reg.c; Reg.m=1; Reg.t=4; }
    public static void LDrr_ad() { Reg.a=Reg.d; Reg.m=1; Reg.t=4; }
    public static void LDrr_ae() { Reg.a=Reg.e; Reg.m=1; Reg.t=4; }
    public static void LDrr_ah() { Reg.a=Reg.h; Reg.m=1; Reg.t=4; }
    public static void LDrr_al() { Reg.a=Reg.l; Reg.m=1; Reg.t=4; }
    public static void LDrr_aa() { Reg.a=Reg.a; Reg.m=1; Reg.t=4; }
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
    public static void LDmmHL() { int i=MMU.rw(Reg.pc); Reg.pc+=2; MMU.ww(i,(Reg.h<<8)+Reg.l); Reg.m=5; Reg.t=20; }

    public static void LDHLIA() { MMU.wb((Reg.h<<8)+Reg.l, Reg.a); Reg.l=(Reg.l+1)&255; if(!Reg.l) Reg.h=(Reg.h+1)&255; Reg.m=2; Reg.t=8; }
    public static void LDAHLI() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.l=(Reg.l+1)&255; if(!Reg.l) Reg.h=(Reg.h+1)&255; Reg.m=2; Reg.t=8; }

    public static void LDHLDA() { MMU.wb((Reg.h<<8)+Reg.l, Reg.a); Reg.l=(Reg.l-1)&255; if(Reg.l==255) Reg.h=(Reg.h-1)&255; Reg.m=2; Reg.t=8; }
    public static void LDAHLD() { Reg.a=MMU.rb((Reg.h<<8)+Reg.l); Reg.l=(Reg.l-1)&255; if(Reg.l==255) Reg.h=(Reg.h-1)&255; Reg.m=2; Reg.t=8; }
    
    public static void LDAIOn() { Reg.a=MMU.rb(0xFF00+MMU.rb(Reg.pc)); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDIOnA() { MMU.wb(0xFF00+MMU.rb(Reg.pc),Reg.a); Reg.pc++; Reg.m=3; Reg.t=12; }
    public static void LDAIOC() { Reg.a=MMU.rb(0xFF00+Reg.c); Reg.m=2; Reg.t=8; }
    public static void LDIOCA() { MMU.wb(0xFF00+Reg.c,Reg.a); Reg.m=2; Reg.t=8; }

    public static void LDHLSPn() { int i=MMU.rb(Reg.pc); if(i>127) i=-((~i+1)&255); Reg.pc++; i+=Reg.sp; Reg.h=(i>>8)&255; Reg.l=i&255; Reg.m=3; Reg.t=12; }
	
	
	
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
	
	// Read a byte from absolute location into A (public static void LD A, addr)
	public static void public static void LDamm() {
		int addr = MMU.rw(Reg.pc); //get address from instr
		Reg.pc += 2; // advance PC
		Reg.a = MMU.rb(addr); // read from address
		Reg.m = 4; Reg.t = 16; // 4 M-times taken;
	}
	
	
	
	
	
	///// RESET /////
	public static void reset() {
		Reg.a=0; Reg.b=0; Reg.c=0; Reg.d=0; Reg.e=0; Reg.h=0; Reg.l=0; Reg.f=0;
		Reg.sp=0; // clear stack pointer
		Reg.pc=0; // start execution at 0
		Clock.m=0; Clock.t=0; // reset clocks
	}
}

