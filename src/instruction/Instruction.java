package instruction;

public class Instruction {
	private String type, instructionPlainText;
	private short rd, rs, rt, imm, address;
	//logs
	//is issued
	
	public Instruction(String instruction, short address){ // relative address
//		System.out.println(instruction + "!!!!");
		instructionPlainText = instruction;
		this.setAddress(address);
		parseInstruction();
	}
	
	private void parseInstruction(){
		String[] str = instructionPlainText.split(" ");
		this.type = str[0];
		String[] regs = str[1].split(",");
		short[] vals = parseRegisters(regs);
		assignRegs(vals);
	}
	
	private void assignRegs(short[] vals) {
		if(type.equalsIgnoreCase("ADD") || type.equalsIgnoreCase("SUB") || 
				type.equalsIgnoreCase("NAND") || type.equalsIgnoreCase("MUL")){ //Arithmetic
			setRd((vals[0])); setRs(vals[1]); setRt(vals[2]);
		}else if(type.equalsIgnoreCase("ADDI") || type.equalsIgnoreCase("LW")){//Immediate or load
			setRd(vals[0]); setRs(vals[1]); setImm(vals[2]);
		}else if(type.equalsIgnoreCase("SW") || type.equalsIgnoreCase("BEQ")){
			setRs(vals[0]); setRt(vals[1]); setImm(vals[2]);
		}else if(type.equalsIgnoreCase("JALR")){
			setRd(vals[0]); setRs(vals[1]);
		}else if(type.equalsIgnoreCase("JMP")){
			setRd(vals[0]); setImm(vals[1]);
		}else if(type.equalsIgnoreCase("RET")){
			setRd(vals[0]);
		}
	}

	private short[] parseRegisters(String[] regs){
		short[] res = new short[regs.length];
		
		for(int i = 0; i < regs.length; i++)
			if(regs[i].contains("0x")) //hex imm
				res[i] = (short) Integer.parseInt(regs[i].substring(2), 16);
			else if(regs[i].contains("0b")) //binary imm
				res[i] = (short) Integer.parseInt(regs[i].substring(2), 2);
			else if(regs[i].contains("R"))
				res[i] = (short) Integer.parseInt(regs[i].substring(1));
			else
				res[i] = (short) Integer.parseInt(regs[i]);
		
		return res;
	}

	public short getRd() {
		return rd;
	}

	public void setRd(short rd) {
		this.rd = rd;
	}

	public short getRs() {
		return rs;
	}

	public void setRs(short rs) {
		this.rs = rs;
	}

	public short getRt() {
		return rt;
	}

	public void setRt(short rt) {
		this.rt = rt;
	}

	public short getImm() {
		return imm;
	}

	public void setImm(short imm) {
		this.imm = imm;
	}

	public short getAddress() {
		return address;
	}

	public void setAddress(short address) {
		this.address = address;
	}
	
	public String getType(){
		return type;
	}
	
	public String getInstructioPlain(){
		return instructionPlainText;
	}
}
