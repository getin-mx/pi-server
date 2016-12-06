
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConfirmationDetailsType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ConfirmationDetailsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Booking"/>
 *     &lt;enumeration value="Tickets"/>
 *     &lt;enumeration value="Seats"/>
 *     &lt;enumeration value="Inventory"/>
 *     &lt;enumeration value="PaymentLines"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ConfirmationDetailsType")
@XmlEnum
public enum ConfirmationDetailsType {

    @XmlEnumValue("Booking")
    BOOKING("Booking"),
    @XmlEnumValue("Tickets")
    TICKETS("Tickets"),
    @XmlEnumValue("Seats")
    SEATS("Seats"),
    @XmlEnumValue("Inventory")
    INVENTORY("Inventory"),
    @XmlEnumValue("PaymentLines")
    PAYMENT_LINES("PaymentLines");
    private final String value;

    ConfirmationDetailsType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConfirmationDetailsType fromValue(String v) {
        for (ConfirmationDetailsType c: ConfirmationDetailsType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
