
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GiftCardBalanceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GiftCardBalanceRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}Request">
 *       &lt;sequence>
 *         &lt;element name="UseGiftCardBalanceMethod" type="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}GiftCardBalanceMethod"/>
 *         &lt;element name="CardNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CardType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CardExpiry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CardExpiryMonth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CardExpiryYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CardBalance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CinemaID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GiftCardBalanceRequest", propOrder = {
    "useGiftCardBalanceMethod",
    "cardNo",
    "cardType",
    "cardExpiry",
    "cardExpiryMonth",
    "cardExpiryYear",
    "cardBalance",
    "cinemaID"
})
public class GiftCardBalanceRequest
    extends Request
{

    @XmlElement(name = "UseGiftCardBalanceMethod", required = true)
    protected GiftCardBalanceMethod useGiftCardBalanceMethod;
    @XmlElement(name = "CardNo")
    protected String cardNo;
    @XmlElement(name = "CardType")
    protected String cardType;
    @XmlElement(name = "CardExpiry")
    protected String cardExpiry;
    @XmlElement(name = "CardExpiryMonth")
    protected String cardExpiryMonth;
    @XmlElement(name = "CardExpiryYear")
    protected String cardExpiryYear;
    @XmlElement(name = "CardBalance")
    protected String cardBalance;
    @XmlElement(name = "CinemaID")
    protected String cinemaID;

    /**
     * Gets the value of the useGiftCardBalanceMethod property.
     * 
     * @return
     *     possible object is
     *     {@link GiftCardBalanceMethod }
     *     
     */
    public GiftCardBalanceMethod getUseGiftCardBalanceMethod() {
        return useGiftCardBalanceMethod;
    }

    /**
     * Sets the value of the useGiftCardBalanceMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link GiftCardBalanceMethod }
     *     
     */
    public void setUseGiftCardBalanceMethod(GiftCardBalanceMethod value) {
        this.useGiftCardBalanceMethod = value;
    }

    /**
     * Gets the value of the cardNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * Sets the value of the cardNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardNo(String value) {
        this.cardNo = value;
    }

    /**
     * Gets the value of the cardType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the value of the cardType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardType(String value) {
        this.cardType = value;
    }

    /**
     * Gets the value of the cardExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardExpiry() {
        return cardExpiry;
    }

    /**
     * Sets the value of the cardExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardExpiry(String value) {
        this.cardExpiry = value;
    }

    /**
     * Gets the value of the cardExpiryMonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardExpiryMonth() {
        return cardExpiryMonth;
    }

    /**
     * Sets the value of the cardExpiryMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardExpiryMonth(String value) {
        this.cardExpiryMonth = value;
    }

    /**
     * Gets the value of the cardExpiryYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardExpiryYear() {
        return cardExpiryYear;
    }

    /**
     * Sets the value of the cardExpiryYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardExpiryYear(String value) {
        this.cardExpiryYear = value;
    }

    /**
     * Gets the value of the cardBalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardBalance() {
        return cardBalance;
    }

    /**
     * Sets the value of the cardBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardBalance(String value) {
        this.cardBalance = value;
    }

    /**
     * Gets the value of the cinemaID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCinemaID() {
        return cinemaID;
    }

    /**
     * Sets the value of the cinemaID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCinemaID(String value) {
        this.cinemaID = value;
    }

}
