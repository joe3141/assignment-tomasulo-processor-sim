package Memory;

import java.util.Arrays;

public class MainMemory {
	 private static final Object memory[] = new Object[65536/2];
	 private static final MainMemory instance = new MainMemory();
	 private static int cycles ;
	 private static double access = 0  ;
	    


		private  MainMemory(){
	    	
	    }
        
		public static void print(){
			for(int i = 0; i < memory.length; i++)
				if(memory[i] != null)
					System.out.println(memory[i]);
		}
		
	    public static MainMemory getInstance(int cycles){
	    	
	        MainMemory.cycles = cycles ;
	        return instance;
	    }
	    
	    public static MainMemory getInstance(){
	        
	        return instance;
	    }
	    
	    public void  write (short address ,Object data){
	    	     access++ ;
	    	 memory[address] = data ;
	    }
	    
        public Object read (short address){
        	     access++ ;
	    	return memory[address] ;
	    }
        public Object[] readLine(int lineSize ,short addr){
                
        	   Object[] res = new Object[lineSize];
        	   int StartingWord = (addr/lineSize)*lineSize ;
        	  for (int i = 0; i < res.length; i++) {
				res[i] = read((short)(StartingWord+i)) ;
			}
        	  return res;
        }
        
        public void WriteLine(int lineSize,short addr,Object[] data){
       	 //lineSize is in bytes so divide by 2
           int numberOfWords = lineSize ;
       	   int StartingWord = (addr/lineSize)* lineSize ;
       	  for (int i = 0; i <numberOfWords; i++) {
				write((short)(StartingWord+i), data[i]);
			}
       	 
       }
        
	    public static int getCycles() {
		return cycles;
	}

	public static void setCycles(int cycles) {
		MainMemory.cycles = cycles;
	}

	public static double getAccess() {
		return access;
	}

	public static void setAccess(int access) {
		MainMemory.access = access;
	}
}
