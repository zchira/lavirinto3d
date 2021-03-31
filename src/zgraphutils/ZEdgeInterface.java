package zgraphutils;

/**
 * 
 * @author zchira
 */
public interface ZEdgeInterface {
	public ZNodeInterface getDest();
	public ZEdgeInterface getLink();
	public int getWeight();		
	public void setDest(ZNodeInterface dest);
	public void setLink(ZEdgeInterface link);
	public void setWeight(int weight);	
}
