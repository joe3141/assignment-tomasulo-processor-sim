package reservation_station;

import instruction.Instruction;
import reorder_buffer.ROBEntry;

public class MultUnit extends ArithmeticUnit{
	public MultUnit(){
		super();
	}
	
	public MultUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}
}
