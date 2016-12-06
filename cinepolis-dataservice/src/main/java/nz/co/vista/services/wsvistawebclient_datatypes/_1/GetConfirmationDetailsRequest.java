
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetConfirmationDetailsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetConfirmationDetailsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ConfirmationDetailsType" type="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}ConfirmationDetailsType"/>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VistaBookingNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VistaTransNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalHistoryID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetConfirmationDetailsRequest", propOrder = {
    "confirmationDetailsType",
    "cinemaId",
    "vistaBookingNumber",
    "vistaTransNumber",
    "optionalHistoryID"
})
public class GetConfirmationDetailsRequest {

    @XmlElement(name = "ConfirmationDetailsType", required = true)
    protected ConfirmationDetailsType confirmationDetailsType;
    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "VistaBookingNumber")
    protected String vistaBookingNumber;
    @XmlElement(name = "VistaTransNumber")
    protected String vistaTransNumber;
    @XmlElement(name = "OptionalHistoryID")
    protected String optionalHistoryID;

    /**
     * Gets the value of the confirmationDetailsType property.
     * 
     * @return
     *     possible object is
     *     {@link ConfirmationDetailsType }
     *     
     */
    public ConfirmationDetailsType getConfirmationDetailsType() {
        return confirmationDetailsType;
    }

    /**
     * Sets the value of the confirmationDetailsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfirmationDetailsType }
     *     
     */
    public void setConfirmationDetailsType(ConfirmationDetailsType value) {
        this.confirmationDetailsType = value;
    }

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
     * Gets the value of the vistaBookingNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVistaBookingNumber() {
        return vistaBookingNumber;
    }

    /**
     * Sets the value of the vistaBookingNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVistaBookingNumber(String value) {
        this.vistaBookingNumber = value;
    }

    /**
     * Gets the value of the vistaTransNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVistaTransNumber() {
        return vistaTransNumber;
    }

    /**
     * Sets the value of the vistaTransNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVistaTransNumber(String value) {
        this.vistaTransNumber = value;
    }

    /**
     * Gets the value of the optionalHistoryID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalHistoryID() {
        return optionalHistoryID;
    }

    /**
     * Sets the value of the optionalHistoryID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalHistoryID(String value) {
        this.optionalHistoryID = value;
    }

}
