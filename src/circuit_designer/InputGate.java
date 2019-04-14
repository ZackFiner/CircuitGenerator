package circuit_designer;

public class InputGate implements Gate{
	private boolean value;
	private int index;
	public void setIndex(int x)
	{
		this.index = x;
	}
	public int getIndex()
	{
		return index;
	}
	public InputGate(boolean value)
	{
		this.value = value;
	}
	public void setValue(boolean value)
	{
		this.value = value;
	}
	@Override
	public boolean getOutput() {
		// TODO Auto-generated method stub
		return value;
	}
	@Override
	public String getStringOutput() {
		// TODO Auto-generated method stub
		return null;
	}
}
