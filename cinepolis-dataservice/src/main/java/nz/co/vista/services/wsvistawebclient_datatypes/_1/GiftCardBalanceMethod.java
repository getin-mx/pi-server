
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GiftCardBalanceMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GiftCardBalanceMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CardNum"/>
 *     &lt;enumeration value="CardExpiry"/>
 *     &lt;enumeration value="CardExpiryMY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GiftCardBalanceMethod")
@XmlEnum
public enum GiftCardBalanceMethod {

    @XmlEnumValue("CardNum")
    CARD_NUM("CardNum"),
    @XmlEnumValue("CardExpiry")
    CARD_EXPIRY("CardExpiry"),
    @XmlEnumValue("CardExpiryMY")
    CARD_EXPIRY_MY("CardExpiryMY");
    private final String value;

    GiftCardBalanceMethod(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GiftCardBalanceMethod fromValue(String v) {
        for (GiftCardBalanceMethod c: GiftCardBalanceMethod.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
