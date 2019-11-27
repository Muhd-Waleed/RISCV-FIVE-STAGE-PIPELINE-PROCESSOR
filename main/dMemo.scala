// See LICENSE.txt for license details.
package merl

import chisel3._

// Problem:
//
// Implement a dual port memory of 256 8-bit words.
// When 'wen' is asserted, write 'wrData' to memory at 'wrAddr'
// When 'ren' is asserted, 'rdData' holds the output
// of reading the memory at 'rdAddr'
//
class dMemo extends Module {
  val io = IO(new Bundle {
    val wen     = Input(UInt(1.W))
    val wrAddr  = Input(UInt(32.W))
    val wrData  = Input(UInt(32.W))
    val ren     = Input(UInt(1.W))
    val rdData  = Output(UInt(32.W))
  })

  val mem =SyncReadMem(1024, UInt(32.W))
    
 
  // write
  when (io.wen === 1.U && io.ren === 0.U) 
	{ 
    mem.write(io.wrAddr,io.wrData)
    io.rdData := 0.U 
  }
  .elsewhen(io.wen === 0.U && io.ren === 1.U) 
	{
    // read
    io.rdData := mem.read(io.wrAddr)
  }
  .otherwise
  {
    io.rdData := 0.U
  }

}
