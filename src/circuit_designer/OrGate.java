package circuit_designer;

import java.util.ArrayList;

public class OrGate implements Gate{
	private Gate op1;
	private int index;
	public void setIndex(int x)
	{
		this.index = x;
	}
	public int getIndex()
	{
		return index;
	}
	public Gate getOp1() {
		return op1;
	}
	public void setOp1(Gate op1) {
		this.op1 = op1;
	}
	public Gate getOp2() {
		return op2;
	}
	public void setOp2(Gate op2) {
		this.op2 = op2;
	}

	private Gate op2;
	
	public OrGate(Gate op1, Gate op2)
	{
		this.op1 = op1;
		this.op2 = op2;
	}
	@Override
	public boolean getOutput() {
		// TODO Auto-generated method stub
		return op1.getOutput() || op2.getOutput();
	}

	@Override
	public String getStringOutput() {
		// TODO Auto-generated method stub
		return "O";
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof OrGate) {
			OrGate other = (OrGate)o;
			return other.getOp1() == this.getOp1() && other.getOp2() == this.getOp2() && other.getIndex() == this.getIndex();
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		ArrayList<Object> hasher = new ArrayList<Object>();
		hasher.add(new Integer(index));
		if (this.op1 != null)
		{
			hasher.add(new Integer(op1.getIndex()));
		}
		if (this.op2 != null)
		{
			hasher.add(new Integer(op2.getIndex()));
		}
		hasher.add(this.getClass().getName());
		return hasher.hashCode();
	}

}
