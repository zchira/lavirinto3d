package zgraphutils;

import java.util.ArrayList;

/**
 * 
 * @author zchira
 */
public class ZGraphTraverser {

	private static int numOfTraversed;
	
	
	public static int getNumOfTraversed() {
		return numOfTraversed;
	}

	public static ZTraversingState breadthTrav(final ZGraphAsList<?> graf,
			final ZNode<?> startField){
		return breadthTrav(graf, startField, null, ZTraversingState.started);
	}
	
	
	public static ZTraversingState breadthTrav(final ZGraphAsList<?> graf,
			final ZNode<?> startField, ArrayList<ZNodeInterface> que, ZTraversingState state) {
		
		ZNodeInterface ptr;
		int retVal = 0;
		if (state == ZTraversingState.started){
			clearStatus(graf);
//			que = new ArrayList<ZNodeInterface>();
			que.clear();
			ptr = graf.findNode(startField.getId());
			if (ptr == null) {
				return ZTraversingState.error;
			}
			que.add(ptr);
			ptr.setStatus(ZNodeStatus.active);
		}
		while (!que.isEmpty()) {
			ptr = (ZNodeInterface) que.get(0);
			que.remove(0);
			Visit(ptr);
			retVal += 1;
			ZEdgeInterface pot = ptr.getEdge();
			while (pot != null) {
				if (pot.getDest().getStatus() == ZNodeStatus.notVisited) {
					pot.getDest().setStatus(ZNodeStatus.active);
					que.add(pot.getDest());
				}
				pot = pot.getLink();
			}
			return ZTraversingState.inProgress;
		}
		return ZTraversingState.finished;
	}
	
	public static ZTraversingState depthTrav(final ZGraphAsList<?> graf,
			final ZNode<?> startField, ArrayList<ZNodeInterface> que, ZTraversingState state) {
		
		ZNodeInterface ptr;
		int retVal = 0;
		if (state == ZTraversingState.started){
			clearStatus(graf);
//			que = new ArrayList<ZNodeInterface>();
			que.clear();
			ptr = graf.findNode(startField.getId());
			if (ptr == null) {
				return ZTraversingState.error;
			}
			que.add(ptr);
			ptr.setStatus(ZNodeStatus.active);
		}
		while (!que.isEmpty()) {
			 int size = que.size();
			ptr = (ZNodeInterface) que.get(size-1);
			que.remove(size-1);
			Visit(ptr);
			retVal += 1;
			ZEdgeInterface pot = ptr.getEdge();
			while (pot != null) {
				if (pot.getDest().getStatus() == ZNodeStatus.notVisited) {
					pot.getDest().setStatus(ZNodeStatus.active);
					que.add(pot.getDest());
				}
				pot = pot.getLink();
			}
			return ZTraversingState.inProgress;
		}
		return ZTraversingState.finished;
	}
	

	private static void Visit(ZNodeInterface ptr) {
		ptr.setStatus(ZNodeStatus.visited);
		numOfTraversed ++;
	}

	private static void clearStatus(ZGraphAsList<?> graf) {
		ZNodeInterface ptr = graf.start;
		while (ptr != null) {
			ptr.setStatus(ZNodeStatus.notVisited);
			ptr = (ZNodeInterface) ptr.getNext();
		}
		numOfTraversed = 0;
	}
}
