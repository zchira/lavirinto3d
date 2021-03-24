package zgraphutils;


/**
 * 
 * @author zchira
 */
public interface ZNodeInterface{
	
	public int getId();
	public ZEdgeInterface getEdge();
	public ZNodeInterface getNext();
	public ZNodeStatus getStatus();
	
	public void setId(int N);
	public void setEdge(ZEdgeInterface E);
	public void setNext(ZNodeInterface N);
	public void setStatus(ZNodeStatus N);
	
	public boolean isEqual(ZNodeInterface cvor);
}
