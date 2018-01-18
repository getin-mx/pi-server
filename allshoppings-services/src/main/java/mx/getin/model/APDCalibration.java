package mx.getin.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mx.getin.Constants;

public class APDCalibration implements ModelKey, Serializable, Identificable, Indexable {

	private static final long serialVersionUID = -8616325869354294048L;
	
	/**
	 * Minimum percentage from the total time slots within the total permanence time of a device
	 * to consider it as a visit.
	 */
	public static final byte DEFAULT_VISIT_PERCENTAGE = 25;
	
	/**
	 * Default time slots to wait for a device to pass by again.
	 */
	public static final byte DEFAULT_GAP = 10;
	
	public static final byte DEFAULT_VISIT_POWER_THRESHOLD = -60;
	public static final int VISIT_MAX_TIME_THRESHOLD_MILLIS = Constants.SIX_HOURS_IN_MILLIS;
	public static final byte DEFAULT_PEDERSTIAN_POWER_THRESHOLD = -80;
	public static final byte MAX_DEFAULT_VISIT_REPETITION = 5;
	public static final byte DEFAULT_TIME_DECAY = 10 *60 /20;
	
	// measuring with power, super power!
	
	public static final byte DEFAULT_POWER_DECAY = 3;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	// Basic APDevice data
	private String hostname;
	
	private Date lastUpdate;
	private Date creationDateTime;
	private short visitPowerThreshold;
	private int visitMaxTimeThreshold;
	private int visitMinTimeThreshold;
	private short visitGapThreshold;
	private long visitCountThreshold;
	private short peasantPowerThreshold;
	private int repeatThreshold;
	private short timeDecay;
	private short powerDecay;
	
	private boolean visitsOnMon;
    private boolean visitsOnTue;
    private boolean visitsOnWed;
    private boolean visitsOnThu;
    private boolean visitsOnFri;
    private boolean visitsOnSat;
    private boolean visitsOnSun;
    
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
    
    private float visitMinTimeSlotPercentage;
    
   	@NotPersistent
	private boolean doIndexNow = true;
	
    public APDCalibration() {
		this.creationDateTime = new Date();
		visitsOnMon = visitsOnTue = visitsOnWed = visitsOnThu = visitsOnFri = visitsOnSat = visitsOnSun = true;
		completeDefaults();
	}
    
    public void completeDefaults() {
    	if( visitGapThreshold <= 0) visitGapThreshold = DEFAULT_GAP;
		if( visitPowerThreshold > 0) visitPowerThreshold = DEFAULT_VISIT_POWER_THRESHOLD;
		if( visitMaxTimeThreshold <= 0) visitMaxTimeThreshold = VISIT_MAX_TIME_THRESHOLD_MILLIS;
		if( peasantPowerThreshold > 0) peasantPowerThreshold = DEFAULT_PEDERSTIAN_POWER_THRESHOLD;
		if( visitCountThreshold < 0) visitCountThreshold = 0;
		if( repeatThreshold < 0) repeatThreshold = MAX_DEFAULT_VISIT_REPETITION;
		if( timeDecay < 0) timeDecay = DEFAULT_TIME_DECAY;
		if(powerDecay > 0) powerDecay = DEFAULT_POWER_DECAY;
		if(visitMinTimeSlotPercentage < 0 || visitMinTimeSlotPercentage > 100)
			visitMinTimeSlotPercentage = DEFAULT_VISIT_PERCENTAGE;
	    
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
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}
	
	/**
	 * @return the visitGapThreshold
	 */
	public short getVisitGapThreshold() {
		return visitGapThreshold;
	}

	/**
	 * @param visitGapThreshold the visitGapThreshold to set
	 */
	public void setVisitGapThreshold(short visitGapThreshold) {
		this.visitGapThreshold = visitGapThreshold;
	}

	/**
	 * @return the visitPowerThreshold
	 */
	public short getVisitPowerThreshold() {
		return visitPowerThreshold;
	}

	/**
	 * @param visitPowerThreshold the visitPowerThreshold to set
	 */
	public void setVisitPowerThreshold(short visitPowerThreshold) {
		this.visitPowerThreshold = visitPowerThreshold;
	}

	/**
	 * @return the visitMaxThreshold
	 */
	public int getVisitMaxThreshold() {
		return visitMaxTimeThreshold;
	}

	/**
	 * @param visitMaxThreshold the visitMaxThreshold to set
	 */
	public void setVisitMaxThreshold(int visitMaxThreshold) {
		this.visitMaxTimeThreshold = visitMaxThreshold;
	}
	
	/**
	 * @return the visitTimeThreshold
	 */
	public int getVisitMinTimeThreshold() {
		return visitMinTimeThreshold;
	}

	/**
	 * @param visitTimeThreshold the visitTimeThreshold to set
	 */
	public void setVisitMinTimeThreshold(int visitTimeThreshold) {
		this.visitMinTimeThreshold = visitTimeThreshold;
	}

	/**
	 * @return the repeatThreshold
	 */
	public int getRepeatThreshold() {
		return repeatThreshold;
	}

	/**
	 * @param repeatThreshold the repeatThreshold to set
	 */
	public void setRepeatThreshold(int repeatThreshold) {
		this.repeatThreshold = repeatThreshold;
	}
	
	/**
	 * @return the peasentPowerThreshold
	 */
	public short getPeasantPowerThreshold() {
		return peasantPowerThreshold;
	}

	/**
	 * @param peasentPowerThreshold the peasentPowerThreshold to set
	 */
	public void setPeasentPowerThreshold(short peasentPowerThreshold) {
		this.peasantPowerThreshold = peasentPowerThreshold;
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
	 * @return the visitCountThreshold
	 */
	public long getVisitCountThreshold() {
		return visitCountThreshold;
	}

	/**
	 * @param visitCountThreshold the visitCountThreshold to set
	 */
	public void setVisitCountThreshold(long visitCountThreshold) {
		this.visitCountThreshold = visitCountThreshold;
	}

	/**
	 * @return the visitsOnMon
	 */
	public boolean getVisitsOnMon() {
		return visitsOnMon;
	}

	/**
	 * @param visitsOnMon the visitsOnMon to set
	 */
	public void setVisitsOnMon(boolean visitsOnMon) {
		this.visitsOnMon = visitsOnMon;
	}

	/**
	 * @return the visitsOnTue
	 */
	public boolean getVisitsOnTue() {
		return visitsOnTue;
	}

	/**
	 * @param visitsOnTue the visitsOnTue to set
	 */
	public void setVisitsOnTue(boolean visitsOnTue) {
		this.visitsOnTue = visitsOnTue;
	}

	/**
	 * @return the visitsOnWed
	 */
	public boolean getVisitsOnWed() {
		return visitsOnWed;
	}

	/**
	 * @param visitsOnWed the visitsOnWed to set
	 */
	public void setVisitsOnWed(boolean visitsOnWed) {
		this.visitsOnWed = visitsOnWed;
	}

	/**
	 * @return the visitsOnThu
	 */
	public boolean getVisitsOnThu() {
		return visitsOnThu;
	}

	/**
	 * @param visitsOnThu the visitsOnThu to set
	 */
	public void setVisitsOnThu(boolean visitsOnThu) {
		this.visitsOnThu = visitsOnThu;
	}

	/**
	 * @return the visitsOnFri
	 */
	public boolean getVisitsOnFri() {
		return visitsOnFri;
	}

	/**
	 * @param visitsOnFri the visitsOnFri to set
	 */
	public void setVisitsOnFri(boolean visitsOnFri) {
		this.visitsOnFri = visitsOnFri;
	}

	/**
	 * @return the visitsOnSat
	 */
	public boolean getVisitsOnSat() {
		return visitsOnSat;
	}

	/**
	 * @param visitsOnSat the visitsOnSat to set
	 */
	public void setVisitsOnSat(boolean visitsOnSat) {
		this.visitsOnSat = visitsOnSat;
	}

	/**
	 * @return the visitsOnSun
	 */
	public boolean getVisitsOnSun() {
		return visitsOnSun;
	}

	/**
	 * @param visitsOnSun the visitsOnSun to set
	 */
	public void setVisitsOnSun(boolean visitsOnSun) {
		this.visitsOnSun = visitsOnSun;
	}
	
	/**
	 * @param peasantPowerThreshold the peasantPowerThreshold to set
	 */
	public void setPeasantPowerThreshold(short peasantPowerThreshold) {
		this.peasantPowerThreshold = peasantPowerThreshold;
	}
	
	/**
	 * @return the peasantDecay
	 */
	public short getTimeDecay() {
		return timeDecay;
	}

	/**
	 * @param peasantDecay the peasantDecay to set
	 */
	public void setTimeDecay(short peasantDecay) {
		this.timeDecay = peasantDecay;
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
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
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
	
	public float getVisitMinTimeSlotPercentage() {
		return visitMinTimeSlotPercentage;
	}

	public void setVisitMinTimeSlotPercentage(float visitMinTimeSlotPercentage) {
		this.visitMinTimeSlotPercentage = visitMinTimeSlotPercentage;
	}
	
	public short getPowerDecay() {
		return powerDecay;
	}

	public void setPowerDecay(short powerDecay) {
		this.powerDecay = powerDecay;
	}
	
	@Override
	public String toString() {
		return "APDCalibration [visitGapThreshold=" + visitGapThreshold + ", visitPowerThreshold="
				+ visitPowerThreshold +", visitTimeThreshold=" + visitMinTimeThreshold + ", visitMaxThreshold="
				+ visitMaxTimeThreshold + ", peasantPowerThreshold=" + peasantPowerThreshold
				+ ", visitCountThreshold=" + visitCountThreshold + ", repeatThreshold=" + repeatThreshold
				+ ", visitsOnMon=" + visitsOnMon + ", visitsOnTue=" + visitsOnTue + ", visitsOnWed="
				+ visitsOnWed + ", visitsOnThu=" + visitsOnThu + ", visitsOnFri=" + visitsOnFri
				+ ", visitsOnSat=" + visitsOnSat + ", visitsOnSun=" + visitsOnSun + ", visitStartMon="
				+ visitStartMon + ", visitEndMon=" + visitEndMon + ", visitStartTue=" + visitStartTue
				+ ", visitEndTue=" + visitEndTue + ", visitStartWed=" + visitStartWed + ", visitEndWed="
				+ visitEndWed + ", visitStartThu=" + visitStartThu + ", visitEndThu=" + visitEndThu
				+ ", visitStartFri=" + visitStartFri + ", visitEndFri=" + visitEndFri + ", visitStartSat="
				+ visitStartSat + ", visitEndSat=" + visitEndSat + ", visitStartSun=" + visitStartSun
				+ ", visitEndSun=" + visitEndSun + ", monitorStart=" + monitorStart + ", monitorEnd="
				+ monitorEnd + "]";
	}

}
