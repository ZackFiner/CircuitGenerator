package circuit_designer;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

public class CompoundCircuit {
	private class circuitTreeBuilder {
		private class ConnectionIterator{
			private ArrayList<Integer> indeces;
			private ArrayList<Gate>lstC;
			private ArrayList<Gate>lstG;
			private int counter=0;
			private Gate lastSelected=null;
			public ArrayList<Integer> ArrayMixOrder(int upTo)
			{
				ArrayList<Integer> r = new ArrayList<Integer>();
				for (int i = 0; i < upTo; i++)
					r.add(i);
				Collections.shuffle(r);
				return r;
			}
			public ConnectionIterator(int upTo, ArrayList<Gate> lastColumn, ArrayList<Gate> lastGates)
			{
				indeces = ArrayMixOrder(upTo);
				lstC = lastColumn;				
				lstG = lastGates;
			}
			public Gate next()
			{
				Gate selected = null;
				if (counter < indeces.size()) {//select all the connections in the previous row
					selected = lstC.get(indeces.get(counter++));
				}
				else 
				{
					selected = lstG.get(indeces.get((int)(Math.random()*indeces.size())));//then choose a wild card connection
					while (selected == lastSelected)
					{
						selected = lstG.get(indeces.get((int)(Math.random()*indeces.size())));//can't pick the same two twice
					}
				}
				lastSelected = selected;
				return selected;
			}
		}
		public ArrayList<ArrayList<Gate>> columns;
		public Gate buildSequence(int inputSize, ArrayList<Gate> starters)
		{
			/*
			 * So what we're doing here, is we're building a tree from the root
			 * and we want to make it in such a way that it has a specific amount of leaves (our input size)
			 * but the internal structure of the tree should be random
			 * 
			 * Strategy:
			 * 1. Start with your input gates
			 * 2. create a row of gates in front of those gates. then decide how
			 * to connect them to gates in the previous rows (it's okay to have the output of gates reference multiple times).
			 * 3. repeat this process, decreasing the size of each row, until there is only one gate left. 
			 * 
			 * Just remember that there needs to be at least enough needed inputs as there are gates in the last row
			 * 
			 * We could add another pass to add Not gates in certain areas.
			 * 
			 * 
			*/
			Random rng = new Random();
			int gateCount = inputSize-(rng.nextInt(2)+1);
			ArrayList<ArrayList<Gate>> columns = new ArrayList<ArrayList<Gate>>();
			ArrayList<Gate> lastColumn = starters;
			ArrayList<Gate> previousGates = new ArrayList<Gate>();
			previousGates.addAll(starters);
			int gateIndex = 0;
			if (inputSize <=1)
			{
				System.err.println("ERROR: YOU MUST HAVE ATLEAST 2 INPUTS");
				return null;
			}
			do //we terminate once we're down to one gate
			{
				ArrayList<Gate> c_col = new ArrayList<Gate>();
				int neededInputs = 0;
				while (neededInputs < lastColumn.size()) // gate generation phase
				{
					switch(rng.nextInt(3))
					{
					case 0:
						NotGate Ngate = new NotGate(null);
						Ngate.setIndex(gateIndex++);
						c_col.add(Ngate);
						neededInputs += 1;
						break;
					case 1:
						AndGate Agate = new AndGate(null,null);
						Agate.setIndex(gateIndex++);
						c_col.add(Agate);
						neededInputs += 2;
						break;
					case 2:
						OrGate Ogate = new OrGate(null,null);
						Ogate.setIndex(gateIndex++);
						c_col.add(Ogate);
						neededInputs += 2;
						break;
					}
				}
				
				
				ConnectionIterator iter = new ConnectionIterator(lastColumn.size(), lastColumn, previousGates);
				for (Gate g: c_col) // gate link phase
				{
					if (g instanceof AndGate)
					{
						AndGate g_o = (AndGate)g;
						g_o.setOp1( iter.next() );
						g_o.setOp2( iter.next() );
					}
					else if (g instanceof OrGate)
					{
						OrGate g_o = (OrGate)g;
						g_o.setOp1( iter.next() );
						g_o.setOp2( iter.next() );
					}
					else if (g instanceof NotGate)
					{
						NotGate g_o = (NotGate)g;
						g_o.setOp( iter.next() );
					}
				}
				columns.add(c_col);
				lastColumn = c_col;
				previousGates.addAll(c_col);
				gateCount = c_col.size();//gateCount will decrease, as the number of gates in a column are limited by the number of outputs from the previous row
			} while (gateCount > 1);
			this.columns = columns;
			return lastColumn.get(0);
		}
	}
	public ArrayList<Gate> inputSequence;
	public ArrayList<ArrayList<Gate>> columns;
	public Gate output;
	
	public CompoundCircuit(int inputSize)
	{
		inputSequence = new ArrayList<Gate>();
		Random rng = new Random();
		for (int i = 0; i < inputSize; i++)
			inputSequence.add(new InputGate(rng.nextBoolean()));
		for (int i = 1; i < inputSize+1; i++)
			inputSequence.get(i-1).setIndex(-i);
		
		circuitTreeBuilder ctb = new circuitTreeBuilder();
		output = ctb.buildSequence(inputSize, inputSequence);
		this.columns = ctb.columns;
	}
	public CompoundCircuit(String circuitstring)
	{
		//TODO: implement a constructor which can build a circuit from a circuit string
	}
	public String getCircuitString()
	{
		String r = "";
		for (Gate b: inputSequence)
		{
			if (b.getOutput())
				r+="1";
			else
				r+="0";
		}
		
		int sum = 0;
		for (int i = 0; i < columns.size(); i++) {
			sum += columns.get(i).size();
		}
		Gate[] gates = new Gate[sum];
		for (int i = 0; i < columns.size(); i++) {
			for (int j = 0; j < columns.get(i).size(); j++)
			{
				gates[columns.get(i).get(j).getIndex()] = columns.get(i).get(j);
			}
		}
		for(int i = 0; i < gates.length; i++)
		{
			Gate selected = gates[i];
			if (selected instanceof AndGate)
			{
				AndGate gate = (AndGate)selected;
				r+=gate.getStringOutput()+getIndexValue(gate.getOp1())+"#"+getIndexValue(gate.getOp2());
			}
			else if (selected instanceof OrGate)
			{
				OrGate gate = (OrGate)selected;
				r+=gate.getStringOutput()+getIndexValue(gate.getOp1())+"#"+getIndexValue(gate.getOp2());
			}
			else if (selected instanceof NotGate)
			{
				NotGate gate = (NotGate)selected;
				r+=gate.getStringOutput()+getIndexValue(gate.getOp());
			}
			else if (selected instanceof InputGate)
			{
				System.out.println("ERROR: TRIED TO OUTPUT INPUT GATE");
			}
		}
		return r;
	}
	public static String getIndexValue(Gate g)
	{
		String r = "";
		if (g instanceof InputGate)
		{
			for (int i = 0; i < g.getIndex()*-1; i++)
				r+="x";
			return r;
		}
		for (int i = 0; i < 1+g.getIndex(); i++)
			r+="y";
		return r;
		
	}
	public static void printGateInfo(Gate g) {
		if (g instanceof AndGate)
		{
			AndGate g_0 = (AndGate)g;
			System.out.println(g_0.getIndex()+": AND OF GATE "+g_0.getOp1().getIndex()+ " & GATE "+g_0.getOp2().getIndex());
			printGateInfo(g_0.getOp1());
			printGateInfo(g_0.getOp2());
		}
		if (g instanceof OrGate)
		{
			OrGate g_0 = (OrGate)g;
			System.out.println(g_0.getIndex()+": OR OF GATE "+g_0.getOp1().getIndex()+ " & GATE "+g_0.getOp2().getIndex());
			printGateInfo(g_0.getOp1());
			printGateInfo(g_0.getOp2());
		}
		if (g instanceof NotGate)
		{
			NotGate g_0 = (NotGate)g;
			System.out.println(g_0.getIndex()+": NOT OF GATE "+g_0.getOp().getIndex());
			printGateInfo(g_0.getOp());
		}
		if (g instanceof InputGate)
		{
			InputGate g_0 = (InputGate)g;
			System.out.println(g_0.getIndex()+": STATIC GATE ");
		}
	}
	public static void main(String[] args)
	{
		CompoundCircuit test = new CompoundCircuit(3);
		System.out.println(test.output.getOutput());
		System.out.print(test.getCircuitString());
	}
}
