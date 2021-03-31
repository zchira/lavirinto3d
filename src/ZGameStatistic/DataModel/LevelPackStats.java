package ZGameStatistic.DataModel;

import java.io.Serializable;

import ZGameStatistic.SortedArrayList;

/**
 * 
 * @author Nenead Novkovic
 *
 */
public class LevelPackStats implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int maxLevel = 0;
	
	private SortedArrayList<Integer> gaindPoints = null;
	
	public LevelPackStats()
	{
		maxLevel = 0;
		gaindPoints = new SortedArrayList<Integer>();		
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public SortedArrayList<Integer> getGaindPoints() {
		return gaindPoints;
	}

	public void setGaindPoints(SortedArrayList<Integer> gaindPoints) {
		this.gaindPoints = gaindPoints;
	}
	
	/**
	 * Adds new reasault for player, corect max level and adds pointsAchived.
	 * @param levelAchived
	 * @param pointsAchived
	 */
	public void addNewResaults(int levelAchived, int pointsAchived)
	{
		if (maxLevel < levelAchived) {
			maxLevel = levelAchived;
		}
		
		gaindPoints.add(new Integer(pointsAchived));
	}
	
	/**
	 * Clears score list.
	 */
	public void clearGaindPoints()
	{
		gaindPoints.clear();
	}
}
