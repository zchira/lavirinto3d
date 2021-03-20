package ZTableUtils;

import zgraphutils.ZGraphAsList;
import zgraphutils.ZNode;

/**
 * 
 * @author zchira
 */
public class ZGraphForGame<T> extends ZGraphAsList<T> {

	@SuppressWarnings("unchecked")
	public boolean insertnode(ZNode newNode) {
		if (newNode == null)
			return false;
		newNode.setEdge(null);
		newNode.setNext(this.start);
		ZNode newNode2 = (ZNode) newNode;
		this.start = newNode2;
		return true;
	}

	public boolean insertBidirectionalEdge(ZNode a, ZNode b){
		boolean tmp = insertEdge(a, b) && insertEdge(b ,a);
		return tmp;
	}
	
	public boolean insertEdge(ZNode a, ZNode b) {
		if (a != null && b != null) {
			return super.insertEdge(a.getId(), b.getId());
		}
		return false;
	}
	
	public void deleteEdges(ZNode a, ZNode b){
		deleteEdge(a, b);
		deleteEdge(b, a);
	}
	
	private boolean deleteEdge(ZNode a, ZNode b){
		if (a != null && b != null){
			return super.deleteEdge(a.getId(), b.getId());
		}
		return false;
	}
}
