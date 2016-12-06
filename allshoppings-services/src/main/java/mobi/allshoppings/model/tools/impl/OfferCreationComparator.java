package mobi.allshoppings.model.tools.impl;

import java.util.Comparator;

import mobi.allshoppings.model.Offer;

public class OfferCreationComparator implements Comparator<Offer>{

	@Override
	public int compare(Offer o1, Offer o2) {
		if( o1.getCreationDateTime().after(o2.getCreationDateTime())) return -1;
		if( o1.getCreationDateTime().before(o2.getCreationDateTime())) return 1;
		return 0;
	}
}
