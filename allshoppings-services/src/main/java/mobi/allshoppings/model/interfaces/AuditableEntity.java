package mobi.allshoppings.model.interfaces;

import java.util.Date;

public interface AuditableEntity {
	
	Date getLastUpdate();
	void setLastUpdate(Date date);
	
	Date getCreationDate();
	void setCreationDate(Date date);
	
	Date getDeletionDate();
	void setDeletionDate(Date date);

}
