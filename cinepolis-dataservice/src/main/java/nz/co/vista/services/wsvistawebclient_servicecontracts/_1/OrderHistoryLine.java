
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for OrderHistoryLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderHistoryLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InitiatedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="BookingNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CinemaName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CinemaName2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MovieName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MoveNameAlt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TicketPriceInCents" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TicketDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TicketDescriptionAlt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SessionTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SeatRowId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SeatNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BookingFeeInCents" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsPaid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackageGroupNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ParentTicketTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentTicketDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentTicketDescriptionAlt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackageSeats" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}ArrayOfSeatInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderHistoryLine", propOrder = {
    "initiatedTime",
    "bookingNumber",
    "cinemaName",
    "cinemaName2",
    "movieName",
    "moveNameAlt",
    "ticketPriceInCents",
    "ticketDescription",
    "ticketDescriptionAlt",
    "sessionTime",
    "seatRowId",
    "seatNumber",
    "bookingFeeInCents",
    "isPaid",
    "cinemaId",
    "packageGroupNumber",
    "parentTicketTypeCode",
    "parentTicketDescription",
    "parentTicketDescriptionAlt",
    "packageSeats"
})
public class OrderHistoryLine {

    @XmlElement(name = "InitiatedTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar initiatedTime;
    @XmlElement(name = "BookingNumber")
    protected String bookingNumber;
    @XmlElement(name = "CinemaName")
    protected String cinemaName;
    @XmlElement(name = "CinemaName2")
    protected String cinemaName2;
    @XmlElement(name = "MovieName")
    protected String movieName;
    @XmlElement(name = "MoveNameAlt")
    protected String moveNameAlt;
    @XmlElement(name = "TicketPriceInCents")
    protected int ticketPriceInCents;
    @XmlElement(name = "TicketDescription")
    protected String ticketDescription;
    @XmlElement(name = "TicketDescriptionAlt")
    protected String ticketDescriptionAlt;
    @XmlElement(name = "SessionTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sessionTime;
    @XmlElement(name = "SeatRowId")
    protected String seatRowId;
    @XmlElement(name = "SeatNumber")
    protected String seatNumber;
    @XmlElement(name = "BookingFeeInCents")
    protected int bookingFeeInCents;
    @XmlElement(name = "IsPaid")
    protected boolean isPaid;
    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "PackageGroupNumber")
    protected int packageGroupNumber;
    @XmlElement(name = "ParentTicketTypeCode")
    protected String parentTicketTypeCode;
    @XmlElement(name = "ParentTicketDescription")
    protected String parentTicketDescription;
    @XmlElement(name = "ParentTicketDescriptionAlt")
    protected String parentTicketDescriptionAlt;
    @XmlElement(name = "PackageSeats")
    protected ArrayOfSeatInfo packageSeats;

    /**
     * Gets the value of the initiatedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInitiatedTime() {
        return initiatedTime;
    }

    /**
     * Sets the value of the initiatedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInitiatedTime(XMLGregorianCalendar value) {
        this.initiatedTime = value;
    }

    /**
     * Gets the value of the bookingNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingNumber() {
        return bookingNumber;
    }

    /**
     * Sets the value of the bookingNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingNumber(String value) {
        this.bookingNumber = value;
    }

    /**
     * Gets the value of the cinemaName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCinemaName() {
        return cinemaName;
    }

    /**
     * Sets the value of the cinemaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCinemaName(String value) {
        this.cinemaName = value;
    }

    /**
     * Gets the value of the cinemaName2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCinemaName2() {
        return cinemaName2;
    }

    /**
     * Sets the value of the cinemaName2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCinemaName2(String value) {
        this.cinemaName2 = value;
    }

    /**
     * Gets the value of the movieName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMovieName() {
        return movieName;
    }

    /**
     * Sets the value of the movieName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMovieName(String value) {
        this.movieName = value;
    }

    /**
     * Gets the value of the moveNameAlt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoveNameAlt() {
        return moveNameAlt;
    }

    /**
     * Sets the value of the moveNameAlt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoveNameAlt(String value) {
        this.moveNameAlt = value;
    }

    /**
     * Gets the value of the ticketPriceInCents property.
     * 
     */
    public int getTicketPriceInCents() {
        return ticketPriceInCents;
    }

    /**
     * Sets the value of the ticketPriceInCents property.
     * 
     */
    public void setTicketPriceInCents(int value) {
        this.ticketPriceInCents = value;
    }

    /**
     * Gets the value of the ticketDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketDescription() {
        return ticketDescription;
    }

    /**
     * Sets the value of the ticketDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketDescription(String value) {
        this.ticketDescription = value;
    }

    /**
     * Gets the value of the ticketDescriptionAlt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketDescriptionAlt() {
        return ticketDescriptionAlt;
    }

    /**
     * Sets the value of the ticketDescriptionAlt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketDescriptionAlt(String value) {
        this.ticketDescriptionAlt = value;
    }

    /**
     * Gets the value of the sessionTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSessionTime() {
        return sessionTime;
    }

    /**
     * Sets the value of the sessionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSessionTime(XMLGregorianCalendar value) {
        this.sessionTime = value;
    }

    /**
     * Gets the value of the seatRowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeatRowId() {
        return seatRowId;
    }

    /**
     * Sets the value of the seatRowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeatRowId(String value) {
        this.seatRowId = value;
    }

    /**
     * Gets the value of the seatNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeatNumber() {
        return seatNumber;
    }

    /**
     * Sets the value of the seatNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeatNumber(String value) {
        this.seatNumber = value;
    }

    /**
     * Gets the value of the bookingFeeInCents property.
     * 
     */
    public int getBookingFeeInCents() {
        return bookingFeeInCents;
    }

    /**
     * Sets the value of the bookingFeeInCents property.
     * 
     */
    public void setBookingFeeInCents(int value) {
        this.bookingFeeInCents = value;
    }

    /**
     * Gets the value of the isPaid property.
     * 
     */
    public boolean isIsPaid() {
        return isPaid;
    }

    /**
     * Sets the value of the isPaid property.
     * 
     */
    public void setIsPaid(boolean value) {
        this.isPaid = value;
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
     * Gets the value of the packageGroupNumber property.
     * 
     */
    public int getPackageGroupNumber() {
        return packageGroupNumber;
    }

    /**
     * Sets the value of the packageGroupNumber property.
     * 
     */
    public void setPackageGroupNumber(int value) {
        this.packageGroupNumber = value;
    }

    /**
     * Gets the value of the parentTicketTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentTicketTypeCode() {
        return parentTicketTypeCode;
    }

    /**
     * Sets the value of the parentTicketTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentTicketTypeCode(String value) {
        this.parentTicketTypeCode = value;
    }

    /**
     * Gets the value of the parentTicketDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentTicketDescription() {
        return parentTicketDescription;
    }

    /**
     * Sets the value of the parentTicketDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentTicketDescription(String value) {
        this.parentTicketDescription = value;
    }

    /**
     * Gets the value of the parentTicketDescriptionAlt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentTicketDescriptionAlt() {
        return parentTicketDescriptionAlt;
    }

    /**
     * Sets the value of the parentTicketDescriptionAlt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentTicketDescriptionAlt(String value) {
        this.parentTicketDescriptionAlt = value;
    }

    /**
     * Gets the value of the packageSeats property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSeatInfo }
     *     
     */
    public ArrayOfSeatInfo getPackageSeats() {
        return packageSeats;
    }

    /**
     * Sets the value of the packageSeats property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSeatInfo }
     *     
     */
    public void setPackageSeats(ArrayOfSeatInfo value) {
        this.packageSeats = value;
    }

}
