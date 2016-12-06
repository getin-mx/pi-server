
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetSessionListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetSessionListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MovieId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalMovieId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalMovieName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalOperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalClientClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalEventCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalBizStartHourOfDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OptionalBizDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalSessionDisplayCutOff" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetSessionListRequest", propOrder = {
    "cinemaId",
    "movieId",
    "optionalMovieId",
    "optionalMovieName",
    "optionalOperatorCode",
    "optionalClientClass",
    "optionalEventCode",
    "optionalBizStartHourOfDay",
    "optionalBizDate",
    "optionalSessionDisplayCutOff"
})
public class GetSessionListRequest {

    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "MovieId")
    protected String movieId;
    @XmlElement(name = "OptionalMovieId")
    protected String optionalMovieId;
    @XmlElement(name = "OptionalMovieName")
    protected String optionalMovieName;
    @XmlElement(name = "OptionalOperatorCode")
    protected String optionalOperatorCode;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;
    @XmlElement(name = "OptionalEventCode")
    protected String optionalEventCode;
    @XmlElement(name = "OptionalBizStartHourOfDay")
    protected int optionalBizStartHourOfDay;
    @XmlElement(name = "OptionalBizDate")
    protected String optionalBizDate;
    @XmlElement(name = "OptionalSessionDisplayCutOff")
    protected int optionalSessionDisplayCutOff;

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
     * Gets the value of the movieId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMovieId() {
        return movieId;
    }

    /**
     * Sets the value of the movieId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMovieId(String value) {
        this.movieId = value;
    }

    /**
     * Gets the value of the optionalMovieId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalMovieId() {
        return optionalMovieId;
    }

    /**
     * Sets the value of the optionalMovieId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalMovieId(String value) {
        this.optionalMovieId = value;
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
     * Gets the value of the optionalSessionDisplayCutOff property.
     * 
     */
    public int getOptionalSessionDisplayCutOff() {
        return optionalSessionDisplayCutOff;
    }

    /**
     * Sets the value of the optionalSessionDisplayCutOff property.
     * 
     */
    public void setOptionalSessionDisplayCutOff(int value) {
        this.optionalSessionDisplayCutOff = value;
    }

}
