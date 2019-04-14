package circuit_designer;

public class Tester {
	public static void main(String[] args)
	{
		System.out.println( new AndGate(new InputGate(true), new InputGate(true)).getOutput());
	}

}
