package circuit_designer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
public class TestBatchGenerator {
	public static void main(String[] args)
	{
		/*
		 * <file name> <number of circuits to generator> <number of input examples each> <t/f whether diagrams are to be created for them>
		 */
		
		String filename = args[0];
		int num_circuits =  Integer.parseInt(args[1]);
		int num_examples = Integer.parseInt(args[2]);
		boolean gen_diagrams = args[3].equals("t");
		ArrayList<String> trueset = new ArrayList<String>();
		ArrayList<String> falseset = new ArrayList<String>();
		Random random = new Random();
		for(int i = 0; i < num_circuits; i++)
		{
			CompoundCircuit tmp = new CompoundCircuit(random.nextInt(8)+2);
			for(int j = 0; j < num_examples; j++)
			{
				String tmp_output = tmp.getCircuitString();
				if (tmp.output.getOutput())
					trueset.add(tmp_output);
				else
					falseset.add(tmp_output);
				
				ArrayList<Boolean> newInputs = new ArrayList<Boolean>();
				for (int k = 0; k < tmp.inputSequence.size(); k++)
					newInputs.add(random.nextBoolean());
				tmp.setInputs(newInputs);
			}
		}
		try {
			FileWriter fw_true = new FileWriter(new File(filename+"_true.txt"));
			PrintWriter pw_true = new PrintWriter(fw_true);
			FileWriter fw_false = new FileWriter(new File(filename+"_false.txt"));
			PrintWriter pw_false = new PrintWriter(fw_false);
			
			for (String s: trueset)
				pw_true.println(s);
			for (String s: falseset)
				pw_false.println(s);
			
			pw_true.close();
			pw_false.close();
			fw_true.close();
			fw_false.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

}
