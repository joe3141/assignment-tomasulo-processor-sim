import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import cache.CacheFile;
import cache.CacheSpecs;
import Memory.MainMemory;
import bus.CDB;
import processor.Processor;
import registers.RegisterFile;
import registers.RegisterStatusBoard;
import reorder_buffer.ROB;
import reservation_station.RSFile;


public class Main {
	
	private static int cacheLevels;
	private static CacheSpecs[] cacheInfo;
	private static int memLatency;
	private static int width, ibSize, robSize;
	private static HashMap<String, int[]> unitInfo; // [unit => [num, latency]]
	private static Scanner sc = new Scanner(System.in);
	

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to the great Tomasulo simulator!!1!!");
		System.out.println("This is the imput phase, please enter the following,");
		
		System.out.println("number of cache levels");
		cacheLevels = sc.nextInt();
		cacheInfo = new CacheSpecs[cacheLevels+1];
		
		for(int i = 1; i <= cacheLevels; i++){
			System.out.println("Enter cache size(S), line size(L) and associativity level(m)");
			int s, l, m, latency, p;
			boolean policy;
			s = sc.nextInt(); l = sc.nextInt(); m = sc.nextInt();
			System.out.println("Enter the write policy, 0 for write back and 1 for write through for this "
					+ "cache level (" + i + ")");
			p = sc.nextInt(); policy = p == 1;
			System.out.println("Enter the clock cycles required to access this cache level (" + i + ")");
			latency = sc.nextInt();
			CacheSpecs curr = new CacheSpecs(s, l, m, policy, latency);
			cacheInfo[i] = curr;
		}
		
		System.out.println("Enter the clock cycles required to access the memory");
		memLatency = sc.nextInt();
		
		System.out.println("Enter the pipeline width, instruction buffer size and the "
				+ "Reorder buffer size");
		width = sc.nextInt(); ibSize = sc.nextInt(); robSize = sc.nextInt();
		
		printS("Enter 0 for static branch prediction\n1 for saturating counter\n2 for adaptive predictor");
		int mode = sc.nextInt();
				
		Processor p = new Processor(width, ibSize, mode);
		ROB rob = new ROB(robSize);
		unitInfo = new HashMap<String, int[]>();
		
		parseFunctionUnits();
		RSFile rsFile = new RSFile(unitInfo.get("add")[0], unitInfo.get("mult")[0], unitInfo.get("load")[0], 
				unitInfo.get("store")[0], unitInfo.get("branch")[0], unitInfo.get("logical")[0], 
				unitInfo.get("call")[0], unitInfo.get("jump")[0], 
				unitInfo.get("add")[1], unitInfo.get("mult")[1], unitInfo.get("load")[1], unitInfo.get("store")[1], 
				unitInfo.get("branch")[1], unitInfo.get("logical")[1], unitInfo.get("call")[1], 
				unitInfo.get("jump")[1]);
		
		CDB cdb = new CDB();
		RegisterFile rf = new RegisterFile();
		RegisterStatusBoard rsb = new RegisterStatusBoard();
		// TODO: init caches and memory
		CacheFile cf = new CacheFile(cacheLevels, memLatency);
		CacheFile.init(cacheInfo);
//		MainMemory.setLatency(memLatency);
		printS("Enter the program file name. press # if you want the predefined program");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		sc.reset();
		String temp = br.readLine();
		String programFile = temp.equals("#") ? "program1" : temp;
		pushProgram(programFile);
		printS("Enter the data file name or # if you don't want any data in the memory");
		String t = br.readLine();
		if(!(t.equals("#")))
			pushData(t);
		sc.close();
		br.close();
//		MainMemory.print();
		while(!(Processor.isFinished())){
			Processor.process();
		}
		
		output();
	}
	
	
	private static void output() {
		
		int cyclesSpanned = Processor.getClockCounter();
		
		double ipc = (((double)Processor.getInstructionCounter()) / ((double)cyclesSpanned));
		double[] hitRatio = new double[cacheLevels];
		double amat = 0;
		int t = Processor.getBranchCounter();
		t = t > 0 ? t : 1;
		double mispredictionPercentage = (double) ((double)(Processor.getMispredictedBranches()) 
				/ ((double)(t))) * 100.0;
		amat = CacheFile.getAMAT();
		hitRatio = CacheFile.getHitRatio();
		printS("Execution time: " + cyclesSpanned);
		printS("IPC: " + ipc);
		for(int i = 0; i < cacheLevels; i++)
			printS("Level " + ((int)(i + 1)) + " cache hit ratio: " + hitRatio[i]);
		printS("Global AMAT: " + amat);
		printS("Branch misprediction percentage: " + mispredictionPercentage);
	}


	private static void parseFunctionUnits(){
		parseFunctionUnit("add");
		parseFunctionUnit("branch");
		parseFunctionUnit("call");
		parseFunctionUnit("jump");
		parseFunctionUnit("load");
		parseFunctionUnit("logical");
		parseFunctionUnit("mult");
		parseFunctionUnit("store");
	}
	
	private static void parseFunctionUnit(String unit){
		printS("Enter the amount of reservation stations for the " + unit +  
				" unit and the number required for"
							+ " its execution");
					int x, y;
					x = sc.nextInt(); y = sc.nextInt();
					int [] temp = new int[2];
//					int[] temp = {x, y};
					temp[0] = x; temp[1] = y;
					unitInfo.put(unit,temp);
	}
	
	private static void pushProgram(String string){
//		System.out.println("###" + string + "###");
		String file = readFile(string);
		System.out.println("###" + string + "###");
		String[] lines = file.split("\n"); // TODO: fe mshkla hna?
		
		short startAddress = (short) Integer.parseInt(lines[0].split(" ")[1]); //must start with .org
		Processor.setPc( (short) (startAddress + 1));
		Processor.setStartAddress( (short) (startAddress + 1) );
		Processor.setProgramSize((short) ((lines.length - 1) + (startAddress))); 
		short offset = 1;
		for(int i = 1; i < lines.length; i++){
			if(!(lines[i].split(" ")[0].equalsIgnoreCase(".org"))) // not a sub routine
				MainMemory.getInstance().write((short)(startAddress + 1 + i - offset), lines[i]); // i-1 because i is at an offset of 1
			else{ // sub routine
				startAddress = (short) Integer.parseInt(lines[i].split(" ")[1]);
				offset = (short) i; // might wanna check this if things went dark
			}
		}
	}
	
	private static void pushData(String name){
		System.out.println("###" + name + "###");
		String file = readFile(name);
		String[] lines = file.split("\n"); // TODO: fe mshkla hna?
		
		for(int i = 0; i < lines.length; i++){
			short address = (short) Integer.parseInt(lines[i].split(" ")[0]);
			short data = (short) Integer.parseInt(lines[i].split(" ")[1]);
			MainMemory.getInstance().write(address, data);
		}
	}

	public static String readFile(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				"src/programs/" + filename)))) {
			String line = br.readLine();
			String res  = "";
			
			while(line != null){
//				System.out.println("###" + "###");
				res += line + "\n";
				line = br.readLine();
			}
			return res;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}
	
	private static void printS(String s){
		System.out.println(s);
	}
}
