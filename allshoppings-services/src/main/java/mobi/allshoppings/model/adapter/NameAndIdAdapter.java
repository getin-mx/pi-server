package mobi.allshoppings.model.adapter;

public class NameAndIdAdapter implements IGenericAdapter {

	private String identifier;
	private String name;
	private String avatarId;
	
	public NameAndIdAdapter() {
		
	}
	
	public NameAndIdAdapter(String identifier, String name, String avatarId) {
		this.identifier = identifier;
		this.name = name;
		this.avatarId = avatarId;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

}
