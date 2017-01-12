package reorder_buffer;

public class ROBEntry {
	private InstructionType type; //
	private int destination;
	private short value;
	private boolean ready;
	private int id;
	
	public ROBEntry(){
		value = 0;
		ready = false;
	}
	
	public boolean equals(ROBEntry r){
		return this.destination ==  r.destination;
	}
	
	public ROBEntry(InstructionType type, int destination){
		this();
		this.type = type;
		this.destination = destination;
	}

	public InstructionType getType() {
		return type;
	}

	public void setType(InstructionType type) {
		this.type = type;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public short getValue() {
		return value;
	}

	public void setValue(short value) {
		this.value = value;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public void reset(){
		ready = false;
		value = 0;
		destination = 0;
		type = null;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
}
