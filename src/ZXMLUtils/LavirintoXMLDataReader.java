package ZXMLUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Nenad Novkovic
 */
public class LavirintoXMLDataReader {

	private Document doc;

	private LavirintoXMLDataReader(String fileName) {
		try {
			InputStream is = getClass().getResourceAsStream(fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private LavirintoXMLDataReader(InputStream is) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static LavirintoXMLDataReader fromResourcePath(String fileName) {
		return new LavirintoXMLDataReader(fileName);

	}

	public static LavirintoXMLDataReader fromXmlContent(String xml) {
		InputStream targetStream = new ByteArrayInputStream(xml.getBytes());
		return new LavirintoXMLDataReader(targetStream);
	}

	public String getValueForElement(String elementName) {
		return getValueForElement(elementName, 0);
	}

	public String getValueForElement(String elementName, int position) {
		String toReturn = null;
		NodeList nodeList = doc.getElementsByTagName(elementName);
		Node node = nodeList.item(position);

		if (node == null) {
			return null;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) node;
			NodeList l3 = e.getChildNodes();
			Node n = l3.item(0);
			toReturn = n.getNodeValue();
		}

		// obrisem tabulatore
		return toReturn.replace("\t", "");
	}

	public String getValueForElement(String rootElement, String subElement) {
		return getValueForElement(rootElement, subElement, 0);
	}

	/**
	 * Ova funkcija zna da pribavi vrednost iz sledece strukture.
	 * 
	 * <!--<rootTag> 0 ti tag <subTag1>value1</subTag1> <subTag2>value2</subTag2>
	 * </rootTag> <rootTag>1 ti tag--> . . . </rootTag>
	 * 
	 * Pribavlja vrednost sub elementa koji je u okviru root taga na odgovarajucoj
	 * poziciji.
	 * 
	 * @param rootElement
	 * @param subElement
	 * @param rootElementPosition
	 * @return
	 */
	public String getValueForElement(String rootElement, String subElement, int rootElementPosition) {
		String toReturn = null;
		NodeList nodeList = doc.getElementsByTagName(rootElement);
		Node node = nodeList.item(rootElementPosition);

		if (node == null) {
			return null;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) node;
			NodeList childNodes = e.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node subNode = childNodes.item(i);
				// ako je element node i bas je trazeni subnod
				if ((subNode.getNodeType() == Node.ELEMENT_NODE) && subNode.getNodeName().equals(subElement)) {
					Element subE = (Element) subNode;
					Node n = subE.getChildNodes().item(0);
					toReturn = n.getNodeValue();
					break;
				}
			}
		}

		// obrisem tabulatore
		return toReturn.replace("\t", "");
	}

	public int getElementCount(String elementName) {
		NodeList nodeList = doc.getElementsByTagName(elementName);
		return nodeList.getLength();
	}

}
