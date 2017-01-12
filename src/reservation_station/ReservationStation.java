package reservation_station;

import instruction.Instruction;
import reorder_buffer.ROBEntry;

public abstract class ReservationStation {
	
	private Operation operation;
	private Status status;
	private short vj, vk;
	private int qj, qk;
	private ROBEntry robEntry;
	private boolean busy;
	private short address;
	private short result ;
	private int latency, cyclesTaken, desRegister;
	private Instruction instruction;
	// id
	
	public ReservationStation(){
		setOperation(null);
		instruction = null;
		robEntry = null;
		setAddress((short) 0);
		setResult((short) 0);
		setDesRegister((short) 0);
		setLatency(0);
		status = Status.FINISHED;
		vj = 0;
		vk = 0;
		qj = 0;
		qk = 0;
		busy = false;
		address = 0;
		result = 0;
		cyclesTaken = 0;
	}
	
	public ReservationStation(Operation operation, Instruction instruction, short vj, short vk, int qj, int qk, ROBEntry robEntry
			, short address, int desRegister, int latency){
		setOperation(operation);
		setInstruction(instruction);
		setQj(qj);
		setQk(qk);
		setVj(vj);
		setVk(vk);
		setRobEntry(robEntry);
		setAddress(address);
		setDesRegister(desRegister);
		setLatency(latency);
		result = 0;
		cyclesTaken = 0;
		status = Status.ISSUE;
		busy = true;
	}
	
	
	public short calculate()  {
		switch(operation){
		case ADD:
		case ADDI:
			return (short) (vj + vk);
		
		case SUB:
			return (short) (vj - vk);
		
		case MUL:
			return (short) (vj * vk);
		
		case NAND:
			return (short) ~(vj & vk);
			
		case LOAD:
			return (short) (vj + address);
			
		case STORE:
			return (short) (vk + address);
		
		case CALL:
			return (short) (getInstruction().getAddress() + 1);
		
		default:
			return 0;
		}
	}
	
	public abstract void update();
	
	public void reset(){
		setOperation(null);
//		instruction = null;
		robEntry = null;
		setAddress((short) 0);
		setResult((short) 0);
		setDesRegister((short) 0);
		setLatency(0);
		status = Status.FINISHED;
		vj = 0;
		vk = 0;
		qj = 0;
		qk = 0;
		busy = false;
		address = 0;
		result = 0;
		cyclesTaken = 0;
		RSFile.clearStation(this);
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public short getVj() {
		return vj;
	}
	public void setVj(short vj) {
		this.vj = vj;
	}
	public short getVk() {
		return vk;
	}
	public void setVk(short vk) {
		this.vk = vk;
	}
	public int getQj() {
		return qj;
	}
	public void setQj(int qj) {
		this.qj = qj;
	}
	public int getQk() {
		return qk;
	}
	public void setQk(int qk) {
		this.qk = qk;
	}
	public ROBEntry getRobEntry() {
		return robEntry;
	}
	public void setRobEntry(ROBEntry robEntry) {
		this.robEntry = robEntry;
	}
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public short getAddress() {
		return address;
	}
	public void setAddress(short address) {
		this.address = address;
	}


	public Operation getOperation() {
		return operation;
	}


	public void setOperation(Operation operation) {
		this.operation = operation;
	}


	public short getResult() {
		return result;
	}


	public void setResult(short result) {
		this.result = result;
	}

	public int getCyclesTaken() {
		return cyclesTaken;
	}

	public void setCyclesTaken(int cyclesTaken) {
		this.cyclesTaken = cyclesTaken;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public int getDesRegister() {
		return desRegister;
	}

	public void setDesRegister(int desRegister) {
		this.desRegister = desRegister;
	}
	
}
