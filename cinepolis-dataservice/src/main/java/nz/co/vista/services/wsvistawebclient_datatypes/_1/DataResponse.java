
package nz.co.vista.services.wsvistawebclient_datatypes._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://vista.co.nz/services/WSVistaWebClient.DataTypes/1/}ResultCode"/>
 *         &lt;element name="DatasetXML" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataResponse", propOrder = {
    "result",
    "datasetXML"
})
public class DataResponse {

    @XmlElement(name = "Result", required = true)
    protected ResultCode result;
    @XmlElement(name = "DatasetXML")
    protected String datasetXML;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultCode }
     *     
     */
    public ResultCode getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultCode }
     *     
     */
    public void setResult(ResultCode value) {
        this.result = value;
    }

    /**
     * Gets the value of the datasetXML property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatasetXML() {
        return datasetXML;
    }

    /**
     * Sets the value of the datasetXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatasetXML(String value) {
        this.datasetXML = value;
    }

}
