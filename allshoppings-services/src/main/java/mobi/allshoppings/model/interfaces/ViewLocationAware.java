package mobi.allshoppings.model.interfaces;

import mobi.allshoppings.model.tools.ViewLocation;

public interface ViewLocationAware {

	public boolean isAvailableFor(ViewLocation vl);
}
