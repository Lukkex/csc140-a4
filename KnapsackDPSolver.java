import java.util.*;

// Dynamic programming solver
public class KnapsackDPSolver implements java.io.Closeable
{
	protected UPPER_BOUND ub;
	protected KnapsackInstance inst;
	protected KnapsackSolution crntSoln;
	protected KnapsackSolution bestSoln;

	int itemCnt; //Total # of items in problem
	int[][] t;
	int capacity;
	int totalvalue;

	public KnapsackDPSolver()
	{
		crntSoln = null;
	}

	public void FindSolns(int itemNum, int load, int untaken_value)
	{
		for (int j = 0; j <= capacity; j++){
			t[0][j] = 0;
		}

		for (int i = 1; i <= itemCnt; i++){
			for (int j = 0; j <= capacity; j++){
				if (inst.GetItemWeight(i) > j) //Item doesn't fit
					t[i][j] = t[i-1][j]; //Take solution from prev row
				else
					t[i][j] = Math.max((inst.GetItemValue(i) + t[i-1][j - inst.GetItemWeight(i)]), t[i-1][j]);
			}
		}

		int remainingCapacity = capacity;
		//Decide for items
		for (int i = itemCnt; i >= 1; i--){
			if (t[i][remainingCapacity] > t[i-1][remainingCapacity]){
				crntSoln.TakeItem(i);
				remainingCapacity -= inst.GetItemWeight(i);
			}
		}

		CheckCrntSoln();
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

	public void close()
	{
		if (crntSoln != null)
		{
			crntSoln = null;
		}
	}
	public void Solve(KnapsackInstance inst_, KnapsackSolution soln_)
	{
		inst = inst_;
		bestSoln = soln_;
		crntSoln = new KnapsackSolution(inst);
		itemCnt = inst.GetItemCnt(); //Total # of items in problem
		capacity = inst.GetCapacity();
		t = new int[itemCnt+1][capacity+1];

		FindSolns(1, 0, 0);
	}
}