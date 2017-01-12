package reservation_station;

import bus.CDB;
import instruction.Instruction;
import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class CallUnit extends ReservationStation{
	
	private boolean written;
	
	public CallUnit() {
		super();
	}
	
	public CallUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}

	@Override
	public void update() {
		
		if(getStatus() == Status.ISSUE)
			if(getQj() == -1)
				setStatus(Status.EXECUTE);
		else if(getStatus() == Status.EXECUTE){
			setCyclesTaken(getCyclesTaken() + 1);
			if(getCyclesTaken() == getLatency()){
					setResult(calculate());
					setStatus(Status.WRITE);
				}
		}else if(getStatus() == Status.WRITE){
				if(!(isWritten())){
					this.getRobEntry().setReady(true);
					this.getRobEntry().setValue(getResult());
					setWritten(true);
				}
				if(CDB.broadcast(getResult(), getRobEntry())){
					setStatus(Status.COMMIT);
					setWritten(false);
				}
		}else if(getStatus() == Status.COMMIT){
			if(ROB.removeEntry(getRobEntry())){ // Its turn to commit?
				RegisterFile.setRegister(getDesRegister(), getResult());
				RegisterStatusBoard.clearEntry(getDesRegister());
				this.reset();
				System.out.println("call finished");
			}
		}
	}
	
	public boolean isWritten() {
		return written;
	}

	public void setWritten(boolean written) {
		this.written = written;
	}
	
	
}
