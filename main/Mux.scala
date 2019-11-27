package merl
import chisel3._
import chisel3.util._
class Mux extends Module
{
	val io=IO(new Bundle{
		val a=Input(UInt(32.W))
		val b= Input(UInt(32.W))
		val sel=Input(UInt(1.W))
		val out=Output(UInt(32.W))
})
	when(io.sel === 0.U)
	{
		io.out := io.a
	}
	.otherwise
	{
		io.out := io.b
	}
	
}
