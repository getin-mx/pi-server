package mobi.allshoppings.model.adapter;

import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.WifiSpot;

public class WifiSpotAdapter extends WifiSpot implements IGenericAdapter, ICompletableAdapter {

	private static final long serialVersionUID = 5657858175564391408L;

	@Override
	public void completeAdaptation(Map<String, Object> options) throws ASException {
	}

}
