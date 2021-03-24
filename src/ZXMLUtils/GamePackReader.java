package ZXMLUtils;


/**
 * 
 * @author Nenad Novkovic
 */
public class GamePackReader extends ZReader
{

	/*
	 * XML Element names
	 */
	private static String PACK_NAME = "packname";
	private static String DESCRIPTION = "description";
	private static String LEVEL = "levelfile";
	private static String START_STORY = "startstory";
	private static String END_STORY = "endstory";
	
	
	public GamePackReader(String gamePackFileName)
	{
		super(gamePackFileName);
	}
	
	public String getPackName()
	{
		return dataReader.getValueForElement(PACK_NAME);
	}
	
	public String getPackDescription()
	{
		return dataReader.getValueForElement(DESCRIPTION);
	}
	
	public String getStartStory()
	{
		String startStory = dataReader.getValueForElement(START_STORY);
		if (startStory == null) {
			return "";
		}
		return startStory;
	}
	
	public String getEndStory()
	{
		String endStory = dataReader.getValueForElement(END_STORY);
		if (endStory == null) {
			return "";
		}
		return endStory;
	}
	
	public int getLevelCount()
	{
		return dataReader.getElementCount(LEVEL);
	}
	
	public LevelReader getLevel(int level)
	{
		String levelFile = dataReader.getValueForElement(LEVEL, level);
		return new LevelReader(levelFile);
	}
}
