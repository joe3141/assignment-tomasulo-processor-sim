package cache;

public class CacheEntry {
       private  boolean valid  ;
       private  boolean dirty  ;
       private  int  tag   ;
       private  CacheLine line ;
       
       
       




	public CacheEntry(boolean valid ,boolean dirty ,int tag ,CacheLine line) {
 	   this.valid  = valid  ;
 	   this.dirty  = dirty ;
 	   this.tag    = tag   ;
 	   this.line   = line  ;
	}
	
	
	public boolean isValid() {
		return valid;
	}



	public void setValid(boolean valid) {
		this.valid = valid;
	}



	public boolean isDirty() {
		return dirty;
	}



	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
       


	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public CacheLine getLine() {
		return line;
	}

	public void setLine(CacheLine line) {
		this.line = line;
		this.valid = true ;
	}
	public String  toString(){
		
		return  "isValid = "+isValid()+" ,isDirty = "+isDirty()+" ,Tag = "+getTag()+" ,data = "+line.toString();
	}


       
       
}
