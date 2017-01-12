package cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import Memory.MainMemory;

public class Cache {
  private int s; //size of cache
  
  private int l; //line size
  
  private int m; //assiocativty
  
  private int cycles; 
  
  private WriteHitPolicy WHP ;
  
  private WriteMissPolicy WMP ;
  
  private TreeMap<Integer, ArrayList<CacheEntry> > blocks;
  
  private MainMemory memory ;
  
  private double access ;
  
  private double hit ;
 
  




public Cache (int s, int l , int m, int c , WriteHitPolicy WHP ,WriteMissPolicy WMP) {
	  this.s = s;
	  this.l = l;
	  this.m = m;
	  this.cycles = c;
	  this.WHP = WHP;
	  this.WMP = WMP;
	  this.blocks = new TreeMap<Integer,ArrayList<CacheEntry> >();
	  memory = MainMemory.getInstance();
	  initCache();
	  access = 0 ;
	  hit    = 0 ;
	
	  
  }





public void initCache(){
	  for (int i = 0; i < Math.pow(2,getIndexSize()); i++) {
		  ArrayList<CacheEntry> newSet = new ArrayList<CacheEntry>();
		  for (int j = 0; j < m; j++) {
			  CacheLine line = new CacheLine(l/2);
			  CacheEntry newEntry = new CacheEntry(false, false, -1, line);
			  newSet.add(newEntry);
		      }
		  blocks.put(i,newSet );
	}
}

//public short fetchDataLine(short address){
//	MemoryAddress addr = parseMemoryAddress(address); 
//    CacheEntry entry = check(address);
//     if(entry != null){
//                  
//    	  return entry.getLine().readWord(addr.getOffset());
//     }
//     else{//miss
//    	
//    	  return -1 ;
//     }
//      
//}


public Object fetchData(short address){
	MemoryAddress addr = parseMemoryAddress(address); 
    CacheEntry entry = check(address);
          access ++ ;
     if(entry != null){
           hit++ ;
    	  return entry.getLine();
     }
     else{//miss
    	   
    	  return null ;
     }
      
}
public Object fetchInstruction(short address){
	return fetchData( address) ;
}

public int  writeData(Object data, short address ){
	    MemoryAddress addr = parseMemoryAddress(address); 
	     int index = addr.getIndex();
	     ArrayList<CacheEntry> block = blocks.get(index);
	     CacheEntry entry = check(address);
	           access ++ ;
	      if(entry != null){  //hit
	    	    hit++ ;
	    	  if(entry.isDirty()){
	    		  // store then write
	    		   
	    		    memory.WriteLine(l/2,reverseToAdress(index,entry.getTag()), entry.getLine().getWords());
	    		    entry.setDirty(false);
	    		    return writeData(data, address);
	    		     
	    	  }else{
	    	
	    		  if(WHP == WHP.WRITE_BACK){			   
		    		    entry.setDirty(true);
		    		    if(data instanceof CacheLine){
		    			   CacheLine line = (CacheLine) data ;
		    		       entry.getLine().setWords((line.getWords()));
		    		    }
		    		    else
		    		       entry.getLine().writeWord(addr.getOffset(), data);
		    		    return  0;
		    		    
		    		     
		    	   }else{ // write-Through
		    		  
		    		   if(data instanceof CacheLine){
		    			   CacheLine line = (CacheLine) data ;
		    		       entry.getLine().setWords((line.getWords()));
		    		    }
		    		    else
		    		       entry.getLine().writeWord(addr.getOffset(), data);
		    		   return 1;
		    	   } 
	    	  }
	    	  
	    	  
	    	  
	      }else{ //miss
	    	    
	    	       if(isFull(index)){
	    	    	   CacheEntry e  = block.get(0);
	                    if(e.isDirty()){
	                        memory.WriteLine(l/2,reverseToAdress(index, e.getTag()), e.getLine().getWords());
	                      
	    	    		    e.setDirty(false);
	    	    		   
	                    }   
	                       
	                        e.setTag(addr.getTag());
	                        e.setValid(true);
	                        return writeData(data, address);
		    		        
	    	       }
	    	       else{
	    	    	for (int i = 0; i < m; i++) {
	    	    		CacheEntry en = block.get(i);
	    	    		if(!en.isValid()){
	    	    			   
	    	    			  if(WMP == WMP.WRITE_ALLOCATE){
	    	    				  
	    	    				  en.setValid(true);
	    	    				  en.setTag(addr.getTag());
	    	    	    		  return 2 ;  
	    	    	    		  
	    	    	    		  
	    	    	    	  }else{ //write-around
	    	    	    		  memory.write(address, data);
	    	    	    		  return 0 ;
	    	    	    		  
	    	    	    	  }
	    	    		}
	    	    		
	    	    		
					}
	    	    	return 6 ;
	    	       }
	    	
	    	  
	      }
}
  
public CacheEntry check(short address ) {
	   MemoryAddress addr = parseMemoryAddress(address); 
	   int addrTag = addr.getTag();
	   ArrayList<CacheEntry> cacheEntries = blocks.get(addr.getIndex());
	   for (int i = 0; i < m; i++) {
		   CacheEntry ent = cacheEntries.get(i) ;
		if(ent.isValid() && addrTag == ent.getTag()){
			return ent;
		}
	}
	   
	return null  ;
	
}

public MemoryAddress parseMemoryAddress(int address){ 
	       int indexSize = getIndexSize();
	       int tagSize   = getTagSize();
	       int offsetSize= getOffsetSize();
	       
	       int i = 0 ;
	       int k = 0 ;
	       int j = 0 ;
	       
	       int offset = 0;
	       int index  = 0;
	       int tag    = 0;
	       
	       while( address>=1) {
	    	    int mod  = address%2 ;
	    	        if(offsetSize>0){
	    	        	offset +=  mod*Math.pow(2,k);
	    	        	offsetSize -= 1 ;
	    	        	k++;
	    	        }else{
	    	        	if(indexSize >0){
	    	        		index += mod*Math.pow(2,i);
	    	        		indexSize -= 1 ;
	    	        		i++ ;
	    	        	}else{
	    	        		if(tagSize >0){
	    	        			tag += mod*Math.pow(2,j);
	    	        			tagSize -= 1 ;
	    	        			j++ ;
	    	        		}
	    	        	}
	    	        }
			     
			   address /= 2 ;
			     
		}

	  return  (new MemoryAddress(tag,index,offset));
	  
}
public short reverseToAdress(int index ,int tag){
	return (short)((tag*Math.pow(2, getIndexSize())+index));
}

public int getIndexSize(){
	int res = (s/(l*m));
	return  (int) Math.ceil(Math.log10(res)/Math.log10(2)) ;
	 
}

public int getTagSize(){
	return 16 - (getIndexSize()+getOffsetSize());
}

public int getOffsetSize(){
	return (int) Math.ceil(Math.log10(l/2)/Math.log10(2)) ;
}
public boolean isFull(int index){
	ArrayList<CacheEntry> list = blocks.get(index);
	for (int i = 0; i < list.size(); i++) {
		    CacheEntry c = list.get(i);
		if(!c.isValid())return false ;
	}
	return true ;
}
  
  
  public int getS() {
	return s;
}

public void setS(int s) {
	this.s = s;
}

public int getL() {
	return l;
}

public void setL(int l) {
	this.l = l;
}

public int getM() {
	return m;
}

public void setM(int m) {
	this.m = m;
}

public int getC() {
	return cycles;
}

public void setC(int c) {
	this.cycles = c;
}


public int getCycles() {
	return cycles;
}


public void setCycles(int cycles) {
	this.cycles = cycles;
}


public WriteHitPolicy getWHP() {
	return WHP;
}


public void setWHP(WriteHitPolicy wHP) {
	WHP = wHP;
}


public WriteMissPolicy getWMP() {
	return WMP;
}


public void setWMP(WriteMissPolicy wMP) {
	WMP = wMP;
}


public TreeMap<Integer, ArrayList<CacheEntry>> getBlocks() {
	return blocks;
}


public void setBlocks(TreeMap<Integer, ArrayList<CacheEntry>> blocks) {
	this.blocks = blocks;
}


public MainMemory getMemory() {
	return memory;
}


public void setMemory(MainMemory memory) {
	this.memory = memory;
}

public double getAccess() {
	return access;
}


public void setAccess(int access) {
	this.access = access;
}


public double getHit() {
	return hit;
}
public double getHitRatio(){
	access = access > 0 ? access : 1;
	return hit/access ;
}
public double getMissRatio() {
	return (access-hit)/access;
}


public void setHit(int hit) {
	this.hit = hit;
}
public void printCache(){
	 System.out.println("///////////CACHE////////////////////////");
     Iterator myIt =   blocks.keySet().iterator();
      int c= 0 ;
     while(myIt.hasNext()){
    	ArrayList<CacheEntry> curr = (ArrayList<CacheEntry>)blocks.get(myIt.next());
    	for (int i = 0; i < curr.size(); i++) {
			 System.out.println(i+":- "+curr.get(i).toString());
		}
    	
    	System.out.println("//////////////////////////////////////////"+c);
    	c++ ;
     }
}

}

