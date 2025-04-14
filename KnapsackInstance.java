import java.util.*;

public class KnapsackInstance implements java.io.Closeable
{
	private int itemCnt; //Number of items
	private int cap; //The capacity
	private int[] weights; //An array of weights
	private int[] values; //An array of values
	private int[] sortedWeights;
	private int[] sortedValues;
	private int[] sortedToOriginal;
	private int[] originalToSorted;
	private ArrayList<Item> VWList; //An array of values / weights
	private int totalvalue = 0;

	//Nested class for fractional knapsack solver
	private class Item {
		private int itemNum;
		private float VW; //value over weight
		
		public Item(int itemNum, float VW){
			this.itemNum = itemNum;
			this.VW = VW;
		}

		public int getItemNum(){
			return this.itemNum;
		}

		public void setItemNum(int itemNum){
			this.itemNum = itemNum;
		}

		public float getVW(){
			return this.VW;
		}

		public void setVW(float VW){
			this.VW = VW;
		}
	}

	public KnapsackInstance(int itemCnt_)
	{
		itemCnt = itemCnt_;

		weights = new int[itemCnt + 1];
		values = new int[itemCnt + 1];
		sortedWeights = new int[itemCnt + 1];
		sortedValues = new int[itemCnt + 1];
		VWList = new ArrayList<Item>();
		cap = 0;
	}
	public void close()
	{
		weights = null;
		values = null;
	}

	public void Generate()
	{
		int i;
        int wghtSum;

		weights[0] = 0;
		values[0] = 0;

		wghtSum = 0;
		for(i=1; i<= itemCnt; i++)
		{
			weights[i] = Math.abs(RandomNumbers.nextNumber()%100 + 1);
			values[i] = weights[i] + 10;
			totalvalue += values[i];
			wghtSum += weights[i];
		}
		cap = wghtSum/2;
	}

	//Sorts by value / weight as a preprocessing step for UB3
	public void sortByValueOverWeight(){
		VWList.add(new Item(0, 0.0f));

		for (int i = 1; i <= itemCnt; i++){
			VWList.add(new Item(i, (float) values[i]/weights[i]));
			//System.out.println("\nVWList Length: " + VWList.size() + "\nAdding Item # " + VWList.get(i).getItemNum() + "\nV/W of " + VWList.get(i).getVW());
		}

		quicksort(VWList, 1, itemCnt);
		/*
		System.out.println("\n\nORIGINAL LIST: ");

		for (int i = 1; i <= itemCnt; i++){
			System.out.print(i + " ");
		}
		
		System.out.println("\n\nSORTED LIST: ");

		for (int i = 1; i <= itemCnt; i++){
			System.out.print(VWList.get(i).getItemNum() + " ");
		}
		*/
		sortedToOriginal = new int[itemCnt+1];
		originalToSorted = new int[itemCnt+1];
		int index;

		for (int i = 1; i <= itemCnt; i++){
			index = VWList.get(i).getItemNum();
			sortedWeights[i] = weights[index];
			sortedValues[i] = values[index];
			sortedToOriginal[i] = index;
			originalToSorted[index] = i;
		}
	}

	public void quicksort(ArrayList<Item> array, int lo, int hi){
		if (lo >= hi) return;

		Item pivot = array.get(hi);
		int i = lo;
		Item temp;

		for (int j = lo; j < hi; j++){
			if (array.get(j).getVW() > pivot.getVW()){ //Descending
				temp = array.get(i);
				array.set(i, array.get(j));
				array.set(j, temp);
				i++;
			}
		}
		temp = array.get(i);
		array.set(hi, temp);
		array.set(i, pivot);

		quicksort(array, lo, i-1);
		quicksort(array, i+1, hi);
	}

	public KnapsackSolution FractionalKnapsack(){
		KnapsackSolution UpperBound = new KnapsackSolution(this);
		int capacity = cap;

		for (int i = 1; i <= itemCnt; i++){
			if (sortedWeights[i] <= capacity){
				capacity -= sortedWeights[i];
				UpperBound.TakeItem(GetSortedToOriginal(i));
			}
			else {
				break;
			}
		}

		return UpperBound;
	}
	public float GetValueOverWeight(int itemNum)
	{
		return VWList.get(itemNum).getVW();
	}
	public int GetSortedToOriginal(int itemNum){
		return sortedToOriginal[itemNum];
	}
	public int GetOriginalToSorted(int itemNum){
		return originalToSorted[itemNum];
	}
	public int GetItemCnt()
	{
		return itemCnt;
	}
	public int GetTotalValue(){
		return totalvalue;
	}
	public int GetItemWeight(int itemNum)
	{
		return weights[itemNum];
	}
	public int GetItemValue(int itemNum)
	{
		return values[itemNum];
	}
	public int GetItemSortedWeight(int itemNum)
	{
		return sortedWeights[itemNum];
	}
	public int GetItemSortedValue(int itemNum)
	{
		return sortedValues[itemNum];
	}
	public int GetCapacity()
	{
		return cap;
	}
	public void Print()
	{
		int i;

		System.out.printf("Number of items = %d, Capacity = %d\n",itemCnt, cap);
		System.out.print("Weights: ");
		for (i = 1; i <= itemCnt; i++)
		{
			System.out.printf("%d ",weights[i]);
		}
		System.out.print("\nValues: ");
		for (i = 1; i <= itemCnt; i++)
		{
			System.out.printf("%d ",values[i]);
		}
		System.out.print("\n");
	}
}
