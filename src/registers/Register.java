package registers;

public class Register {
	private short data;
	
	public Register(short data){
		this.data = data;
	}
	
	public short getData(){
		return this.data;
	}
	
	public void setData(short input) {
		this.data = input;
	}
	
}
