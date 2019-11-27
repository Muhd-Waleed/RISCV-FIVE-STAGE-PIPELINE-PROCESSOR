package merl
import chisel3._
class WBreg extends Module
{
	val io= IO(new Bundle{

    val win1   = Input(UInt(1.W))
    val win2   = Input(UInt(1.W))
    val win3   = Input(UInt(32.W))
    val win4   = Input(UInt(32.W))
    val win5   = Input(UInt(5.W))
    val win6   = Input(UInt(1.W))
    val wout1  = Output(UInt(1.W))
    val wout2  = Output(UInt(1.W))
    val wout3  = Output(UInt(32.W))
    val wout4  = Output(UInt(32.W))
    val wout5  = Output(UInt(5.W))
    val wout6  = Output(UInt(1.W))
    
})
val Wbreg1 = RegInit(0.U(1.W))
val Wbreg2 = RegInit(0.U(1.W))
val Wbreg3 = RegInit(0.U(32.W))
val Wbreg4 = RegInit(0.U(32.W))
val Wbreg5 = RegInit(0.U(5.W))
val Wbreg6 = RegInit(0.U(1.W))

    Wbreg1:=io.win1
    io.wout1 := Wbreg1
    Wbreg2:=io.win2
    io.wout2 := Wbreg2
    Wbreg3 :=io.win3
    io.wout3 := Wbreg3
    Wbreg4 := io.win4
    io.wout4 := Wbreg4
    Wbreg5 := io.win5
    io.wout5 := Wbreg5
    Wbreg6 := io.win6
    io.wout6 := Wbreg6
}