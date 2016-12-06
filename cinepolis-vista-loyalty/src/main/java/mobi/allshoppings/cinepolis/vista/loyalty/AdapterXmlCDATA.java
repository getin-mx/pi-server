package mobi.allshoppings.cinepolis.vista.loyalty;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * to parse CDATA values.
 * 
 * @author Laura Liparulo
 */
public class AdapterXmlCDATA extends XmlAdapter<String, String> {

    @Override
    public String marshal(String value) throws Exception {
        return "<![CDATA[" + value + "]]>";
    }
    @Override
    public String unmarshal(String value) throws Exception {
        return value;
    }

}
