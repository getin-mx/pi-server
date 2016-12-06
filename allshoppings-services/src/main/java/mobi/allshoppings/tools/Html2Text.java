package mobi.allshoppings.tools;

import java.io.IOException;
import java.io.StringReader;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Take HTML and give back the text part while dropping the HTML tags.
 *
 * There is some risk that using TagSoup means we'll permute non-HTML text.
 * However, it seems to work the best so far in test cases.
 *
 * @author dan
 * @see <a href="http://home.ccil.org/~cowan/XML/tagsoup/">TagSoup</a> 
 */
public class Html2Text implements ContentHandler {
	private StringBuffer sb;

	public Html2Text() {
	}

	public void parse(String str) throws IOException, SAXException {
		str = str.replaceAll("\\<!--.*?-->","");
		XMLReader reader = new Parser();
		reader.setContentHandler(this);
		sb = new StringBuffer();
		reader.parse(new InputSource(new StringReader(str)));
	}

	public String getText() {
		return sb.toString();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		for (int idx = 0; idx < length; idx++) {
			sb.append(ch[idx+start]);
		}
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		sb.append(ch);
	}

	// The methods below do not contribute to the text
	public void endDocument() throws SAXException {
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}


	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void startDocument() throws SAXException {
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}
}