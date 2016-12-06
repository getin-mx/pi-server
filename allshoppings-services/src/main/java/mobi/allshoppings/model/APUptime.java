package mobi.allshoppings.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;

import com.inodes.datanucleus.model.Key;

@PersistenceCapable(detachable="true")
public class APUptime implements ModelKey, Serializable, Identificable {

	private static final long serialVersionUID = 1L;
	
	public static final Integer REPORT_STATUS_REPORTED = 1;
	public static final Integer REPORT_STATUS_NOT_REPORTED = 0;
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat t = new SimpleDateFormat("HH:mm");

	public static final long SEPARATOR = 300000;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	private String hostname;
	private String date;
	
	@Persistent(defaultFetchGroup = "true")
	private Map<String, Integer> record;
	
	private Date creationDateTime;
	private Date lastUpdate;

	public APUptime() {
		super();
		this.creationDateTime = new Date();
		try {
			createRecordMatrix();
		} catch( Exception e ) {}
	}

	public APUptime(String hostname, Date date) {
		super();
		this.creationDateTime = new Date();
		this.hostname = hostname;
		this.date = sdf.format(date);
		try {
			createRecordMatrix();
		} catch( Exception e ) {}
	}

	/**
	 * Creates the internal record matrix
	 * @throws ParseException
	 */
	private void createRecordMatrix() throws ParseException {
		record = CollectionFactory.createMap();
		Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1980-01-01 00:00:00");
		long pointer = d.getTime();
		long limit = d.getTime() + 86400000;
		while( pointer < limit ) {
			String key = t.format(new Date(pointer));
			Integer val = 0;
			record.put(key, val);
			pointer += SEPARATOR;
		}
	}
	
	/**
	 * Obtains an internal record key
	 * 
	 * @param date
	 *            The date for the internal record
	 * @return The internal record key
	 */
	public static String getRecordKey(Date date) {
		long d = ((long)(date.getTime() / SEPARATOR)) * SEPARATOR;
		return t.format(new Date(d));
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
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the record
	 */
	public Map<String, Integer> getRecord() {
		return record;
	}

	/**
	 * @param record the record to set
	 */
	public void setRecord(Map<String, Integer> record) {
		this.record = record;
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
		APUptime other = (APUptime) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "APUptime [key=" + key + ", hostname=" + hostname + ", date="
				+ date + ", record=" + record + ", creationDateTime="
				+ creationDateTime + ", lastUpdate=" + lastUpdate + "]";
	}

}
