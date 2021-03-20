package ZXMLUtils;


/**
 * 
 * @author Nenad Novkovic
 */
public class ZXMLEntity {
	
	private int xCord;
	
	private int yCord;
	
	public ZXMLEntity(int xCord, int yCord) 
	{
		this.xCord = xCord;
		this.yCord = yCord;
	}
	
	public ZXMLEntity(String xCord, String yCord) 
	{
		this.xCord = Integer.parseInt(xCord);
		this.yCord = Integer.parseInt(yCord);
	}
	
	public int getXCord()
	{
		return xCord;		
	}
	
	public int getYCord()
	{
		return yCord;
	}
}
