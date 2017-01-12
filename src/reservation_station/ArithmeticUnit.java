package reservation_station;

import bus.CDB;
import instruction.Instruction;
import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class ArithmeticUnit extends ReservationStation{

	private boolean written = false;
	
	public ArithmeticUnit() {
		super();
	}
	
	public ArithmeticUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}
	
	@Override
	public void update(){
		
		if(getStatus() == Status.ISSUE){
			if(getQj() == -1 && getQk() == -1)
				setStatus(Status.EXECUTE);
			System.out.println("issue");
		}
		else if(getStatus() == Status.EXECUTE){
			System.out.println("execute");
			setCyclesTaken(getCyclesTaken() + 1);
			if(getCyclesTaken() == getLatency()){
				System.out.println("finished executing");
					setResult(calculate());
					setStatus(Status.WRITE);
				}
		}else if(getStatus() == Status.WRITE){
			System.out.println("write");
				if(!(isWritten())){
					System.out.println("ready");
					this.getRobEntry().setReady(true);
					this.getRobEntry().setValue(getResult());
					setWritten(true);
				}
				if(CDB.broadcast(getResult(), getRobEntry())){
					System.out.println("written");
					setStatus(Status.COMMIT);
					setWritten(false);
				}
		}else if(getStatus() == Status.COMMIT){
			System.out.println("commit");
			if(ROB.removeEntry(getRobEntry())){ // Its turn to commit?
				System.out.println("committed");
				RegisterFile.setRegister(getDesRegister(), getResult());
				System.out.println("test neww" + getResult());
				System.out.println("test neww" + RegisterFile.getRegister(getDesRegister()));
				RegisterStatusBoard.clearEntry(getDesRegister());
				this.reset();
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
