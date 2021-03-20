package ZGameStatistic.DataModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Nenad Novkovic
 *
 */
public class ZStats implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	

	
	public ZStats()
	{
		this.playerStats = new ArrayList<ZPlayerStats>();
		lastPlayerId = -1;
	}
	
	private int lastPlayerId;
	
	public int getLastPlayerId() {
		return lastPlayerId;
	}

	public void setLastPlayerId(int lastPlayerId) {
		this.lastPlayerId = lastPlayerId;
	}

	private ArrayList<ZPlayerStats> playerStats = null;

	public ArrayList<ZPlayerStats> getPlayerStats() 
	{
		return playerStats;
	}

	public void setPlayerStats(ArrayList<ZPlayerStats> playerStats)
	{
		this.playerStats = playerStats;
	}
	
	/**
	 * Finds available id for player
	 * @return
	 */
	public int getAvailableId()
	{
		int maxId = 0;
		
		for (ZPlayerStats player : getPlayerStats()) {
			if(maxId < player.getPlayerId()) {
				maxId = player.getPlayerId();
			}			
		}
		
		return maxId + 1;
	}
	
	public void addPlayer(ZPlayerStats player)
	{
		getPlayerStats().add(player);
	}
	
	public boolean removePlayer(int playerId)
	{
		int toRemove = -1;
		for(int i = 0; i < getPlayerStats().size(); i++)
		{
			if(getPlayerStats().get(i).getPlayerId() == playerId) {
				toRemove = i;
				break;
			}
		}
		
		if (toRemove > -1) {
			getPlayerStats().remove(toRemove);
			return true;			
		}
		
		return false;
	}
	
	public ZPlayerStats getPlayer(int playerId)
	{
		ZPlayerStats toReturn = null;
		for (ZPlayerStats player : getPlayerStats()) {
			if(player.getPlayerId() == playerId) {
				toReturn = player;
				break;
			}
		}
		
		return toReturn;
		
	}
}
