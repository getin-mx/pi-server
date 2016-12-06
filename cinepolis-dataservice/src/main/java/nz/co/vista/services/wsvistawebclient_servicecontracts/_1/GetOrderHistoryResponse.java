
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetOrderHistoryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetOrderHistoryResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}Response">
 *       &lt;sequence>
 *         &lt;element name="NonPackageTickets" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}ArrayOfOrderHistoryLine" minOccurs="0"/>
 *         &lt;element name="PackageTickets" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}ArrayOfOrderHistoryLine" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetOrderHistoryResponse", propOrder = {
    "nonPackageTickets",
    "packageTickets"
})
public class GetOrderHistoryResponse
    extends Response
{

    @XmlElement(name = "NonPackageTickets")
    protected ArrayOfOrderHistoryLine nonPackageTickets;
    @XmlElement(name = "PackageTickets")
    protected ArrayOfOrderHistoryLine packageTickets;

    /**
     * Gets the value of the nonPackageTickets property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOrderHistoryLine }
     *     
     */
    public ArrayOfOrderHistoryLine getNonPackageTickets() {
        return nonPackageTickets;
    }

    /**
     * Sets the value of the nonPackageTickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOrderHistoryLine }
     *     
     */
    public void setNonPackageTickets(ArrayOfOrderHistoryLine value) {
        this.nonPackageTickets = value;
    }

    /**
     * Gets the value of the packageTickets property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOrderHistoryLine }
     *     
     */
    public ArrayOfOrderHistoryLine getPackageTickets() {
        return packageTickets;
    }

    /**
     * Sets the value of the packageTickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOrderHistoryLine }
     *     
     */
    public void setPackageTickets(ArrayOfOrderHistoryLine value) {
        this.packageTickets = value;
    }

}
