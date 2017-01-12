package reservation_station;

import instruction.Instruction;

import java.util.Arrays;
import java.util.HashMap;

import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;

public class RSFile {
	

	private static HashMap<String, ReservationStation[]> reservationStations;
	private static HashMap<String, Integer> used;
	private static HashMap<String, Integer> latencies;
	
	public RSFile(int addC, int multC, int loadC, int storeC, int branchC, int logicalC, int callC,
			int jumpC, int addL, int multL, int loadL, int storeL, int branchL, int logicalL, int callL,
			int jumpL){
		reservationStations = new HashMap<String, ReservationStation[]>();
		used = new HashMap<String, Integer>();
		latencies = new HashMap<String, Integer>();
			
		reservationStations.put("add", new AddUnit[addC]);
		Arrays.fill(reservationStations.get("add"), new AddUnit());
		used.put("add", 0);
		latencies.put("add", addL);
		
		reservationStations.put("mult", new MultUnit[multC]);
		Arrays.fill(reservationStations.get("mult"), new MultUnit());
		used.put("mult", 0);
		latencies.put("mult", multL);
		
		reservationStations.put("load", new LoadUnit[loadC]);
		Arrays.fill(reservationStations.get("load"), new LoadUnit());
		used.put("load", 0);
		latencies.put("load", loadL);
		
		reservationStations.put("store", new StoreUnit[storeC]);
		Arrays.fill(reservationStations.get("store"), new StoreUnit());
		used.put("store", 0);
		latencies.put("store", storeL);
		
		reservationStations.put("branch", new BranchUnit[branchC]);
		Arrays.fill(reservationStations.get("branch"), new BranchUnit());
		used.put("branch", 0);
		latencies.put("branch", branchL);
		
		reservationStations.put("logical", new LogicalUnit[logicalC]);
		Arrays.fill(reservationStations.get("logical"), new LogicalUnit());
		used.put("logical", 0);
		latencies.put("logical", logicalL);
		
		reservationStations.put("call", new CallUnit[callC]);
		Arrays.fill(reservationStations.get("call"), new CallUnit());
		used.put("call", 0);
		latencies.put("call", callL);
		
		reservationStations.put("jump", new JumpUnit[jumpC]);
		Arrays.fill(reservationStations.get("jump"), new JumpUnit());
		used.put("jump", 0);
		latencies.put("jump", jumpL);
	}
	
//	public static HashMap<String, ReservationStation[]> getRS(){
//		return reservationStations;
//	}
	
	public static ReservationStation reserveStation(Instruction inst, int id){
		String type = inst.getType();
		String station = getStation(type);
//		ReservationStation unit = initStation();
		ReservationStation unit = null;
		ROBEntry entry = ROB.getEntry(id);
		short address = 0;
		int desRegister = inst.getRd();
		System.out.println(station + " new test");
		int latency = latencies.get(station); // or station
		short vk = inst.getImm();
		short vj = 0;
		int qj = -1, qk = -1;
		Operation operation = null;
		switch (station) {
		case "add":
			if(type.equals("ADDI")){
				 vk = inst.getImm();
				operation = Operation.ADDI;
				if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
					vj = RegisterFile.getRegister(inst.getRs());
				else
					qj = RegisterStatusBoard.getROBEntry(inst.getRs());
				unit = new AddUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
				fillStation("add", unit);
			}else{
				operation = type.equalsIgnoreCase("sub") ? Operation.SUB : Operation.ADD;
				
				if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
					vj = RegisterFile.getRegister(inst.getRs());
				else
					qj = RegisterStatusBoard.getROBEntry(inst.getRs());
				
				if(RegisterStatusBoard.getROBEntry(inst.getRt()) != -1)
					vk = RegisterFile.getRegister(inst.getRt());
				else
					qk = RegisterStatusBoard.getROBEntry(inst.getRt());
				
				unit = new AddUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
				fillStation("add", unit);
			}
			break;
		
		case "mult":
			operation = Operation.MUL;
			
			if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
				vj = RegisterFile.getRegister(inst.getRs());
			else
				qj = RegisterStatusBoard.getROBEntry(inst.getRs());
			
			if(RegisterStatusBoard.getROBEntry(inst.getRt()) != -1)
				vk = RegisterFile.getRegister(inst.getRt());
			else
				qk = RegisterStatusBoard.getROBEntry(inst.getRt());
			
			unit = new MultUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("mult", unit);
			break;
		case "logical":
			operation = Operation.NAND;
			
			if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
				vj = RegisterFile.getRegister(inst.getRs());
			else
				qj = RegisterStatusBoard.getROBEntry(inst.getRs());
			
			if(RegisterStatusBoard.getROBEntry(inst.getRt()) != -1)
				vk = RegisterFile.getRegister(inst.getRt());
			else
				qk = RegisterStatusBoard.getROBEntry(inst.getRt());
			
			unit = new LogicalUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("logical", unit);
			break;
		
		case "load":
			operation = Operation.LOAD;
			address = inst.getImm();
			if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
				vj = RegisterFile.getRegister(inst.getRs());
			else
				qj = RegisterStatusBoard.getROBEntry(inst.getRs());
			unit = new LoadUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("load", unit);
//			System.out.println("Case");
			break;
			
		case "store":
			operation = Operation.LOAD;
			address = inst.getImm();
			if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
				vj = RegisterFile.getRegister(inst.getRs());
			else
				qj = RegisterStatusBoard.getROBEntry(inst.getRs());
			
			if(RegisterStatusBoard.getROBEntry(inst.getRt()) != -1)
				vk = RegisterFile.getRegister(inst.getRt());
			else
				qk = RegisterStatusBoard.getROBEntry(inst.getRt());
			
			unit = new StoreUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("store", unit);
			break;
		
		case "branch":
			if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
				vj = RegisterFile.getRegister(inst.getRs());
			else
				qj = RegisterStatusBoard.getROBEntry(inst.getRs());
			
			if(RegisterStatusBoard.getROBEntry(inst.getRt()) != -1)
				vk = RegisterFile.getRegister(inst.getRt());
			else
				qk = RegisterStatusBoard.getROBEntry(inst.getRt());
			address = inst.getImm();
			
			unit = new BranchUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("branch", unit);
			break;
	
		case "call":
			if(inst.getType().equalsIgnoreCase("ret"))
				if(RegisterStatusBoard.getROBEntry(inst.getRs()) != -1)
					vj = RegisterFile.getRegister(inst.getRs());
				else
					qj = RegisterStatusBoard.getROBEntry(inst.getRs());
			
			unit = new CallUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("call", unit);
			break;
			
		case "jump":
			unit = new JumpUnit(operation, inst, vj, vk, qj, qk, entry, address, desRegister, latency);
			fillStation("jump", unit);
			break;
		default:
			break;
		}
	return unit;
	}
	
	private static String getStation(String type){
		String station = "";
		type = type.toUpperCase();
		switch(type){
		case "ADD":
		case "ADDI":
		case "SUB":
			station = "add";
			break;
		case "NAND":
			station = "logical";
			break;
		case "MUL":
			station  = "mult";
			break;
		case "RET":
		case "JALR":
			station = "call";
			break;
		case "BEQ":
			station = "branch";
			break;
		case "JMP":
			station = "jump";
			break;
		case "LW":
			station = "load";
			break;
		case "SW":
			station = "store";
			break;
		}
		return station;
	}
	
	private static void initStation(){
		
	}
	
	private static void fillStation(String unit, ReservationStation rs){
		used.put(unit, used.get(unit)+1);
		for(int i = 0; i < reservationStations.get(unit).length; i++)
			if(!(reservationStations.get(unit)[i].isBusy())){
				reservationStations.get(unit)[i] = rs;
				break;
			}
	}
	
	public static void clearStation(ReservationStation rs){
		if(rs instanceof AddUnit)
			used.put("add", used.get("add")-1);
		else if(rs instanceof BranchUnit)
			used.put("branch", used.get("branch")-1);
		else if(rs instanceof CallUnit)
			used.put("call", used.get("call")-1);
		else if(rs instanceof JumpUnit)
			used.put("jump", used.get("jump")-1);
		else if(rs instanceof LoadUnit)
			used.put("load", used.get("load")-1);
		else if(rs instanceof LogicalUnit)
			used.put("logical", used.get("logical")-1);
		else if(rs instanceof MultUnit)
			used.put("mult", used.get("mult")-1);
		else if(rs instanceof StoreUnit)
			used.put("store", used.get("store")-1);
		
	}
	
	public static boolean canReserve(String unit){
		int c = used.get(getStation(unit));
		return c < reservationStations.get(getStation(unit)).length;
	}

	public static HashMap<String, Integer> getLatencies() {
		return latencies;
	}

	public static void setLatencies(HashMap<String, Integer> latencies) {
		RSFile.latencies = latencies;
	}

}