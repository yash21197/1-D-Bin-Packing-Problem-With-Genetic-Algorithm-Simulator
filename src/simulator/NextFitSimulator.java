package simulator;

public class NextFitSimulator extends Fitness {
	
	public NextFitSimulator(int cap){
		super();
		this.capacity = cap;
	}
	
	public int getMinimumBins(int per[]){
		int bins=0;
		int space=0;
		
		for(int i=0;i<per.length;i++){
			if(per[i]<=space){
				space -= per[i];
			}else{
				bins++;
				space = this.capacity;
				space -= per[i];
			}
		}
		
		return bins;
	}
}
