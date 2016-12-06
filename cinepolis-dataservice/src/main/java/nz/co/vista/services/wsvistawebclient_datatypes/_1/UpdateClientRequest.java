
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateClientRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateClientRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}CreateClientRequest">
 *       &lt;sequence>
 *         &lt;element name="Status" type="{http://microsoft.com/wsdl/types/}char"/>
 *         &lt;element name="ConfigXml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateClientRequest", propOrder = {
    "status",
    "configXml"
})
public class UpdateClientRequest
    extends CreateClientRequest
{

    @XmlElement(name = "Status", required = true, type = Integer.class, nillable = true)
    protected Integer status;
    @XmlElement(name = "ConfigXml")
    protected String configXml;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

    /**
     * Gets the value of the configXml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigXml() {
        return configXml;
    }

    /**
     * Sets the value of the configXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigXml(String value) {
        this.configXml = value;
    }

}
