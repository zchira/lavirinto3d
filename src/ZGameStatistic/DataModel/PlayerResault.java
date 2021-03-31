package ZGameStatistic.DataModel;

/**
 * 
 * @author Nenad Novkovic
 *
 */
public class PlayerResault extends PlayerInfo
{	
	private int points;
	
	public PlayerResault(ZPlayerStats player, int points)
	{
		super(player);	
		this.points = points;
	}	

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
