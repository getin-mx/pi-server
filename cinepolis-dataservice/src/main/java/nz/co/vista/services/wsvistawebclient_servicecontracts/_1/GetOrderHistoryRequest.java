
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetOrderHistoryRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetOrderHistoryRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LoyaltyMemberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetOrderHistoryRequest", propOrder = {
    "memberEmail",
    "loyaltyMemberId"
})
public class GetOrderHistoryRequest {

    @XmlElement(name = "MemberEmail")
    protected String memberEmail;
    @XmlElement(name = "LoyaltyMemberId")
    protected String loyaltyMemberId;

    /**
     * Gets the value of the memberEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemberEmail() {
        return memberEmail;
    }

    /**
     * Sets the value of the memberEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemberEmail(String value) {
        this.memberEmail = value;
    }

    /**
     * Gets the value of the loyaltyMemberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoyaltyMemberId() {
        return loyaltyMemberId;
    }

    /**
     * Sets the value of the loyaltyMemberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoyaltyMemberId(String value) {
        this.loyaltyMemberId = value;
    }

}
