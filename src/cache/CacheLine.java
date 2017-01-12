package cache;



public class CacheLine {
       
       private  Object[] words ;
       

	public CacheLine (int size){
		   
    	   words = new Object[size];
       }
	
	public Object readWord(int x){
		
		  return words[x] ;
	}
	public void writeWord(int x,Object data){
	     
		   words[x] = data ;
	}
	



	public Object[] getWords() {
		return words;
	}



	public void setWords(Object[] words) {
		this.words = words;
	}
	public String toString(){
        
		String res="" ;
		for (int i = 0; i < words.length; i++) {
			if(i == words.length-1)
				res += words[i]+"" ;
			else
			    res += words[i]+" , " ;
		}
		
		return res ;
	}
       
}
