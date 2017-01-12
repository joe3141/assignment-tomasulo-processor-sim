package reservation_station;

import cache.CacheFile;
import bus.CDB;
import instruction.Instruction;
import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class LoadUnit extends ReservationStation{
private boolean calculated = false;
private boolean written = false;
private boolean waited = false;
	
	public LoadUnit(){
		super();
	}
	
	public LoadUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}

	@Override
	public void update() {
		
		if(getStatus() == Status.ISSUE){
			System.out.println("issue");
			if(getQj() == -1 )
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
				System.out.println("finished execute");
				Short data = (Short) CacheFile.fetchData(getAddress()).getData();
				if(CacheFile.fetchData(getAddress()).getCycles() > getLatency() && !waited){
					setCyclesTaken(CacheFile.fetchData(getAddress()).getCycles() - getLatency());
					waited = true;
				}else{
					setResult(data == null ? 0 : data);
					setStatus(Status.WRITE);
					waited = false;
				}
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
			System.out.println("committing");
			if(ROB.removeEntry(getRobEntry())){ // Its turn to commit?
				System.out.println("committed");
				RegisterFile.setRegister(getDesRegister(), getResult());
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

	public boolean isWritten() {
		return written;
	}

	public void setWritten(boolean written) {
		this.written = written;
	}
}
