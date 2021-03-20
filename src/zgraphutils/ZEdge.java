package zgraphutils;

/**
 * 
 * @author zchira
 */
public class ZEdge implements ZEdgeInterface{

	private ZNodeInterface dest;
	private ZEdgeInterface link;
	private int weight;
	
	@Override
	public ZNodeInterface getDest() {
		return dest;
	}

	@Override
	public ZEdgeInterface getLink() {
		return link;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	public void setDest(ZNodeInterface dest) {
		this.dest = dest;
	}

	public void setLink(ZEdgeInterface link) {
		this.link = link;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
