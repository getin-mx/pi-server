
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetMovieShowtimesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMovieShowtimesRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BizDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BizStartTimeOfDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OptionalClientClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrderMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalSessionDisplayCutOffInMins" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AllSalesChannels" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMovieShowtimesRequest", propOrder = {
    "cinemaId",
    "bizDate",
    "bizStartTimeOfDay",
    "optionalClientClass",
    "orderMode",
    "optionalSessionDisplayCutOffInMins",
    "allSalesChannels"
})
public class GetMovieShowtimesRequest {

    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "BizDate")
    protected String bizDate;
    @XmlElement(name = "BizStartTimeOfDay")
    protected int bizStartTimeOfDay;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;
    @XmlElement(name = "OrderMode")
    protected String orderMode;
    @XmlElement(name = "OptionalSessionDisplayCutOffInMins")
    protected String optionalSessionDisplayCutOffInMins;
    @XmlElement(name = "AllSalesChannels")
    protected boolean allSalesChannels;

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
     * Gets the value of the bizDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBizDate() {
        return bizDate;
    }

    /**
     * Sets the value of the bizDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBizDate(String value) {
        this.bizDate = value;
    }

    /**
     * Gets the value of the bizStartTimeOfDay property.
     * 
     */
    public int getBizStartTimeOfDay() {
        return bizStartTimeOfDay;
    }

    /**
     * Sets the value of the bizStartTimeOfDay property.
     * 
     */
    public void setBizStartTimeOfDay(int value) {
        this.bizStartTimeOfDay = value;
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

    /**
     * Gets the value of the orderMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderMode() {
        return orderMode;
    }

    /**
     * Sets the value of the orderMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderMode(String value) {
        this.orderMode = value;
    }

    /**
     * Gets the value of the optionalSessionDisplayCutOffInMins property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalSessionDisplayCutOffInMins() {
        return optionalSessionDisplayCutOffInMins;
    }

    /**
     * Sets the value of the optionalSessionDisplayCutOffInMins property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalSessionDisplayCutOffInMins(String value) {
        this.optionalSessionDisplayCutOffInMins = value;
    }

    /**
     * Gets the value of the allSalesChannels property.
     * 
     */
    public boolean isAllSalesChannels() {
        return allSalesChannels;
    }

    /**
     * Sets the value of the allSalesChannels property.
     * 
     */
    public void setAllSalesChannels(boolean value) {
        this.allSalesChannels = value;
    }

}
