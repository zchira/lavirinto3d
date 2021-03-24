package zgame;

import java.util.ArrayList;

import ZXMLUtils.GamePackReader;
import ZXMLUtils.LevelReader;

/**
 * 
 * @author zchira
 */
public class Status {
	private static int lives;
	
	private static long points;
	
	private static int level;
	
	private static ArrayList<String> levels;
	
	private static PlayngState gameState;
	
	/**
	 * cuva stanje u koje se vraca posle pauze.
	 */
	private static PlayngState backupState;
	
	private static GamePackReader gamePack;
	
	public enum PlayngState{
		gameover, lifelost, play, allFieldsConnected, levelCompleted, _showLevelInfo, levelPackCompleted, paused
	}
	
	public static void resetStatus(){
		gameState = PlayngState.play;
		lives = 3;
		points = 0;
//		level = 0;
	}

	public static int getLives() {
		return lives;
	}

	public static void setLives(int lives) {
		Status.lives = lives;
	}

	public static long getPoints() {
		return points;
	}

	public static void setPoints(long points) {
		Status.points = points;
	}
	
	public static void addPoints(long p) {
		Status.points += p;
	}
	

	public static int getLevel() {
		return level;
	}

	public static void setLevel(int level) {
		Status.level = level;
	}

	public static PlayngState getPlayngState() {
		return gameState;
	}

	public static void setPlayngState(PlayngState gameState) {
		Status.gameState = gameState;
	}
	
	public static void nextLevel(){
		level++;
	}
	
	public static LevelReader getCurrentLevel(){
		if (level < getGamePack().getLevelCount()){
			return getGamePack().getLevel(level);
		}
		return null;
	}
	
	public static void setCurrentLevel(int l){
		if (l >= 0 &&  l <= getGamePack().getLevelCount()){
			level = l;
		}else{
			level = 0;
		}
		
	}

	
	@Deprecated
	public static ArrayList<String> getLevels() {
		if (levels == null){
			levels = new ArrayList<String>();
			levels.add("/res/level1.xml");
			levels.add("/res/level2.xml");
			levels.add("/res/level3.xml");
		}
		return levels;
	}

	@Deprecated
	public static void setLevels(ArrayList<String> lev) {
		levels = lev;
	}

	public static GamePackReader getGamePack() {
		return gamePack;
	}

	public static void setGamePack(GamePackReader gamePack) {
		Status.gamePack = gamePack;
	}
	
	public static void pauseGame(){
		backupState = gameState;
		gameState = PlayngState.paused;
	}
	
	public static void unPauseGame(){
		gameState = backupState;
	}
	
}
