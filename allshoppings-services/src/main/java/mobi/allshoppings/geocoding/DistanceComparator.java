package mobi.allshoppings.geocoding;

import java.util.Comparator;

public class DistanceComparator implements Comparator<DistanceAndFavoriteAware>{

	@Override
	public int compare(DistanceAndFavoriteAware o1, DistanceAndFavoriteAware o2) {
		return o1.getDistance() - o2.getDistance();
	}
}
