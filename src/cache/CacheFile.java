package cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import Memory.MainMemory;

public class CacheFile {
          private  static MainMemory memory ;
          private  static Cache[]    dataCaches ;
          private  static Cache[]    instructionCaches ;
          private  static int        n;  //number of cache levels
          private  static int  latency ;
           
           
     

		public CacheFile(int n ,int memoryCycles) {
        	dataCaches        = new Cache[n];
        	instructionCaches = new Cache[n];
        	memory = MainMemory.getInstance(memoryCycles);
        	this.n = n ;
		}
          
          public static int writeData(Object data, short address){
        	   latency = 0 ;
        	   writeDataHelper(data, address, 0);
        	  
       	    return latency ;
          }
          public static void writeDataHelper(Object data, short address,int i){
               if(i==n){
            	     latency += MainMemory.getCycles();
            	     if(data instanceof CacheLine){
            	  	   CacheLine linee = (CacheLine) data ;
                	   memory.WriteLine(dataCaches[0].getL()/2, address, linee.getWords());
		    		    }
		    		    else
		               memory.write(address, data);
            
                    
               }
               else{
        	  Cache curr =  dataCaches[i] ;
        	  latency += curr.getCycles();

        	  int res = curr.writeData(data, address);
        	      if(res == 1){
        	    	 
        	         writeDataHelper(data,address,i+1);  		  
        	  }else{
        		  if(res == 2) {//write-alocate
        			//fetch then write 
        			   fetchDataHelper(address, i+1);
        			   writeDataHelper(data, address,i);
        		  }
        	  }
               }
        	  
          }
          
          public static Object fetchDataHelper(short address,int i ){
        	  
        	  if(i == n){
        		  latency += MainMemory.getCycles();
        		  Cache curr =  dataCaches[0] ;
        		  CacheLine line = new CacheLine(curr.getL()/2);
        		  line.setWords(memory.readLine(curr.getL()/2,address));
        		 // return memory.read( address) ;
        		  return line ;
        	  }
        	  
        	  Cache curr =  dataCaches[i] ;
        	  latency += curr.getCycles();
        	  Object res = curr.fetchData(address);
        	  if(res == null){
        		  
        		   res = fetchDataHelper(address,i+1);
        		  writeDataHelper(res,address,i);
        	  }  
        	  return res;
        	  
        	  
        	  
          }
          
          public static FetchedObject fetchData(short address ){
        	     latency = 0 ;
        	    Object obj =  fetchDataHelper(address, 0);
        	    MemoryAddress addr = dataCaches[0].parseMemoryAddress(address);
          	  CacheLine l = (CacheLine) obj ;
          	    
          	 Object res =  l.readWord(addr.getOffset());
        	    FetchedObject d = new FetchedObject(latency, res);
        	    return d ;
          }
          
          
          
          public static Object fetchInstructionHelper(short address,int i ){
        	  
        	  if(i == n){
        		  latency += MainMemory.getCycles();
        		  Cache curr =  instructionCaches[0] ;
        		  CacheLine line = new CacheLine(curr.getL()/2);
        		  line.setWords(memory.readLine(curr.getL()/2,address));
        		 // return memory.read( address) ;
        		  return line ;
        	  }
        	  
        	  Cache curr =  instructionCaches[i] ;
        	  latency += curr.getCycles();
        	  Object res = curr.fetchData(address);
        	  if(res == null){
        		  
        		   res = fetchDataHelper(address,i+1);
        		  writeDataHelper(res,address,i);
        		  
        	  }  
        	 
        	  return res ;
          }
          
          public static Object fetchInstruction(short address ){
        	  latency = 0 ;
        	  Object obj =  fetchInstructionHelper(address, 0);
        	  
        	  MemoryAddress addr = instructionCaches[0].parseMemoryAddress(address);
        	  CacheLine l = (CacheLine) obj ;
        	    
        	 Object res =  l.readWord(addr.getOffset());
 
        	 
      	    FetchedObject d = new FetchedObject(latency, res);
      	    return d ;
          }
          public static double getAMAT(){
        	  return MainMemory.getAccess()*MainMemory.getCycles();
          }
          public static double[] getHitRatio(){
        	    double[] res = new double[dataCaches.length];
        	  for (int i = 0; i < dataCaches.length; i++) {
				   res[i]= dataCaches[i].getHitRatio()+instructionCaches[i].getHitRatio();
			}
        	  return res ;
          }
          
          public static void init(CacheSpecs[] caches){
        	  WriteHitPolicy whp ;
        	    for (int i = 1; i <= caches.length-1; i++) {
        	    	    if(caches[i].isPolicy())
        	    	    	 whp = WriteHitPolicy.WRITE_THROUGH ;
        	    	    else
        	    	    	whp = WriteHitPolicy.WRITE_BACK ;
					   Cache curr = new Cache(caches[i].getS(), caches[i].getL(), caches[i].getM(),caches[i].getLatency(), whp,WriteMissPolicy.WRITE_ALLOCATE);
				   dataCaches[i-1] = curr ;
				   instructionCaches[i-1] = curr ;
        	    }
          }
          
          
          public MainMemory getMemory() {
  			return memory;
  		}

  		public void setMemory(MainMemory memory) {
  			this.memory = memory;
  		}

  		public Cache[] getDataCaches() {
  			return dataCaches;
  		}

  		public void setDataCaches(Cache[] dataCaches) {
  			this.dataCaches = dataCaches;
  		}

  		public Cache[] getInstructionCaches() {
  			return instructionCaches;
  		}

  		public void setInstructionCaches(Cache[] instructionCaches) {
  			this.instructionCaches = instructionCaches;
  		}

  		public int getN() {
  			return n;
  		}

  		public void setN(int n) {
  			this.n = n;
  		}      
          
          
          
}
