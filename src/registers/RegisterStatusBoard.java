package registers;

import java.util.Arrays;

public class RegisterStatusBoard {
	private static int[] stats; // [register_index : ROB entry number]
	// private static ROBEntry[] stats;
	
	public RegisterStatusBoard(){
		stats = new int[8];
		Arrays.fill(stats, -1); 
	}
	
	public static int getROBEntry(int register){
		return stats[register];
	}
	
	public static void setROBEntry(int register, int entry){
		stats[register] = entry; //ROBEntry id
	}
	
	public static void clearEntry(int register){
		stats[register] = -1;
	}

	public static void reset() {
		Arrays.fill(stats, -1); 
	}
	
}
