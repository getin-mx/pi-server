
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCinemaListAllRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCinemaListAllRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalIncludeOperator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalOrderByOperator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "GetCinemaListAllRequest", namespace = "http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1/", propOrder = {
    "optionalIncludeOperator",
    "optionalOrderByOperator",
    "optionalIncludeGiftStores"
})
public class GetCinemaListAllRequest {

    @XmlElement(name = "OptionalIncludeOperator")
    protected boolean optionalIncludeOperator;
    @XmlElement(name = "OptionalOrderByOperator")
    protected boolean optionalOrderByOperator;
    @XmlElement(name = "OptionalIncludeGiftStores")
    protected String optionalIncludeGiftStores;

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
