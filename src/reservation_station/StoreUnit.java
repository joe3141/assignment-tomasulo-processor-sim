package reservation_station;

import cache.CacheFile;
import bus.CDB;
import instruction.Instruction;
import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class StoreUnit extends ReservationStation{
	
	private boolean calculated = false;
	private boolean waited = false;
	
	public StoreUnit(){
		super();
	}
	
	public StoreUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}
	
	@Override
	public void update() {
		
		if(getStatus() == Status.ISSUE){
			System.out.println("issue");
			if(getQj() == -1 && getQk() == -1)
				if(!(isCalculated())){
					setAddress(calculate());
					setCalculated(true);
				}else{
					setStatus(Status.EXECUTE);
					setCalculated(false);
				}
		}else if(getStatus() == Status.EXECUTE){
			System.out.println("execute");
			setCyclesTaken(getCyclesTaken() + 1);
			if(getCyclesTaken() == getLatency()){
				System.out.println("fininshed excute");
				int wait = CacheFile.writeData(getAddress(), getVj());
				if(wait > getLatency() && !waited){
					System.out.println("waiting");
					setCyclesTaken(wait - getLatency());
					waited = true;
				}else{
					System.out.println("finished executing");
					setStatus(Status.WRITE);
					waited = false;
				}
			}
		}else if(getStatus() == Status.WRITE){
			System.out.println("write");
				getRobEntry().setReady(true);
				setStatus(Status.COMMIT);
		}else if(getStatus() == Status.COMMIT){
			System.out.println("committing");
			if(ROB.removeEntry(getRobEntry())){ // Its turn to commit?
				System.out.println("committed");
				RegisterStatusBoard.clearEntry(getDesRegister());
				this.reset();
			}
		}
	}

	public boolean isCalculated() {
		return calculated;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}
	
}
