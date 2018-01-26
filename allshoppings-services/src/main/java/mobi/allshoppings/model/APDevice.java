package mobi.allshoppings.model;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mx.getin.Constants;

@PersistenceCapable(detachable="true")
public class APDevice implements ModelKey, Serializable, Identificable, Indexable, StatusAware {

	private static final long serialVersionUID = 1L;
	
	public static final Integer REPORT_STATUS_REPORTED = 1;
	public static final Integer REPORT_STATUS_NOT_REPORTED = 0;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	// Basic APDevice data
	private String hostname;
	private String description;
	

	// Model and installed data
	private String model;
	private String mode;
	private String version;
	private String tunnelIp;
	private String lanIp;
	private String wanIp;
	private String publicIp;
	private Date lastInfoUpdate;
	private Boolean external;

	// Geo location
	private String country;
	private String province;
	private String city;
	private Double lat;
	private Double lon;
	
	// Parameters
	private Long visitTimeThreshold;
	private Long visitGapThreshold;
	private Long visitPowerThreshold;
	private Long visitMaxThreshold;
	private Long peasantPowerThreshold;
	private Long visitCountThreshold;
	private Long viewerPowerThreshold;
	private Integer repeatThreshold;
	private int viewerMinTimeThreshold;
	private int viewerMaxTimeThreshold;

	/**
	 * This Value must be positive
	 */
	private int offSetViewer;
	
	private Long visitDecay;
	private Long peasantDecay;
    
    // Times
    private String timezone;
    private Boolean visitsOnMon;
    private Boolean visitsOnTue;
    private Boolean visitsOnWed;
    private Boolean visitsOnThu;
    private Boolean visitsOnFri;
    private Boolean visitsOnSat;
    private Boolean visitsOnSun;
    private String visitStartMon;
    private String visitEndMon;
    private String visitStartTue;
    private String visitEndTue;
    private String visitStartWed;
    private String visitEndWed;
    private String visitStartThu;
    private String visitEndThu;
    private String visitStartFri;
    private String visitEndFri;
    private String visitStartSat;
    private String visitEndSat;
    private String visitStartSun;
    private String visitEndSun;
    private String monitorStart;
    private String monitorEnd;
    private String passStart;
    private String passEnd;

	// Status data
	private Boolean reportable;
	private Integer reportStatus;
	@Persistent(defaultFetchGroup = "true")
	private List<String> reportMailList;
	private Integer status;
	private Date creationDateTime;
	private Date lastRecordDate;
	private Integer lastRecordCount;
	private Date lastUpdate;

	@NotPersistent
	private boolean doIndexNow = true;

	public APDevice() {
		this.creationDateTime = new Date();
		this.status = StatusAware.STATUS_ENABLED;
		this.reportStatus = REPORT_STATUS_NOT_REPORTED;
		this.external = false;
		
		completeDefaults();
	}
	
	public void completeDefaults() {

		if( visitTimeThreshold == null) visitTimeThreshold = 0L;
		if( visitGapThreshold == null) visitGapThreshold = 10L;
		if( visitPowerThreshold == null) visitPowerThreshold = -60L;
		if( visitMaxThreshold == null) visitMaxThreshold = 480L;
		if( peasantPowerThreshold == null) peasantPowerThreshold = -80L;
		if( viewerPowerThreshold == null) viewerPowerThreshold = (visitPowerThreshold +peasantPowerThreshold) /2; 
		if( visitCountThreshold == null) visitCountThreshold = 0L;
		if( repeatThreshold == null ) repeatThreshold = 5;
		if( visitDecay == null ) visitDecay = visitGapThreshold;
		if( peasantDecay == null ) peasantDecay = visitGapThreshold; 
		if( offSetViewer <= 0) offSetViewer = 5; 
		
		if(viewerMinTimeThreshold < 0) viewerMinTimeThreshold = visitTimeThreshold.intValue();
		if(viewerMaxTimeThreshold < viewerMinTimeThreshold) viewerMaxTimeThreshold = Constants.FIVE_MINUTES_IN_MILLIS;
	    
		if( timezone == null) timezone = "CDT";
		if( visitsOnMon == null) visitsOnMon = true;
		if( visitsOnTue == null) visitsOnTue = true;
		if( visitsOnWed == null) visitsOnWed = true;
		if( visitsOnThu == null) visitsOnThu = true;
		if( visitsOnFri == null) visitsOnFri = true;
		if( visitsOnSat == null) visitsOnSat = true;
		if( visitsOnSun == null) visitsOnSun = true;
		if( visitStartMon == null) visitStartMon = "11:00";
		if( visitEndMon == null) visitEndMon = "20:00";
	    if( visitStartTue == null) visitStartTue = "11:00";
	    if( visitEndTue == null) visitEndTue = "20:00";
	    if( visitStartWed == null) visitStartWed = "11:00";
	    if( visitEndWed == null) visitEndWed = "20:00";
	    if( visitStartThu == null) visitStartThu = "11:00";
	    if( visitEndThu == null) visitEndThu = "20:00";
	    if( visitStartFri == null) visitStartFri = "11:00";
	    if( visitEndFri == null) visitEndFri = "20:00";
	    if( visitStartSat == null) visitStartSat = "11:00";
	    if( visitEndSat == null) visitEndSat = "20:00";
	    if( visitStartSun == null) visitStartSun = "11:00";
	    if( visitEndSun == null) visitEndSun = "20:00";
	    if( monitorStart == null) monitorStart = "09:00";
	    if( monitorEnd == null) monitorEnd = "21:00";
	    if( passStart == null) passStart = "05:00";
	    if( passEnd == null) passEnd = "03:00";
	}
	
	/**
	 * @return this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public void preStore() {
		this.lastUpdate = new Date();
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

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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
	 * @return the visitTimeThreshold
	 */
	public Long getVisitTimeThreshold() {
		return visitTimeThreshold;
	}

	/**
	 * @param visitTimeThreshold the visitTimeThreshold to set
	 */
	public void setVisitTimeThreshold(Long visitTimeThreshold) {
		this.visitTimeThreshold = visitTimeThreshold;
	}

	/**
	 * @return the visitGapThreshold
	 */
	public Long getVisitGapThreshold() {
		return visitGapThreshold;
	}

	/**
	 * @param visitGapThreshold the visitGapThreshold to set
	 */
	public void setVisitGapThreshold(Long visitGapThreshold) {
		this.visitGapThreshold = visitGapThreshold;
	}

	/**
	 * @return the visitPowerThreshold
	 */
	public Long getVisitPowerThreshold() {
		return visitPowerThreshold;
	}

	/**
	 * @param visitPowerThreshold the visitPowerThreshold to set
	 */
	public void setVisitPowerThreshold(Long visitPowerThreshold) {
		this.visitPowerThreshold = visitPowerThreshold;
	}

	/**
	 * @return the visitMaxThreshold
	 */
	public Long getVisitMaxThreshold() {
		return visitMaxThreshold;
	}

	/**
	 * @param visitMaxThreshold the visitMaxThreshold to set
	 */
	public void setVisitMaxThreshold(Long visitMaxThreshold) {
		this.visitMaxThreshold = visitMaxThreshold;
	}

	/**
	 * @return the repeatThreshold
	 */
	public Integer getRepeatThreshold() {
		return repeatThreshold;
	}

	/**
	 * @param repeatThreshold the repeatThreshold to set
	 */
	public void setRepeatThreshold(Integer repeatThreshold) {
		this.repeatThreshold = repeatThreshold;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
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
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public Double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(Double lon) {
		this.lon = lon;
	}

	/**
	 * @return the peasentPowerThreshold
	 */
	public Long getPeasantPowerThreshold() {
		return peasantPowerThreshold;
	}
	
	/**
	 * @return the viewerPowerThreshold
	 */
	public Long getViewerPowerThreshold() {
		return viewerPowerThreshold;
	}

	/**
	 * @param peasentPowerThreshold the peasentPowerThreshold to set
	 */
	public void setPeasentPowerThreshold(Long peasentPowerThreshold) {
		this.peasantPowerThreshold = peasentPowerThreshold;
	}
	
	/**
	 * @param viewerPowerThreshold the viewerPowerThreshold to set
	 */
	public void setViewerPowerThreshold(long viewerPowerThreshold) {
		this.viewerPowerThreshold = viewerPowerThreshold;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the visitStartMon
	 */
	public String getVisitStartMon() {
		return visitStartMon;
	}

	/**
	 * @param visitStartMon the visitStartMon to set
	 */
	public void setVisitStartMon(String visitStartMon) {
		this.visitStartMon = visitStartMon;
	}

	/**
	 * @return the visitEndMon
	 */
	public String getVisitEndMon() {
		return visitEndMon;
	}

	/**
	 * @param visitEndMon the visitEndMon to set
	 */
	public void setVisitEndMon(String visitEndMon) {
		this.visitEndMon = visitEndMon;
	}

	/**
	 * @return the visitStartTue
	 */
	public String getVisitStartTue() {
		return visitStartTue;
	}

	/**
	 * @param visitStartTue the visitStartTue to set
	 */
	public void setVisitStartTue(String visitStartTue) {
		this.visitStartTue = visitStartTue;
	}

	/**
	 * @return the visitEndTue
	 */
	public String getVisitEndTue() {
		return visitEndTue;
	}

	/**
	 * @param visitEndTue the visitEndTue to set
	 */
	public void setVisitEndTue(String visitEndTue) {
		this.visitEndTue = visitEndTue;
	}

	/**
	 * @return the visitStartWed
	 */
	public String getVisitStartWed() {
		return visitStartWed;
	}

	/**
	 * @param visitStartWed the visitStartWed to set
	 */
	public void setVisitStartWed(String visitStartWed) {
		this.visitStartWed = visitStartWed;
	}

	/**
	 * @return the visitEndWed
	 */
	public String getVisitEndWed() {
		return visitEndWed;
	}

	/**
	 * @param visitEndWed the visitEndWed to set
	 */
	public void setVisitEndWed(String visitEndWed) {
		this.visitEndWed = visitEndWed;
	}

	/**
	 * @return the visitStartThu
	 */
	public String getVisitStartThu() {
		return visitStartThu;
	}

	/**
	 * @param visitStartThu the visitStartThu to set
	 */
	public void setVisitStartThu(String visitStartThu) {
		this.visitStartThu = visitStartThu;
	}

	/**
	 * @return the visitEndThu
	 */
	public String getVisitEndThu() {
		return visitEndThu;
	}

	/**
	 * @param visitEndThu the visitEndThu to set
	 */
	public void setVisitEndThu(String visitEndThu) {
		this.visitEndThu = visitEndThu;
	}

	/**
	 * @return the visitStartFri
	 */
	public String getVisitStartFri() {
		return visitStartFri;
	}

	/**
	 * @param visitStartFri the visitStartFri to set
	 */
	public void setVisitStartFri(String visitStartFri) {
		this.visitStartFri = visitStartFri;
	}

	/**
	 * @return the visitEndFri
	 */
	public String getVisitEndFri() {
		return visitEndFri;
	}

	/**
	 * @param visitEndFri the visitEndFri to set
	 */
	public void setVisitEndFri(String visitEndFri) {
		this.visitEndFri = visitEndFri;
	}

	/**
	 * @return the visitStartSat
	 */
	public String getVisitStartSat() {
		return visitStartSat;
	}

	/**
	 * @param visitStartSat the visitStartSat to set
	 */
	public void setVisitStartSat(String visitStartSat) {
		this.visitStartSat = visitStartSat;
	}

	/**
	 * @return the visitEndSat
	 */
	public String getVisitEndSat() {
		return visitEndSat;
	}

	/**
	 * @param visitEndSat the visitEndSat to set
	 */
	public void setVisitEndSat(String visitEndSat) {
		this.visitEndSat = visitEndSat;
	}

	/**
	 * @return the visitStartSun
	 */
	public String getVisitStartSun() {
		return visitStartSun;
	}

	/**
	 * @param visitStartSun the visitStartSun to set
	 */
	public void setVisitStartSun(String visitStartSun) {
		this.visitStartSun = visitStartSun;
	}

	/**
	 * @return the visitEndSun
	 */
	public String getVisitEndSun() {
		return visitEndSun;
	}

	/**
	 * @param visitEndSun the visitEndSun to set
	 */
	public void setVisitEndSun(String visitEndSun) {
		this.visitEndSun = visitEndSun;
	}

	/**
	 * @return the monitorStart
	 */
	public String getMonitorStart() {
		return monitorStart;
	}

	/**
	 * @param monitorStart the monitorStart to set
	 */
	public void setMonitorStart(String monitorStart) {
		this.monitorStart = monitorStart;
	}

	/**
	 * @return the monitorEnd
	 */
	public String getMonitorEnd() {
		return monitorEnd;
	}

	/**
	 * @param monitorEnd the monitorEnd to set
	 */
	public void setMonitorEnd(String monitorEnd) {
		this.monitorEnd = monitorEnd;
	}

	/**
	 * @return the passStart
	 */
	public String getPassStart() {
		return passStart;
	}

	/**
	 * @param passStart the passStart to set
	 */
	public void setPassStart(String passStart) {
		this.passStart = passStart;
	}

	/**
	 * @return the passEnd
	 */
	public String getPassEnd() {
		return passEnd;
	}

	/**
	 * @param passEnd the passEnd to set
	 */
	public void setPassEnd(String passEnd) {
		this.passEnd = passEnd;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		APDevice other = (APDevice) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the tunnelIp
	 */
	public String getTunnelIp() {
		return tunnelIp;
	}

	/**
	 * @param tunnelIp the tunnelIp to set
	 */
	public void setTunnelIp(String tunnelIp) {
		this.tunnelIp = tunnelIp;
	}

	/**
	 * @return the lanIp
	 */
	public String getLanIp() {
		return lanIp;
	}

	/**
	 * @param lanIp the lanIp to set
	 */
	public void setLanIp(String lanIp) {
		this.lanIp = lanIp;
	}

	/**
	 * @return the wanIp
	 */
	public String getWanIp() {
		return wanIp;
	}

	/**
	 * @param wanIp the wanIp to set
	 */
	public void setWanIp(String wanIp) {
		this.wanIp = wanIp;
	}

	/**
	 * @return the publicIp
	 */
	public String getPublicIp() {
		return publicIp;
	}

	/**
	 * @param publicIp the publicIp to set
	 */
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the lastInfoUpdate
	 */
	public Date getLastInfoUpdate() {
		return lastInfoUpdate;
	}

	/**
	 * @param lastInfoUpdate the lastInfoUpdate to set
	 */
	public void setLastInfoUpdate(Date lastInfoUpdate) {
		this.lastInfoUpdate = lastInfoUpdate;
	}

	/**
	 * @return the visitCountThreshold
	 */
	public Long getVisitCountThreshold() {
		return visitCountThreshold;
	}

	/**
	 * @param visitCountThreshold the visitCountThreshold to set
	 */
	public void setVisitCountThreshold(Long visitCountThreshold) {
		this.visitCountThreshold = visitCountThreshold;
	}

	/**
	 * @return the visitsOnMon
	 */
	public Boolean getVisitsOnMon() {
		return visitsOnMon;
	}

	/**
	 * @param visitsOnMon the visitsOnMon to set
	 */
	public void setVisitsOnMon(Boolean visitsOnMon) {
		this.visitsOnMon = visitsOnMon;
	}

	/**
	 * @return the visitsOnTue
	 */
	public Boolean getVisitsOnTue() {
		return visitsOnTue;
	}

	/**
	 * @param visitsOnTue the visitsOnTue to set
	 */
	public void setVisitsOnTue(Boolean visitsOnTue) {
		this.visitsOnTue = visitsOnTue;
	}

	/**
	 * @return the visitsOnWed
	 */
	public Boolean getVisitsOnWed() {
		return visitsOnWed;
	}

	/**
	 * @param visitsOnWed the visitsOnWed to set
	 */
	public void setVisitsOnWed(Boolean visitsOnWed) {
		this.visitsOnWed = visitsOnWed;
	}

	/**
	 * @return the visitsOnThu
	 */
	public Boolean getVisitsOnThu() {
		return visitsOnThu;
	}

	/**
	 * @param visitsOnThu the visitsOnThu to set
	 */
	public void setVisitsOnThu(Boolean visitsOnThu) {
		this.visitsOnThu = visitsOnThu;
	}

	/**
	 * @return the visitsOnFri
	 */
	public Boolean getVisitsOnFri() {
		return visitsOnFri;
	}

	/**
	 * @param visitsOnFri the visitsOnFri to set
	 */
	public void setVisitsOnFri(Boolean visitsOnFri) {
		this.visitsOnFri = visitsOnFri;
	}

	/**
	 * @return the visitsOnSat
	 */
	public Boolean getVisitsOnSat() {
		return visitsOnSat;
	}

	/**
	 * @param visitsOnSat the visitsOnSat to set
	 */
	public void setVisitsOnSat(Boolean visitsOnSat) {
		this.visitsOnSat = visitsOnSat;
	}

	/**
	 * @return the visitsOnSun
	 */
	public Boolean getVisitsOnSun() {
		return visitsOnSun;
	}

	/**
	 * @param visitsOnSun the visitsOnSun to set
	 */
	public void setVisitsOnSun(Boolean visitsOnSun) {
		this.visitsOnSun = visitsOnSun;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @param peasantPowerThreshold the peasantPowerThreshold to set
	 */
	public void setPeasantPowerThreshold(Long peasantPowerThreshold) {
		this.peasantPowerThreshold = peasantPowerThreshold;
	}

	/**
	 * @return the external
	 */
	public Boolean getExternal() {
		if( null == external ) return false;
		return external;
	}

	/**
	 * @param external the external to set
	 */
	public void setExternal(Boolean external) {
		this.external = external;
	}

	/**
	 * @return the visitDecay
	 */
	public Long getVisitDecay() {
		return visitDecay;
	}

	/**
	 * @param visitDecay the visitDecay to set
	 */
	public void setVisitDecay(Long visitDecay) {
		this.visitDecay = visitDecay;
	}

	/**
	 * @return the peasantDecay
	 */
	public Long getPeasantDecay() {
		return peasantDecay;
	}

	/**
	 * @param peasantDecay the peasantDecay to set
	 */
	public void setPeasantDecay(Long peasantDecay) {
		this.peasantDecay = peasantDecay;
	}
	
	public void setOffsetView(int offSetViewer) {
		this.offSetViewer = Math.abs(offSetViewer);
	}
	
	public int getOffsetViewer() {
		return offSetViewer;
	}

	@Override
	public boolean doIndex() {
		return doIndexNow;
	}

	@Override
	public void disableIndexing(boolean val) {
		this.doIndexNow = !val;
	}
	
	public int getViewerMinTimeThreshold() {
		return viewerMinTimeThreshold;
	}

	public void setViewerMinTimeThreshold(int viewerMinTimeThreshold) {
		this.viewerMinTimeThreshold = viewerMinTimeThreshold;
	}
	
	public int getViewerMaxTimeThreshold() {
		return viewerMaxTimeThreshold;
	}

	public void setViewerMaxTimeThreshold(int viewerMaxTimeThreshold) {
		this.viewerMaxTimeThreshold = viewerMaxTimeThreshold;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "APDevice [key=" + key + ", hostname=" + hostname + ", description=" + description 
				+ ", model=" + model + ", mode=" + mode + ", version=" + version
				+ ", tunnelIp=" + tunnelIp + ", lanIp=" + lanIp + ", wanIp=" + wanIp + ", publicIp=" + publicIp
				+ ", lastInfoUpdate=" + lastInfoUpdate + ", external=" + external + ", country=" + country
				+ ", province=" + province + ", city=" + city + ", lat=" + lat + ", lon=" + lon
				+ ", visitTimeThreshold=" + visitTimeThreshold + ", visitGapThreshold=" + visitGapThreshold
				+ ", visitPowerThreshold=" + visitPowerThreshold + ", visitMaxThreshold=" + visitMaxThreshold
				+ ", peasantPowerThreshold=" + peasantPowerThreshold + ", visitCountThreshold=" + visitCountThreshold
				+ ", repeatThreshold=" + repeatThreshold + ", timezone=" + timezone + ", visitsOnMon=" + visitsOnMon
				+ ", visitsOnTue=" + visitsOnTue + ", visitsOnWed=" + visitsOnWed + ", visitsOnThu=" + visitsOnThu
				+ ", visitsOnFri=" + visitsOnFri + ", visitsOnSat=" + visitsOnSat + ", visitsOnSun=" + visitsOnSun
				+ ", visitStartMon=" + visitStartMon + ", visitEndMon=" + visitEndMon + ", visitStartTue="
				+ visitStartTue + ", visitEndTue=" + visitEndTue + ", visitStartWed=" + visitStartWed + ", visitEndWed="
				+ visitEndWed + ", visitStartThu=" + visitStartThu + ", visitEndThu=" + visitEndThu + ", visitStartFri="
				+ visitStartFri + ", visitEndFri=" + visitEndFri + ", visitStartSat=" + visitStartSat + ", visitEndSat="
				+ visitEndSat + ", visitStartSun=" + visitStartSun + ", visitEndSun=" + visitEndSun + ", monitorStart="
				+ monitorStart + ", monitorEnd=" + monitorEnd + ", passStart=" + passStart + ", passEnd=" + passEnd
				+ ", reportable=" + reportable + ", reportStatus=" + reportStatus + ", reportMailList=" + reportMailList
				+ ", status=" + status + ", creationDateTime=" + creationDateTime + ", lastRecordDate=" + lastRecordDate
				+ ", lastRecordCount=" + lastRecordCount + ", lastUpdate=" + lastUpdate + ", doIndexNow=" + doIndexNow
				+ "]";
	}

}
