
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetTicketTypePackageRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetTicketTypePackageRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TicketTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetTicketTypePackageRequest", propOrder = {
    "cinemaId",
    "sessionId",
    "ticketTypeCode"
})
public class GetTicketTypePackageRequest {

    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "SessionId")
    protected String sessionId;
    @XmlElement(name = "TicketTypeCode")
    protected String ticketTypeCode;

    /**
     * Gets the value of the cinemaId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCinemaId() {
        return cinemaId;
    }

    /**
     * Sets the value of the cinemaId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCinemaId(String value) {
        this.cinemaId = value;
    }

    /**
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the ticketTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketTypeCode() {
        return ticketTypeCode;
    }

    /**
     * Sets the value of the ticketTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketTypeCode(String value) {
        this.ticketTypeCode = value;
    }

}
