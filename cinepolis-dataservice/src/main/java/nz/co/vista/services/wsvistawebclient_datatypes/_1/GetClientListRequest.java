
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetClientListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetClientListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalClientId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalClientDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetClientListRequest", propOrder = {
    "optionalClientId",
    "optionalClientDescription"
})
public class GetClientListRequest {

    @XmlElement(name = "OptionalClientId")
    protected String optionalClientId;
    @XmlElement(name = "OptionalClientDescription")
    protected String optionalClientDescription;

    /**
     * Gets the value of the optionalClientId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalClientId() {
        return optionalClientId;
    }

    /**
     * Sets the value of the optionalClientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalClientId(String value) {
        this.optionalClientId = value;
    }

    /**
     * Gets the value of the optionalClientDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalClientDescription() {
        return optionalClientDescription;
    }

    /**
     * Sets the value of the optionalClientDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalClientDescription(String value) {
        this.optionalClientDescription = value;
    }

}
