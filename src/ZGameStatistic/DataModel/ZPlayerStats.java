package ZGameStatistic.DataModel;

import java.io.Serializable;
import java.util.HashMap;


/**
 * 
 * @author Nenad Novkovic
 *
 */
public class ZPlayerStats implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int playerId = 0;
	
	private String name = null;
		
	private boolean isActive;

	private HashMap<String, LevelPackStats> levelPackData = null;
	
	public ZPlayerStats()
	{
		this(-1, "");
	}
	
	public ZPlayerStats(int playerId, String playerName)
	{
		// By default player is active, until someone changes thats.
		this.isActive = true;
		this.playerId = playerId;
		this.name = playerName;
		this.levelPackData = new HashMap<String, LevelPackStats>();
	}
	
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, LevelPackStats> getLevelPackData() {
		return levelPackData;
	}

	public void setLevelPackData(HashMap<String, LevelPackStats> levelPackData) {
		this.levelPackData = levelPackData;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Retrun level pack for appropriate key.
	 * If dose not exist appropriate key adds new LevelPack for given key.
	 * @param name
	 * @return
	 */
	public LevelPackStats getLevelPack(String name)
	{
		// ako ne postoji ovaj levelpack, dodam ga 
		if (!levelPackData.containsKey(name)) {
			LevelPackStats lp = new LevelPackStats();
			levelPackData.put(name, lp);
		}
			
		return levelPackData.get(name);
	}
	
	/**
	 * Removes for all level packs points that player achieved. 
	 */
	public void clearGaindPoints()
	{
		
		for (LevelPackStats levelPack : getLevelPackData().values()) {
			levelPack.clearGaindPoints();
		}
	}

	

}
