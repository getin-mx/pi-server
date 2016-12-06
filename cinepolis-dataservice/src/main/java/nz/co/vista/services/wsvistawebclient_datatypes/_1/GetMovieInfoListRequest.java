
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetMovieInfoListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMovieInfoListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalMovieName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalTypeFlag" type="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}MovieTypeFlag"/>
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
@XmlType(name = "GetMovieInfoListRequest", propOrder = {
    "optionalMovieName",
    "optionalTypeFlag",
    "optionalClientClass"
})
public class GetMovieInfoListRequest {

    @XmlElement(name = "OptionalMovieName")
    protected String optionalMovieName;
    @XmlElement(name = "OptionalTypeFlag", required = true)
    protected MovieTypeFlag optionalTypeFlag;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;

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
     * Gets the value of the optionalTypeFlag property.
     * 
     * @return
     *     possible object is
     *     {@link MovieTypeFlag }
     *     
     */
    public MovieTypeFlag getOptionalTypeFlag() {
        return optionalTypeFlag;
    }

    /**
     * Sets the value of the optionalTypeFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovieTypeFlag }
     *     
     */
    public void setOptionalTypeFlag(MovieTypeFlag value) {
        this.optionalTypeFlag = value;
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
