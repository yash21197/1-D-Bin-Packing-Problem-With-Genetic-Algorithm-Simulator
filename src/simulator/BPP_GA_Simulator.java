package simulator;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class BPP_GA_Simulator {
	
	private static int capacity;
	private static int no_items;
	private static int items[];
	private static int population_size;
	private static int generation;
	private static Chromosome chromosomes[];
	private static Fitness fit;
	private static int global_minimum_bins;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("********  Simulator Start ********");
		System.out.println();
		
		System.out.print("Insert Bin Capacity : ");
		capacity = sc.nextInt();
		sc.nextLine();
		System.out.println();
		
		System.out.println("==> DATA Set :");
		System.out.println("A> Random");
		System.out.println("B> Manual");
		System.out.print("Insert Choise : ");
		char ch = sc.nextLine().charAt(0);
		System.out.println();
		
		switch(ch){
		case 'A' | 'a' : 
			randomDataGenerator();
			break;
		case 'B' | 'b' :
			manualDataGenerator();
			break;
		default :
			System.out.println("Insert Valid Choise.");
			sc.close();
			return ;
		}
		
		System.out.print("Lower Bound : ");
		System.out.println(calculateLowerBound());
		System.out.println();
		
		System.out.print("Insert Number Of Population : ");
		population_size = sc.nextInt();
		System.out.print("Insert Number Of Generation : ");
		generation = sc.nextInt();
		System.out.println();
		
		fit = new FirstFitSimulator(capacity);
		createChromosomes(fit);
		global_minimum_bins = getMinimumBinsRequired();
		System.out.println("Minimum Bins Required : " + global_minimum_bins);
		System.out.println();
		
		System.out.println("We Are Using 2-Tournament Selection For Generateing New Generation.");
		System.out.println();
		
		while(generation!=0){
			// new generation creating
			System.out.println("Generation : " + generation);
			generation--;
			chromosomes = createNewGeneration();
			printPopulation();
			int local_minimum_bins = getMinimumBinsRequired();
			if(local_minimum_bins<global_minimum_bins){
				global_minimum_bins=local_minimum_bins;
			}
			System.out.println("Minimum Bins Required : " + local_minimum_bins);
			System.out.println();
		}
		
		System.out.println("Finaly Minimum Bins Required : " + global_minimum_bins);
		
		sc.close();
	}
	
	private static void randomDataGenerator(){
		Scanner sc = new Scanner(System.in);
		Random ran = new Random();
		
		System.out.println("==> Random Data Generator");
		
		System.out.print("No. Of Items : ");
		no_items = sc.nextInt();
		items = new int[no_items];
		
		for(int i=0;i<no_items;i++){
			items[i] = ran.nextInt(capacity)+1;
			System.out.print(items[i] + "  ");
		}
		System.out.println();
		System.out.println();
	}
	
	private static void manualDataGenerator(){
		Scanner sc = new Scanner(System.in);
		
		System.out.println("==> Manual Data Generator");
		
		System.out.print("No. Of Items : ");
		no_items = sc.nextInt();
		items = new int[no_items];
		System.out.print("Insert Items' Size : ");
		for(int i=0;i<no_items;i++){
			items[i] = sc.nextInt();
		}
		System.out.println();
	}
	
	private static int calculateLowerBound(){
		int sum=0;
		for(int i=0;i<items.length;i++){
			sum+=items[i];
		}
		return (int)Math.ceil(sum/capacity);
	}
	
	private static void createChromosomes(Fitness fit){
		System.out.println("==> Creating Chromosomes By Random Permutations And Counting Its Fitness Is Started.");
		chromosomes = new Chromosome[population_size];
		int index_permutation[] = items;
		for(int i=0;i<chromosomes.length;i++){
			index_permutation = nextPermutation(index_permutation);
			chromosomes[i] = new Chromosome(index_permutation,fit);
		}
		printPopulation();
		System.out.println("==> Creating Chromosomes By Random Permutations And Counting Its Fitness Is Overed.");
	}
	
	private static int[] nextPermutation(int main_permutation[]){
		Random ran = new Random();
		int new_permutation[] = new int[main_permutation.length];
		for(int i=0;i<new_permutation.length;i++){
			new_permutation[i] = main_permutation[i];
		}
		for(int i=0;i<new_permutation.length;i++){
			int pos = ran.nextInt(new_permutation.length);
			int a = new_permutation[i];
			new_permutation[i] = new_permutation[pos];
			new_permutation[pos] = a;
		}
		return new_permutation;
	}
	
	private static void printPopulation(){
		for(int i=0;i<population_size;i++){
			for(int j=0;j<chromosomes[i].permutation.length;j++){
				System.out.print(chromosomes[i].permutation[j] + "  ");
			}
			System.out.println("Fitness : " + chromosomes[i].fitness);
		}
	}
	
	private static Chromosome[] createNewGeneration(){
		Random ran = new Random();
		Chromosome new_generation[] = new Chromosome[population_size];
		int new_population_size = population_size;
		
		while(new_population_size!=0){
			// parent selection
			new_population_size--;
			Chromosome parent[] = new Chromosome[2];
			int parent0 = ran.nextInt(population_size);
			int parent1 = ran.nextInt(population_size);
			parent[0] = (chromosomes[parent0].fitness <= chromosomes[parent1].fitness) ? chromosomes[parent0] : chromosomes[parent1];
			parent[1] = (chromosomes[parent0].fitness <= chromosomes[parent1].fitness) ? chromosomes[parent1] : chromosomes[parent0];
			// crossover and offspring
			int new_permutation[] = new int[no_items];
			HashMap<Integer , Integer> parent0_state = new HashMap<>();
			HashMap<Integer , Integer> parent1_state = new HashMap<>();
			for(int i=0;i<parent[0].permutation.length;i++){
				if(!parent0_state.containsKey(parent[0].permutation[i])){
					parent0_state.put(parent[0].permutation[i], 1);
					parent1_state.put(parent[0].permutation[i], 1);
				}else{
					parent0_state.put(parent[0].permutation[i], parent0_state.get(parent[0].permutation[i])+1);
					parent1_state.put(parent[0].permutation[i], parent1_state.get(parent[0].permutation[i])+1);
				}
			}
			int index=0;
			for(int i=0;i<parent[0].permutation.length;i++){
				if(parent0_state.get(parent[0].permutation[i]) <= parent1_state.get(parent[0].permutation[i])){
					new_permutation[index] = parent[0].permutation[i];
					index++;
				}
				parent0_state.put(parent[0].permutation[i], parent0_state.get(parent[0].permutation[i])-1);
				if(parent0_state.get(parent[1].permutation[i]) >= parent1_state.get(parent[1].permutation[i])){
					new_permutation[index] = parent[1].permutation[i];
					index++;
				}
				parent1_state.put(parent[1].permutation[i], parent1_state.get(parent[1].permutation[i])-1);
			}
			//mutation
			int pos0 = ran.nextInt(new_permutation.length);
			int pos1 = ran.nextInt(new_permutation.length);
			int temp = new_permutation[pos0];
			new_permutation[pos0] = new_permutation[pos1];
			new_permutation[pos1] = temp;
			// create new chromosome
			Chromosome new_chromosome = new Chromosome(new_permutation, fit);
			new_generation[new_population_size] = new_chromosome;
		}
		return new_generation;
	}
	
	private static int getMinimumBinsRequired(){
		int min_bins=Integer.MAX_VALUE;
		for(int i=0;i<chromosomes.length;i++){
			if(chromosomes[i].fitness < min_bins){
				min_bins = chromosomes[i].fitness;
			}
		}
		return min_bins;
	}
}