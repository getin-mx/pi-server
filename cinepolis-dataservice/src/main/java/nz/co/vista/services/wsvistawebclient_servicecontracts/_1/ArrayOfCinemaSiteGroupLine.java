
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCinemaSiteGroupLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCinemaSiteGroupLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaSiteGroupLine" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}CinemaSiteGroupLine" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCinemaSiteGroupLine", propOrder = {
    "cinemaSiteGroupLine"
})
public class ArrayOfCinemaSiteGroupLine {

    @XmlElement(name = "CinemaSiteGroupLine", nillable = true)
    protected List<CinemaSiteGroupLine> cinemaSiteGroupLine;

    /**
     * Gets the value of the cinemaSiteGroupLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cinemaSiteGroupLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCinemaSiteGroupLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CinemaSiteGroupLine }
     * 
     * 
     */
    public List<CinemaSiteGroupLine> getCinemaSiteGroupLine() {
        if (cinemaSiteGroupLine == null) {
            cinemaSiteGroupLine = new ArrayList<CinemaSiteGroupLine>();
        }
        return this.cinemaSiteGroupLine;
    }

}
