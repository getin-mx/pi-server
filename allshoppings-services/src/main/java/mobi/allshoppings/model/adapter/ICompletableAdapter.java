package mobi.allshoppings.model.adapter;

import java.util.Map;

import mobi.allshoppings.exception.ASException;

public interface ICompletableAdapter extends IAdaptable {

	public void completeAdaptation(Map<String, Object> options) throws ASException;
	
}
