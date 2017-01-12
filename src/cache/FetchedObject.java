package cache;

public class FetchedObject {
        private int cycles ;

		private Object data ;
        
        public FetchedObject(int c , Object data) {
        	cycles = c ;
        	this.data = data ;
		}
        
        public int getCycles() {
			return cycles;
		}

		public void setCycles(int cycles) {
			this.cycles = cycles;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
}
