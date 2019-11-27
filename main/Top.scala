package merl
import chisel3._
import chisel3.util.Cat
class Top extends Module
{
    val io = IO(new Bundle{
        val clock = Input(Clock())
        val out = Output(UInt(32.W))
    })
    val Deco               = Module(new Decode())
    val Instruction_Memory = Module(new Fetch())
    val Data_mem           = Module(new dMemo())
    val Register_file      = Module(new RegFile())
    val Execute            = Module(new Alu())
    val Program_counter    = Module(new Pc1())
    val jump               = Module(new JALR())
    val AluC               = Module(new AluControll())
    val stall              = Module(new Stall())
    val mux                = Module(new Mux())
    val mux4               = Module(new Mux4())
    val branch             = Module(new Branch())
    val branch_forwarding  = Module(new BranchForward())
    val Register_file_hazard = Module(new StructuralHazard())

    Register_file.io.clock <> io.clock
    Program_counter.io.clock <> io.clock
    //                         ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //*************************INTRODUCING PIPELINE REGISTERS*****************************
    //                         ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    val Freg   = Module(new FetchReg())
    val Decreg = Module(new DecodeReg())
    val Exreg  = Module(new Exreg())
    val Wbreg  = Module(new WBreg())

    //************************* INTRODUCING FORWARDING UNIT ********************************
    val fwd = Module(new Forward())
    val fwdM = Module(new ForwardM())
    //************** WIRING FOR PC AND INSTRUCTION MEMORY ***********
    //*********** OR WIRING BETWEEN PC AND INSTRUCTION MEMORY ***********

    Instruction_Memory.io.wrAddr := Program_counter.io.p(11,2)

    //      ****************************************** WIRING FOR INSTRUCTION AND DECODE *******************************************
    //       ********************************** OR WIRING BETWEEN INSTRUCTION MEMORY AND DECODE ********************************************************
    when((Deco.io.Branch & branch.io.out) === 0.U)
    {
    when(stall.io.out3 === 0.U)
    {
        Freg.io.fin1        := Program_counter.io.out
        Freg.io.fin2        := Program_counter.io.p
        Freg.io.fin3        := Instruction_Memory.io.rdData
        Freg.io.fin4        := Program_counter.io.p
    }
    .otherwise
    {
        Freg.io.fin1        := Freg.io.fout1
        Freg.io.fin2        := Freg.io.fout2
        Freg.io.fin3        := Freg.io.fout3
        Freg.io.fin4        := Freg.io.fout4
    }
    }
    .otherwise
    {
     
        Freg.io.fin1        := 0.U
        Freg.io.fin2        := 0.U
        Freg.io.fin3        := 0.U
        Freg.io.fin4        := 0.U
    }
   
      Deco.io.pc          :=  Freg.io.fout4
      Deco.io.instruction :=  Freg.io.fout3
    
    // *****************  WIRING FOR REISTER FILE AND DECODE **************
    // ************* OR WIRING BETWEEN REGISTER FILE AND DECODE ***************

  //  Register_file.io.write_sel := Deco.io.WriteReg
    Register_file.io.read_sel1 := Deco.io.Readreg_1 //OUTPUT FROM DECODE READ REG 1 WIRED TO REGISTER FILE READ SEL1
    Register_file.io.read_sel2 := Deco.io.Readreg_2 //OUTPUT FROM DECODE READ REG 2 WIRED TO REGISTER FILE READ SEL2
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //REGISTER FILE READ_SEL1 & READ_SEL2 WIRED WITH FORWARDING UNIT IDRS1 AND IDRS2
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    Decreg.io.din16 := Register_file.io.read_sel1
    fwd.io.IDrs1    := Decreg.io.dout16
    Decreg.io.din17 := Register_file.io.read_sel2
    fwd.io.IDrs2    := Decreg.io.dout17

    fwd.io.MBregW   :=  Wbreg.io.wout1
    fwd.io.MBrd     :=  Wbreg.io.wout5
    
    fwdM.io.EMrs2    := Exreg.io.eout8
    fwdM.io.EMwrite  := Exreg.io.eout1
    fwdM.io.MBread   := Wbreg.io.wout6
    fwdM.io.MBrd     := Wbreg.io.wout5

    stall.io.IDrs1   := Deco.io.Readreg_1
    stall.io.IDrs2   := Deco.io.Readreg_2

     //********************* JUMP MODULE *******************************
    //*********************    OR JALR   *********************************

    jump.io.a := Register_file.io.read_data1  //OUTPUT FROM THE REGISTER FILE
    jump.io.b := Deco.io.SUI // COMBINE OUTPUT FROM S-TYPE, U-TYPE AND I-TYPE (MUX)

    //********************** WIRING FOR ALU CONTROL AND DECODE *************************
    //******************* OR WIRING BETWEEN ALU CONTROL AND DECODE ***********************
     
     
    


   //  Register_file.io.wEn := Decreg.io.dout3 //REG WRITE OUTPUT FROM CONTROL WIRED TO REGISTER FILE WRITE ENABLE THROUGH PIPELINE REGISTER
     when(stall.io.out2 === 0.U)
     {
        Decreg.io.din1       := Deco.io.Memwrite
        Decreg.io.din2       := Deco.io.Memread
        Decreg.io.din3       := Deco.io.Regwrite
        Decreg.io.din4       := Deco.io.MemtoReg
        Decreg.io.din5       := Deco.io.AluOp
        Decreg.io.din6       := Deco.io.OpA_sel
        Decreg.io.din7       := Deco.io.OpB_sel
     }
     .otherwise
     {
        Decreg.io.din1       := 0.U
        Decreg.io.din2       := 0.U
        Decreg.io.din3       := 0.U
        Decreg.io.din4       := 0.U
        Decreg.io.din5       := 0.U
        Decreg.io.din6       := 0.U
        Decreg.io.din7       := 0.U
     }
     AluC.io.Aluop        := Decreg.io.dout5
     Decreg.io.din8       := Freg.io.fout2
     Decreg.io.din9       := Freg.io.fout1
     Decreg.io.din10      := Register_file.io.read_data1
     Decreg.io.din11      := Register_file.io.read_data2
     Decreg.io.din12      := Deco.io.SUI
     Decreg.io.din13      := Deco.io.WriteReg
     Decreg.io.din14      := Freg.io.fout3(14,12)//Deco.io.instruction(14,12)//OUTPUT SLICED IN 12-14 A BITS REPRESENT func3 THROUGH PIPELINE REGISTER
     AluC.io.func3        := Decreg.io.dout14
     Decreg.io.din15      := Freg.io.fout3(31,25)//Deco.io.instruction(31,25)//OUTPUT SLICED IN 25-30 A BITS REPRESENT func7 THROUGH PIPELINE REGISTER
     AluC.io.func7        := Decreg.io.dout15
     Execute.io.func      := AluC.io.out

    //4x1 MUX Is For Output From ControlDecode That Is OpA_sel
    //2x1 MUX Is For Output From ControlDecode That Is OpB_sel

    Exreg.io.ein1        := Decreg.io.dout1
    Exreg.io.ein2        := Decreg.io.dout2
    Exreg.io.ein3        := Decreg.io.dout3
    Exreg.io.ein4        := Decreg.io.dout4
    Exreg.io.ein5        := Execute.io.out
    Exreg.io.ein6        := Decreg.io.dout9
    Exreg.io.ein7        := Decreg.io.dout13
    Exreg.io.ein8        := Decreg.io.dout17
    fwd.io.EMregW        := Exreg.io.eout3
    fwd.io.EMrd          := Exreg.io.eout7
    Exreg.io.ein1        := Decreg.io.dout1 //MEMORY WRITE OUTPUT FROM CONTROL WIRED TO DATA MEMORY WRITE ENABLE THROUGH PIPELINE REGISTER
    Data_mem.io.wen      := Exreg.io.eout1 //Data Mem Write Enable connected with the output of execute register
    Exreg.io.ein2        := Decreg.io.dout2 //MEMORY READ OUTPUT FROM CONTROL WIRED TO DATA MEMORY READ ENABLE THROUGH PIPELINE REGISTER
    Data_mem.io.ren      := Exreg.io.eout2 //Data Mem Read  Enable connected with the output of execute register
    stall.io.EXwrite     := Decreg.io.dout1 //changes
    stall.io.EMread      := Decreg.io.dout2
    stall.io.EMrd        := Decreg.io.dout13

    when(Decreg.io.dout6 === "b00".U)
    {
        when(fwd.io.out1 === "b01".U)
        {
            Execute.io.in1 := Register_file.io.write_data // forward data from Write Back STAGE
        }
        .elsewhen(fwd.io.out1 === "b10".U)
        {
           Execute.io.in1  := Exreg.io.eout5 // forward data from Write Back STAGE
        }
        .otherwise
        {
            Execute.io.in1 := Decreg.io.dout10 // Register_file.io.read_data1
        }
    }
    .elsewhen(Decreg.io.dout6 === "b01".U)
    {
        Execute.io.in1      := Decreg.io.dout9
    }
    .elsewhen(Decreg.io.dout6 === "b10".U)
    {
        Execute.io.in1      := Decreg.io.dout8
    }
    .otherwise
    {
       Execute.io.in1      := Decreg.io.dout10
    }
    mux4.io.in1        := Decreg.io.dout11
    mux4.io.in2        := Register_file.io.write_data
    mux4.io.in3        := Exreg.io.eout5
    mux4.io.in4        := Decreg.io.dout11
    mux4.io.sel        := fwd.io.out2
    mux.io.a           := mux4.io.out
    Exreg.io.ein6      := mux4.io.out
    mux.io.b           := Decreg.io.dout12
    mux.io.sel         := Decreg.io.dout7 
    Execute.io.in2     := mux.io.out 
    
     //    ****************************** WIRING FOR DATA MEMORY, ALU AND DECODE ***********************************
     // ****************************** OR WIRING BETWEEN DATA MEMORY, ALU AND DECODE ****************************************

    Wbreg.io.win1 := Exreg.io.eout3
    Wbreg.io.win2 := Exreg.io.eout4
    Wbreg.io.win3 := Data_mem.io.rdData
    Wbreg.io.win4 := Exreg.io.eout5
    Wbreg.io.win5 := Exreg.io.eout7
    Wbreg.io.win6 := Exreg.io.eout2
    

    Register_file.io.write_sel := Wbreg.io.wout5
    Register_file.io.wEn := Wbreg.io.wout1

    Data_mem.io.wrAddr          := Exreg.io.eout5(9,0)
 //   Data_mem.io.wrData          := Exreg.io.eout6 
    
    when(Wbreg.io.wout2 === "b1".U)
    {
        Register_file.io.write_data := Wbreg.io.wout3
    }
    .otherwise
    {
       Register_file.io.write_data := Wbreg.io.wout4
    }
   
     // ************************** CONDITIONAL LOGIC ***********************************
    when (Deco.io.NextPc_sel === "b00".U)
    {
            when(stall.io.out1 === 0.U)
            {
                Program_counter.io.in := Program_counter.io.out
            }
            .otherwise
            {
                 Program_counter.io.in := Program_counter.io.p
            }

    }
    .elsewhen(Deco.io.NextPc_sel === "b01".U )
    {
        when((Deco.io.Branch & branch.io.out) === "b1".U)
        {
            Program_counter.io.in := Deco.io.Sb_type
        }
        .otherwise
        {
            when(stall.io.out1 === 0.U)
            {
                 Program_counter.io.in := Program_counter.io.out
            }
            .otherwise
            {
                 Program_counter.io.in := Program_counter.io.p
            }
        }
    }
    .otherwise
    {
        Program_counter.io.in     := Deco.io.Uj_type
    }

    //for forwardM
    when(fwdM.io.out === "b0".U)
    {
        Data_mem.io.wrData := Exreg.io.eout6
    }
    .otherwise{
        Data_mem.io.wrData := Wbreg.io.wout3
    }
 // ****************************************** INTRODUCING BRANCHES **************************************
  //  branch.io.in1  := Register_file.io.read_data1
   // branch.io.in2  := Register_file.io.read_data2
    branch.io.func3 :=Deco.io.instruction(14,12)
    
//  ******************************************* CONNECTION FOR FORWARDING UNIT OF BRANCH **************************************
    branch_forwarding.io.Cbranch := Deco.io.Branch
    branch_forwarding.io.EXMemrd := Decreg.io.dout13
    branch_forwarding.io.IDRs1   := Deco.io.Readreg_1
    branch_forwarding.io.IDRs2   := Deco.io.Readreg_2
    branch_forwarding.io.EXerd   := Exreg.io.eout7
    branch_forwarding.io.MemWBrd := Wbreg.io.wout5
    branch_forwarding.io.Memread:= Exreg.io.eout2
//  ******************************************* BRANCH FIRST OUTPUT FORWARDING *****************************************************
 
    when(branch_forwarding.io.Out1 === "b01".U)
    {
        branch.io.in1     := Execute.io.out
    }
    .elsewhen(branch_forwarding.io.Out1 === "b10".U)
    {
        branch.io.in1     := Exreg.io.eout5
    }
    .elsewhen(branch_forwarding.io.Out1 === "b11".U)
    {
        branch.io.in1     := Register_file.io.write_data
    }
    .otherwise
    {
        branch.io.in1     := Register_file.io.read_data1
    }


//  ****************************************** BRANCH SECOND OUTPUT FORWARDING ****************************************************
      
        // for 2nd input which is in2

      when(branch_forwarding.io.Out2 === "b00".U)
    {
        branch.io.in2     := Execute.io.out
    }
    .elsewhen(branch_forwarding.io.Out2 === "b10".U)
    {
        branch.io.in2     := Exreg.io.eout5
    }
    .elsewhen(branch_forwarding.io.Out2 === "b10".U)
    {
        branch.io.in2     := Register_file.io.write_data
    }
    .otherwise
    {
        branch.io.in2    := Register_file.io.read_data2
    }
// **************************************** MAKING FETCH REGISTER ZERO ****************************************************

    when((Deco.io.Branch & branch.io.out) === 1.U)
    {
        Freg.io.fin1 := 0.U
        Freg.io.fin1 := 0.U
        Freg.io.fin3 := 0.U
        Freg.io.fin4 := 0.U
    }
    .otherwise
    {
        Freg.io.fin1        := Program_counter.io.out
        Freg.io.fin2        := Program_counter.io.p
        Freg.io.fin3        := Instruction_Memory.io.rdData
        Freg.io.fin4        := Program_counter.io.p
    }
// ***************************************JALR BRANCHING *******************************************************************
    
    when(branch_forwarding.io.Out1 === "b0110".U)
    {
        jump.io.a := Execute.io.out
    }
    .elsewhen(branch_forwarding.io.Out1 === "b0111".U)
    {
        jump.io.a := Exreg.io.eout5
    }
    .elsewhen(branch_forwarding.io.Out1 === "b1000".U || branch_forwarding.io.Out1 === "b1010".U)
    {
        jump.io.a := Data_mem.io.rdData
    }
    .elsewhen(branch_forwarding.io.Out1 === "b1001".U)
    {
        jump.io.a := Register_file.io.write_data
    }
    .otherwise
    {
        jump.io.a := Register_file.io.read_data1
    }

// ************************************* SOLVING REGISTER FILE HAZARD *******************************************************
    Register_file_hazard.io.RegWrite := Deco.io.Regwrite
    Register_file_hazard.io.WBrd     := Wbreg.io.wout5
    Register_file_hazard.io.IDrs1    := Deco.io.Readreg_1
    Register_file_hazard.io.IDrs2    := Deco.io.Readreg_2

    // forwarding logic for structural hazards 
        when(Register_file_hazard.io.out1 === 1.U)
        {
            Decreg.io.din10 := Register_file.io.write_data
        }
        .otherwise
        {
            Decreg.io.din10:= Register_file.io.read_data1
        }

        when(Register_file_hazard.io.out2 === 1.U)
        {
            Decreg.io.din11 := Register_file.io.write_data
        }
        .otherwise
        {
            Decreg.io.din11:= Register_file.io.read_data2
        }

    io.out := Execute.io.out

}