
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetMovieListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMovieListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalCinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalOperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalOrderByOperator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalClientClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalBizDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalBizStartTimeOfDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OptionalIncludeGiftStores" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMovieListRequest", propOrder = {
    "optionalCinemaId",
    "optionalOperatorCode",
    "optionalOrderByOperator",
    "optionalClientClass",
    "optionalBizDate",
    "optionalBizStartTimeOfDay",
    "optionalIncludeGiftStores"
})
public class GetMovieListRequest {

    @XmlElement(name = "OptionalCinemaId")
    protected String optionalCinemaId;
    @XmlElement(name = "OptionalOperatorCode")
    protected String optionalOperatorCode;
    @XmlElement(name = "OptionalOrderByOperator")
    protected boolean optionalOrderByOperator;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;
    @XmlElement(name = "OptionalBizDate")
    protected String optionalBizDate;
    @XmlElement(name = "OptionalBizStartTimeOfDay")
    protected int optionalBizStartTimeOfDay;
    @XmlElement(name = "OptionalIncludeGiftStores")
    protected boolean optionalIncludeGiftStores;

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
     */
    public boolean isOptionalIncludeGiftStores() {
        return optionalIncludeGiftStores;
    }

    /**
     * Sets the value of the optionalIncludeGiftStores property.
     * 
     */
    public void setOptionalIncludeGiftStores(boolean value) {
        this.optionalIncludeGiftStores = value;
    }

}
