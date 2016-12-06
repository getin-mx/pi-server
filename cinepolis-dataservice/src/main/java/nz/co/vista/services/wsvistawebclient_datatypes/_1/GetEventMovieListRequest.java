
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetEventMovieListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetEventMovieListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EventCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalClientClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetEventMovieListRequest", propOrder = {
    "cinemaId",
    "eventCode",
    "optionalClientClass"
})
public class GetEventMovieListRequest {

    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "EventCode")
    protected String eventCode;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;

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
     * Gets the value of the eventCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * Sets the value of the eventCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventCode(String value) {
        this.eventCode = value;
    }

    /**
     * Gets the value of the optionalClientClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalClientClass() {
        return optionalClientClass;
    }

    /**
     * Sets the value of the optionalClientClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalClientClass(String value) {
        this.optionalClientClass = value;
    }

}
