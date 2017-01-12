package cache;


public class CacheSpecs {
	private int s, l ,m;
	private boolean policy; //0 => write back, 1 => write through
	private int latency;
	
	public CacheSpecs(int s, int l, int m, boolean policy, int latency){
		this.l = l;
		this.s = s;
		this.m = m;
		this.policy = policy;
		this.latency = latency;
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
	public boolean isPolicy() {
		return policy;
	}
	public void setPolicy(boolean policy) {
		this.policy = policy;
	}
	public int getLatency() {
		return latency;
	}
	public void setLatency(int latency) {
		this.latency = latency;
	}
}
