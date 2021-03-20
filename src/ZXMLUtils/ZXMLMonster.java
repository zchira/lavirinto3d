package ZXMLUtils;

/**
 * 
 * @author Nenad Novkovic
 */
public class ZXMLMonster extends ZXMLEntity {
	
	public static String DUMMY_CCW = "dummyCcw";
	
	private ZMonsterType type;
	
	
	public ZXMLMonster(String row, String column)
	{
		this(row, column, DUMMY_CCW);
	}
	
	public ZXMLMonster(String row, String column, String type)
	{
		super(row, column);
		setType(ZMonsterType.valueOf(type));		
	}
	
	
	public ZMonsterType getType() {
		return type;
	}

	public void setType(ZMonsterType type) {
		this.type = type;
	}
	
	
}
