package ZXMLUtils;

/** 
 * Retriving GamePacks. List of game pack are defined in XML file.
 * For evry game pack shoud be stored data for name and file location.
 * Evry GamePack hav
 *   
 * @author Nenad Novkovic
 *
 */
public class LevelPacksReader extends ZReader {

	private static String GAME_PACK = "gamepack";
	private static String GAME_PACK_NAME = "name";
	private static String GAME_PACK_FILE = "file";
	
	
	public LevelPacksReader(String fileName) {
		super(fileName, true);		
	}
	
	public int getGamePacksCount() 
	{
		return dataReader.getElementCount(GAME_PACK);
	}
	
	public String getGamePackName(int gamePackPosition)
	{
		return dataReader.getValueForElement(GAME_PACK_NAME, gamePackPosition);
	}
	
	public GamePackReader getGamePack(int gamePackPosition)
	{
		String fileName = dataReader.getValueForElement(GAME_PACK_FILE, gamePackPosition);
		return new GamePackReader(fileName);
	}
}
