// See LICENSE.txt for license details.
package merl
import chisel3._

class BranchForward extends Module {
    val io = IO(new Bundle {
    val Memread   = Input(UInt(1.W))
    val Cbranch   = Input(UInt(1.W))
    val EXMemrd     = Input(UInt(5.W))
    val IDRs1    = Input(UInt(5.W))
    val IDRs2    = Input(UInt(5.W))
    val EXerd     = Input(UInt(5.W))
    val MemWBrd     = Input(UInt(5.W))
    val Out1     = Output(UInt(4.W))
    val Out2     = Output(UInt(4.W))
  })


    io.Out1 := "b0000".U
    io.Out2 := "b0000".U
// forwaeding from ALU
   when((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)  && (io.IDRs2 === io.EXerd))
   {
       io.Out1 := "b0001".U
       io.Out2 := "b0001".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U))
   {
       io.Out1 := "b0001".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U))
   {
       io.Out2 := "b0001".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U))
   {
       io.Out1 := "b0110".U
   }

// forwarding from EX/MEM

   when((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U)   &&(io.IDRs2 === io.EXMemrd) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)  && (io.IDRs2 === io.EXerd)))
   {
     io.Out1 := "b0010".U
     io.Out2 := "b0010".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)))
   {
       io.Out1 := "b0010".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~ ((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U)))
   {
       io.Out2 := "b0010".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)))
   {
       io.Out1 := "b0111".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U)   && ((io.IDRs2 === io.EXMemrd) && ~(io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)   && (io.IDRs2 === io.EXerd)))
   {
     io.Out1 := "b0100".U
     io.Out2 := "b0100".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)))
   {
       io.Out1 := "b0100".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~ ((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U)))
   {
       io.Out2 := "b0100".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)))
   {
       io.Out1 := "b1000".U
   }


// forwarding from MEM/WB


   when((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.MemWBrd) && (io.MemWBrd =/= 0.U)   && (io.IDRs2 === io.MemWBrd) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)  && (io.IDRs2 === io.EXerd)) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U)   &&(io.IDRs2 === io.EXMemrd) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)  && (io.IDRs2 === io.EXerd))))
   {
       io.Out1 := "b0011".U
       io.Out2 := "b011".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.MemWBrd) && (io.MemWBrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U))))
   {
       io.Out1 := "b0011".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.MemWBrd) && (io.MemWBrd =/= 0.U)  && ~ ((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U)) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~ ((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U))))
   {
       io.Out2 := "b0011".U
   }
   .elsewhen((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.MemWBrd) && (io.MemWBrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U))))
   {
       io.Out1 := "b1001".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.MemWBrd) && (io.MemWBrd =/= 0.U)   && (io.IDRs2 === io.MemWBrd) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)  && (io.IDRs2 === io.EXerd)) && ~((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U)   && ((io.IDRs2 === io.EXMemrd) && ~(io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)   && (io.IDRs2 === io.EXerd))))
   {
       io.Out1 := "b0101".U
       io.Out2 := "b0101".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.MemWBrd) && (io.MemWBrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)) && ~((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U))))
   {
       io.Out1 := "b0101".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.MemWBrd) && (io.MemWBrd =/= 0.U)  && ~ ((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U)) && ~((io.Memread === 1.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~ ((io.Memread === 0.U) && (io.Cbranch === 1.U) && (io.IDRs2 === io.EXerd) && (io.EXerd =/= 0.U))))
   {
       io.Out2 := "b0101".U
   }
   .elsewhen((io.Memread === 1.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.MemWBrd) && (io.MemWBrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U)) && ~((io.Memread === 1.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXMemrd) && (io.EXMemrd =/= 0.U) && ~((io.Memread === 0.U) && (io.Cbranch === 0.U) && (io.IDRs1 === io.EXerd) && (io.EXerd =/= 0.U))))
   {
       io.Out1 := "b1010".U
   }



   
  
}




// .elsewhen((io.MBregW === 1.U) && (io.MBrd=/= 0.U) && (io.MBrd=== io.IDrs1)) 
//   {
//   io.out1 := "b01".U
//   }
//   .otherwise 
//   {
//      io.out1 := "b00".U
//   }