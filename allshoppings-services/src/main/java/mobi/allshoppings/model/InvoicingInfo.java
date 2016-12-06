package mobi.allshoppings.model;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * This class represents contact info for an entity
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public final class InvoicingInfo implements Serializable {

	@Persistent
	/**
	 * Invoicing company name
	 */
	private String companyName;

	@Persistent
	/**
	 * Inscription type. <br>
	 * For example, in Argentina it could be "Responsable Inscripto"
	 */
	private String inscription;

	@Persistent
	/**
	 * VAT Number. <br>
	 * For example, in Argentina, it could be the CUIT Number
	 */
	private String vatNumber;

	@Persistent
	/**
	 * Country in which the address is set. <br> Shouldn't it be another class?
	 */
    private String invoiceCountry;
    
	@Persistent
    /**
     * Province / State in which the address is set
     */
    private String invoiceProvince;
    
	@Persistent
    /**
     * City in which the address is set
     */
    private String invoiceCity;
    
	@Persistent
    /**
     * Address Street Name
     */
    private String invoiceStreetName;
    
	@Persistent
    /**
     * Address Street number. It should include floor and department number in case of a building
     */
    private String invoiceStreetNumber;
    
	@Persistent
    /**
     * Zip / Postal Code
     */
    private String invoiceZipCode;

	/**
	 * Default constructor
	 */
	public InvoicingInfo() {
		super();
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the inscription
	 */
	public String getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(String inscription) {
		this.inscription = inscription;
	}

	/**
	 * @return the vatNumber
	 */
	public String getVatNumber() {
		return vatNumber;
	}

	/**
	 * @param vatNumber the vatNumber to set
	 */
	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	/**
	 * @return the invoiceCountry
	 */
	public String getInvoiceCountry() {
		return invoiceCountry;
	}

	/**
	 * @param invoiceCountry the invoiceCountry to set
	 */
	public void setInvoiceCountry(String invoiceCountry) {
		this.invoiceCountry = invoiceCountry;
	}

	/**
	 * @return the invoiceProvince
	 */
	public String getInvoiceProvince() {
		return invoiceProvince;
	}

	/**
	 * @param invoiceProvince the invoiceProvince to set
	 */
	public void setInvoiceProvince(String invoiceProvince) {
		this.invoiceProvince = invoiceProvince;
	}

	/**
	 * @return the invoiceCity
	 */
	public String getInvoiceCity() {
		return invoiceCity;
	}

	/**
	 * @param invoiceCity the invoiceCity to set
	 */
	public void setInvoiceCity(String invoiceCity) {
		this.invoiceCity = invoiceCity;
	}

	/**
	 * @return the invoiceStreetName
	 */
	public String getInvoiceStreetName() {
		return invoiceStreetName;
	}

	/**
	 * @param invoiceStreetName the invoiceStreetName to set
	 */
	public void setInvoiceStreetName(String invoiceStreetName) {
		this.invoiceStreetName = invoiceStreetName;
	}

	/**
	 * @return the invoiceStreetNumber
	 */
	public String getInvoiceStreetNumber() {
		return invoiceStreetNumber;
	}

	/**
	 * @param invoiceStreetNumber the invoiceStreetNumber to set
	 */
	public void setInvoiceStreetNumber(String invoiceStreetNumber) {
		this.invoiceStreetNumber = invoiceStreetNumber;
	}

	/**
	 * @return the invoiceZipCode
	 */
	public String getInvoiceZipCode() {
		return invoiceZipCode;
	}

	/**
	 * @param invoiceZipCode the invoiceZipCode to set
	 */
	public void setInvoiceZipCode(String invoiceZipCode) {
		this.invoiceZipCode = invoiceZipCode;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result
				+ ((inscription == null) ? 0 : inscription.hashCode());
		result = prime * result
				+ ((vatNumber == null) ? 0 : vatNumber.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvoicingInfo other = (InvoicingInfo) obj;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (inscription == null) {
			if (other.inscription != null)
				return false;
		} else if (!inscription.equals(other.inscription))
			return false;
		if (vatNumber == null) {
			if (other.vatNumber != null)
				return false;
		} else if (!vatNumber.equals(other.vatNumber))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InvoicingInfo [companyName=" + companyName + ", inscription="
				+ inscription + ", vatNumber=" + vatNumber + "]";
	}

}
