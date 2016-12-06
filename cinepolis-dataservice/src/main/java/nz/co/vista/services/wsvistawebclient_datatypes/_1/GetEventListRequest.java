
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetEventListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetEventListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TypeFlag" type="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}MovieTypeFlag"/>
 *         &lt;element name="OptionalMovieName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalOperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalEventCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "GetEventListRequest", propOrder = {
    "cinemaId",
    "typeFlag",
    "optionalMovieName",
    "optionalOperatorCode",
    "optionalEventCode",
    "optionalClientClass"
})
public class GetEventListRequest {

    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "TypeFlag", required = true)
    protected MovieTypeFlag typeFlag;
    @XmlElement(name = "OptionalMovieName")
    protected String optionalMovieName;
    @XmlElement(name = "OptionalOperatorCode")
    protected String optionalOperatorCode;
    @XmlElement(name = "OptionalEventCode")
    protected String optionalEventCode;
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
     * Gets the value of the typeFlag property.
     * 
     * @return
     *     possible object is
     *     {@link MovieTypeFlag }
     *     
     */
    public MovieTypeFlag getTypeFlag() {
        return typeFlag;
    }

    /**
     * Sets the value of the typeFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovieTypeFlag }
     *     
     */
    public void setTypeFlag(MovieTypeFlag value) {
        this.typeFlag = value;
    }

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
     * Gets the value of the optionalEventCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalEventCode() {
        return optionalEventCode;
    }

    /**
     * Sets the value of the optionalEventCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalEventCode(String value) {
        this.optionalEventCode = value;
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
