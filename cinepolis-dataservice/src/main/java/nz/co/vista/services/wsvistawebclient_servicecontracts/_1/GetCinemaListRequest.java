
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCinemaListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCinemaListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalMovieName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalOperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalIncludeOperator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalOrderByOperator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalClientClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalCinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalBizDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalBizStartTimeOfDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OptionalIncludeGiftStores" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCinemaListRequest", namespace = "http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1/", propOrder = {
    "optionalMovieName",
    "optionalOperatorCode",
    "optionalIncludeOperator",
    "optionalOrderByOperator",
    "optionalClientClass",
    "optionalCinemaId",
    "optionalBizDate",
    "optionalBizStartTimeOfDay",
    "optionalIncludeGiftStores"
})
public class GetCinemaListRequest {

    @XmlElement(name = "OptionalMovieName")
    protected String optionalMovieName;
    @XmlElement(name = "OptionalOperatorCode")
    protected String optionalOperatorCode;
    @XmlElement(name = "OptionalIncludeOperator")
    protected boolean optionalIncludeOperator;
    @XmlElement(name = "OptionalOrderByOperator")
    protected boolean optionalOrderByOperator;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;
    @XmlElement(name = "OptionalCinemaId")
    protected String optionalCinemaId;
    @XmlElement(name = "OptionalBizDate")
    protected String optionalBizDate;
    @XmlElement(name = "OptionalBizStartTimeOfDay")
    protected int optionalBizStartTimeOfDay;
    @XmlElement(name = "OptionalIncludeGiftStores")
    protected String optionalIncludeGiftStores;

    /**
     * Gets the value of the optionalMovieName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalMovieName() {
        return optionalMovieName;
    }

    /**
     * Sets the value of the optionalMovieName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalMovieName(String value) {
        this.optionalMovieName = value;
    }

    /**
     * Gets the value of the optionalOperatorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalOperatorCode() {
        return optionalOperatorCode;
    }

    /**
     * Sets the value of the optionalOperatorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalOperatorCode(String value) {
        this.optionalOperatorCode = value;
    }

    /**
     * Gets the value of the optionalIncludeOperator property.
     * 
     */
    public boolean isOptionalIncludeOperator() {
        return optionalIncludeOperator;
    }

    /**
     * Sets the value of the optionalIncludeOperator property.
     * 
     */
    public void setOptionalIncludeOperator(boolean value) {
        this.optionalIncludeOperator = value;
    }

    /**
     * Gets the value of the optionalOrderByOperator property.
     * 
     */
    public boolean isOptionalOrderByOperator() {
        return optionalOrderByOperator;
    }

    /**
     * Sets the value of the optionalOrderByOperator property.
     * 
     */
    public void setOptionalOrderByOperator(boolean value) {
        this.optionalOrderByOperator = value;
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
     * Gets the value of the optionalCinemaId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalCinemaId() {
        return optionalCinemaId;
    }

    /**
     * Sets the value of the optionalCinemaId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalCinemaId(String value) {
        this.optionalCinemaId = value;
    }

    /**
     * Gets the value of the optionalBizDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalBizDate() {
        return optionalBizDate;
    }

    /**
     * Sets the value of the optionalBizDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalBizDate(String value) {
        this.optionalBizDate = value;
    }

    /**
     * Gets the value of the optionalBizStartTimeOfDay property.
     * 
     */
    public int getOptionalBizStartTimeOfDay() {
        return optionalBizStartTimeOfDay;
    }

    /**
     * Sets the value of the optionalBizStartTimeOfDay property.
     * 
     */
    public void setOptionalBizStartTimeOfDay(int value) {
        this.optionalBizStartTimeOfDay = value;
    }

    /**
     * Gets the value of the optionalIncludeGiftStores property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalIncludeGiftStores() {
        return optionalIncludeGiftStores;
    }

    /**
     * Sets the value of the optionalIncludeGiftStores property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalIncludeGiftStores(String value) {
        this.optionalIncludeGiftStores = value;
    }

}
