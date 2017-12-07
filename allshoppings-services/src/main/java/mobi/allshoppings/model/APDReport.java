package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;

public class APDReport implements Identificable, Indexable, ModelKey, Serializable, StatusAware {

	private static final long serialVersionUID = -4895563837537941331L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	// Basic APDevice data
	private String hostname;
	
	private Date lastUpdate;
	private Date creationDateTime;
	
	// Status data
	private Boolean reportable;
	private Integer reportStatus;
	@Persistent(defaultFetchGroup = "true")
	private List<String> reportMailList;
	private Integer status;
	private Date lastRecordDate;
	private Integer lastRecordCount;
	
	@NotPersistent
	private boolean doIndexNow = true;
	
	public APDReport() {
		this.status = StatusAware.STATUS_ENABLED;
		this.reportStatus = APDevice.REPORT_STATUS_NOT_REPORTED;
		this.creationDateTime = new Date();
	}
	
	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	@Override
	public Integer getStatus() {
		return status;
	}

	@Override
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public Key getKey() {
		return key;
	}

	@Override
	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	@Override
	public void preStore() {
		this.lastUpdate = new Date(); 
	}

	@Override
	public boolean doIndex() {
		return doIndexNow;
	}

	@Override
	public void disableIndexing(boolean val) {
		this.doIndexNow = !val;
	}

	@Override
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}
	
	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}
	
	/**
	 * @return the lastRecordDate
	 */
	public Date getLastRecordDate() {
		return lastRecordDate;
	}

	/**
	 * @param lastRecordDate the lastRecordDate to set
	 */
	public void setLastRecordDate(Date lastRecordDate) {
		this.lastRecordDate = lastRecordDate;
	}
	
	/**
	 * @return the reportable
	 */
	public Boolean getReportable() {
		return reportable;
	}

	/**
	 * @param reportable the reportable to set
	 */
	public void setReportable(Boolean reportable) {
		this.reportable = reportable;
	}

	/**
	 * @return the reportStatus
	 */
	public Integer getReportStatus() {
		return reportStatus;
	}

	/**
	 * @param reportStatus the reportStatus to set
	 */
	public void setReportStatus(Integer reportStatus) {
		this.reportStatus = reportStatus;
	}

	/**
	 * @return the reportMailList
	 */
	public List<String> getReportMailList() {
		return reportMailList;
	}

	/**
	 * @param reportMailList the reportMailList to set
	 */
	public void setReportMailList(List<String> reportMailList) {
		this.reportMailList = reportMailList;
	}

	/**
	 * @return the lastRecordCount
	 */
	public Integer getLastRecordCount() {
		return lastRecordCount;
	}

	/**
	 * @param lastRecordCount the lastRecordCount to set
	 */
	public void setLastRecordCount(Integer lastRecordCount) {
		this.lastRecordCount = lastRecordCount;
	}
	
	/**
	 * @return the doIndexNow
	 */
	public boolean isDoIndexNow() {
		return doIndexNow;
	}

	/**
	 * @param doIndexNow the doIndexNow to set
	 */
	public void setDoIndexNow(boolean doIndexNow) {
		this.doIndexNow = doIndexNow;
	}

}
