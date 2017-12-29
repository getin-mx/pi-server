package mobi.allshoppings.model;

import java.io.Serializable;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.PersistenceCapable;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@Cacheable("false")
public class WeeklyDashboardIndicatorData extends DashboardIndicatorData implements ModelKey, Serializable, Identificable {

    public Key key;

	public WeeklyDashboardIndicatorData() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 7919;
		int result = 1;
		result = prime * result
				+ ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result
				+ ((elementSubId == null) ? 0 : elementSubId.hashCode());
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((entityKind == null) ? 0 : entityKind.hashCode());
		result = prime * result
				+ ((shoppingId == null) ? 0 : shoppingId.hashCode());
		result = prime * result
				+ ((stringDate == null) ? 0 : stringDate.hashCode());
		result = prime * result
				+ ((subentityId == null) ? 0 : subentityId.hashCode());
		result = prime * result
				+ ((timeZone == null) ? 0 : timeZone.hashCode());
		result = prime * result
				+ ((screenName == null) ? 0 : screenName.hashCode());
		result = prime * result
				+ ((elementName == null) ? 0 : elementName.hashCode());
		result = prime * result
				+ ((elementSubName == null) ? 0 : elementSubName.hashCode());
		result = prime * result
				+ ((shoppingName == null) ? 0 : shoppingName.hashCode());
		result = prime * result
				+ ((subentityName == null) ? 0 : subentityName.hashCode());
		result = prime * result
				+ ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
		result = prime * result
				+ ((timeZone == null) ? 0 : timeZone.hashCode());
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
		WeeklyDashboardIndicatorData other = (WeeklyDashboardIndicatorData) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (elementSubId == null) {
			if (other.elementSubId != null)
				return false;
		} else if (!elementSubId.equals(other.elementSubId))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (entityKind == null) {
			if (other.entityKind != null)
				return false;
		} else if (!entityKind.equals(other.entityKind))
			return false;
		if (shoppingId == null) {
			if (other.shoppingId != null)
				return false;
		} else if (!shoppingId.equals(other.shoppingId))
			return false;
		if (stringDate == null) {
			if (other.stringDate != null)
				return false;
		} else if (!stringDate.equals(other.stringDate))
			return false;
		if (subentityId == null) {
			if (other.subentityId != null)
				return false;
		} else if (!subentityId.equals(other.subentityId))
			return false;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		return true;
	}

}
