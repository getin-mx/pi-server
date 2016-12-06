
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetConfigSettingListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetConfigSettingListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SettingNames" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetConfigSettingListRequest", propOrder = {
    "settingNames"
})
public class GetConfigSettingListRequest {

    @XmlElement(name = "SettingNames")
    protected ArrayOfString settingNames;

    /**
     * Gets the value of the settingNames property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getSettingNames() {
        return settingNames;
    }

    /**
     * Sets the value of the settingNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setSettingNames(ArrayOfString value) {
        this.settingNames = value;
    }

}
