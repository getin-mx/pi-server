
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPrintStreamRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPrintStreamRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserSessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrintDocumentType" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}PrintDocumentType"/>
 *         &lt;element name="PrintDocumentCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPrintStreamRequest", propOrder = {
    "userSessionId",
    "printDocumentType",
    "printDocumentCode"
})
public class GetPrintStreamRequest {

    @XmlElement(name = "UserSessionId")
    protected String userSessionId;
    @XmlElement(name = "PrintDocumentType", required = true)
    protected PrintDocumentType printDocumentType;
    @XmlElement(name = "PrintDocumentCode", required = true, type = Integer.class, nillable = true)
    protected Integer printDocumentCode;

    /**
     * Gets the value of the userSessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserSessionId() {
        return userSessionId;
    }

    /**
     * Sets the value of the userSessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserSessionId(String value) {
        this.userSessionId = value;
    }

    /**
     * Gets the value of the printDocumentType property.
     * 
     * @return
     *     possible object is
     *     {@link PrintDocumentType }
     *     
     */
    public PrintDocumentType getPrintDocumentType() {
        return printDocumentType;
    }

    /**
     * Sets the value of the printDocumentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrintDocumentType }
     *     
     */
    public void setPrintDocumentType(PrintDocumentType value) {
        this.printDocumentType = value;
    }

    /**
     * Gets the value of the printDocumentCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrintDocumentCode() {
        return printDocumentCode;
    }

    /**
     * Sets the value of the printDocumentCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrintDocumentCode(Integer value) {
        this.printDocumentCode = value;
    }

}
