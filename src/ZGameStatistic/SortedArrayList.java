package ZGameStatistic;

import java.util.ArrayList;

import ZGameStatistic.DataModel.PlayerResault;

public class SortedArrayList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(E element) {
		
		if(size() == 0) {
			return super.add(element);
		}
		
		int index = size();
		
		for(int i = 0; i < size(); i++) {
				
			if(element instanceof Integer) {
				Integer el1 = (Integer)element;
				Integer el2 = (Integer)get(i);
				
				// kad elemet 2 postane manji od elementa 1
				if (el2.intValue() < el1.intValue()) {
					index = i;
					break;
				}				
			}
			
			if(element instanceof PlayerResault) {
				PlayerResault el1 = (PlayerResault)element;
				PlayerResault el2 = (PlayerResault)get(i);
				
				// kad elemet 2 postane manji od elementa 1 po poenima
				if (el2.getPoints() < el1.getPoints()) {
					index = i;
					break;
				}				
			}
		}
				
		
		super.add(index, element);
		
		return true;
	}
}
