package resources;
import java.awt.Image;
import java.awt.Toolkit;
public class ResourceLoader {
	static ResourceLoader rl = new ResourceLoader();
	
	public static Image loadImage(String imageName)
	{
		Image img = Toolkit.getDefaultToolkit().getImage(rl.getClass().getResource(imageName));
		return img;
	}
}
