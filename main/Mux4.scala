package merl

import chisel3._
class Mux4 extends Module
{
	val io=IO(new Bundle{
		val in1=Input(UInt(32.W))
		val in2=Input(UInt(32.W))
		val in3=Input(UInt(32.W))
		val in4=Input(UInt(32.W))
		val sel=Input(UInt(2.W))
		val out=Output(UInt(32.W))
})
	when(io.sel ==="b00".U)
	{
		io.out := io.in1
	}
	.elsewhen(io.sel ==="b01".U)
	{
		io.out := io.in2
	}
	.elsewhen(io.sel ==="b10".U)
	{
		io.out := io.in3
	}
	.otherwise
	{
		io.out := io.in4
	}
} 



