import java.util.*;

// Brute-force solver
public class KnapsackBFSolver implements java.io.Closeable
{
	protected KnapsackInstance inst;
	protected KnapsackSolution crntSoln;
	protected KnapsackSolution bestSoln;

	//Recursively tries all combinations of items
	public void FindSolns(int itemNum)
	{
		int itemCnt = inst.GetItemCnt(); //Total # of items in problem
    
		if (itemNum == itemCnt + 1) //If all items have been checked, evaluate the solution
		{
			CheckCrntSoln(); //Check if current solution is valid & better than the best
			return;
		}

		//TAKE, then DON'T TAKE
		//Each item has 2 choices; take, or don't take
		crntSoln.TakeItem(itemNum);
		FindSolns(itemNum + 1); //Go to next item and repeat
		crntSoln.DontTakeItem(itemNum); 
		FindSolns(itemNum + 1); //Go to next item and repeat
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
	public KnapsackBFSolver()
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
		FindSolns(1);
	}
}