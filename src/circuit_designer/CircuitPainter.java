package circuit_designer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
public class CircuitPainter {
	private static Image and_gate;
	private static Image or_gate;
	private static Image not_gate;
	static class Pos
	{
		public int x;
		public int y;
		public boolean equals(Object o)
		{
			Pos other = (Pos)o;
			return other.x==this.x && other.y ==this.y;
		}
		public int hashCode()
		{
			ArrayList<Object> hasher = new ArrayList<Object>();
			hasher.add(new Integer(x));
			hasher.add(new Integer(y));
			return hasher.hashCode();
		}
		public Pos(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	static class SortByInputs<Gate> implements Comparator
	{
		private HashMap<Integer, Pos> outputs;
		public SortByInputs(HashMap<Integer, Pos> outputs) {
			this.outputs = outputs;
		}
		private int avgIndex(int ... arg1)
		{
			if (arg1.length == 0)
					return 0;
			int sum = 0;
			for (int c: arg1)
			{
				sum += outputs.get(c).y;
			}
			return sum/arg1.length;
		}
		@Override
		public int compare(Object arg0, Object arg1) {
			// I HAVE MANY REGRETS
			int decision;
			if (arg0 instanceof AndGate)
			{
				AndGate gate0 = (AndGate)arg0;
				if (arg1 instanceof AndGate)
				{
					AndGate gate1 = (AndGate)arg1;
					decision= avgIndex(gate0.getOp1().getIndex(),gate0.getOp2().getIndex())-
							avgIndex(gate1.getOp1().getIndex(),gate1.getOp2().getIndex());
				}
				else if (arg1 instanceof OrGate)
				{
					OrGate gate1 = (OrGate)arg1;
					decision= avgIndex(gate0.getOp1().getIndex(),gate0.getOp2().getIndex())-
							avgIndex(gate1.getOp1().getIndex(),gate1.getOp2().getIndex());
				}
				else
				{
					NotGate gate1 = (NotGate)arg1;
					decision= avgIndex(gate0.getOp1().getIndex(),gate0.getOp2().getIndex())-
							avgIndex(gate1.getOp().getIndex());
				}
			}
			else if (arg0 instanceof OrGate)
			{
				OrGate gate0 = (OrGate)arg0;
				if (arg1 instanceof AndGate)
				{
					AndGate gate1 = (AndGate)arg1;
					decision= avgIndex(gate0.getOp1().getIndex(),gate0.getOp2().getIndex())-
							avgIndex(gate1.getOp1().getIndex(),gate1.getOp2().getIndex());
				}
				else if (arg1 instanceof OrGate)
				{
					OrGate gate1 = (OrGate)arg1;
					decision= avgIndex(gate0.getOp1().getIndex(),gate0.getOp2().getIndex())-
							avgIndex(gate1.getOp1().getIndex(),gate1.getOp2().getIndex());
				}
				else
				{
					NotGate gate1 = (NotGate)arg1;
					decision= avgIndex(gate0.getOp1().getIndex(),gate0.getOp2().getIndex())-
							avgIndex(gate1.getOp().getIndex());
				}
			}
			else
			{
				NotGate gate0 = (NotGate)arg0;
				if (arg1 instanceof AndGate)
				{
					AndGate gate1 = (AndGate)arg1;
					decision= avgIndex(gate0.getOp().getIndex())-
							avgIndex(gate1.getOp1().getIndex(),gate1.getOp2().getIndex());
				}
				else if (arg1 instanceof OrGate)
				{
					OrGate gate1 = (OrGate)arg1;
					decision= avgIndex(gate0.getOp().getIndex())-
							avgIndex(gate1.getOp1().getIndex(),gate1.getOp2().getIndex());
				}
				else
				{
					NotGate gate1 = (NotGate)arg1;
					decision= avgIndex(gate0.getOp().getIndex())-
							avgIndex(gate1.getOp().getIndex());
				}
			}
			return decision;
		}
		
	}
	public static Color getRandomColor()
	{
		int r = (int)(Math.random()*255.0);
		int g = (int)(Math.random()*255.0);
		int b = (int)(Math.random()*255.0);
		int a = 255;
		Color r_c = new Color(r,g,b,a);
		return r_c;
	}
	private static void drawGates(Graphics2D painter, 
			BufferedImage image, 
			HashMap<Integer, ArrayList<Pos>> inputs, 
			HashMap<Integer, Pos> outputs,
			CompoundCircuit c1,
			boolean drawInputs)
	{
		for (int i = 0; i < c1.inputSequence.size(); i++) // input drawing phase
		{
			int gap_size = image.getHeight()/c1.inputSequence.size();
			int diameter = 30;
			int y_pos = gap_size/2 + i*gap_size;
			if (drawInputs)
			{
				if (c1.inputSequence.get(i).getOutput()) {
					painter.setPaint(Color.RED);
					painter.drawString("1", (int)(20+diameter*0.4), (int)(y_pos+diameter*0.7));
				}
				else
				{
					painter.setColor(Color.BLACK);
					painter.drawString("0", (int)(20+diameter*0.4), (int)(y_pos+diameter*0.7));
				}
			}
			painter.drawOval(20, y_pos, diameter, diameter);
			outputs.put(c1.inputSequence.get(i).getIndex(), new Pos(20+diameter,y_pos+diameter/2));
			painter.setColor(Color.BLACK);
		}
		
		for (int i = 0; i < c1.columns.size(); i++) // gate drawing phase
		{
			int gap_size_x = image.getWidth()/c1.columns.size();
			Collections.sort(c1.columns.get(i), new SortByInputs(outputs));// this is to relax circuits, so there aren't as many bundles of connections
			for (int j = 0; j < c1.columns.get(i).size(); j++)
			{
				Gate selected_gate = c1.columns.get(i).get(j);
				int gap_size_y = image.getHeight()/c1.columns.get(i).size();
				int x_cntr = gap_size_x/2 + gap_size_x*i;
				int y_cntr = gap_size_y/2 + gap_size_y*j;
				int icon_width = 50;
				int icon_height = 50;
				Image icon;
				if (selected_gate instanceof AndGate)
				{
					AndGate gate = (AndGate)selected_gate;
					icon = and_gate;
					ArrayList<Pos> in_p = new ArrayList<Pos>();
					in_p.add(new Pos((int)(x_cntr), (int)(y_cntr+icon_height*0.2)));
					in_p.add(new Pos((int)(x_cntr), (int)(y_cntr+icon_height*0.8)));
					inputs.put(gate.getIndex(), in_p);
				}
				else if (selected_gate instanceof OrGate)
				{
					OrGate gate = (OrGate)selected_gate;
					icon = or_gate;
					ArrayList<Pos> in_p = new ArrayList<Pos>();
					in_p.add(new Pos((int)(x_cntr+icon_width*0.1), (int)(y_cntr+icon_height*0.2)));
					in_p.add(new Pos((int)(x_cntr+icon_width*0.1), (int)(y_cntr+icon_height*0.8)));
					inputs.put(gate.getIndex(), in_p);
				}
				else if (selected_gate instanceof NotGate)
				{
					NotGate gate = (NotGate)selected_gate;
					icon = not_gate;
					ArrayList<Pos> in_p = new ArrayList<Pos>();
					in_p.add(new Pos((int)(x_cntr), (int)(y_cntr+icon_height*0.5)));
					inputs.put(gate.getIndex(), in_p);
				}
				else
				{
					System.err.println("ERROR: columns contained invalid class type");
					break;
				}
				outputs.put(selected_gate.getIndex(), new Pos(x_cntr+icon_width, y_cntr+icon_height/2));
				painter.drawImage(icon, x_cntr, y_cntr, icon_width, icon_height, null);
				painter.drawString("y"+selected_gate.getIndex(), (int)(x_cntr+(icon_width*0.0)), (int)(y_cntr+(icon_height*0.0)));
			}
		}
	}
	private static void drawAllConnections(Graphics2D painter, 
			BufferedImage image, 
			HashMap<Integer, ArrayList<Pos>> inputs, 
			HashMap<Integer, Pos> outputs,
			CompoundCircuit c1)
	{
		for (int i = 0; i < c1.columns.size(); i++)
		{
			for (int j = 0; j < c1.columns.get(i).size(); j++)
			{
				Gate g = c1.columns.get(i).get(j);
				if (g instanceof AndGate)
				{
					AndGate gate = (AndGate)g;
					double mixv1, mixv2;
					if (gate.getOp1().getIndex() < 0)
					{
						mixv1 = ((double)gate.getOp1().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv1 = ((double)gate.getOp1().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					if (gate.getOp2().getIndex() < 0)
					{
						mixv2 = ((double)gate.getOp2().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv2 = ((double)gate.getOp2().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					
					Gate g1;
					Gate g2;
					if (outputs.get(gate.getOp1().getIndex()).y <= outputs.get(gate.getOp2().getIndex()).y)
					{
						g1 = gate.getOp1();
						g2 = gate.getOp2();
					}
					else
					{
						g1 = gate.getOp2();
						g2 = gate.getOp1();
					}
					drawConnection(image, inputs.get(gate.getIndex()).get(0).x, 
							inputs.get(gate.getIndex()).get(0).y, 
							outputs.get(g1.getIndex()).x, 
							outputs.get(g1.getIndex()).y, 
							getRandomColor(), Math.abs(mixv1));
					drawConnection(image, inputs.get(gate.getIndex()).get(1).x, 
							inputs.get(gate.getIndex()).get(1).y, 
							outputs.get(g2.getIndex()).x, 
							outputs.get(g2.getIndex()).y, 
							getRandomColor(), Math.abs(mixv2));
				}
				else if (g instanceof OrGate)
				{
					OrGate gate = (OrGate)g;
					double mixv1, mixv2;
					if (gate.getOp1().getIndex() < 0)
					{
						mixv1 = ((double)gate.getOp1().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv1 = ((double)gate.getOp1().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					if (gate.getOp2().getIndex() < 0)
					{
						mixv2 = ((double)gate.getOp2().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv2 = ((double)gate.getOp2().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					
					Gate g1;
					Gate g2;
					if (outputs.get(gate.getOp1().getIndex()).y <= outputs.get(gate.getOp2().getIndex()).y)
					{
						g1 = gate.getOp1();
						g2 = gate.getOp2();
					}
					else
					{
						g1 = gate.getOp2();
						g2 = gate.getOp1();
					}
					drawConnection(image, inputs.get(gate.getIndex()).get(0).x, 
							inputs.get(gate.getIndex()).get(0).y, 
							outputs.get(g1.getIndex()).x, 
							outputs.get(g1.getIndex()).y, 
							getRandomColor(), Math.abs(mixv1));
					drawConnection(image, inputs.get(gate.getIndex()).get(1).x, 
							inputs.get(gate.getIndex()).get(1).y, 
							outputs.get(g2.getIndex()).x, 
							outputs.get(g2.getIndex()).y, 
							getRandomColor(), Math.abs(mixv2));
				}
				else if (g instanceof NotGate)
				{
					NotGate gate = (NotGate)g;
					double mixv;
					if (gate.getOp().getIndex() < 0)
					{
						mixv = ((double)gate.getOp().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv = ((double)gate.getOp().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					
					drawConnection(image, inputs.get(gate.getIndex()).get(0).x, 
							inputs.get(gate.getIndex()).get(0).y, 
							outputs.get(gate.getOp().getIndex()).x, 
							outputs.get(gate.getOp().getIndex()).y, 
							getRandomColor(),Math.abs(mixv));
				}
				else
				{
					System.err.println("ERROR: columns contained invalid class type");
					break;
				}
			}
		}
	}
	private static Color isLive(boolean value)
	{
		if (value)
			return Color.RED;
		return Color.BLACK;
	}
	private static void drawAllLiveConnections(Graphics2D painter, 
			BufferedImage image, 
			HashMap<Integer, ArrayList<Pos>> inputs, 
			HashMap<Integer, Pos> outputs,
			CompoundCircuit c1)
	{
		for (int i = 0; i < c1.columns.size(); i++)
		{
			for (int j = 0; j < c1.columns.get(i).size(); j++)
			{
				Gate g = c1.columns.get(i).get(j);
				if (g instanceof AndGate)
				{
					AndGate gate = (AndGate)g;
					double mixv1, mixv2;
					if (gate.getOp1().getIndex() < 0)
					{
						mixv1 = ((double)gate.getOp1().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv1 = ((double)gate.getOp1().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					if (gate.getOp2().getIndex() < 0)
					{
						mixv2 = ((double)gate.getOp2().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv2 = ((double)gate.getOp2().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					
					Gate g1;
					Gate g2;
					if (outputs.get(gate.getOp1().getIndex()).y <= outputs.get(gate.getOp2().getIndex()).y)
					{
						g1 = gate.getOp1();
						g2 = gate.getOp2();
					}
					else
					{
						g1 = gate.getOp2();
						g2 = gate.getOp1();
					}
					drawConnection(image, inputs.get(gate.getIndex()).get(0).x, 
							inputs.get(gate.getIndex()).get(0).y, 
							outputs.get(g1.getIndex()).x, 
							outputs.get(g1.getIndex()).y, 
							isLive(g1.getOutput()), Math.abs(mixv1));
					drawConnection(image, inputs.get(gate.getIndex()).get(1).x, 
							inputs.get(gate.getIndex()).get(1).y, 
							outputs.get(g2.getIndex()).x, 
							outputs.get(g2.getIndex()).y, 
							isLive(g2.getOutput()), Math.abs(mixv2));
				}
				else if (g instanceof OrGate)
				{
					OrGate gate = (OrGate)g;
					double mixv1, mixv2;
					if (gate.getOp1().getIndex() < 0)
					{
						mixv1 = ((double)gate.getOp1().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv1 = ((double)gate.getOp1().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					if (gate.getOp2().getIndex() < 0)
					{
						mixv2 = ((double)gate.getOp2().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv2 = ((double)gate.getOp2().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					
					Gate g1;
					Gate g2;
					if (outputs.get(gate.getOp1().getIndex()).y <= outputs.get(gate.getOp2().getIndex()).y)
					{
						g1 = gate.getOp1();
						g2 = gate.getOp2();
					}
					else
					{
						g1 = gate.getOp2();
						g2 = gate.getOp1();
					}

					drawConnection(image, inputs.get(gate.getIndex()).get(0).x, 
							inputs.get(gate.getIndex()).get(0).y, 
							outputs.get(g1.getIndex()).x, 
							outputs.get(g1.getIndex()).y, 
							isLive(g1.getOutput()), Math.abs(mixv1));
					drawConnection(image, inputs.get(gate.getIndex()).get(1).x, 
							inputs.get(gate.getIndex()).get(1).y, 
							outputs.get(g2.getIndex()).x, 
							outputs.get(g2.getIndex()).y, 
							isLive(g2.getOutput()), Math.abs(mixv2));
				}
				else if (g instanceof NotGate)
				{
					NotGate gate = (NotGate)g;
					double mixv;
					if (gate.getOp().getIndex() < 0)
					{
						mixv = ((double)gate.getOp().getIndex())/(c1.inputSequence.size());
					}
					else
					{
						mixv = ((double)gate.getOp().getIndex()-c1.columns.get(i-1).get(0).getIndex())/(c1.columns.get(i-1).size());
					}
					
					drawConnection(image, inputs.get(gate.getIndex()).get(0).x, 
							inputs.get(gate.getIndex()).get(0).y, 
							outputs.get(gate.getOp().getIndex()).x, 
							outputs.get(gate.getOp().getIndex()).y, 
							isLive(gate.getOp().getOutput()),Math.abs(mixv));
				}
				else
				{
					System.err.println("ERROR: columns contained invalid class type");
					break;
				}
			}
		}
	}
	public static BufferedImage drawCircuit(CompoundCircuit c1) {
		try {
			and_gate = ImageIO.read(new File("g_and.png"));
			or_gate = ImageIO.read(new File("g_or.png"));
			not_gate = ImageIO.read(new File("g_not.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		BufferedImage image = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
		Graphics2D painter = image.createGraphics();
		painter.setPaint(Color.WHITE);
		painter.fillRect(0, 0, image.getWidth(), image.getHeight());
		painter.setPaint(Color.BLACK);
		
		HashMap<Integer, ArrayList<Pos>> inputs = new HashMap<Integer, ArrayList<Pos>>();
		HashMap<Integer, Pos> outputs = new HashMap<Integer, Pos>();
		
		drawGates(painter, image, inputs, outputs, c1, false);
		drawAllConnections(painter, image, inputs, outputs, c1);
		return image;
	}
	public static BufferedImage drawLiveCircuit(CompoundCircuit c1) {
		try {
			and_gate = ImageIO.read(new File("g_and.png"));
			or_gate = ImageIO.read(new File("g_or.png"));
			not_gate = ImageIO.read(new File("g_not.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		BufferedImage image = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
		Graphics2D painter = image.createGraphics();
		painter.setPaint(Color.WHITE);
		painter.fillRect(0, 0, image.getWidth(), image.getHeight());
		painter.setPaint(Color.BLACK);
		
		HashMap<Integer, ArrayList<Pos>> inputs = new HashMap<Integer, ArrayList<Pos>>();
		HashMap<Integer, Pos> outputs = new HashMap<Integer, Pos>();
		
		drawGates(painter, image, inputs, outputs, c1, true);
		drawAllLiveConnections(painter, image, inputs, outputs, c1);
		return image;
	} 
	public static void drawConnection(BufferedImage canvas, int x1, int y1, int x2, int y2, Color c, double cheatVal)
	{
		Graphics2D painter = canvas.createGraphics();
		int midx = (int)((x2-x1)*(cheatVal*0.8+0.1))+x1;
		painter.setPaint(c);
		painter.drawLine(x1, y1, midx, y1);
		painter.drawLine(midx, y1, midx, y2);
		painter.drawLine(midx, y2, x2, y2);
		
	}
	public static void saveLiveCircuit(CompoundCircuit c, String filename)
	{
		try
		{
			BufferedImage bi = drawLiveCircuit(c);
			File outputfile = new File(filename);
			outputfile.getParentFile().mkdirs();
			ImageIO.write(bi, "png", outputfile);
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
	}
	public static void saveCircuit(CompoundCircuit c, String filename)
	{
		try
		{
			BufferedImage bi = drawCircuit(c);
			File outputfile = new File(filename);
			outputfile.getParentFile().mkdirs();
			ImageIO.write(bi, "png", outputfile);
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
	}
	public static void main(String[] args)
	{
		CompoundCircuit test = new CompoundCircuit(2);
		try {
		    // retrieve image
		    BufferedImage bi = drawCircuit(test);
		    File outputfile = new File("saved.png");
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
		    
		}
	}
}
