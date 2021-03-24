package ZXMLUtils;

/**
 * 
 * @author Nenad Novkovic
 */
public class ZReader {
	
	protected LavirintoXMLDataReader dataReader = null;
	
	public ZReader(String fileName)
	{
		dataReader = new LavirintoXMLDataReader(fileName);
	}
}
