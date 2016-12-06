
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCinemaSiteGroupsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCinemaSiteGroupsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CinemaSiteGroups" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}ArrayOfCinemaSiteGroupLine" minOccurs="0"/>
 *         &lt;element name="CinemaSiteGroupLinks" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}ArrayOfCinemaSiteGroupLinkLine" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCinemaSiteGroupsResponse", propOrder = {
    "resultCode",
    "cinemaSiteGroups",
    "cinemaSiteGroupLinks"
})
public class GetCinemaSiteGroupsResponse {

    @XmlElement(name = "ResultCode")
    protected int resultCode;
    @XmlElement(name = "CinemaSiteGroups")
    protected ArrayOfCinemaSiteGroupLine cinemaSiteGroups;
    @XmlElement(name = "CinemaSiteGroupLinks")
    protected ArrayOfCinemaSiteGroupLinkLine cinemaSiteGroupLinks;

    /**
     * Gets the value of the resultCode property.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the cinemaSiteGroups property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCinemaSiteGroupLine }
     *     
     */
    public ArrayOfCinemaSiteGroupLine getCinemaSiteGroups() {
        return cinemaSiteGroups;
    }

    /**
     * Sets the value of the cinemaSiteGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCinemaSiteGroupLine }
     *     
     */
    public void setCinemaSiteGroups(ArrayOfCinemaSiteGroupLine value) {
        this.cinemaSiteGroups = value;
    }

    /**
     * Gets the value of the cinemaSiteGroupLinks property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCinemaSiteGroupLinkLine }
     *     
     */
    public ArrayOfCinemaSiteGroupLinkLine getCinemaSiteGroupLinks() {
        return cinemaSiteGroupLinks;
    }

    /**
     * Sets the value of the cinemaSiteGroupLinks property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCinemaSiteGroupLinkLine }
     *     
     */
    public void setCinemaSiteGroupLinks(ArrayOfCinemaSiteGroupLinkLine value) {
        this.cinemaSiteGroupLinks = value;
    }

}
