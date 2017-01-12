package reservation_station;

import instruction.Instruction;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class JumpUnit extends ReservationStation{
	public JumpUnit() {
		super();
	}
	
	public JumpUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}

	@Override
	public void update() {
		if(getStatus() == Status.ISSUE)
			setStatus(Status.EXECUTE);
		else if(getStatus() == Status.EXECUTE){
			setCyclesTaken(getCyclesTaken() + 1);
			if(getCyclesTaken() == getLatency())
				setStatus(Status.WRITE);
		}else if(getStatus() == Status.WRITE){
				this.getRobEntry().setReady(true);
				setStatus(Status.COMMIT);
		}else if(getStatus() == Status.COMMIT){
			if(ROB.removeEntry(getRobEntry())){ // Its turn to commit?
				this.reset();
			}
		}
		
	}

}
