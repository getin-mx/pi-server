
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCinemaSiteGroupLinkLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCinemaSiteGroupLinkLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CinemaSiteGroupLinkLine" type="{http://vista.co.nz/services/WSVistaWebClient.ServiceContracts/1}CinemaSiteGroupLinkLine" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCinemaSiteGroupLinkLine", propOrder = {
    "cinemaSiteGroupLinkLine"
})
public class ArrayOfCinemaSiteGroupLinkLine {

    @XmlElement(name = "CinemaSiteGroupLinkLine", nillable = true)
    protected List<CinemaSiteGroupLinkLine> cinemaSiteGroupLinkLine;

    /**
     * Gets the value of the cinemaSiteGroupLinkLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cinemaSiteGroupLinkLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCinemaSiteGroupLinkLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CinemaSiteGroupLinkLine }
     * 
     * 
     */
    public List<CinemaSiteGroupLinkLine> getCinemaSiteGroupLinkLine() {
        if (cinemaSiteGroupLinkLine == null) {
            cinemaSiteGroupLinkLine = new ArrayList<CinemaSiteGroupLinkLine>();
        }
        return this.cinemaSiteGroupLinkLine;
    }

}
