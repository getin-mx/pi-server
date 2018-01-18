package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class UserEntityCache implements ModelKey, Serializable {

	public static final long DEFAULT_CACHE_DURATION = 43200000 * 2; // 24 Hours
	
	public static final int TYPE_NORMAL_SORT = 0;
	public static final int TYPE_FAVORITES_FIRST = 1;
	public static final int TYPE_FAVORITES_ONLY = 2;
	public static final int TYPE_MY_OFFERS = 3;
	public static final int TYPE_BUNDLE = 4;
	public static final int TYPE_GEOPRIORITY = 5;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String userId;
	private byte entityKind;
	private int returnType;
	@Persistent(defaultFetchGroup = "true")
	private List<String> entities;
	private Date lastUpdate;
	private Date expiresOn;
	
    public UserEntityCache() {
    }

    public UserEntityCache(String userId, byte entityKind, int returnType) {
    	this.userId = userId;
    	this.entityKind = entityKind;
    	this.returnType = returnType;
		lastUpdate = null;
    	expiresOn = null;
    }
    
    public UserEntityCache(String userId, byte entityKind, int returnType, long cacheDuration) {
    	this.userId = userId;
    	this.entityKind = entityKind;
    	this.returnType = returnType;
		lastUpdate = null;
    	expiresOn = new Date();
		expiresOn.setTime(expiresOn.getTime() + cacheDuration);
    	entities = CollectionFactory.createList();
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

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the entity kind
	 */
	public byte getEntityKind() {
		return entityKind;
	}

	/**
	 * @param entityKind the entity kind to set
	 */
	public void setEntityKind(byte entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * @return the usingFavorites
	 */
	public Integer getReturnType() {
		return returnType;
	}

	public void setReturnType(Integer returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the entities
	 */
	public List<String> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<String> entities) {
		this.entities = entities;
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

	@Override
	public void preStore() {
		this.lastUpdate = new Date();
	}

	/**
	 * @return the expiresOn
	 */
	public Date getExpiresOn() {
		return expiresOn;
	}

	/**
	 * @param expiresOn the expiresOn to set
	 */
	public void setExpiresOn(Date expiresOn) {
		this.expiresOn = expiresOn;
	}

	@Override
	public Date getCreationDateTime() {
		return lastUpdate;
	}
}
