package circuit_designer;

import java.util.ArrayList;

public class NotGate implements Gate {
	private Gate op;
	private int index;
	public void setIndex(int x)
	{
		this.index = x;
	}
	public int getIndex()
	{
		return index;
	}
	public Gate getOp() {
		return op;
	}

	public void setOp(Gate op) {
		this.op = op;
	}

	public NotGate(Gate op)
	{
		this.op = op;
	}
	
	@Override
	public boolean getOutput() {
		// TODO Auto-generated method stub
		return !op.getOutput();
	}

	@Override
	public String getStringOutput() {
		// TODO Auto-generated method stub
		return "N";
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof NotGate) {
			NotGate other = (NotGate)o;
			return other.getOp() == this.getOp() && other.getIndex() == this.getIndex();
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		ArrayList<Object> hasher = new ArrayList<Object>();
		hasher.add(new Integer(index));
		if (this.op != null)
		{
			hasher.add(new Integer(op.getIndex()));
		}
		hasher.add(this.getClass().getName());
		return hasher.hashCode();
	}
}
