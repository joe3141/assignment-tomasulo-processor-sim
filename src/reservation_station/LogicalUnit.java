package reservation_station;

import instruction.Instruction;
import reorder_buffer.ROBEntry;

public class LogicalUnit extends ArithmeticUnit{
	
	public LogicalUnit(){
		super();
	}
	
	public LogicalUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}
}
