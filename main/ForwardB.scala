package merl
import chisel3._
import chisel3.Bool

class ForwardB extends Module {
    val io = IO(new Bundle {
    val EMregW   = Input(UInt(1.W))
    val EMrd     = Input(UInt(5.W))
    val IDrs1    = Input(UInt(5.W))
    val IDrs2    = Input(UInt(5.W))
    val MBregW   = Input(UInt(1.W))
    val WBrd     = Input(UInt(5.W))
    //val Out1     = Output(UInt(2.W))
    val Out2     = Output(UInt(2.W))
  })
   when((io.EMregW === 1.U) && (io.EMrd =/= 0.U) && (io.EMrd === io.IDrs2))  
   {
   io.Out2 := "b10".U
   }
   .otherwise
   {
       io.Out2:= "b00".U
  }
   when((io.MBregW === 1.U) && (io.WBrd=/= 0.U) && (~(io.EMregW === 1.U) && (io.EMrd =/= 0.U) && (io.EMrd === io.IDrs2))&& (io.WBrd=== io.IDrs2)) 
   {
   io.Out2 := "b01".U
   }
  .otherwise
   {
       io.Out2 := "b00".U
   }
  }