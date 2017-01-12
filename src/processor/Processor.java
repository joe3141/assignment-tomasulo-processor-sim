package processor;
import instruction.Instruction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import cache.CacheFile;
import cache.FetchedObject;
import branch_predictor.BranchPredictor;
import bus.CDB;
import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.InstructionType;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;
import reservation_station.RSFile;
import reservation_station.ReservationStation;
import reservation_station.Status;


public class Processor {
	private static int width, ibSize;
	private static int instructionCounter, branchCounter, clockCounter,  mispredictedBranches;
	
	private static short pc, programSize, startAddress;
	private static boolean finished, stalled, reset;
	
	private static boolean waited = false;
	private static Instruction curr = null;
	private static int fetchTimer = 0;
	
	private static Deque<Instruction> instructionBuffer, finishedInstructions; // in case of mispredictions
	private static Deque<ReservationStation> activeRS;
	private static boolean returned = false, pleaseFinish = false;
	
	private static BranchPredictor p;
	
	public Processor(int width, int ibSize, int predictionMode){
		Processor.setWidth(width);
		Processor.setIbSize(ibSize);
		pleaseFinish = false;
		returned = false;
		pc = 0;
		setFinished(false);
		stalled = false;
		
		setInstructionBuffer(new ArrayDeque<Instruction>());
		setFinishedInstructions(new ArrayDeque<Instruction>());
		setActiveRS(new ArrayDeque<ReservationStation>());
		
		p = new BranchPredictor(predictionMode);
		waited = false;
		curr = null;
		fetchTimer = 0;
	}
	
	
//	public void writeData(short address, short data){
//		CacheFile.write(address, data, data);
//	}
//	
//	public void writeInstruction()
	
	public static void process(){
		CDB.setWriting(false);
		int numIssues = 0;
		int numFetches = 0;
		System.out.println("" + pc + " " + programSize);
//		System.out.println(waited + " 103test");
		while(instructionBuffer.size() < ibSize && numFetches < width && pc < programSize && !stalled && !
				pleaseFinish){
//			System.out.println(waited + "103test");
			if(!waited){
//				System.out.println("!waited");
				FetchedObject fo = (FetchedObject) CacheFile.fetchInstruction(pc);
				int wait = fo.getCycles();
				curr = new Instruction((String)fo.getData(), pc) ;
				if(returned && curr.getType().equalsIgnoreCase("ret") && pc == programSize){
					pleaseFinish = true;
					instructionBuffer.clear();
					activeRS.clear();
					break;
				}
					
				fetchTimer = wait;
				waited = true;
			}else if(fetchTimer != 0)
				fetchTimer--;
			else if(fetchTimer == 0){
				instructionBuffer.addLast(curr);
				numFetches++;
				if(!(speculate(curr))){
					stalled = true;
					instructionBuffer.removeLast();
					numFetches--;
					break;
				}
				waited = false;
			}
			
			if(canIssue(numIssues) && !stalled){
//				System.out.println(waited + " 103test up");
				numIssues++;
				issueInstruction();
			}else
				break; // To preserve in order issuing
			
		}
		
//		System.out.println("test 103 " + canIssue(numIssues));
		
		while(canIssue(numIssues) && !pleaseFinish){
			issueInstruction();
			numIssues++;
		}
		if(!pleaseFinish){
		for(ReservationStation r : activeRS){
			if(r.getStatus() == Status.FINISHED){
//				System.out.println(r.getInstruction().getInstructioPlain());
				if(r.getInstruction().getType().equalsIgnoreCase("BEQ"))
					branchCounter++;
				instructionCounter++;
				System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
				activeRS.remove(r);
			}else{
//				System.out.println(r.getInstruction().getInstructioPlain());
//				System.out.println("test 103 " + r.getClass());
				r.update();
				if(reset){
					reset = false;
					break;
				}
			}
			
		}
		}
		clockCounter++;
		
		if(instructionBuffer.isEmpty() && activeRS.isEmpty() && pc >= programSize )
			finished = true;
		if(pleaseFinish)
			finished = true;
		if(pc == programSize)
			pleaseFinish = true;
	}
	
	private static void issueInstruction(){
		Instruction curr = instructionBuffer.removeFirst();
		InstructionType t;
		System.out.println("issueInstruction");
		//Reserving an ROB entry
		switch(curr.getType()){
		case "ADD":
		case "MUL":
		case "NAND":
		case "SUB":
			t = InstructionType.FP;
			break;
		case "STORE":
			t = InstructionType.ST;
			break;
		case "LOAD":
			t = InstructionType.LD;
			break;
		case "ADDI":
			t = InstructionType.INT;
			break;
		default:
			t = InstructionType.BR;
			break;
		}
		
		int id = ROB.addEntry(new ROBEntry(t, curr.getRd()));
		
		if(id == -1)
			System.out.println("ABORTTTTTTTTTTT");
		
		// Reserving an RSB entry
		RegisterStatusBoard.setROBEntry(curr.getRd(), id);
		// Reserving an RS and queuing it.
		activeRS.addLast(RSFile.reserveStation(curr, id));
	}
	
	private static boolean speculate(Instruction curr) {
		
		short address = curr.getImm();
		
		if(curr.getType().equalsIgnoreCase("BEQ"))
			if(p.speculate(address))
				pc += 1 + address;
			else
				pc++;
		else if(curr.getType().equalsIgnoreCase("JMP")){
			if(RegisterStatusBoard.getROBEntry(curr.getRd()) == -1)
				pc += 1 + address + RegisterFile.getRegister(curr.getRd());
			else if(ROB.getEntry(RegisterStatusBoard.getROBEntry(curr.getRd())).isReady())
				pc += 1 + address + ROB.getEntry(RegisterStatusBoard.getROBEntry(curr.getRd())).getValue();
			else
				return false;
		}else if(curr.getType().equalsIgnoreCase("JALR")){
			if(RegisterStatusBoard.getROBEntry(curr.getRs()) == -1){
				System.out.println(curr.getInstructioPlain());
				System.out.println(curr.getRs());
				System.out.println(RegisterFile.getRegister(curr.getRs()));
				System.out.println("found");
				pc =  (short) (RegisterFile.getRegister(curr.getRs()) + startAddress);
			}
			else if(ROB.getEntry(RegisterStatusBoard.getROBEntry(curr.getRs())).isReady()){
				pc = (short) (ROB.getEntry(RegisterStatusBoard.getROBEntry(curr.getRs())).getValue() + startAddress);
				System.out.println("found2");
			}else{
				System.out.println("found3");
				return false;
			}
		}else if(curr.getType().equalsIgnoreCase("RET")){
			if(RegisterStatusBoard.getROBEntry(curr.getRd()) == -1){
				pc =  (short) (RegisterFile.getRegister(curr.getRd()) + startAddress);
				returned = true;
			}
			else if(ROB.getEntry(RegisterStatusBoard.getROBEntry(curr.getRd())).isReady()){
				pc = (short) (ROB.getEntry(RegisterStatusBoard.getROBEntry(curr.getRd())).getValue() + startAddress);
				returned = true;
			}else
				return false;
		}else
			pc++;
		return true;
	}


	
	
	private static boolean canIssue(int num){
		return num < width && instructionBuffer.size() > 0 && ROB.hasSpace() && RSFile.canReserve(
				instructionBuffer.peek().getType().toLowerCase());
	}
	
	//TODO: Reset execution
	public static void reset(short realAddress, short address, boolean correctPrediction) {
		reset = true;
		while(!instructionBuffer.isEmpty())
			instructionBuffer.removeLast();
		while(!activeRS.isEmpty())
			activeRS.removeLast();
		pc = realAddress;
		mispredictedBranches++;
		branchCounter++;
		p.update(correctPrediction, address);
	}
	
	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		Processor.width = width;
	}
	

	public static int getIbSize() {
		return ibSize;
	}

	public static void setIbSize(int ibSize) {
		Processor.ibSize = ibSize;
	}

	public static int getInstructionCounter() {
		return instructionCounter;
	}

	public static void setInstructionCounter(int instructionCounter) {
		Processor.instructionCounter = instructionCounter;
	}

	public static int getBranchCounter() {
		return branchCounter;
	}

	public static void setBranchCounter(int branchCounter) {
		Processor.branchCounter = branchCounter;
	}

	public static int getClockCounter() {
		return clockCounter;
	}

	public static void setClockCounter(int clockCounter) {
		Processor.clockCounter = clockCounter;
	}

	public static void setProgramSize(short size){
		programSize = size;
	}
	
	public static short getProgramSize(){
		return programSize;
	}
	
	public static int getMispredictedBranches() {
		return mispredictedBranches;
	}

	public static void setMispredictedBranches(int mispredictedBranches) {
		Processor.mispredictedBranches = mispredictedBranches;
	}
	
	
	public static short getPc() {
		return pc;
	}

	public static void setPc(short pc) {
		Processor.pc = pc;
	}

	public static boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		Processor.finished = finished;
	}

	public static Deque<Instruction> getInstructionBuffer() {
		return instructionBuffer;
	}

	public static void setInstructionBuffer(ArrayDeque<Instruction> arrayDeque) {
		Processor.instructionBuffer = arrayDeque;
	}

	public static Deque<Instruction> getFinishedInstructions() {
		return finishedInstructions;
	}

	public static void setFinishedInstructions(ArrayDeque<Instruction> arrayDeque) {
		Processor.finishedInstructions = arrayDeque;
	}


	public static Deque<ReservationStation> getActiveRS() {
		return activeRS;
	}


	public static void setActiveRS(Deque<ReservationStation> activeRS) {
		Processor.activeRS = activeRS;
	}


	public static short getStartAddress() {
		return startAddress;
	}


	public static void setStartAddress(short startAddress) {
		Processor.startAddress = startAddress;
	}
	
	public static boolean getPrediction(short address){
		return p.speculate(address);
	}

}
