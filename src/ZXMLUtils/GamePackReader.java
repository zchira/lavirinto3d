package ZXMLUtils;

import levelgenerator.LevelGenerator;

/**
 * 
 * @author Nenad Novkovic
 */
public class GamePackReader extends ZReader {

	/*
	 * XML Element names
	 */
	private static String PACK_NAME = "packname";
	private static String DESCRIPTION = "description";
	private static String LEVEL = "levelfile";
	private static String START_STORY = "startstory";
	private static String END_STORY = "endstory";
	private static String MODE = "mode";

	public GamePackReader(String gamePackFileName) {
		super(gamePackFileName, true);
	}

	public String getPackName() {
		return dataReader.getValueForElement(PACK_NAME);
	}

	public String getPackDescription() {
		return dataReader.getValueForElement(DESCRIPTION);
	}

	public String getStartStory() {
		String startStory = dataReader.getValueForElement(START_STORY);
		if (startStory == null) {
			return "";
		}
		return startStory;
	}

	public String getEndStory() {
		String endStory = dataReader.getValueForElement(END_STORY);
		if (endStory == null) {
			return "";
		}
		return endStory;
	}

	public int getLevelCount() {
		if (getMode() == null) {
			return dataReader.getElementCount(LEVEL);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * 
	 * @return endless for randomly generated levels
	 */
	public String getMode() {
		String mode = dataReader.getValueForElement(MODE);
		return mode;
	}

	private int cachedLevel = -1;
	private LevelReader cachedLevelReader = null;

	public LevelReader getLevel(int level) {
		if (level == cachedLevel && cachedLevelReader != null) {
			return cachedLevelReader;
		}

		cachedLevel = level;
		String mode = getMode();
		if ("endless".equals(mode)) {
			int w = level + 3;
			int h = level + 3;
			int max = w * h;
			int seed = level * 7;
			LevelGenerator lg = new LevelGenerator(w, h, seed, max);
			String xmlLevel = lg.getLevelXml();
			cachedLevelReader = LevelReader.fromXmlString(xmlLevel);
		} else {
			String levelFile = dataReader.getValueForElement(LEVEL, level);
			cachedLevelReader = LevelReader.fromLevelPath(levelFile);
		}
		return cachedLevelReader;
	}
}
