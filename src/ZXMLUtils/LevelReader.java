package ZXMLUtils;

import java.util.ArrayList;

import zgame.ZColor;
/**
 * 
 * @author Nenad Novkovic
 */
public class LevelReader extends ZReader{

	/*
	 * XML Element names
	 */
	private static String LEVEL_NAME = "levelname";
	private static String LEVEL_DESCRIPTION = "description";
	private static String LEVEL_WIDTH = "width";
	private static String LEVEL_HEIGHT = "height";
	private static String LEVEL_BOARDDATA = "boarddata";
	private static String PLAYER_TAG ="player";
	private static String X_CORD = "x_cord";
	private static String Y_CORD = "y_cord";
	private static String MONSTER_TAG = "monster";
	private static String MONSTER_TYPE = "type";
	private static String TIME = "time";
	private static String BACKGROUND_IMAGE = "backgroundimage";
	private static String CONECTED_FIELDS_COLOR = "conectedfieldscolor";
	private static String DISCONECTED_FIELDS_COLOR = "disconectedfieldscolor";
	private static String RED = "red";
	private static String GREEN = "green";
	private static String BLUE = "blue";
	
	

	/*
	 * Board data fields
	 */
	private static String EMPTIY_FIELD = "0";

	private static String FULL_FIELD = "1";


	public LevelReader(String levelName)
	{
		super(levelName);
	}

	public String getLevelName()
	{
		String value = dataReader.getValueForElement(LEVEL_NAME); 
		if (value == null) {
			return "Level name";
		}
			
		return value; 
	}

	public String getLevelDescription()
	{
		String value = dataReader.getValueForElement(LEVEL_DESCRIPTION);
		if (value == null) {
			return "Level description";
		}
		
		return value;
	}

	public int getLevelWidth()
	{
		String width = dataReader.getValueForElement(LEVEL_WIDTH);
		
		if (width == null) {
			throw new ZXMLException("Element: " + LEVEL_WIDTH + " must be defined.");
		}
		
		return Integer.parseInt(width);
	}

	public int getLevelHeight()
	{
		String height = dataReader.getValueForElement(LEVEL_HEIGHT);
		if (height == null) {
			throw new ZXMLException("Element: " + LEVEL_HEIGHT + " must be defined.");
		}
		return Integer.parseInt(height);
	}

	/**
	 * Na osnovu matrice koja definise tablu i pozicije igraca i cudovista
	 * formira se matrica koja definise samo izgled table. Podrazumeva se da
	 * polja na koja se nalaze igraci i cudovista postoje. 
	 * @return
	 */
	public TableFieldType[][] getLevelBoardField()
	{
		String[][] boardData = getLevelBoardData();
		int height = getLevelHeight();
		int width = getLevelWidth();

		TableFieldType[][] tableData = new TableFieldType[width][height];

		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				String cell = boardData[i][j];
				if (cell.equals(EMPTIY_FIELD)) {
					tableData[i][j] = TableFieldType.EmptiyField;
				}
				else if (cell.equals(FULL_FIELD)) {
					tableData[i][j] = TableFieldType.FullField;
				}
			}
		}

		return tableData;
	}

	/**
	 * Na osnovu polja LEVEL_BOARDDATA u XML fajlu fomira matricu koja definise izgled
	 * table. U ovoj matrici se cuvaju i informacije o igracima i cudovistima.
	 * @return
	 */
	private String[][] getLevelBoardData()
	{
		String nativBoardData = dataReader.getValueForElement(LEVEL_BOARDDATA);
		
		if (nativBoardData == null) {
			throw new ZXMLException("Element: " + LEVEL_BOARDDATA + " must be defined.");
		}		
		
		String clearBoardData = removeWhiteCharacters(nativBoardData);

		int height = getLevelHeight();
		int width = getLevelWidth();

		String[][] boardData = new String[width][height];

		for(int i = 0; i < clearBoardData.length(); i++)
		{
			int row = i / width;
			int column = i % width;
			String field = clearBoardData.substring(i, i + 1);
			boardData[column][row] = field;
		}

		return boardData;
	}

	// FIXME nesha: mislim da ovo moze lepse da se napise (mozda neki regularni izraz)
	private String removeWhiteCharacters(String s)
	{
		String s2 = s.replace("\t", "");
		String s3 = s2.replace(" ", "");
		String s4 = s3.replace("\n", "");
		return s4;
	}
	
	/**
	 * Cita podatke o igracu.
	 * @return
	 */
	public ZXMLPlayer getPlayerData()
	{
		String x_cord = dataReader.getValueForElement(PLAYER_TAG, X_CORD);
		String y_cord = dataReader.getValueForElement(PLAYER_TAG, Y_CORD);
		
		if(x_cord == null || y_cord == null) {
			return null;
		}

		return new ZXMLPlayer(x_cord, y_cord);
	}
	
	public ArrayList<ZXMLMonster> getMonstersData()
	{
		ArrayList<ZXMLMonster> toReturn = new ArrayList<ZXMLMonster>();
		int monsterCount = dataReader.getElementCount(MONSTER_TAG);
		
		for(int i = 0; i < monsterCount; i++) {
			String x_cord = dataReader.getValueForElement(MONSTER_TAG, X_CORD, i);
			String y_cord = dataReader.getValueForElement(MONSTER_TAG, Y_CORD, i);
			
			String type = dataReader.getValueForElement(MONSTER_TYPE, i);
			
			// samo ako postoje kordinate.
			if (x_cord != null && y_cord != null) {
				if (type == null || type.equals("")) {
					toReturn.add(new ZXMLMonster(x_cord, y_cord));
				}
				else {
					toReturn.add(new ZXMLMonster(x_cord, y_cord, type));
				}
					
			}
		}			
		
		return toReturn;
	}
	
	/**
	 * Boja obidjenih polja. Default: Color.BLUE 
	 * @return
	 */
	public ZColor getConectedFieldsColor()
	{
		String red = dataReader.getValueForElement(CONECTED_FIELDS_COLOR, RED);
		String green = dataReader.getValueForElement(CONECTED_FIELDS_COLOR, GREEN);
		String blue = dataReader.getValueForElement(CONECTED_FIELDS_COLOR, BLUE);
		
		if (red == null || green == null || blue == null) {
			return new ZColor(0, 0, 255);
		}
		
		return new ZColor(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));		
	}
	
	/**
	 * Boja ne obidjenih polja. Default Color.GRAY
	 * @return
	 */
	public ZColor getDisconectedFieldsColor()
	{
		String red = dataReader.getValueForElement(DISCONECTED_FIELDS_COLOR, RED);
		String green = dataReader.getValueForElement(DISCONECTED_FIELDS_COLOR, GREEN);
		String blue = dataReader.getValueForElement(DISCONECTED_FIELDS_COLOR, BLUE);
		
		if (red == null || green == null || blue == null) {
			return new ZColor(166, 166, 166);
		}
		
		return new ZColor(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));		
	}
	
	/**
	 * vreme u sekundama. Defalut 200s.
	 * @return
	 */
	public int getTime()
	{
		String time = dataReader.getValueForElement(TIME);
		if(time == null) {
			return 200;
		}
		
		return Integer.parseInt(time);
	}
	
	public String getBackgrounImage()
	{
		return dataReader.getValueForElement(BACKGROUND_IMAGE);
	}



}
