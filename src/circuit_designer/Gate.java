package circuit_designer;

public interface Gate {
	public boolean getOutput();
	public String getStringOutput();
	public void setIndex(int x);
	public int getIndex();
}
