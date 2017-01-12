package bus;

import java.util.Deque;

import processor.Processor;
import reorder_buffer.ROB;
import reorder_buffer.ROBEntry;
import reservation_station.ReservationStation;

public class CDB {
	
	private static boolean writing;

	
	public CDB(){
		writing = false;
	}
	
	public static boolean broadcast(short data, ROBEntry rob){
		if(writing)
			return false;
		writing = true;
		Deque<ReservationStation> temp = Processor.getActiveRS();
		
		for(ReservationStation r : temp){
			if(r.isBusy()){
				if(r.getQj() != -1)
					if(ROB.getEntry(r.getQj()).equals(rob)){
						r.setVj(data);
						r.setQj(-1);
					}
				if(r.getQk() != -1)
					if(ROB.getEntry(r.getQk()).equals(rob)){
						r.setVk(data);
						r.setQk(-1);
					}
			}
		}
		return true;
	}
	
	public static boolean isWriting() {
		return writing;
	}

	public static void setWriting(boolean writing) {
		CDB.writing = writing;
	}
}
