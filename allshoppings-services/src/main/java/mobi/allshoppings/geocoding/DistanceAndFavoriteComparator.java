package mobi.allshoppings.geocoding;

import java.util.Comparator;

public class DistanceAndFavoriteComparator implements Comparator<DistanceAndFavoriteAware>{

	@Override
	public int compare(DistanceAndFavoriteAware o1, DistanceAndFavoriteAware o2) {
		if( o1.getFavorite() && !o2.getFavorite()) return -1;
		if( !o1.getFavorite() & o2.getFavorite()) return +1;
		return o1.getDistance() - o2.getDistance();
	}
}
