package simulator;

public class FirstFitSimulator extends Fitness {
	
	public FirstFitSimulator(int cap){
		super();
		this.capacity = cap;
	}
	
	public int getMinimumBins(int per[]){
		
		int bins_space[] = new int[per.length];
		int bins=0;
		
		for(int i=0;i<bins_space.length;i++){
			bins_space[i] = this.capacity;
		}
		
		for(int i=0;i<per.length;i++){
			int j;
			for(j=0;j<bins;j++){
				if(bins_space[j] >= per[i]){
					bins_space[j] -= per[i];
					break;
				}
			}
			if(j==bins){
				bins++;
				bins_space[bins-1] -= per[i];
			}
		}
		
		return bins;
	}
	
}
