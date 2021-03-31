package zgraphutils;


/**
 * 
 * @author zchira
 */
public class ZNode<T> implements ZNodeInterface{

	private int id; //node
	private ZEdgeInterface edge; //adj
	private ZNodeInterface next;
	private ZNodeStatus status;
	
	private T data;
	
	public ZNode(int id, T data){
		setId(id);
		setData(data);	
		setStatus(ZNodeStatus.notVisited);
	}
	
	@Override
	public ZEdgeInterface getEdge() {
		// TODO Auto-generated method stub
		return edge;
	}

	@Override
	public ZNodeInterface getNext() {
		return next;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public boolean isEqual(ZNodeInterface cvor) {
		return cvor.getId() == this.getId();
	}

	@Override
	public void setEdge(ZEdgeInterface E) {
		edge = E;
	}

	@Override
	public void setNext(ZNodeInterface N) {
		next = N;
	}

	@Override
	public void setId(int N) {
		this.id = N;
	}

	@Override
	public ZNodeStatus getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(ZNodeStatus N) {
		// TODO Auto-generated method stub
		this.status = N;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
