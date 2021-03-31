package ZXMLUtils;

/**
 * 
 * @author Nenad Novkovic
 */
public class ZReader {

	protected LavirintoXMLDataReader dataReader = null;

	public ZReader(String resource, boolean path) {
		if (path) {
			dataReader = LavirintoXMLDataReader.fromResourcePath(resource);
		} else {
			dataReader = LavirintoXMLDataReader.fromXmlContent(resource);
		}
	}
}
