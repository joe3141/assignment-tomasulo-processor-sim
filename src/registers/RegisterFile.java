package registers;

import java.util.Arrays;

public class RegisterFile {
	private static Register[] registers = new Register[8];
	
	public RegisterFile(){
		Arrays.fill(registers, new Register((short)0));
	}
	
	public static void setRegister(int register, short data){
		if(register != 0)
			registers[register].setData(data);;
	}
	
	public static short getRegister(int register){
		return registers[register].getData();
	}
	
	
}
