
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfOrderHistoryLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfOrderHistoryLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrderHistoryLine" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}OrderHistoryLine" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfOrderHistoryLine", propOrder = {
    "orderHistoryLine"
})
public class ArrayOfOrderHistoryLine {

    @XmlElement(name = "OrderHistoryLine", nillable = true)
    protected List<OrderHistoryLine> orderHistoryLine;

    /**
     * Gets the value of the orderHistoryLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderHistoryLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderHistoryLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderHistoryLine }
     * 
     * 
     */
    public List<OrderHistoryLine> getOrderHistoryLine() {
        if (orderHistoryLine == null) {
            orderHistoryLine = new ArrayList<OrderHistoryLine>();
        }
        return this.orderHistoryLine;
    }

}
