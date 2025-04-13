
// Branch-and-Bound solver
public class KnapsackBBSolver extends KnapsackBFSolver
{
	protected UPPER_BOUND ub;
	protected KnapsackInstance inst;
	protected KnapsackSolution crntSoln;
	protected KnapsackSolution bestSoln;
	protected KnapsackSolution upperBound;

	int itemCnt; //Total # of items in problem
	int capacity;
	int totalvalue;
	
	//Recursively tries all combinations of items
	public void FindSolnsUB1(int itemNum, int load, int untaken_value)
	{
		if (itemNum == itemCnt + 1) //If all items have been checked, evaluate the solution
		{
			CheckCrntSoln(); //Check if current solution is valid & better than the best
			return;
		}
		else if (totalvalue - untaken_value <= bestSoln.GetValue()){ //If best possible in this subtree isn't > than best solution, don't search
			return;
		}

		//DONT TAKE is first, THEN it does TAKE
		//Each item has 2 choices; take, or don't take
		crntSoln.DontTakeItem(itemNum);
		FindSolnsUB1(itemNum + 1, load, untaken_value + inst.GetItemValue(itemNum)); //Go to next item and repeat

		load += inst.GetItemWeight(itemNum); //Keep track of load so far in knapsack

		if (load > capacity) return; 

		crntSoln.TakeItem(itemNum);
		FindSolnsUB1(itemNum + 1, load, untaken_value); //Go to next item and repeat

		crntSoln.DontTakeItem(itemNum); //Backtrack
	}

	//Recursively tries all combinations of items
	//UB2: the sum of taken item values and the values of the undecided items that fit in the 
	//remaining capacity at each node. As described in class, this can be computed in O(n) time 
	//at each node by checking all the remaining items and adding the value of every item whose 
	//weight is less than or equal to the remaining capacity.
	public void FindSolnsUB2(int itemNum, int load, int current_value)
	{
		if (itemNum == itemCnt + 1) //If all items have been checked, evaluate the solution
		{
			CheckCrntSoln(); //Check if current solution is valid & better than the best
			return;
		}

		int remainingCapacity = capacity - load;
		int upperbound = current_value;

		for (int i = itemNum; i <= itemCnt; i++){
			if (inst.GetItemWeight(i) <= remainingCapacity)
				upperbound += inst.GetItemValue(i);
		}

		if (upperbound <= bestSoln.GetValue()){ //If current value + undecided items that fit values aren't better than best, skip
			return;
		}

		//DONT TAKE is first, THEN it does TAKE
		//Each item has 2 choices; take, or don't take
		crntSoln.DontTakeItem(itemNum); 
		FindSolnsUB2(itemNum + 1, load, current_value); //Go to next item and repeat

		int itemvalue = inst.GetItemValue(itemNum);
		int itemweight = inst.GetItemWeight(itemNum);

		if (load + itemweight > capacity) return;

		crntSoln.TakeItem(itemNum);
		FindSolnsUB2(itemNum + 1, load + itemweight, current_value + itemvalue); //Go to next item and repeat

		crntSoln.DontTakeItem(itemNum);
	}

	//Recursively tries all combinations of items
	//UB3: solve the remaining sub-problem at each node as a Fractional Knapsack problem.
	//Can be computed in O(n) time at each node if you sort the items before you start the search (in the preprocessing step).
	public void FindSolnsUB3(int itemNum, int load, int current_value)
	{
		if (itemNum == itemCnt + 1) //If all items have been checked, evaluate the solution
		{
			CheckCrntSoln(); //Check if current solution is valid & better than the best
			return;
		}

		int remainingCapacity = capacity - load;
		float upperbound = current_value;

		crntSoln.ComputeValue();

		//Upper bound calculation using fractional knapsack
		for (int i = itemNum; i <= itemCnt; i++){
			if (inst.GetOriginalToSorted(i) >= itemNum && inst.GetItemSortedWeight(inst.GetOriginalToSorted(i)) <= remainingCapacity){
				remainingCapacity -= inst.GetItemWeight(i);
				upperbound += inst.GetItemValue(i);
			}
			else {
				upperbound += (inst.GetValueOverWeight(inst.GetOriginalToSorted(itemNum))) * remainingCapacity;
				break;
			}
		}

		if (upperbound <= bestSoln.GetValue()){ //If best possible w/ fractional isn't better than best so far, skip
			return;
		}

		int actualIndex = inst.GetSortedToOriginal(itemNum);

		//DONT TAKE is first, THEN it does TAKE
		//Each item has 2 choices; take, or don't take
		crntSoln.DontTakeItem(itemNum); 
		FindSolnsUB3(itemNum + 1, load, current_value); //Go to next item and repeat

		load += inst.GetItemWeight(itemNum); //Keep track of load so far in knapsack

		if (load > capacity) return; 

		crntSoln.TakeItem(itemNum);
		FindSolnsUB3(itemNum + 1, load, current_value + inst.GetItemValue(itemNum)); //Go to next item and repeat
		
		crntSoln.DontTakeItem(itemNum); 
	}
	
	//Checks if current solution is valid & if best so far
	public void CheckCrntSoln()
	{
		int crntVal = crntSoln.ComputeValue(); //Calculate total value of current selection
		System.out.print("\nChecking solution ");
		crntSoln.Print(" ");

		if (crntVal == DefineConstants.INVALID_VALUE) //If value is invalid aka = -1 / over capacity, skip it
		{
			return;
		}
	
		if (bestSoln.GetValue() == DefineConstants.INVALID_VALUE) //The first solution is initially the best solution
		{
			if (upperBound != null)
				bestSoln = upperBound;
			else
				bestSoln.Copy(crntSoln);
		}
		else
		{
			if (crntVal > bestSoln.GetValue())
			{
				bestSoln.Copy(crntSoln);
			}
		}
	}
	
	//Constructor for the solver
	public KnapsackBBSolver(UPPER_BOUND ub_)
	{
		ub = ub_;
		crntSoln = null;
	}

	//Cleans up crntSoln reference
	public void close()
	{
		if (crntSoln != null)
		{
			crntSoln = null;
		}
	}

	//Begin recursion from item #1
	public void Solve(KnapsackInstance inst_, KnapsackSolution soln_)
	{
		inst = inst_;
		bestSoln = soln_;
		crntSoln = new KnapsackSolution(inst);
		itemCnt = inst.GetItemCnt(); //Total # of items in problem
		capacity = inst.GetCapacity();
		totalvalue = inst.GetTotalValue();

		if (ub == UPPER_BOUND.UB1)
			FindSolnsUB1(1, 0, 0);
		else if (ub == UPPER_BOUND.UB2)
			FindSolnsUB2(1, 0, 0);
		else if (ub == UPPER_BOUND.UB3){
			inst.sortByValueOverWeight();
			upperBound = inst.FractionalKnapsack();
			//bestSoln = upperBound;
			//bestSoln.ComputeValue();
			FindSolnsUB3(1, 0, 0);
		}
		else
			return;
	}
}