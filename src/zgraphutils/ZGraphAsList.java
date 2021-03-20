package zgraphutils;


/**
 * 
 * @author zchira
 */
public class ZGraphAsList<T>{

	protected ZNode<T> start;
	
	@SuppressWarnings("unchecked")
	public ZNode<T> findNode(int pod){
		ZNode<T> toReturn = start;
		while (toReturn != null && !(toReturn.getId()==pod)){
			toReturn = (ZNode<T>) toReturn.getNext();
		}		
		return toReturn;
	}
	
	public ZEdgeInterface findEdge(int a, int b){
		ZNode<T> pa = findNode(a);
		ZNode<T> pb = findNode(b);
		if (pa == null || pb == null) return null;
		ZEdgeInterface toReturn = pa.getEdge();
		while (toReturn != null && !toReturn.getDest().isEqual(pb)){
			toReturn = toReturn.getLink();
		}
		
		return toReturn;
	}
	
	public boolean insertnode(int id, T data){		
		ZNode<T> newNode = new ZNode<T>(id, data);
		if (newNode == null) return false;
		newNode.setEdge(null);
		newNode.setNext(this.start);
		this.start = newNode;
		return true;
	}

	public boolean insertEdge(int a, int b) {
		ZNode<T> pa = findNode(a);
		ZNode<T> pb = findNode(b);
		if (findEdge(a, b) != null) return false;
		if (pa == null || pb == null)
			return false;
		ZEdge ptr = new ZEdge();
		if (ptr == null)
			return false;
		ptr.setDest(pb);
		ptr.setLink(pa.getEdge());
		pa.setEdge(ptr);
		return true;
	}

	public boolean deleteEdge(int a, int b){
		ZEdgeInterface pot =  findEdge(a, b);
		if (pot == null) return false;
		ZNode<T> pa = findNode(a);
		if (pot == pa.getEdge()){
			pa.setEdge(pot.getLink());
			return true;
		}
		
		ZEdgeInterface tpot = pa.getEdge();
		while (tpot.getLink() != pot){
			tpot = tpot.getLink();
		}
		tpot.setLink(pot.getLink());
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean deleteNode(int id){
		ZNodeInterface ptr;
		if (this.start == null) return false;
		if (start.getId() == id){
			ptr = start;
			ZEdgeInterface pot = start.getEdge();
			while (pot != null){
				ZEdgeInterface tpot = pot.getLink();
				pot = tpot;
			}
	//		deleteEdgeToNode(start);
			start = (ZNode<T>) start.getNext();
			return true;			
		}else{
			ptr = start.getNext();
			ZNodeInterface par = start;
			while (ptr !=null && ptr.getId() != id){
				par = ptr;
				ptr = ptr.getNext();
			}
			if (ptr == null) return false;
			par.setNext(ptr.getNext());
			ZEdgeInterface pot = ptr.getEdge();
			while (pot != null){
				ZEdgeInterface tpot = pot.getLink();
				pot = tpot;
			}
		//	deleteEdgeToNode(start);
			return true;
		}
	}
	
	protected void deleteEdgeToNode(ZNodeInterface ptr){
		ZNodeInterface pa = start;
		while (pa != null){
			ZEdgeInterface pot = pa.getEdge();
			ZEdge prev = new ZEdge();
			while (pot != null){
				if (pot.getDest() == ptr){
					if (pa.getEdge() == pot){
						pa.setEdge(pot.getLink());
						pot = pa.getEdge();
					}else{
						prev.setLink(pot.getLink());
						pot = prev.getLink();
					}
				}else{
					prev = (ZEdge) pot;
					pot = pot.getLink();
				}
			}
			pa = pa.getNext();
		}
	}
	
	
	
	
	
	
}
