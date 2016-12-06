
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrintDocumentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PrintDocumentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="All"/>
 *     &lt;enumeration value="BookingVoucher"/>
 *     &lt;enumeration value="Receipt"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PrintDocumentType")
@XmlEnum
public enum PrintDocumentType {

    @XmlEnumValue("All")
    ALL("All"),
    @XmlEnumValue("BookingVoucher")
    BOOKING_VOUCHER("BookingVoucher"),
    @XmlEnumValue("Receipt")
    RECEIPT("Receipt");
    private final String value;

    PrintDocumentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PrintDocumentType fromValue(String v) {
        for (PrintDocumentType c: PrintDocumentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
