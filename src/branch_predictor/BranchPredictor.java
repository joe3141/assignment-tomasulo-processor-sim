package branch_predictor;

import java.util.Arrays;

public class BranchPredictor {
	
	// 0 => static/basic prediction(the one in the description)
	// 1 => Saturating counter
	// 2 => One level adaptive predictor
	private int mode = 0;
	private int satuCounter = 1;
	private byte[] pht;
	
	public BranchPredictor(int mode){
		this.mode = mode;
		satuCounter = 1;
		pht = mode == 2 ? new byte[1<<8] : pht;
		if(mode == 2)
			Arrays.fill(pht, (byte) 1);
	}
	
	public boolean speculate(short address){
		switch(mode){
		case 0:
			return address < 0;
		case 1:
			return saturatingCounter();
		case 2:
			return adaptive(address);
		default:
			return false;
		}
	}
	
	public void update(boolean taken, short address){
		switch(mode){
		case 0:
			return;
		case 1:
			satuCounter = taken ? Math.max(3, satuCounter + 1) : Math.min(0, satuCounter - 1); 
		case 2:
			byte index = indexify(address);
			pht[index] = (byte) (taken ? Math.max(3, pht[index] + 1) : Math.min(0, pht[index] - 1));
		default:
			return;
		}
	}
	
	private boolean saturatingCounter(){
		return satuCounter > 1;
	}
	
	private boolean adaptive(short address){
		return pht[indexify(address)] > 1;
	}
	
	private byte indexify(short address){
		return (byte) (address / (1<<8));
	}
	
}
