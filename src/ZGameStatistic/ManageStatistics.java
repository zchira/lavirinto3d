package ZGameStatistic;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ZGameStatistic.DataModel.LevelPackStats;
import ZGameStatistic.DataModel.PlayerInfo;
import ZGameStatistic.DataModel.PlayerResault;
import ZGameStatistic.DataModel.ZPlayerStats;
import ZGameStatistic.DataModel.ZStats;


/**
 * Interface for managing statistics.
 * @author Nenad Novkovic
 */
public class ManageStatistics
{
	
	private static String HIGH_SCORE = "highscore.dat";
	
	//
	// singlton
	//
	private static ZStats statsInstance = null;
	
	private static ZStats getStatsInstance()
	{
		if (statsInstance == null) {
			try {
				FileInputStream fis = new FileInputStream(HIGH_SCORE);

				//				XMLDecoder decoder = new XMLDecoder(os);
				//				statsInstance = (ZStats)decoder.readObject();
				//				decoder.close();
				try {
					ObjectInputStream ois = new ObjectInputStream(fis);
					statsInstance = (ZStats)ois.readObject();

					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			catch (FileNotFoundException e) {
				statsInstance = new ZStats();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return statsInstance;
	}
	//
	// end singlton
	//
	
	private ManageStatistics()
	{
		
	}
	
	/**
	 * Adds new player and return it with assigned id
	 * @param name
	 * @return
	 */
	public static PlayerInfo addPlayer(String name) {
		
		ZStats stats = getStatsInstance();
		int playerId = stats.getAvailableId();
		ZPlayerStats player = new ZPlayerStats(playerId, name);
		stats.addPlayer(player);
		
		
		return new PlayerInfo(player);
	}
	
	/**
	 * Gets all players
	 * @return
	 */
	public static ArrayList<PlayerInfo> getAllPlayers()
	{
		
		ArrayList<ZPlayerStats> players = getStatsInstance().getPlayerStats();
		ArrayList<PlayerInfo> toRetrun = new ArrayList<PlayerInfo>();
		
		for (ZPlayerStats playerStat : players) {
			toRetrun.add(new PlayerInfo(playerStat));
		}
		
		return toRetrun;
	}
	
	/**
	 * Gets all actve players
	 * @return
	 */
	public static ArrayList<PlayerInfo> getActivePlayers()
	{		
		ArrayList<ZPlayerStats> players = getStatsInstance().getPlayerStats();
		ArrayList<PlayerInfo> toRetrun = new ArrayList<PlayerInfo>();
		
		for (ZPlayerStats playerStat : players) {
			if(playerStat.isActive()) {
				toRetrun.add(new PlayerInfo(playerStat));
			}
		}
		
		return toRetrun;
	}
		
	/**
	 * Sets is active flag for player with specific id. 
	 * @param playerId
	 * @return player or null.
	 */
	public static PlayerInfo setIsPlayerActive(int playerId, boolean isActive)
	{
		PlayerInfo toReturn = null;
		ZPlayerStats player = getStatsInstance().getPlayer(playerId);		
		
		if(player != null) {
			player.setActive(isActive);
			toReturn = new PlayerInfo(player);
		}
		
		return toReturn;			

	}
	
	
	
	/**
	 * Gets specific player, if player dose not exist returns null.
	 * @param playerId
	 * @return
	 */
	public static PlayerInfo getPlayer(int playerId)
	{
		PlayerInfo toReturn = null;
		ZPlayerStats player = getStatsInstance().getPlayer(playerId);		
		
		if(player != null) {
			toReturn = new PlayerInfo(player);
		}
		
		return toReturn;			
	}
	
	/**
	 * Remove player with appropriate id
	 * @param playerId
	 * @return
	 */
	public static boolean removePlayer(int playerId) 
	{
		return getStatsInstance().removePlayer(playerId);		
	}
	
	/**
	 * Adds new result for player.
	 * @param playerId
	 * @param levelPackName
	 * @param levelAchived
	 * @param pointsAchived
	 */
	public static void addPlayerResults(int playerId, String levelPackName, 
			int levelAchived, int pointsAchived)
	{
		ZPlayerStats player = getStatsInstance().getPlayer(playerId);
		LevelPackStats levelPack = player.getLevelPack(levelPackName);
		levelPack.addNewResaults(levelAchived, pointsAchived);		
	}
	
	/**
	 * Gets top results for level pack
	 * @param top
	 * @param levelPackName
	 * @return
	 */
	public static ArrayList<PlayerResault> getTopResults(int top, String levelPackName)
	{
		SortedArrayList<PlayerResault> resauluts = new SortedArrayList<PlayerResault>();
		
		for(ZPlayerStats player : getStatsInstance().getPlayerStats())
		{
			LevelPackStats levelPack = player.getLevelPack(levelPackName);
			SortedArrayList<Integer> gained =  levelPack.getGaindPoints();
			int n = gained.size();
			if (n > top) {
				n = top;
			}
			
			for(int i = 0; i < n; i++)
			{
				PlayerResault pr = new PlayerResault(player, gained.get(i));
				resauluts.add(pr);
			}
		}
		
		ArrayList<PlayerResault> toReturn = new ArrayList<PlayerResault>();
		
		int n = resauluts.size();
		if (n > top) {
			n = top;
		}
		
		for(int i = 0; i < n; i++) {
			toReturn.add(resauluts.get(i));
		}
		
		return toReturn;		
	}
	
	/**
	 * Gets player's max achieved level for level pack
	 * @param playerId
	 * @param levelPackName
	 * @return
	 */
	public static int getAchievedLeve(int playerId, String levelPackName)
	{
		ZPlayerStats player = getStatsInstance().getPlayer(playerId);		
		return player.getLevelPack(levelPackName).getMaxLevel();
	}
	
	/**
	 * Saves result data to file.
	 */
	public static void SaveStats()
	{
		try {
			FileOutputStream fos = new FileOutputStream(HIGH_SCORE);
//			XMLEncoder encoder = new XMLEncoder(os);		
//			encoder.writeObject(getStatsInstance());
//			encoder.close();
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(getStatsInstance());
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();			
		} 
	}
	
	
	/**
	 * Last player.
	 * @param playerId
	 */
	public static void setLastPlayer(int playerId)
	{
		getStatsInstance().setLastPlayerId(playerId);
	}
	
	/**
	 * Gets last player. Only if player is active.
	 * @return
	 */
	public static PlayerInfo getLastPlayer() 
	{		
		PlayerInfo player = getPlayer(getStatsInstance().getLastPlayerId());
		
		// provera da li je igrac aktivan
		if(player != null && player.isActive() == false) {
			return null;
		}
		
		return player;
	}
	
	/**
	 * Removes inactive players and clears score lists for active players.
	 */
	public static void clearPlayerScores()
	{
		ArrayList<PlayerInfo> playerList = getAllPlayers();
		for (PlayerInfo playerInfo : playerList) {
			if(playerInfo.isActive()) {
				ZPlayerStats player = getStatsInstance().getPlayer(playerInfo.getPlayerId());
				player.clearGaindPoints();				
			}
			else {
				removePlayer(playerInfo.getPlayerId());
			}
				
			
		}
	}
}
