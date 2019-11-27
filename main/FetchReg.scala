package merl
import chisel3._
class FetchReg extends Module
{
	val io= IO(new Bundle{
  val fin1 = Input(UInt(32.W))
  val fin2 = Input(UInt(32.W))
  val fin3 = Input(UInt(32.W))
  val fin4 = Input(UInt(32.W))
  val fout1 = Output(UInt(32.W))
  val fout2 = Output(UInt(32.W))
  val fout3 = Output(UInt(32.W))
  val fout4 = Output(UInt(32.W))

		
})
    val Freg1=RegInit(0.U(32.W))
    val Freg2=RegInit(0.U(32.W))
    val Freg3=RegInit(0.U(32.W))
    val Freg4=RegInit(0.U(32.W))
    Freg1 := io.fin1
    io.fout1  := Freg1
    Freg2 := io.fin2
    io.fout2  := Freg2
    Freg3 := io.fin3
    io.fout3  := Freg3
    Freg4 := io.fin4
    io.fout4  := Freg4
		
}