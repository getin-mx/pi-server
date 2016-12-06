
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetTicketTypeListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetTicketTypeListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionalUserSessionIdForLoyaltyTickets" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalLoyaltyTicketMatchesHOCode" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CinemaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalShowNonATMTickets" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalReturnAllRedemptionAndCompTickets" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalReturnAllLoyaltyTickets" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalAreaCategoryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalClientClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OptionalReturnLoyaltyRewardFlag" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalSeparatePaymentBasedTickets" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalShowLoyaltyTicketsToNonMembers" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalEnforceChildTicketLogic" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptionalIncludeZeroValueTickets" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetTicketTypeListRequest", propOrder = {
    "optionalUserSessionIdForLoyaltyTickets",
    "optionalLoyaltyTicketMatchesHOCode",
    "cinemaId",
    "sessionId",
    "optionalShowNonATMTickets",
    "optionalReturnAllRedemptionAndCompTickets",
    "optionalReturnAllLoyaltyTickets",
    "optionalAreaCategoryCode",
    "optionalClientClass",
    "optionalReturnLoyaltyRewardFlag",
    "optionalSeparatePaymentBasedTickets",
    "optionalShowLoyaltyTicketsToNonMembers",
    "optionalEnforceChildTicketLogic",
    "optionalIncludeZeroValueTickets"
})
public class GetTicketTypeListRequest {

    @XmlElement(name = "OptionalUserSessionIdForLoyaltyTickets")
    protected String optionalUserSessionIdForLoyaltyTickets;
    @XmlElement(name = "OptionalLoyaltyTicketMatchesHOCode")
    protected boolean optionalLoyaltyTicketMatchesHOCode;
    @XmlElement(name = "CinemaId")
    protected String cinemaId;
    @XmlElement(name = "SessionId")
    protected String sessionId;
    @XmlElement(name = "OptionalShowNonATMTickets")
    protected boolean optionalShowNonATMTickets;
    @XmlElement(name = "OptionalReturnAllRedemptionAndCompTickets")
    protected boolean optionalReturnAllRedemptionAndCompTickets;
    @XmlElement(name = "OptionalReturnAllLoyaltyTickets")
    protected boolean optionalReturnAllLoyaltyTickets;
    @XmlElement(name = "OptionalAreaCategoryCode")
    protected String optionalAreaCategoryCode;
    @XmlElement(name = "OptionalClientClass")
    protected String optionalClientClass;
    @XmlElement(name = "OptionalReturnLoyaltyRewardFlag")
    protected boolean optionalReturnLoyaltyRewardFlag;
    @XmlElement(name = "OptionalSeparatePaymentBasedTickets")
    protected boolean optionalSeparatePaymentBasedTickets;
    @XmlElement(name = "OptionalShowLoyaltyTicketsToNonMembers")
    protected boolean optionalShowLoyaltyTicketsToNonMembers;
    @XmlElement(name = "OptionalEnforceChildTicketLogic")
    protected boolean optionalEnforceChildTicketLogic;
    @XmlElement(name = "OptionalIncludeZeroValueTickets")
    protected boolean optionalIncludeZeroValueTickets;

    /**
     * Gets the value of the optionalUserSessionIdForLoyaltyTickets property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalUserSessionIdForLoyaltyTickets() {
        return optionalUserSessionIdForLoyaltyTickets;
    }

    /**
     * Sets the value of the optionalUserSessionIdForLoyaltyTickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalUserSessionIdForLoyaltyTickets(String value) {
        this.optionalUserSessionIdForLoyaltyTickets = value;
    }

    /**
     * Gets the value of the optionalLoyaltyTicketMatchesHOCode property.
     * 
     */
    public boolean isOptionalLoyaltyTicketMatchesHOCode() {
        return optionalLoyaltyTicketMatchesHOCode;
    }

    /**
     * Sets the value of the optionalLoyaltyTicketMatchesHOCode property.
     * 
     */
    public void setOptionalLoyaltyTicketMatchesHOCode(boolean value) {
        this.optionalLoyaltyTicketMatchesHOCode = value;
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
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the optionalShowNonATMTickets property.
     * 
     */
    public boolean isOptionalShowNonATMTickets() {
        return optionalShowNonATMTickets;
    }

    /**
     * Sets the value of the optionalShowNonATMTickets property.
     * 
     */
    public void setOptionalShowNonATMTickets(boolean value) {
        this.optionalShowNonATMTickets = value;
    }

    /**
     * Gets the value of the optionalReturnAllRedemptionAndCompTickets property.
     * 
     */
    public boolean isOptionalReturnAllRedemptionAndCompTickets() {
        return optionalReturnAllRedemptionAndCompTickets;
    }

    /**
     * Sets the value of the optionalReturnAllRedemptionAndCompTickets property.
     * 
     */
    public void setOptionalReturnAllRedemptionAndCompTickets(boolean value) {
        this.optionalReturnAllRedemptionAndCompTickets = value;
    }

    /**
     * Gets the value of the optionalReturnAllLoyaltyTickets property.
     * 
     */
    public boolean isOptionalReturnAllLoyaltyTickets() {
        return optionalReturnAllLoyaltyTickets;
    }

    /**
     * Sets the value of the optionalReturnAllLoyaltyTickets property.
     * 
     */
    public void setOptionalReturnAllLoyaltyTickets(boolean value) {
        this.optionalReturnAllLoyaltyTickets = value;
    }

    /**
     * Gets the value of the optionalAreaCategoryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionalAreaCategoryCode() {
        return optionalAreaCategoryCode;
    }

    /**
     * Sets the value of the optionalAreaCategoryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionalAreaCategoryCode(String value) {
        this.optionalAreaCategoryCode = value;
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

    /**
     * Gets the value of the optionalReturnLoyaltyRewardFlag property.
     * 
     */
    public boolean isOptionalReturnLoyaltyRewardFlag() {
        return optionalReturnLoyaltyRewardFlag;
    }

    /**
     * Sets the value of the optionalReturnLoyaltyRewardFlag property.
     * 
     */
    public void setOptionalReturnLoyaltyRewardFlag(boolean value) {
        this.optionalReturnLoyaltyRewardFlag = value;
    }

    /**
     * Gets the value of the optionalSeparatePaymentBasedTickets property.
     * 
     */
    public boolean isOptionalSeparatePaymentBasedTickets() {
        return optionalSeparatePaymentBasedTickets;
    }

    /**
     * Sets the value of the optionalSeparatePaymentBasedTickets property.
     * 
     */
    public void setOptionalSeparatePaymentBasedTickets(boolean value) {
        this.optionalSeparatePaymentBasedTickets = value;
    }

    /**
     * Gets the value of the optionalShowLoyaltyTicketsToNonMembers property.
     * 
     */
    public boolean isOptionalShowLoyaltyTicketsToNonMembers() {
        return optionalShowLoyaltyTicketsToNonMembers;
    }

    /**
     * Sets the value of the optionalShowLoyaltyTicketsToNonMembers property.
     * 
     */
    public void setOptionalShowLoyaltyTicketsToNonMembers(boolean value) {
        this.optionalShowLoyaltyTicketsToNonMembers = value;
    }

    /**
     * Gets the value of the optionalEnforceChildTicketLogic property.
     * 
     */
    public boolean isOptionalEnforceChildTicketLogic() {
        return optionalEnforceChildTicketLogic;
    }

    /**
     * Sets the value of the optionalEnforceChildTicketLogic property.
     * 
     */
    public void setOptionalEnforceChildTicketLogic(boolean value) {
        this.optionalEnforceChildTicketLogic = value;
    }

    /**
     * Gets the value of the optionalIncludeZeroValueTickets property.
     * 
     */
    public boolean isOptionalIncludeZeroValueTickets() {
        return optionalIncludeZeroValueTickets;
    }

    /**
     * Sets the value of the optionalIncludeZeroValueTickets property.
     * 
     */
    public void setOptionalIncludeZeroValueTickets(boolean value) {
        this.optionalIncludeZeroValueTickets = value;
    }

}
