package reservation_station;

import processor.Processor;
import bus.CDB;
import instruction.Instruction;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class BranchUnit extends ReservationStation{
	
	private short realAddress;
	private boolean prediction;
	private boolean correctPrediction;
	
	public BranchUnit() {
		super();
	}
	
	public BranchUnit(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		super(operation, instruction, vj, vk, qj, qk, robEntry, address, desRegister, latency);
	}

	@Override
	public void update() {
		
		if(getStatus() == Status.ISSUE){
			System.out.println("issue");
			if(getQj() == -1 && getQk() == -1)
				setStatus(Status.EXECUTE);
		}else if(getStatus() == Status.EXECUTE){
			System.out.println("execute");
			setCyclesTaken(getCyclesTaken() + 1);
			if(getCyclesTaken() == getLatency()){
				System.out.println("finished executing");
					if(getVj() == getVk()){
						realAddress = (short) (getInstruction().getAddress() + 1 + 
								getAddress());
//						prediction = getInstruction().getImm() < 0;
						prediction = Processor.getPrediction(getInstruction().getImm()) ? true : false;
						correctPrediction = true;
					}else{
						realAddress = (short) (getInstruction().getAddress() + 1);
//						prediction = getInstruction().getImm() >= 0;
						prediction = Processor.getPrediction(getInstruction().getImm()) ?  false : true;
						correctPrediction = false;
					}
					setStatus(Status.WRITE);
				}
		}else if(getStatus() == Status.WRITE){
					this.getRobEntry().setReady(true);
					setStatus(Status.COMMIT);
					
		}else if(getStatus() == Status.COMMIT){
			if(ROB.removeEntry(getRobEntry())){ // Its turn to commit?
				if(!prediction){
					ROB.flush();
					RegisterStatusBoard.reset();
					Processor.reset(realAddress, getInstruction().getImm(), correctPrediction);
				}else{
					RegisterStatusBoard.clearEntry(getDesRegister());
				}
				this.reset();
			}
		}
	}	
}
