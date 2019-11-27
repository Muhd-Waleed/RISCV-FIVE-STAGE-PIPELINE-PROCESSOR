package merl
import chisel3._
class Branch extends Module
{
	val io= IO(new Bundle{
		val in1      = Input(UInt(32.W))
		val in2      = Input(UInt(32.W))
        val func3    = Input(UInt(3.W))
		val out      = Output(UInt(1.W))
})      
	when ( io.func3 === "b000".U)
    {

    io.out :=  (io.in1 === io.in2)

    }
    .elsewhen (io.func3  === "b001".U)
    {

    io.out := (io.in1 =/= io.in2)
    
    }
    .elsewhen (io.func3 === "b100".U)
    {
        io.out := (io.in1 < io.in2)
    }
    .elsewhen (io.func3 === "b101".U)
    {
        io.out := (io.in1 > io.in2)

    }
    .elsewhen ( io.func3 === "b110".U)
    {
        io.out := (io.in1 < io.in2)
    }
    .otherwise
    {
        io.out := (io.in1 > io.in2)
    }

}
