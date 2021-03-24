package ZGameStatistic.DataModel;

/**
 * 
 * @author Nenad Novkovic
 *
 */
public class PlayerInfo {
	
	private int playerId;	
	
	private String name;
	
	private boolean isActive;

//	public PlayerInfo(int playerId,  String name)
//	{
//		this.playerId = playerId;		
//		this.name = name;
//	}
	
	public PlayerInfo(ZPlayerStats player)
	{
		this.playerId = player.getPlayerId();
		this.name = player.getName();
		this.isActive = player.isActive();
	}
	
	public int getPlayerId()
	{
		return playerId;
	}

	public void setPlayerId(int playerId) 
	{
		this.playerId = playerId;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
