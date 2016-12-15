package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.embedded.CinemaEmbedd;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class Movie implements ModelKey, Serializable, Identificable, StatusAware {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;
	private String internalIdentifier;
	private String brandId;
	private String name;
	private String originalName;
	private String avatarId;
	private String rate;
	private String lenght;
	private String actors;
	private String sinopsis;
	private String movieGender;
	private String director;
	private Integer status;

	@Persistent(defaultFetchGroup = "true")
	private List<CinemaEmbedd> cinemas;
	
	private Date creationDateTime;
	private Date lastUpdate;

	public Movie() {
		super();
		this.cinemas = new ArrayList<CinemaEmbedd>();
		this.creationDateTime = new Date();
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
	 * @return this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	/**
	 * Pre store information to assign index values
	 */
	@Override
	public void preStore() {
		this.lastUpdate = new Date();
	}

	/**
	 * Sets last update parameter
	 */
	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Obtains last update parameter
	 */
	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @return the brandId
	 */
	public String getBrandId() {
		return brandId;
	}

	/**
	 * @param brandId the brandId to set
	 */
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the internalIdentifier
	 */
	public String getInternalIdentifier() {
		return internalIdentifier;
	}

	/**
	 * @param internalIdentifier the internalIdentifier to set
	 */
	public void setInternalIdentifier(String internalIdentifier) {
		this.internalIdentifier = internalIdentifier;
	}

	/**
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * @return the avatarId
	 */
	public String getAvatarId() {
		return avatarId;
	}

	/**
	 * @param avatarId the avatarId to set
	 */
	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}

	/**
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}

	/**
	 * @return the lenght
	 */
	public String getLenght() {
		return lenght;
	}

	/**
	 * @param lenght the lenght to set
	 */
	public void setLenght(String lenght) {
		this.lenght = lenght;
	}

	/**
	 * @return the actors
	 */
	public String getActors() {
		return actors;
	}

	/**
	 * @param actors the actors to set
	 */
	public void setActors(String actors) {
		this.actors = actors;
	}

	/**
	 * @return the sinopsis
	 */
	public String getSinopsis() {
		return sinopsis;
	}

	/**
	 * @param sinopsis the sinopsis to set
	 */
	public void setSinopsis(String sinopsis) {
		this.sinopsis = sinopsis;
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
	 * @return the movieGender
	 */
	public String getMovieGender() {
		return movieGender;
	}

	/**
	 * @param movieGender the movieGender to set
	 */
	public void setMovieGender(String movieGender) {
		this.movieGender = movieGender;
	}

	/**
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}

	/**
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}

	/**
	 * @return the cinemas
	 */
	public List<CinemaEmbedd> getCinemas() {
		return cinemas;
	}

	/**
	 * @param cinemas the cinemas to set
	 */
	public void setCinemas(List<CinemaEmbedd> cinemas) {
		this.cinemas = cinemas;
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
		Movie other = (Movie) obj;
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
		return "Movie [key=" + key + ", internalIdentifier="
				+ internalIdentifier + ", brandId=" + brandId + ", name="
				+ name + ", originalName=" + originalName + ", avatarId="
				+ avatarId + ", rate=" + rate + ", lenght=" + lenght
				+ ", actors=" + actors + ", sinopsis=" + sinopsis
				+ ", movieGender=" + movieGender + ", director=" + director
				+ ", status=" + status + ", cinemas=" + cinemas
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
