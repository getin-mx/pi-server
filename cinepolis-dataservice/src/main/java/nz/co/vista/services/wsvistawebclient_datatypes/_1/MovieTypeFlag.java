
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MovieTypeFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MovieTypeFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NotSet"/>
 *     &lt;enumeration value="Feature"/>
 *     &lt;enumeration value="NowShowing"/>
 *     &lt;enumeration value="ComingSoon"/>
 *     &lt;enumeration value="ComingSoonOnSale"/>
 *     &lt;enumeration value="ComingSoonNoSession"/>
 *     &lt;enumeration value="NowShowingToday"/>
 *     &lt;enumeration value="NowShowingOnSale"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MovieTypeFlag")
@XmlEnum
public enum MovieTypeFlag {

    @XmlEnumValue("NotSet")
    NOT_SET("NotSet"),
    @XmlEnumValue("Feature")
    FEATURE("Feature"),
    @XmlEnumValue("NowShowing")
    NOW_SHOWING("NowShowing"),
    @XmlEnumValue("ComingSoon")
    COMING_SOON("ComingSoon"),
    @XmlEnumValue("ComingSoonOnSale")
    COMING_SOON_ON_SALE("ComingSoonOnSale"),
    @XmlEnumValue("ComingSoonNoSession")
    COMING_SOON_NO_SESSION("ComingSoonNoSession"),
    @XmlEnumValue("NowShowingToday")
    NOW_SHOWING_TODAY("NowShowingToday"),
    @XmlEnumValue("NowShowingOnSale")
    NOW_SHOWING_ON_SALE("NowShowingOnSale");
    private final String value;

    MovieTypeFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MovieTypeFlag fromValue(String v) {
        for (MovieTypeFlag c: MovieTypeFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
