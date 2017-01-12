package cache;

 public class MemoryAddress {
	   //offset is zero
        private int index  ; 
        private int tag    ;
        private int offset ;
    

		public MemoryAddress(int tag ,int index,int offset){
        	  
        	   this.index  = index  ;
        	   this.tag    = tag    ;
        	   this.offset = offset ;
         }

		
	

		public int getIndex() {
			return index;
		}

		public int getTag() {
			return tag;
		}
		
		
		public int getOffset(){
			return offset ;
		}


	
		
	     
      
         
      
}

