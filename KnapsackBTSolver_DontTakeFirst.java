import java.util.*;

// Backtracking solver
public class KnapsackBTSolver_DontTakeFirst extends KnapsackBFSolver
{
	protected KnapsackInstance inst;
	protected KnapsackSolution crntSoln;
	protected KnapsackSolution bestSoln;

	int itemCnt; //Total # of items in problem
	int capacity;

	//Recursively tries all combinations of items
	public void FindSolns(int itemNum, int load)
	{
		if (itemNum == itemCnt + 1) //If all items have been checked, evaluate the solution
		{
			CheckCrntSoln(); //Check if current solution is valid & better than the best
			return;
		}


		//DONT TAKE is first, THEN it does TAKE
		//Each item has 2 choices; take, or don't take
		crntSoln.DontTakeItem(itemNum); 
		FindSolns(itemNum + 1, load); //Go to next item and repeat

		load += inst.GetItemWeight(itemNum); //Keep track of load so far in knapsack

		if (load > capacity) return; //If load + 

		crntSoln.TakeItem(itemNum);
		FindSolns(itemNum + 1, load); //Go to next item and repeat
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

	//Constructor for the solver
	public KnapsackBTSolver_DontTakeFirst()
	{
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
		FindSolns(1, 0);
	}
}