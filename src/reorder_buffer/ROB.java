package reorder_buffer;

import java.util.ArrayDeque;
import java.util.Deque;


public class ROB {
	
	private static Deque<ROBEntry> entries;
	private static int size;
	private static int counter;
	
	public ROB(int size){
		entries = new ArrayDeque<ROBEntry>(size);
		ROB.size = size;
		counter = 0;
	}
	
	public boolean isFull(){
		return entries.size() == size;
	}
	
	public static ROBEntry[] getROB(){
		return (ROBEntry[]) entries.toArray();
	}
	
	public static ROBEntry getEntry(int id){
		for(ROBEntry r : entries){
			if(r.getId() == id)
				return r;
		}
		
		return null;
	}
	
	public static int addEntry(ROBEntry e){
		if(entries.size() < size){
			counter = (counter + 1) % size;
			e.setId(counter);
//			counter %= size + 10000;
			entries.offerLast(e); 
			return counter;
		}else
			return -1; // returns -1 if it's full
	}
	
	public static boolean removeEntry(ROBEntry r){
		if(entries.peekFirst().equals(r)){ // Checking to commit in order
			entries.pollFirst(); // returns null if it's empty instead of an excpetion
			return true;
		}
		return false;
	}
	
	public static void flush(){
		entries = new ArrayDeque<ROBEntry>(size);
	}
	
	public static boolean hasSpace(){
		return size > entries.size();
	}
}
