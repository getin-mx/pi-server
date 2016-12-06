
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetShowtimeDateListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetShowtimeDateListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalBizStartHourOfDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetShowtimeDateListRequest", propOrder = {
    "optionalBizStartHourOfDay",
    "cinemaId"
})
public class GetShowtimeDateListRequest {

    @XmlElement(name = "OptionalBizStartHourOfDay")
    protected int optionalBizStartHourOfDay;
    @XmlElement(name = "CinemaId")
    protected String cinemaId;

    /**
     * Gets the value of the optionalBizStartHourOfDay property.
     * 
     */
    public int getOptionalBizStartHourOfDay() {
        return optionalBizStartHourOfDay;
    }

    /**
     * Sets the value of the optionalBizStartHourOfDay property.
     * 
     */
    public void setOptionalBizStartHourOfDay(int value) {
        this.optionalBizStartHourOfDay = value;
    }

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

}
