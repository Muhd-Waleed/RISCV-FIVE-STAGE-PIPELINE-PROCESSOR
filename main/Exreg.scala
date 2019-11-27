package merl
import chisel3._
class Exreg extends Module
{
	val io= IO(new Bundle{
    val ein1   = Input(UInt(1.W))
    val ein2   = Input(UInt(1.W))
    val ein3   = Input(UInt(1.W))
    val ein4   = Input(UInt(1.W))
    val ein5   = Input(UInt(32.W))
    val ein6   = Input(UInt(32.W))
    val ein7   = Input(UInt(5.W))
    val ein8   = Input(UInt(5.W))
    val eout1  = Output(UInt(1.W))
    val eout2  = Output(UInt(1.W))
    val eout3  = Output(UInt(1.W))
    val eout4  = Output(UInt(1.W))
    val eout5  = Output(UInt(32.W))
    val eout6  = Output(UInt(32.W))
    val eout7  = Output(UInt(5.W))
    val eout8  = Output(UInt(5.W))
 
    
		
})
    val Exreg1 = RegInit(0.U(1.W))
    val Exreg2 = RegInit(0.U(1.W))
    val Exreg3 = RegInit(0.U(1.W))
    val Exreg4 = RegInit(0.U(1.W))
    val Exreg5 = RegInit(0.U(32.W))
    val Exreg6 = RegInit(0.U(32.W))
    val Exreg7 = RegInit(0.U(5.W))
   val Exreg8 = RegInit(0.U(5.W))
    Exreg1:=io.ein1
    io.eout1 := Exreg1
    Exreg2:=io.ein2
    io.eout2 := Exreg2
    Exreg3 :=io.ein3
    io.eout3 := Exreg3
    Exreg4 := io.ein4
    io.eout4 := Exreg4
    Exreg5 := io.ein5
    io.eout5 := Exreg5
    Exreg6 := io.ein6
    io.eout6 := Exreg6
    Exreg7 := io.ein7
    io.eout7 := Exreg7
    Exreg8 := io.ein8
    io.eout8 := Exreg8
    

}